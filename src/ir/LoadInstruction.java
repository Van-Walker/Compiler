package ir;

public class LoadInstruction extends IRInstruction {
    public IRRegister reg;
    public IRType irType;
    public IRBase address;

    public LoadInstruction(IRBlock block, IRRegister reg, IRBase address) {
        super(block);
        this.reg = reg;
        this.irType = reg.irType;
        this.address = address;
    }

    @Override
    public String toString() {
        return reg + " = load " + irType + ", " + address.toStringWithType();
    }

}
