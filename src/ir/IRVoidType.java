package ir;

public class IRVoidType extends IRType {
    public IRVoidType() {
        super("void", 0);
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public IRBase defaultValue() {
        return null;
    }
}
