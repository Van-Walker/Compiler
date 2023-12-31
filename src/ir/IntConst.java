package ir;

public class IntConst extends IRConst {
    public int val;

    public IntConst(int val) {
        super(irIntType);
        this.val = val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public String toStringWithType() {
        return "i32 " + toString();
    }
}
