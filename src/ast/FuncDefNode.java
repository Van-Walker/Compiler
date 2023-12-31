package ast;

import java.util.ArrayList;
import utils.*;
import ir.*;

public class FuncDefNode extends Node {
    public TypeNode returnType;
    public String funcName;
    public ParameterListNode parameterListNode = null;
    public ArrayList <StmtNode> stmts = new ArrayList<>();
    public IRFunction irFunction = null;
    public String className = null;

    public FuncDefNode(Position position, String funcName) {
        super(position);
        this.funcName = funcName;
    }

    public FuncDefNode(Position position, Type type, String funcName, Type paramType, int cnt) {
        super(position);
        this.returnType = new TypeNode(position, type.typeName, type.dim);
        this.funcName = funcName;
        if (paramType != null && cnt > 0) this.parameterListNode = new ParameterListNode(position, paramType, cnt);
    }

    public FuncDefNode(Position position, Type type, String funcName, String className, Type paramType, int cnt) {
        super(position);
        this.returnType = new TypeNode(position, type.typeName, type.dim);
        this.funcName = funcName;
        this.className = className;
        if (paramType != null && cnt > 0) this.parameterListNode = new ParameterListNode(position, paramType, cnt);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
