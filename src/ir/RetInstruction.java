package ir;

public class RetInstruction extends IRInstruction {
    public IRBase val;

    public RetInstruction(IRBlock block, IRBase val) {
        super(block);
        this.val = val;
    }

    @Override
    public String toString() {
        return "ret " + val.toStringWithType();
    }
}
