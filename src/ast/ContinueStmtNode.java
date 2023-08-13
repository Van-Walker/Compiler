package ast;

import utils.*;

public class ContinueStmtNode extends StmtNode {
    public ContinueStmtNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
