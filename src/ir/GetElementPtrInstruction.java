package ir;

import java.util.ArrayList;
import java.util.Collections;

public class GetElementPtrInstruction extends IRInstruction {
    public IRRegister reg;
    public IRType toType;
    public IRBase ptr;
    public ArrayList<IRBase> indexList = new ArrayList<>();

    public GetElementPtrInstruction(IRBlock block, IRBase ptr, IRRegister reg, IRBase... indexList) {
        super(block);
        this.ptr = ptr;
        this.reg = reg;
        this.toType = ((IRPointerType) ptr.irType).toType();
        Collections.addAll(this.indexList, indexList);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(reg + " = getelementptr " + toType + ", " + ptr.toStringWithType());
        for (var index : indexList) {
            s.append(", ").append(index.toStringWithType());
        }
        return s.toString();
    }
}
