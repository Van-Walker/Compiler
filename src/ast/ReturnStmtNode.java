package ast;

import utils.*;

public class ReturnStmtNode extends StmtNode {
    public ExprNode exprNode;

    public ReturnStmtNode(Position position, ExprNode exprNode) {
        super(position);
        this.exprNode = exprNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
