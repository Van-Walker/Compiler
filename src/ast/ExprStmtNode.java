package ast;

import utils.Position;

public class ExprStmtNode extends StmtNode {
    public ExprNode exprNode;

    public ExprStmtNode(Position position, ExprNode exprNode) {
        super(position);
        this.exprNode = exprNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
