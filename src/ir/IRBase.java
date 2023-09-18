package ir;

import utils.*;

public abstract class IRBase implements BuiltinElements {
    public IRType irType;

    IRBase(IRType irType) {
        this.irType = irType;
    }

    public abstract String toString();

    public abstract String toStringWithType();
}
