package ast;

import utils.*;

public class AssignExprNode extends BinaryExprNode{
    public AssignExprNode(Position position, ExprNode lhs, ExprNode rhs) {
        super(position, lhs, "=", rhs);
    }

    @Override
    public boolean isLeftValue() {
        return true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
