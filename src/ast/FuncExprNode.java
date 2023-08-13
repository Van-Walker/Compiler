package ast;

import utils.*;

public class FuncExprNode extends ExprNode{
    public ExprNode funcName;
    public ExprListNode args;

    public FuncExprNode(Position position, ExprNode funcName) {
        super(position);
        this.funcName = funcName;
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

