package ir;

public class StoreInstruction extends IRInstruction {
    public IRBase val;
    public IRRegister address;
    public int paramIndex = -1;

    public StoreInstruction(IRBlock block, IRBase val, IRRegister address) {
        super(block);
        this.val = val;
        this.address = address;
    }

    public StoreInstruction(IRBlock block, IRBase val, IRRegister address, int index) {
        super(block);
        this.val = val;
        this.address = address;
        this.paramIndex = index;
    }

    @Override
    public String toString() {
        return "store " + val.toStringWithType() + ", " + address.toStringWithType();
    }

}
