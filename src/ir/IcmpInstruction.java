package ir;

public class IcmpInstruction extends IRInstruction {
    public IRType irType;
    public IRRegister cmp;
    public String op;
    public IRBase lhs, rhs;

    public IcmpInstruction(IRBlock block, IRType irType, IRRegister cmp, IRBase lhs, IRBase rhs, String op) {
        super(block);
        this.irType = irType;
        this.cmp = cmp;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return cmp + " = icmp " + op + " " + irType + " " + lhs + ", " + rhs;
    }
}
