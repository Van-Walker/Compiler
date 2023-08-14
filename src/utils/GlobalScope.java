package utils;

import ast.*;
import java.util.HashMap;

public class GlobalScope extends Scope implements BuiltinElements {
    public HashMap <String, FuncDefNode> funcMap = new HashMap<>();
    public HashMap <String, ClassDefNode> classMap = new HashMap<>();

    public GlobalScope() {
        // System.out.println("Global init success!");
        funcMap.put("print", PrintFunc);
        funcMap.put("println", PrintlnFunc);
        funcMap.put("printInt", PrintIntFunc);
        funcMap.put("printlnInt", PrintlnIntFunc);
        funcMap.put("getString", GetStringFunc);
        funcMap.put("getInt", GetIntFunc);
        funcMap.put("toString", ToStringFunc);

        ClassDefNode stringDef = new ClassDefNode(null, "string");
        stringDef.funcMap.put("length", StringLengthFunc);
        stringDef.funcMap.put("substring", StringSubStringFunc);
        stringDef.funcMap.put("parseInt", StringParseIntFunc);
        stringDef.funcMap.put("ord", StringOrdFunc);
        classMap.put("string", stringDef);
        classMap.put("int", new ClassDefNode(null, "int"));
        classMap.put("bool", new ClassDefNode(null, "bool"));
    }

    public void addFunc(String name, FuncDefNode funcDefNode) {
        funcMap.put(name, funcDefNode);
    }

    public FuncDefNode getFuncDef(String name) {
        return funcMap.get(name);
    }

    public void addClass(String name, ClassDefNode classDefNode) {
        classMap.put(name, classDefNode);
    }

    public ClassDefNode getClassDef(String name) {
        return classMap.get(name);
    }
}
