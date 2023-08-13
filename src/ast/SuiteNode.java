package ast;

import utils.*;
import java.util.ArrayList;

public class SuiteNode extends StmtNode {
    public ArrayList <StmtNode> stmts = new ArrayList<StmtNode>();

    public SuiteNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
