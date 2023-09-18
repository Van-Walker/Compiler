package ir;

public class IRIntType extends IRType {
    public int length;

    public IRIntType(int length) {
        super("i" + String.valueOf(length));
        if (length == 1) this.size = 1;
        else this.size = length / 8;
        this.length = length;
    }

    @Override
    public String toString() {
        return "i" + String.valueOf(size);
    }

    @Override
    public IRBase defaultValue() {
        return intConstZero;
    }
}
