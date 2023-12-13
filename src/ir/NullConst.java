package ir;

public class NullConst extends IRConst {
    public NullConst() {
        super(irNullType);
    }

    public NullConst(IRType irType) {
        super(irType);
    }

    @Override
    public String toString() {
        return "null";
    }

    // todo
    @Override
    public String toStringWithType() {
        if (irType == irNullType) return "null";
        return irType + " null";
    }
}
