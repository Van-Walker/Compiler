package ast;

import utils.*;

public abstract class ExprNode extends Node {
    public String str;
    public Type type;
    public FuncDefNode funcDef = null;

    public ExprNode(Position position) {
        super(position);
    }

    public abstract boolean isLeftValue();
}
