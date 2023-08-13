package ast;

import utils.*;

public class UnaryExprNode extends ExprNode {
    public String op;
    public ExprNode expr;

    public UnaryExprNode(Position position, String op, ExprNode expr) {
        super(position);
        this.op = op;
        this.expr = expr;
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
