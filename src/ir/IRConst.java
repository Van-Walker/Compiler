package ir;

public abstract class IRConst extends IRBase {
    public IRConst(IRType irType) {
        super(irType);
    }

    @Override
    public abstract String toString();

    @Override
    public abstract String toStringWithType();

}
