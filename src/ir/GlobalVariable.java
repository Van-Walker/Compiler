package ir;

public class GlobalVariable extends IRRegister {
    public IRBase init;

    // todo
    public GlobalVariable(String name, IRType irType) {
        super(name, new IRPointerType(irType));
        --registerCnt;
    }

    @Override
    public String toString() {
        return "@" + name;
    }

    @Override
    public String toStringWithType() {
        return irType + " " + toString();
    }
}
