package utils;

import ast.FuncDefNode;
import ir.*;

// todo
public interface BuiltinElements {
    Type VoidType = new Type("void");
    Type IntType = new Type("int");
    Type BoolType = new Type("bool");
    Type StringType = new Type("string");
    Type NullType = new Type("null");
    Type ThisType = new Type("this");

    FuncDefNode PrintFunc = new FuncDefNode(null, VoidType, "print", StringType, 1);
    FuncDefNode PrintlnFunc = new FuncDefNode(null, VoidType, "println", StringType, 1);
    FuncDefNode PrintIntFunc = new FuncDefNode(null, VoidType, "printInt", IntType, 1);
    FuncDefNode PrintlnIntFunc = new FuncDefNode(null, VoidType, "printlnInt", IntType, 1);
    FuncDefNode GetStringFunc = new FuncDefNode(null, StringType, "getString", null, 0);
    FuncDefNode GetIntFunc = new FuncDefNode(null, IntType, "getInt", null, 0);
    FuncDefNode ToStringFunc = new FuncDefNode(null, StringType, "toString", IntType, 1);

    FuncDefNode StringLengthFunc = new FuncDefNode(null, IntType, "length", null, 0);
    FuncDefNode StringSubStringFunc = new FuncDefNode(null, StringType, "substring", IntType, 2);
    FuncDefNode StringParseIntFunc = new FuncDefNode(null, IntType, "parseInt", null, 0);
    FuncDefNode StringOrdFunc = new FuncDefNode(null, IntType, "ord", IntType, 1);
    FuncDefNode ArraySizeFunc = new FuncDefNode(null, IntType, "size", null, 0);

    // IR Types:
    IRType irVoidType = new IRVoidType();
    IRType irNullType = new IRPointerType(irVoidType);
    IRType irIntType = new IRIntType(32);
    IRType irIntPtrType = new IRPointerType(irIntType);
    IRType irBoolType = new IRIntType(1);
    IRType irCharType = new IRIntType(8);
    IRType irStringType = new IRPointerType(irCharType);

    // IR Constants:
    VoidConst voidConst = new VoidConst();
    NullConst nullConst = new NullConst();
    BoolConst trueConst = new BoolConst(true);
    BoolConst falseConst = new BoolConst(false);
    IntConst intConstZero = new IntConst(0);
    IntConst intConstOne = new IntConst(1);
    IntConst intConstNegativeOne = new IntConst(-1);
    IntConst intConstFour = new IntConst(4);

}
