package ast;

import ir.IRBlock;
import utils.*;

public abstract class LoopStmtNode extends StmtNode {
    public IRBlock condBlock, loopBlock, nextBlock;

    public LoopStmtNode(Position position) {
        super(position);
    }
}
