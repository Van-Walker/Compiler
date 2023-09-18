package ir;

public class BrInstruction extends IRInstruction {
    public IRBase condition;
    public IRBlock thenBlock, elseBlock;

    public BrInstruction(IRBlock block, IRBase condition, IRBlock thenBlock, IRBlock elseBlock) {
        super(block);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        return "br " + condition.toStringWithType() + ", label %" + thenBlock.name + ", label %" + elseBlock.name;
    }
}
