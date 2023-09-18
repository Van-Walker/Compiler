package ir;

public class StoreInstruction extends IRInstruction {
    public IRBase val, address;

    public StoreInstruction(IRBlock block, IRBase val, IRBase address) {
        super(block);
        this.val = val;
        this.address = address;
    }

    @Override
    public String toString() {
        return "store " + val.toStringWithType() + ", " + address.toStringWithType();
    }

}
