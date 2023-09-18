package ir;

public class IRPointerType extends IRType {
    public IRType irType;
    public int dim;

    public IRPointerType(IRType irType) {
        super(irType.name, 4);
        this.irType = irType;
        this.dim = 1;
    }

    public IRPointerType(IRType irType, int dim) {
        super(irType.name, 4);
        this.irType = irType;
        this.dim = dim;
    }

    public IRType toType() {
        if (dim == 1) return irType;
        return new IRPointerType(irType, dim - 1);
    }

    public boolean equals(Object object) {
        if (!(object instanceof IRPointerType)) return false;
        return irType.equals(((IRPointerType) object).irType) && dim == ((IRPointerType) object).dim;
    }

    @Override
    public IRBase defaultValue() {
        return nullConst;
    }

    @Override
    public String toString() {
        return "ptr";
    }
}
