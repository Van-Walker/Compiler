package ir;

public class JumpInstruction extends IRInstruction {
    public IRBlock toBlock;

    public JumpInstruction(IRBlock block, IRBlock toBlock) {
        super(block);
        this.toBlock = toBlock;
    }

    @Override
    public String toString() {
        return "br label %" + toBlock.name;
    }
}
