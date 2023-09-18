package ir;

public class BoolConst extends IRBase {
    public boolean val;

    public BoolConst(boolean val) {
        super(irBoolType);
        this.val = val;
    }

    @Override
    public String toString() {
        return val ? "1" : "0";
    }

    @Override
    public String toStringWithType() {
        return "i1 " + toString();
    }
}
