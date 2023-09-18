package ir;

public class StringConst extends IRBase {
    public String val;
    public int id;
    public static int stringCnt = 0;

    public StringConst(String val) {
        super(new IRPointerType(new ArrayType(irBoolType, val.length() + 1)));
        this.val = val;
        this.id = stringCnt++;
    }

    public String print() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < val.length(); ++i) {
            char c = val.charAt(i);
            switch (c) {
                case '\n' -> ret.append("\\0A");
                case '\"' -> ret.append("\\22");
                case '\\' -> ret.append("\\\\");
                default -> ret.append(c);
            }
        }
        return ret + "\\00";
    }

    @Override
    public String toString() {
        return "@str." + String.valueOf(id);
    }

    @Override
    public String toStringWithType() {
        return "[" + String.valueOf(val.length() + 1) + " x i8]* " + toString();
    }

}
