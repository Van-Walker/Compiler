package ir;

import java.util.LinkedList;

public class IRBlock {
    public String name;
    public static int blockCnt = 0;
    public IRFunction parentFunc = null;
    public LinkedList<IRInstruction> instructionList = new LinkedList<>();
    public IRInstruction terminal = null;
    public boolean isFinished = false;

    public IRBlock(IRFunction irFunction, String name) {
        this.name = name + String.valueOf(blockCnt++);
        this.parentFunc = irFunction;
    }

    public IRBlock(IRFunction function, String name, IRBlock toBlock) {
        this.parentFunc = function;
        this.name = name + String.valueOf(blockCnt++);
        this.terminal = new JumpInstruction(this, toBlock);
    }

    public void add(IRInstruction instruction) {
        if (isFinished) return;
        if (instruction instanceof AllocaInstruction) {
            parentFunc.allocaInstructions.add((AllocaInstruction) instruction);
        } else if (instruction instanceof BrInstruction || instruction instanceof JumpInstruction) {
            terminal = instruction;
        } else {
            instructionList.add(instruction);
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder(name + ":\n");
        for (IRInstruction instruction : instructionList)
            ret.append(" ").append(instruction).append("\n");
        if (terminal != null)
            ret.append(" ").append(terminal).append("\n");
        return ret.toString();
    }
}
