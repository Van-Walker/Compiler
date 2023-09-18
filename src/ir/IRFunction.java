package ir;

import java.util.ArrayList;
import java.util.LinkedList;

public class IRFunction {
    public String name;
    public IRType retType;
    public IRRegister retAddr;
    public IRBlock exit;
    public ArrayList<IRRegister> params = new ArrayList<>();
    public LinkedList<IRBlock> blocks = new LinkedList<>();
    public ArrayList<AllocaInstruction> allocaInstructions = new ArrayList<>();

    public IRFunction(String name, IRType retType) {
        this.name = name;
        this.retType = retType;
    }

    public IRBlock appendBlock(IRBlock block) {
        blocks.add(block);
        return block;
    }

    public void finish() {
        IRBlock entry = blocks.getFirst();
        for (AllocaInstruction instruction : allocaInstructions) {
            entry.instructionList.addFirst(instruction);
        }
        blocks.add(exit);
    }

    public String toString() {
        StringBuilder s = new StringBuilder("define " + retType.toString() + " @" + name + "(");
        IRRegister.registerCnt = 0;
        for (int i = 0; i < params.size(); ++i) {
            s.append(params.get(i).toStringWithType());
            if (i != params.size() - 1) s.append(", ");
        }
        s.append(") {\n");
        for (IRBlock block : blocks) {
            s.append(block.toString());
        }
        return s + "}\n";
    }
}
