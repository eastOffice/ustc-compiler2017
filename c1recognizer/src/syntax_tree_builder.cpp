
#include "syntax_tree_builder.h"
#include <memory>

using namespace c1_recognizer;
using namespace c1_recognizer::syntax_tree;

syntax_tree_builder::syntax_tree_builder(error_reporter &_err) : err(_err) {}

antlrcpp::Any syntax_tree_builder::visitCompilationUnit(C1Parser::CompilationUnitContext *ctx)
{
    auto result = new assembly;
    result->line = 1;
    result->pos = 0;
    auto decls = ctx->decl();
    auto funcdefs = ctx->funcdef();
    int decls_i = 0, funcdefs_i = 0;
    for(auto child : ctx->children)
    {
        if(antlrcpp::is<C1Parser::DeclContext *>(child))
        {
            auto decllist = visit(decls[decls_i++]).as<ptr_list<var_def_stmt_syntax > >();
            for(int i = 0; i < decllist.size(); ++i)
            {
                result->global_defs.push_back(decllist[i]);
            }
        }
        else if(antlrcpp::is<C1Parser::FuncdefContext *>(child))
        {
            ptr<global_def_syntax> temp;
            temp.reset(visit(funcdefs[funcdefs_i++]).as<global_def_syntax *>());
            result->global_defs.push_back(temp);
        }
    }
    return static_cast<assembly *>(result);
}

antlrcpp::Any syntax_tree_builder::visitDecl(C1Parser::DeclContext *ctx)
{
//  constdecl | vardecl;
    if(ctx->constdecl())
        return visit(ctx->constdecl());
    else if(ctx->vardecl())
        return visit(ctx->vardecl());
}

antlrcpp::Any syntax_tree_builder::visitConstdecl(C1Parser::ConstdeclContext *ctx)
{
// Const (Int)? constdef (Comma constdef)* SemiColon;
// give a warning if int is missing
    int line = ctx->getStart()->getLine();
    int pos = ctx->getStart()->getCharPositionInLine();
    if(!ctx->Int())
        err.warn(line, pos, ": 'int' is missing.");

    ptr_list<var_def_stmt_syntax> result;
    auto constdefs = ctx->constdef();
    for(int i = 0; i < constdefs.size(); ++i)
    {
        ptr<var_def_stmt_syntax> temp;
        temp.reset(visit(constdefs[i]).as<var_def_stmt_syntax *>());
        result.push_back(temp);
    }
    return static_cast<ptr_list<var_def_stmt_syntax> >(result);
}

antlrcpp::Any syntax_tree_builder::visitConstdef(C1Parser::ConstdefContext *ctx)
{
    auto expressions = ctx->exp();
    auto result = new var_def_stmt_syntax;
    int comma_num = ctx->Comma().size();
    int exp_num = expressions.size();
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    result->is_constant = true;
    result->name = std::string(ctx->Identifier()->getSymbol()->getText());
    result->initializers.clear();
    // Identifier Assign exp
    if(!ctx->LeftBracket())
    {
        result->array_length.reset();
        result->initializers.resize(1);
        result->initializers[0].reset(visit(expressions[0]).as<expr_syntax *>());       
    }
    // Identifier LeftBracket exp RightBracket Assign LeftBrace exp (Comma exp)* RightBrace
    else if(exp_num - 2 == comma_num)
    {
        result->array_length.reset(visit(expressions[0]).as<expr_syntax *>());
        for(int i = 1; i < exp_num; ++i)
        {
            std::shared_ptr<expr_syntax> temp;
            temp.reset(visit(expressions[i]).as<expr_syntax *>());
            result->initializers.push_back(temp);
        }
    }
    // Identifier LeftBracket  RightBracket Assign LeftBrace exp (Comma exp)* RightBrace    
    else
    {
        if(comma_num == 0)
            result->array_length.reset();
        else
        {
            literal_syntax *temp = new literal_syntax;
            temp->line = ctx->getStart()->getLine();
            temp->pos = ctx->LeftBracket()->getSymbol()->getCharPositionInLine() + 1;
            temp->number = comma_num + 1;
            result->array_length.reset(temp);
        }
        for(int i = 0; i < exp_num; ++i)
        {
            std::shared_ptr<expr_syntax> temp;
            temp.reset(visit(expressions[i]).as<expr_syntax *>());
            result->initializers.push_back(temp);
        }
    }
    return static_cast<var_def_stmt_syntax *>(result);
}

