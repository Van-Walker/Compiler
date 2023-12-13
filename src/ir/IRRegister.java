package ir;

public class IRRegister extends IRBase {
    public String name;
    public int id = -1;
    public static int registerCnt = 0;

    public IRRegister(String name, IRType irType) {
        super(irType);
        this.name = name;
        this.id = registerCnt++;
    }

    @Override
    public String toString() {
        if (id == -1 && (name == null || !name.equals("retval"))) id = registerCnt++;
        return "%" + (name != null && name.equals("retval") ? name : "." + String.valueOf(id));
    }

    @Override
    public String toStringWithType() {
        return irType + " " + toString();
    }

}
