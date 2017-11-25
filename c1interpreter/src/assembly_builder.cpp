
#include "assembly_builder.h"

#include <vector>

using namespace llvm;
using namespace c1_recognizer::syntax_tree;

void assembly_builder::visit(assembly &node)
{
    in_global = true;
    bb_count = 0;
    value_result = ConstantInt::get(Type::getInt32Ty(context), 0);
    const_result = 0;
    error_flag = false;
    for(auto def : node.global_defs)
    {
        def->accept(*this);
    }
}

void assembly_builder::visit(func_def_syntax &node)
{
    auto func_name = node.name;
    // name check
    for(auto t : functions)
    {
        if(t.first == func_name)
        {
            error_flag = true;
            err.error(node.line, node.pos, "function: " + func_name + " has already been declared.");
            return;
        }
    }
    auto func = Function::Create(FunctionType::get(Type::getVoidTy(context), std::vector<Type *>(), false),
                                 GlobalValue::LinkageTypes::ExternalLinkage,
                                 func_name, module.get());
    current_function = func;
    auto func_entry = BasicBlock::Create(context, "entry", func);
    builder.SetInsertPoint(func_entry);
    functions[func_name] = func;
    in_global = false;
    node.body->accept(*this);
    builder.CreateRetVoid();
    in_global = true;
}

void assembly_builder::visit(cond_syntax &node)
{
    constexpr_expected = false;
    node.lhs->accept(*this);
    auto _lhs = value_result;
    node.rhs->accept(*this);
    auto _rhs = value_result;
    if(node.op == relop::equal)
        value_result = builder.CreateICmpEQ(_lhs, _rhs);
    else if(node.op == relop::non_equal)
        value_result = builder.CreateICmpNE(_lhs, _rhs);
    else if(node.op == relop::less)
        value_result = builder.CreateICmpSLT(_lhs, _rhs);
    else if(node.op == relop::less_equal)
        value_result = builder.CreateICmpSLE(_lhs, _rhs);
    else if(node.op == relop::greater)
        value_result = builder.CreateICmpSGT(_lhs, _rhs);
    else if(node.op == relop::greater_equal)
        value_result = builder.CreateICmpSGE(_lhs, _rhs);
}

void assembly_builder::visit(binop_expr_syntax &node)
{
    if(constexpr_expected == false)
    {
        node.lhs->accept(*this);
        auto _lhs = value_result;
        node.rhs->accept(*this);
        auto _rhs = value_result;
        if(node.op == binop::plus)
            value_result = builder.CreateNSWAdd(_lhs, _rhs);
        else if(node.op == binop::minus)
            value_result = builder.CreateNSWSub(_lhs, _rhs);
        else if(node.op == binop::multiply)
            value_result = builder.CreateNSWMul(_lhs, _rhs);
        else if(node.op == binop::divide)
            value_result = builder.CreateSDiv(_lhs, _rhs);
        else if(node.op == binop::modulo)
            value_result = builder.CreateSRem(_lhs, _rhs);
    }
    else
    {
        node.lhs->accept(*this);
        int _lhs = const_result;
        node.rhs->accept(*this);
        int _rhs = const_result;
        if(node.op == binop::plus)
            const_result = _lhs + _rhs;
        else if(node.op == binop::minus)
            const_result = _lhs - _rhs;
        else if(node.op == binop::multiply)
            const_result = _lhs * _rhs;
        else if(node.op == binop::divide)
            const_result = _lhs / _rhs;
        else if(node.op == binop::modulo)
            const_result = _lhs % _rhs;
    }
}

void assembly_builder::visit(unaryop_expr_syntax &node)
{
    // only need to deal with minus
    node.rhs->accept(*this);
    if(node.op == unaryop::minus)
    {
        if(constexpr_expected)
            const_result = -const_result;
        else{
            Value* const_int32_0 = ConstantInt::get(Type::getInt32Ty(context), 0);
            value_result = builder.CreateNSWSub(const_int32_0, value_result);
        }
    }
}

