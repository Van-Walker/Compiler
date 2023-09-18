package ir;

public class BinaryInstruction extends IRInstruction {
    public IRType irType;
    public IRRegister reg;
    public String op;
    public IRBase lhs, rhs;

    public BinaryInstruction(IRBlock block, IRType type, IRRegister reg, IRBase lhs, IRBase rhs, String op) {
        super(block);
        this.irType = type;
        this.reg = reg;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return reg + " = " + op + " " + lhs.toStringWithType() + ", " + rhs;
    }
}
