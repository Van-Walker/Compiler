package utils;

import ast.*;
import ir.IRFunction;
import ir.IRRegister;

import java.util.HashMap;

public class Scope {
    public Scope parentScope = null;
    public Type returnType = null;
    public ClassDefNode inWhichClass = null;
    public boolean inLoop = false;
    public LoopStmtNode inWhichLoop = null;
    public boolean returned = false;
    public HashMap <String, Type> varMap = new HashMap<>();
    public HashMap <String, IRRegister> IRVarMap = new HashMap<>();

    public Scope() {}

    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.inLoop = parentScope.inLoop;
        this.inWhichClass = parentScope.inWhichClass;
        this.inWhichLoop = parentScope.inWhichLoop;
    }

    public Scope(Scope parentScope, boolean inLoop) {
        this(parentScope);
        this.inLoop = inLoop;
    }

    public Scope(Scope parentScope, Type returnType) {
        this.parentScope = parentScope;
        this.inWhichClass = parentScope.inWhichClass;
        this.returnType = returnType;
    }

    public Scope(Scope parentScope, ClassDefNode inWhichClass) {
        this.parentScope = parentScope;
        this.inWhichClass = inWhichClass;
    }

    public Scope(Scope parentScope, LoopStmtNode inWhichLoop) {
        this(parentScope);
        this.inLoop = true;
        this.inWhichLoop = inWhichLoop;
    }

    public void addVar(String name, Type type) {
        varMap.put(name, type);
    }

    public void addIRVar(String name, IRRegister irRegister) {
        IRVarMap.put(name, irRegister);
    }

    public boolean isRepeatedName(String name) {
        return varMap.containsKey(name);
    }

    public Type getVarType(String name) {
        if (varMap.containsKey(name)) return varMap.get(name);
        return parentScope == null ? null : parentScope.getVarType(name);
    }

    public IRRegister getIRVarPtr(String name) {
        if (IRVarMap.containsKey(name)) return IRVarMap.get(name);
        return parentScope != null ? parentScope.getIRVarPtr(name) : null;
    }


}