antlrcpp::Any syntax_tree_builder::visitVardecl(C1Parser::VardeclContext *ctx)
{
// Int vardef (Comma vardef)* SemiColon;
    ptr_list<var_def_stmt_syntax> result;
    auto vardefs = ctx->vardef();
    for(int i = 0; i < vardefs.size(); ++i)
    {
        ptr<var_def_stmt_syntax> temp;
        temp.reset(visit(vardefs[i]).as<var_def_stmt_syntax *>());
        result.push_back(temp);
    }
    return static_cast<ptr_list<var_def_stmt_syntax> >(result);
}

antlrcpp::Any syntax_tree_builder::visitVardef(C1Parser::VardefContext *ctx)
{
    auto result = new var_def_stmt_syntax;
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    result->is_constant = false;
    result->name = std::string(ctx->Identifier()->getSymbol()->getText());
    result->initializers.clear();
    auto expressions = ctx->exp();
    int comma_num = ctx->Comma().size(); 
    int exp_num = expressions.size();
    // Identifier | Indentifier Assign exp
    if(!ctx->LeftBracket()) 
    {
        if(ctx->Assign())
        {
        result->array_length.reset();
        result->initializers.resize(1);
        result->initializers[0].reset(visit(expressions[0]).as<expr_syntax *>());
        }
        else
        {
            result->array_length.reset();
            result->initializers.resize(1);
            result->initializers[0].reset();
        }
    }
    // Identifier LeftBracket exp RightBracket
    else if(!ctx->LeftBrace())
    {
        result->array_length.reset(visit(expressions[0]).as<expr_syntax *>());
        result->initializers.clear();
    }
    // Identifier LeftBracket exp RightBracket Assign LeftBrace exp (Comma exp)* RightBrace
    else if(exp_num - 2 == comma_num)
    {
        result->array_length.reset(visit(expressions[0]).as<expr_syntax *>());
        for(int i = 1; i < exp_num; ++i)
        {
            std::shared_ptr<expr_syntax> temp;
            temp.reset(visit(expressions[i]).as<expr_syntax *>());
            result->initializers.push_back(temp);
        }
    }
    // Identifier LeftBracket RightBracket Assign LeftBrace exp (Comma exp)* RightBrace
    else
    {
        if(comma_num == 0)
            result->array_length.reset();
        else
        {
            literal_syntax *temp = new literal_syntax;
            temp->line = ctx->getStart()->getLine();
            temp->pos = ctx->LeftBracket()->getSymbol()->getCharPositionInLine() + 1;
            temp->number = comma_num + 1;
            result->array_length.reset(temp);
        }
            
        for(int i = 0; i < exp_num; ++i)
        {
            std::shared_ptr<expr_syntax> temp;
            temp.reset(visit(expressions[i]).as<expr_syntax *>());
            result->initializers.push_back(temp);
        }
    }
    return static_cast<var_def_stmt_syntax *>(result);
}

antlrcpp::Any syntax_tree_builder::visitFuncdef(C1Parser::FuncdefContext *ctx)
{
// Void Identifier LeftParen RightParen block;
    auto result = new func_def_syntax;
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    auto indent = ctx->Identifier();
    result->name = std::string(indent->getSymbol()->getText());
    result->body.reset(visit(ctx->block()).as<block_syntax *>());
    return static_cast<global_def_syntax *>(result);
}

antlrcpp::Any syntax_tree_builder::visitBlock(C1Parser::BlockContext *ctx)
{
// LeftBrace (decl|stmt)* RightBrace;
    auto decls = ctx->decl();
    auto stmts = ctx->stmt();
    auto result = new block_syntax;
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    result->body.clear();
    int decls_i = 0, stmts_i = 0;
    for(auto child : ctx->children)
    {
        std::shared_ptr<stmt_syntax> temp;
        if(antlrcpp::is<C1Parser::DeclContext *>(child))
        {
            auto decllist = visit(decls[decls_i++]).as<ptr_list<var_def_stmt_syntax> >();
            for(int i = 0; i < decllist.size(); ++i)
            {
                result->body.push_back(decllist[i]);
            }
        }
        else if(antlrcpp::is<C1Parser::StmtContext *>(child))
        {
            temp.reset(visit(stmts[stmts_i++]).as<stmt_syntax *>());
            result->body.push_back(temp);
        }
    }
    return static_cast<block_syntax *>(result);
}

