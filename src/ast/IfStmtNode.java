package ast;

import utils.*;
import java.util.ArrayList;

public class IfStmtNode extends StmtNode {
    public ExprNode condition;
    public ArrayList <StmtNode> thenStmts = new ArrayList<StmtNode>();
    public ArrayList <StmtNode> elseStmts = new ArrayList<StmtNode>();

    public IfStmtNode(Position position, ExprNode condition) {
        super(position);
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
