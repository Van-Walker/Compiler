package ast;

import utils.*;

public class VarExprNode extends AtomExprNode {
    public VarExprNode(Position position, String str) {
        super(position, str);
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
