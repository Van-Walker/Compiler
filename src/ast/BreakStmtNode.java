package ast;

import utils.*;

public class BreakStmtNode extends StmtNode {
    public BreakStmtNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
