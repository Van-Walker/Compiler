package ir;

public abstract class IRInstruction {
    public IRBlock parentBlock = null;

    public IRInstruction(IRBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public abstract String toString();
}
