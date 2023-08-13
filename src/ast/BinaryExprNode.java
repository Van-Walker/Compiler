package ast;

import utils.*;

public class BinaryExprNode extends ExprNode{
    public String op;
    public ExprNode lhs, rhs;

    public BinaryExprNode(Position position, ExprNode lhs, String op, ExprNode rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public boolean isLeftValue() {
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
