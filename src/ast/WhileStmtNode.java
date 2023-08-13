package ast;

import utils.*;
import java.util.ArrayList;

public class WhileStmtNode extends StmtNode {
    public ExprNode condition;
    public ArrayList <StmtNode> stmts = new ArrayList<>();

    public WhileStmtNode(Position position, ExprNode condition) {
        super(position);
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
