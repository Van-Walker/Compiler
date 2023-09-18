package ir;

public class ArrayType extends IRType {
    public IRType irType;
    public int cnt;

    public ArrayType(IRType irType, int cnt) {
        super("[" + String.valueOf(cnt) + " x " + irType.name + "]", irType.size * cnt);
        this.irType = irType;
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "[" + String.valueOf(cnt) + " x " + irType.toString() + "]";
    }

    @Override
    public IRBase defaultValue() {
        return nullConst;
    }
}
