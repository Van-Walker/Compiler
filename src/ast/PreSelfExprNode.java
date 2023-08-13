package ast;

import utils.*;

public class PreSelfExprNode extends UnaryExprNode {
    public PreSelfExprNode(Position position, String op, ExprNode expr) {
        super(position, op, expr);
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
