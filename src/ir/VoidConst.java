package ir;

public class VoidConst extends IRBase {
    public VoidConst() {
        super(irVoidType);
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public String toStringWithType() {
        return "void";
    }
}