antlrcpp::Any syntax_tree_builder::visitStmt(C1Parser::StmtContext *ctx)
{
    auto stmts = ctx->stmt();
    // lval Assign exp SemiColon
    if(ctx->lval())
    {
        auto result = new assign_stmt_syntax;
        result->line = ctx->getStart()->getLine();
        result->pos = ctx->getStart()->getCharPositionInLine();
        result->target.reset(visit(ctx->lval()).as<lval_syntax *>());
        result->value.reset(visit(ctx->exp()).as<expr_syntax *>());
        return static_cast<stmt_syntax *>(result);
    }
    // Identifier LeftParen RightParen SemiColon
    else if(auto identifier = ctx->Identifier())
    {
        auto result = new func_call_stmt_syntax;
        result->line = identifier->getSymbol()->getLine();
        result->pos = identifier->getSymbol()->getCharPositionInLine();
        result->name = std::string(identifier->getSymbol()->getText());
        return static_cast<stmt_syntax *>(result);
    }
    // block
    else if(ctx->block())
    {
        block_syntax *result = visit(ctx->block());
        return static_cast<stmt_syntax *>(result);
    }
    // If LeftParen cond RightParen stmt (Else stmt)?
    else if(ctx->If())
    {
        auto result = new if_stmt_syntax;
        result->line = ctx->getStart()->getLine();
        result->pos = ctx->getStart()->getCharPositionInLine();
        result->pred.reset(visit(ctx->cond()).as<cond_syntax *>());
        result->then_body.reset(visit(stmts[0]).as<stmt_syntax *>());
        if(ctx->Else())
            result->else_body.reset(visit(stmts[1]).as<stmt_syntax *>());
        else
            result->else_body.reset();
        return static_cast<stmt_syntax *>(result);
    }
    // While LeftParen cond RightParen stmt
    else if(ctx->While())
    {
        auto result = new while_stmt_syntax;
        result->line = ctx->getStart()->getLine();
        result->pos = ctx->getStart()->getCharPositionInLine();
        result->pred.reset(visit(ctx->cond()).as<cond_syntax *>());
        result->body.reset(visit(stmts[0]).as<stmt_syntax *>());
        return static_cast<stmt_syntax *>(result);
    }
    // Semicolon
    else
    {
    auto result = new empty_stmt_syntax;
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    return static_cast<stmt_syntax *>(result);
    }
}

antlrcpp::Any syntax_tree_builder::visitLval(C1Parser::LvalContext *ctx)
{
    // Identifier | Identifier LeftBracket exp RightBracket;
    auto result = new lval_syntax;
    auto identifier = ctx->Identifier();
    result->line = identifier->getSymbol()->getLine();
    result->pos = identifier->getSymbol()->getCharPositionInLine();
    result->name = std::string(identifier->getSymbol()->getText());
    if(ctx->exp())
        result->array_index.reset(visit(ctx->exp()).as<expr_syntax *>());
    else
        result->array_index.reset();
    return static_cast<lval_syntax *>(result);
}

antlrcpp::Any syntax_tree_builder::visitCond(C1Parser::CondContext *ctx)
{
    // exp (Equal | NonEqual | Less | Greater | LessEqual | GreaterEqual) exp;
    auto result = new cond_syntax;
    result->line = ctx->getStart()->getLine();
    result->pos = ctx->getStart()->getCharPositionInLine();
    auto expressions = ctx->exp();
    result->lhs.reset(visit(expressions[0]).as<expr_syntax *>());
    if(ctx->Equal())
        result->op = relop::equal;
    else if(ctx->NonEqual())
        result->op = relop::non_equal;
    else if(ctx->Less())
        result->op = relop::less;
    else if(ctx->Greater())
        result->op = relop::greater;
    else if(ctx->LessEqual())
        result->op = relop::less_equal;
    else if(ctx->GreaterEqual())
        result->op = relop::greater_equal;
    result->rhs.reset(visit(expressions[1]).as<expr_syntax *>());
    return static_cast<cond_syntax *>(result);
}

