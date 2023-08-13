package utils;

import ast.*;
import java.util.HashMap;

public class Scope {
    public Scope parentScope = null;
    public Type returnType = null;
    public ClassDefNode inWhichClass = null;
    public boolean inLoop = false;
    public boolean returned = false;
    public HashMap <String, Type> varMap = new HashMap<>();

    public Scope() {}

    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.inLoop = parentScope.inLoop;
        this.inWhichClass = parentScope.inWhichClass;
    }

    public Scope(Scope parentScope, boolean inLoop) {
        this(parentScope);
        this.inLoop = inLoop;
    }

    // todo
    public Scope(Scope parentScope, Type returnType) {
        this(parentScope);
        this.returnType = returnType;
    }

    public Scope(Scope parentScope, ClassDefNode inWhichClass) {
        this.parentScope = parentScope;
        this.inWhichClass = inWhichClass;
    }

    public void addVar(String name, Type type) {
        varMap.put(name, type);
    }

    public boolean isRepeatedName(String name) {
        return varMap.containsKey(name);
    }

    public Type getVarType(String name) {
        if (varMap.containsKey(name)) return varMap.get(name);
        return parentScope == null ? null : parentScope.getVarType(name);
    }
}
