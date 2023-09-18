package ast;

import utils.*;
import ir.*;

public abstract class ExprNode extends Node {
    public String str;
    public Type type;
    public FuncDefNode funcDef = null;
    public IRBase value = null;
    public IRRegister store = null;

    public ExprNode(Position position) {
        super(position);
    }

    public abstract boolean isLeftValue();
}
