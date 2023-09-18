package ir;

public class AllocaInstruction extends IRInstruction {
    public IRType type;
    public IRRegister reg;

    public AllocaInstruction(IRBlock block, IRType type, IRRegister reg) {
        super(block);
        this.type = type;
        this.reg = reg;
    }

    @Override
    public String toString() {
        return reg + " = alloc " + type;
    }
}
