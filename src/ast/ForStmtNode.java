package ast;

import ir.IRBlock;
import utils.*;
import java.util.ArrayList;

public class ForStmtNode extends LoopStmtNode {
    public VarDefNode varDefNode;
    public ExprNode init, condition, step;
    public ArrayList <StmtNode> stmts = new ArrayList<>();
    public IRBlock condBlock, stepBlock, loopBlock, nextBlock;

    public ForStmtNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
