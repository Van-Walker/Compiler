package ir;

import utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class IRProgram implements BuiltinElements {
    // todo
    public IRFunction initFunc = new IRFunction("__program_var_init", irVoidType), mainFunc;
    public IRBlock initBlock = new IRBlock(initFunc, "entry_");
    public LinkedList <IRFunction> functions = new LinkedList<>();
    public ArrayList <GlobalVariable> globalVariables = new ArrayList<>();
    public ArrayList <StructType> classes = new ArrayList<>();
    public HashMap <String, StringConst> stringConst = new HashMap<>();

    public IRProgram() {
        initFunc.appendBlock(initBlock);
        initFunc.exit = new IRBlock(initFunc, "return_");
        initFunc.exit.terminal = new RetInstruction(initFunc.exit, voidConst);
        initBlock.terminal = new JumpInstruction(initBlock, initFunc.exit);
    }

    public StringConst addString(String s) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\') {
                ++i;
                switch (s.charAt(i)) {
                    case 'n' -> ret.append('\n');
                    case '\"' -> ret.append('\"');
                    default -> ret.append('\\');
                }
            } else ret.append(c);
        }
        if (!stringConst.containsKey(ret.toString())) stringConst.put(ret.toString(), new StringConst(ret.toString()));
        return stringConst.get(ret.toString());
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (StructType structType : classes) {
            ret.append(structType).append(" = type {");
            for (int i = 0; i < structType.memberType.size(); ++i) {
                ret.append(structType.memberType.get(i));
                if (i != structType.memberType.size() - 1) ret.append(", ");
            }
            ret.append("}\n");
        }
        for (StringConst str : stringConst.values())
            ret.append("@str.").append(str.id).append(" = private unnamed_addr constant [").append(String.valueOf(str.val.length() + 1)).append(" x i8] c\"").append(str.print()).append("\"\n");
        for (GlobalVariable globalVariable : globalVariables)
            ret.append(globalVariable).append(" = dso_local global ").append(((IRPointerType) globalVariable.irType).toType()).append(" ").append(globalVariable.init).append("\n");
        ret.append("\ndeclare dso_local ptr @malloc(i32)\n");
        ret.append("declare dso_local i32 @strlen(ptr)\n");
        ret.append("declare dso_local void @print(ptr)\n");
        ret.append("declare dso_local void @println(ptr)\n");
        ret.append("declare dso_local void @printInt(i32)\n");
        ret.append("declare dso_local void @printlnInt(i32)\n");
        ret.append("declare dso_local ptr @getString()\n");
        ret.append("declare dso_local i32 @getInt()\n");
        ret.append("declare dso_local ptr @toString(i32)\n");
        ret.append("declare ptr @__string_subString(ptr, i32, i32)\n");
        ret.append("declare i32 @__string_parseInt(ptr)\n");
        ret.append("declare i32 @__string_ord(ptr, i32)\n");
        ret.append("declare ptr @__string_add(ptr, ptr)\n");
        ret.append("declare i1 @__string_equal(ptr, ptr)\n");
        ret.append("declare i1 @__string_notEqual(ptr, ptr)\n");
        ret.append("declare i1 @__string_less(ptr, ptr)\n");
        ret.append("declare i1 @__string_lessOrEqual(ptr, ptr)\n");
        ret.append("declare i1 @__string_greater(ptr, ptr)\n");
        ret.append("declare i1 @__string_greaterOrEqual(ptr, ptr)\n\n");
        ret.append("declare i32 @__array_size(ptr)\n");
        ret.append("declare ptr @__newPtrArray(i32, i32)\n");
        for (IRFunction irFunction : functions) ret.append(irFunction).append("\n");
        return ret.toString();
    }
}
