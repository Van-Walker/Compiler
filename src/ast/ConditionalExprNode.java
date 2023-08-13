package ast;

import utils.*;

public class ConditionalExprNode extends ExprNode {
    public ExprNode condition, thenExpr, elseExpr;

    public ConditionalExprNode(Position position, ExprNode condition, ExprNode thenExpr, ExprNode elseExpr) {
        super(position);
        this.condition = condition;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    public boolean isLeftValue() {
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
