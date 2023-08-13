package ast;

import utils.*;
import java.util.ArrayList;

public class ForStmtNode extends StmtNode {
    public VarDefNode varDefNode;
    public ExprNode init, condition, step;
    public ArrayList <StmtNode> stmts = new ArrayList<>();

    public ForStmtNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
