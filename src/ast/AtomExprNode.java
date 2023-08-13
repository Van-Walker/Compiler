package ast;

import utils.*;

public class AtomExprNode extends ExprNode {
    public AtomExprNode(Position position, String str) {
        super(position);
        this.str = str;
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