void assembly_builder::visit(lval_syntax &node)
{
    if(constexpr_expected == true)
    {
        err.error(node.line, node.pos, "constant value expected.");
        error_flag = true;
        return;
    }
    auto t = lookup_variable(node.name);
    if(!std::get<0>(t))
    {
        err.error(node.line, node.pos, "use of undeclared variable : " + node.name);
        error_flag = true;
        return;
    }
    auto lval =  std::get<0>(t); // data
    bool is_const = std::get<1>(t);
    bool is_array = std::get<2>(t);
    if(is_array == false)
    {
        if(node.array_index)
        {
            err.error(node.line, node.pos, "type mismatch: expecting a non-array.");
            error_flag = true;
            return;
        }
        if(lval_as_rval == false)
        {
            if(is_const == true)
            {
                err.error(node.line, node.pos, "const value cannot be a lval.");
                error_flag = true;
                return;
            }
            value_result = lval;
        }
        else
        {
            value_result = builder.CreateLoad(lval);
        }
    }
    else
    {
        if(!node.array_index)
        {
            err.error(node.line, node.pos, "type mismatch: expecting an array.");
            error_flag = true;
            return;
        }
        std::vector<Value *> index;
        int temp_lval = lval_as_rval;
        lval_as_rval = true;
        node.array_index->accept(*this);
        lval_as_rval = temp_lval;
        Value * const_int_index;
        const_int_index = value_result;
        index.push_back((Value *)ConstantInt::get(Type::getInt32Ty(context), 0));
        index.push_back(const_int_index);
        Value * element = builder.CreateGEP(lval, index);
        if(lval_as_rval == false)
        {
            if(is_const == true)
            {
                err.error(node.line, node.pos, "const value cannot be a lval.");
                error_flag = true;
                return;
            }
            value_result = element;
        }
        else
        {
            value_result = builder.CreateLoad(element);
        }
    }
}

void assembly_builder::visit(literal_syntax &node)
{
    if(constexpr_expected == true)
    {
        const_result = node.number;
    }
    else
    {
        value_result = ConstantInt::get(Type::getInt32Ty(context), node.number);
    }
}

void assembly_builder::visit(var_def_stmt_syntax &node)
{
    //printf("visiting var_def.\n");
    auto var_name = node.name;
    bool is_array = false;
    if(node.array_length) is_array = true;
    Value * var_ptr;
    int temp_array_len = 0;
    if(in_global)
    {
        // global var can only be initialized with a constant
        //printf("declaring global var: %s\n", node.name.c_str());
        constexpr_expected = true;
        const_result = 0;
        if(is_array == false)
        {
            if(!node.initializers.empty())
            {
                node.initializers[0]->accept(*this);
            }
            var_ptr = new GlobalVariable(*module,
                                        Type::getInt32Ty(module->getContext()),
                                        node.is_constant,
                                        GlobalValue::ExternalLinkage,
                                        ConstantInt::get(Type::getInt32Ty(module->getContext()),const_result),
                                        var_name);
        }
        else
        {
            std::vector<Constant *> init_array;
            Constant * const_int_0 = ConstantInt::get(Type::getInt32Ty(context), 0);
            node.array_length->accept(*this);
            //if(error_flag == true) return;
            int array_len = const_result;
            temp_array_len = const_result;
            if(temp_array_len < 0)
            {
                err.error(node.line, node.pos, "size of an array must be greater than 0.");
                error_flag = true;
                return;
            }
            if(temp_array_len == 0)
            {
                err.warn(node.line, node.pos, "declaring a 0-sized array.");
            }
            int init_len = node.initializers.size();
            if(init_len > temp_array_len)
            {
                err.error(node.line, node.pos, "length of initializer larger than that of array.");
                error_flag = true;
                return;
            }
            for(int i = 0; i < temp_array_len; i++)
            {
                if(i < init_len)
                {
                    node.initializers[i]->accept(*this);
                    Constant * temp_init = ConstantInt::get(Type::getInt32Ty(context), const_result);
                    init_array.push_back(temp_init);
                }
                else
                {
                    init_array.push_back(const_int_0);
                }
            }
            var_ptr = new GlobalVariable(*module,
                                        ArrayType::get(Type::getInt32Ty(context), temp_array_len), 
                                        node.is_constant,
                                        GlobalValue::ExternalLinkage, 
                                        ConstantArray::get(ArrayType::get(Type::getInt32Ty(context), temp_array_len), init_array), 
                                        var_name);
        }
    }
    else
    {
        // local var
        if(is_array == false)
        {
            var_ptr = builder.CreateAlloca(Type::getInt32Ty(context), nullptr, var_name);
        }
        else
        {
            constexpr_expected = true;
            node.array_length->accept(*this);
            //Value * array_len = ConstantInt::get(Type::getInt32Ty(context), const_result);
            temp_array_len = const_result;
            if(temp_array_len < 0)
            {
                err.error(node.line, node.pos, "size of an array must be greater than 0.");
                error_flag = true;
                return;
            }
            if(temp_array_len == 0)
            {
                err.warn(node.line, node.pos, "declaring a 0-sized array.");
            }
            var_ptr = builder.CreateAlloca(ArrayType::get(Type::getInt32Ty(context), temp_array_len), nullptr, var_name);
        }
    }
    // deal with initialization
    if(in_global == false && node.initializers.size())
    {
        constexpr_expected = false;
        if(is_array)
        {
            int init_len = node.initializers.size();
            if(init_len > temp_array_len)
            {
                err.error(node.line, node.pos, "length of initializer larger than that of array.");
                error_flag = true;
                return;
            }
            Value * element;
            std::vector<Value *> index;
            index.push_back((Value *)ConstantInt::get(Type::getInt32Ty(context), 0));
            for(int len = 0; len < temp_array_len; len++)
            {
                index.push_back((Value *)ConstantInt::get(Type::getInt32Ty(context), len));
                element = builder.CreateGEP(var_ptr, index);
                index.pop_back();
                if(len < init_len)
                {
                    node.initializers[len]->accept(*this);
                    Value * init = value_result;
                    builder.CreateStore(init, element);
                    
                }
                else
                {
                    Value * init = ConstantInt::get(Type::getInt32Ty(context), 0);
                    builder.CreateStore(init, element);
                }
            }
        }
        else
        {
            if(node.initializers.size() != 1)
            {
                err.error(node.line, node.pos, "more than one initializers to a variable.");
                error_flag = true;
                return;
                // acturally, parser will deal with this.
            }
            else
            {
                //printf("initializing variable\n");
                node.initializers[0]->accept(*this);
                builder.CreateStore(value_result, var_ptr);
            }
        }
    }
    if(!declare_variable(var_name, var_ptr, node.is_constant, is_array))
    {
        err.error(node.line, node.pos, "variable: " + node.name + " has already been declared.");
        error_flag = true;
        return;
    }
}

