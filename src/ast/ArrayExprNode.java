package ast;

import utils.*;

public class ArrayExprNode extends ExprNode {
    public ExprNode array, index;

    public ArrayExprNode(Position position, ExprNode array, ExprNode index) {
        super(position);
        this.array = array;
        this.index = index;
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
