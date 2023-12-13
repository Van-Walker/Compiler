package ir;

public class AllocaInstruction extends IRInstruction {
    public IRType type;
    public IRRegister reg;
    public int paraIndex = -1;

    public AllocaInstruction(IRBlock block, IRType type, IRRegister reg) {
        super(block);
        this.type = type;
        this.reg = reg;
    }

    public AllocaInstruction(IRBlock block, IRType type, IRRegister reg, int index) {
        super(block);
        this.type = type;
        this.reg = reg;
        this.paraIndex = index;
    }

    @Override
    public String toString() {
        return reg + " = alloca " + type;
    }
}
