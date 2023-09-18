package ast;

import ir.IRBlock;
import utils.*;
import java.util.ArrayList;

public class WhileStmtNode extends LoopStmtNode {
    public ExprNode condition;
    public ArrayList <StmtNode> stmts = new ArrayList<>();
    public IRBlock condBlock, loopBlock, nextBlock;

    public WhileStmtNode(Position position, ExprNode condition) {
        super(position);
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
