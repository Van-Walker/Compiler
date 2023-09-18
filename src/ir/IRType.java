package ir;

import utils.*;

public abstract class IRType implements BuiltinElements {
    public String name;
    public int size;

    public IRType(String name) {
        this.name = name;
    }

    public IRType(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public abstract String toString();

    public abstract IRBase defaultValue();
}
