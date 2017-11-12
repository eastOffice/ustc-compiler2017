#include <llvm/IR/BasicBlock.h>
#include <llvm/IR/Constants.h>
#include <llvm/IR/DerivedTypes.h>
#include <llvm/IR/Function.h>
#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/Module.h>
#include <llvm/IR/Type.h>
#include <llvm/IR/Verifier.h>
#include <map>
#include <memory>

using namespace llvm;

int main()
{
    LLVMContext context;
    IRBuilder<> builder(context);

    auto module = new Module("gen_fib", context);
    Value* const_int32_0 = ConstantInt::get(Type::getInt32Ty(context), 0);
    Value* const_int32_1 = ConstantInt::get(Type::getInt32Ty(context), 1);
    Value* const_int32_2 = ConstantInt::get(Type::getInt32Ty(context), 2);
    Value* const_int32_10 = ConstantInt::get(Type::getInt32Ty(context), 10);
// create func fib
    auto func_fib = Function::Create(FunctionType::get(Type::getInt32Ty(context), Type::getInt32Ty(context), false),
                                 GlobalValue::LinkageTypes::ExternalLinkage,
                                 "fib", module);
    
    std::map<std::string, Value*> FibArg;   
    auto fib_bb0 = BasicBlock::Create(context, "", func_fib);
    builder.SetInsertPoint(fib_bb0);
     for (auto &Arg : func_fib->args())
    {   
       //Arg.setName("n");
       FibArg["n"] = &Arg;
    }
    auto fib_i0 = FibArg["n"];
    auto fib_i2 = builder.CreateAlloca(Type::getInt32Ty(context));
    fib_i2 -> setAlignment(4);
    auto fib_i3 = builder.CreateICmpEQ(fib_i0, const_int32_0);
    auto fib_i4 = BasicBlock::Create(context, "", func_fib);
    auto fib_i5 = BasicBlock::Create(context, "", func_fib);
    builder.CreateCondBr(fib_i3, fib_i4, fib_i5);

    builder.SetInsertPoint(fib_i4);
    builder.CreateStore(const_int32_0, fib_i2) -> setAlignment(4);
    auto fib_return = BasicBlock::Create(context, "return", func_fib);
    builder.CreateBr(fib_return);
    
    builder.SetInsertPoint(fib_i5);
    auto fib_i6 = builder.CreateICmpEQ(fib_i0, const_int32_1);
    auto fib_i7 = BasicBlock::Create(context, "", func_fib);
    auto fib_i8 = BasicBlock::Create(context, "", func_fib);
    builder.CreateCondBr(fib_i6, fib_i7, fib_i8);

    builder.SetInsertPoint(fib_i7);
    builder.CreateStore(const_int32_1, fib_i2) -> setAlignment(4);
    builder.CreateBr(fib_return);

    builder.SetInsertPoint(fib_i8);
    auto fib_i9 = builder.CreateNSWSub(fib_i0, const_int32_1);
    auto fib_i10 = builder.CreateCall(func_fib, {fib_i9});
    auto fib_i11 = builder.CreateNSWSub(fib_i0, const_int32_2);
    auto fib_i12 = builder.CreateCall(func_fib, {fib_i11});
    auto fib_i13 = builder.CreateNSWAdd(fib_i10, fib_i12);
    builder.CreateStore(fib_i13, fib_i2)->setAlignment(4);
    builder.CreateBr(fib_return);

    builder.SetInsertPoint(fib_return);
    auto fib_i14 = builder.CreateLoad(fib_i2);
    fib_i14 -> setAlignment(4);
    builder.CreateRet(fib_i14);

// create func main
    auto func_main = Function::Create(FunctionType::get(Type::getInt32Ty(context), std::vector<Type *>(), false),
                                 GlobalValue::LinkageTypes::ExternalLinkage,
                                 "main", module);
    
    auto main_bb0 = BasicBlock::Create(context, "", func_main);
    builder.SetInsertPoint(main_bb0);
    auto main_i1 = builder.CreateAlloca(Type::getInt32Ty(context));
    auto main_i2 = builder.CreateAlloca(Type::getInt32Ty(context));
    auto main_i3 = builder.CreateAlloca(Type::getInt32Ty(context));
    main_i1 -> setAlignment(4);
    main_i2 -> setAlignment(4);
    main_i3 -> setAlignment(4);
    builder.CreateStore(const_int32_0, main_i1) -> setAlignment(4);
    builder.CreateStore(const_int32_0, main_i2) -> setAlignment(4);
    builder.CreateStore(const_int32_0, main_i3) -> setAlignment(4);
    auto main_loop = BasicBlock::Create(context, "loop", func_main);
    builder.CreateBr(main_loop);

    builder.SetInsertPoint(main_loop);
    auto main_i4 = builder.CreateLoad(main_i3);
    main_i4 -> setAlignment(4);
    auto main_i5 = builder.CreateICmpSLT(main_i4, const_int32_10);
    auto main_i6 = BasicBlock::Create(context, "", func_main);
    auto main_return = BasicBlock::Create(context, "return", func_main);
    builder.CreateCondBr(main_i5, main_i6, main_return);

    builder.SetInsertPoint(main_i6);
    auto main_i7 = builder.CreateLoad(main_i3);
    main_i7 -> setAlignment(4);
    auto main_i8 = builder.CreateCall(func_fib, {main_i7});
    auto main_i9 = builder.CreateLoad(main_i2);
    main_i9 -> setAlignment(4);
    auto main_i10 = builder.CreateNSWAdd(main_i9, main_i8);
    builder.CreateStore(main_i10, main_i2) -> setAlignment(4);
    auto main_i11 = builder.CreateNSWAdd(main_i7, const_int32_1);
    builder.CreateStore(main_i11, main_i3) -> setAlignment(4);
    builder.CreateBr(main_loop);

    builder.SetInsertPoint(main_return);
    auto main_i12 = builder.CreateLoad(main_i2);
    main_i12 -> setAlignment(4);
    builder.CreateRet(main_i12);

    builder.ClearInsertionPoint();

    module->print(outs(), nullptr);
    delete module;
    return 0;
}