void assembly_builder::visit(assign_stmt_syntax &node)
{
    constexpr_expected = false;
    lval_as_rval = false;
    node.target->accept(*this);
    auto _target = value_result;
    lval_as_rval = true;
    constexpr_expected = false;
    node.value->accept(*this);
    auto _value = value_result;
    builder.CreateStore(_value, _target);
}

void assembly_builder::visit(func_call_stmt_syntax &node)
{
    auto func_name = node.name;
    // check if the functions is declared
    int found = 0;
    for(auto t : functions)
    {
        if(t.first == func_name)
        {
            found = 1;
            break;
        }
    }
    if(found == 0)
    {
        err.error(node.line, node.pos, "use of undeclared function : " + node.name);
        error_flag = true;
        return;
    }
    auto func = functions[func_name];
    builder.CreateCall(func, {});
}

void assembly_builder::visit(block_syntax &node)
{
    enter_scope();
    for(auto t : node.body)
    {
        t->accept(*this);
    }
    exit_scope();
}

void assembly_builder::visit(if_stmt_syntax &node)
{
    node.pred->accept(*this);
    auto if_cond = value_result;
    auto if_then = BasicBlock::Create(context, "", current_function);
    auto if_else = BasicBlock::Create(context, "", current_function);
    if(node.else_body)
    {
        auto if_next = BasicBlock::Create(context, "", current_function);
        builder.CreateCondBr(if_cond, if_then, if_else);
        builder.SetInsertPoint(if_then);
        node.then_body->accept(*this);
        builder.CreateBr(if_next);
        builder.SetInsertPoint(if_else);
        node.else_body->accept(*this);
        builder.CreateBr(if_next);
        builder.SetInsertPoint(if_next);
    }
    else
    {
        builder.CreateCondBr(if_cond, if_then, if_else);
        builder.SetInsertPoint(if_then);
        node.then_body->accept(*this);
        builder.CreateBr(if_else);
        builder.SetInsertPoint(if_else);
    }
    
}

void assembly_builder::visit(while_stmt_syntax &node)
{
    auto while_loop = BasicBlock::Create(context, "", current_function);
    auto while_true = BasicBlock::Create(context, "", current_function);
    auto while_next = BasicBlock::Create(context, "", current_function);
    builder.CreateBr(while_loop);
    builder.SetInsertPoint(while_loop);
    node.pred->accept(*this);
    auto while_cond = value_result;
    builder.CreateCondBr(while_cond, while_true, while_next);
    builder.SetInsertPoint(while_true);
    node.body->accept(*this);
    builder.CreateBr(while_loop);
    builder.SetInsertPoint(while_next);

}

void assembly_builder::visit(empty_stmt_syntax &node)
{
}
