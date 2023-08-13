package ast;

import utils.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassDefNode extends Node {
    public String name;
    public ArrayList <VarDefNode> varDefList = new ArrayList<VarDefNode>();
    public ArrayList <FuncDefNode> funcDefList = new ArrayList<FuncDefNode>();
    public HashMap <String, FuncDefNode> funcMap = new HashMap<String, FuncDefNode>();
    public HashMap <String, VarDefUnitNode> varMap = new HashMap<String, VarDefUnitNode>();
    public ClassBuildNode classBuild;

    public ClassDefNode(Position position, String name) {
        super(position);
        this.name = name;
    }

    public FuncDefNode getFuncDef(String name) {
        return funcMap.get(name);
    }

    public Type getVarType(String name) {
        VarDefUnitNode var = varMap.get(name);
        if (var == null) return null;
        return var.typeNode.type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
