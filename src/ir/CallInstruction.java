package ir;

import java.util.ArrayList;
import java.util.Collections;

public class CallInstruction extends IRInstruction {
    public IRType ret;
    public String funcName;
    public IRRegister reg;
    public ArrayList<IRBase> args = new ArrayList<>();

    public CallInstruction(IRBlock block, IRType ret, String funcName) {
        super(block);
        this.ret = ret;
        this.funcName = funcName;
    }

    public CallInstruction(IRBlock block, IRRegister reg, IRType ret, String funcName, IRBase... args) {
        super(block);
        this.ret = ret;
        this.funcName = funcName;
        this.reg = reg;
        Collections.addAll(this.args, args);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder((reg != null ? reg + " = call " : "call ") + ret + " @" + funcName + "(");
        for (int i = 0; i < args.size(); ++i) {
            s.append(args.get(i).toStringWithType());
            if (i != args.size() - 1) s.append(", ");
        }
        return s + ")";
    }
}