// Returns antlrcpp::Any, which is constructable from any type.
// However, you should be sure you use the same type for packing and depacking the `Any` object.
// Or a std::bad_cast exception will rise.
// This function always returns an `Any` object containing a `expr_syntax *`.
antlrcpp::Any syntax_tree_builder::visitExp(C1Parser::ExpContext *ctx)
{
    // Get all sub-contexts of type `exp`.
    auto expressions = ctx->exp();
    // Two sub-expressions presented: this indicates it's a expression of binary operator, aka `binop`.
    if (expressions.size() == 2)
    {
        auto result = new binop_expr_syntax;
        // Set line and pos.
        result->line = ctx->getStart()->getLine();
        result->pos = ctx->getStart()->getCharPositionInLine();
        // visit(some context) is equivalent to calling corresponding visit method; dispatching is done automatically
        // by ANTLR4 runtime. For this case, it's equivalent to visitExp(expressions[0]).
        // Use reset to set a new pointer to a std::shared_ptr object. DO NOT use assignment; it won't work.
        // Use `.as<Type>()' to get value from antlrcpp::Any object; notice that this Type must match the type used in
        // constructing the Any object, which is constructed from (usually pointer to some derived class of
        // syntax_node, in this case) returning value of the visit call.
        result->lhs.reset(visit(expressions[0]).as<expr_syntax *>());
        // Check if each token exists.
        // Returnd value of the calling will be nullptr (aka NULL in C) if it isn't there; otherwise non-null pointer.
        if (ctx->Plus())
            result->op = binop::plus;
        if (ctx->Minus())
            result->op = binop::minus;
        if (ctx->Multiply())
            result->op = binop::multiply;
        if (ctx->Divide())
            result->op = binop::divide;
        if (ctx->Modulo())
            result->op = binop::modulo;
        result->rhs.reset(visit(expressions[1]).as<expr_syntax *>());
        return static_cast<expr_syntax *>(result);
    }
    // Otherwise, if `+` or `-` presented, it'll be a `unaryop_expr_syntax`.
    if (ctx->Plus() || ctx->Minus())
    {
        auto result = new unaryop_expr_syntax;
        result->line = ctx->getStart()->getLine();
        result->pos = ctx->getStart()->getCharPositionInLine();
        if (ctx->Plus())
            result->op = unaryop::plus;
        if (ctx->Minus())
            result->op = unaryop::minus;
        result->rhs.reset(visit(expressions[0]).as<expr_syntax *>());
        return static_cast<expr_syntax *>(result);
    }
    // In the case that `(` exists as a child, this is an expression like `'(' expressions[0] ')'`.
    if (ctx->LeftParen())
        return visit(expressions[0]); // Any already holds expr_syntax* here, no need for dispatch and re-patch with casting.
    // If `Number` exists as a child, we can say it's a literal integer expression.
    if (auto number = ctx->Number())
    {
        auto result = new literal_syntax;
        result->line = number->getSymbol()->getLine();
        result->pos = number->getSymbol()->getCharPositionInLine();
        auto text = number->getSymbol()->getText();
        if (text[0] == '0' && text[1] == 'x')              // Hexadecimal
            result->number = std::stoi(text, nullptr, 16); // std::stoi will eat '0x'
        else                                               // Decimal
            result->number = std::stoi(text, nullptr, 10);
        return static_cast<expr_syntax *>(result);
    }
    if(ctx->lval())
        return static_cast<expr_syntax *>(visit(ctx->lval()).as<lval_syntax *>());
}

ptr<syntax_tree_node> syntax_tree_builder::operator()(antlr4::tree::ParseTree *ctx)
{
    auto result = visit(ctx);
    if (result.is<syntax_tree_node *>())
        return ptr<syntax_tree_node>(result.as<syntax_tree_node *>());
    if (result.is<assembly *>())
        return ptr<syntax_tree_node>(result.as<assembly *>());
    if (result.is<global_def_syntax *>())
        return ptr<syntax_tree_node>(result.as<global_def_syntax *>());
    if (result.is<func_def_syntax *>())
        return ptr<syntax_tree_node>(result.as<func_def_syntax *>());
    if (result.is<cond_syntax *>())
        return ptr<syntax_tree_node>(result.as<cond_syntax *>());
    if (result.is<expr_syntax *>())
        return ptr<syntax_tree_node>(result.as<expr_syntax *>());
    if (result.is<binop_expr_syntax *>())
        return ptr<syntax_tree_node>(result.as<binop_expr_syntax *>());
    if (result.is<unaryop_expr_syntax *>())
        return ptr<syntax_tree_node>(result.as<unaryop_expr_syntax *>());
    if (result.is<lval_syntax *>())
        return ptr<syntax_tree_node>(result.as<lval_syntax *>());
    if (result.is<literal_syntax *>())
        return ptr<syntax_tree_node>(result.as<literal_syntax *>());
    if (result.is<stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<stmt_syntax *>());
    if (result.is<var_def_stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<var_def_stmt_syntax *>());
    if (result.is<assign_stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<assign_stmt_syntax *>());
    if (result.is<func_call_stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<func_call_stmt_syntax *>());
    if (result.is<block_syntax *>())
        return ptr<syntax_tree_node>(result.as<block_syntax *>());
    if (result.is<if_stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<if_stmt_syntax *>());
    if (result.is<while_stmt_syntax *>())
        return ptr<syntax_tree_node>(result.as<while_stmt_syntax *>());
    return nullptr;
}
