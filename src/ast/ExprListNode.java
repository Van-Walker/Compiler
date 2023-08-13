package ast;

import utils.*;
import java.util.ArrayList;

public class ExprListNode extends Node {
    public ArrayList <ExprNode> exprs = new ArrayList<ExprNode>();

    public ExprListNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
