package ast;

import utils.*;

public class VarDefUnitNode extends Node {
    public TypeNode typeNode;
    public String varName;
    public ExprNode init;

    public VarDefUnitNode(Position position, TypeNode typeNode, String varName) {
        super(position);
        this.typeNode = typeNode;
        this.varName = varName;
    }

    public VarDefUnitNode(Position position, TypeNode typeNode, String varName, ExprNode init) {
        super(position);
        this.typeNode = typeNode;
        this.varName = varName;
        this.init = init;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
