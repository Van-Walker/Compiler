package ir;

import java.util.ArrayList;
import java.util.HashMap;

public class StructType extends IRType {
    public ArrayList<IRType> memberType = new ArrayList<>();
    public HashMap<String, Integer> memberMap = new HashMap<>();
    public boolean hasBuild = false;

    public StructType(String name) {
        super(name);
    }

    public void addMember(String name, IRType irType) {
        memberType.add(irType);
        memberMap.put(name, memberType.size() - 1);
    }

    public IRType getMemberType(String name) {
        if (!findMember(name)) return null;
        return memberType.get(memberMap.get(name));
    }

    public boolean findMember(String name) {
        return memberMap.containsKey(name);
    }

    @Override
    public IRBase defaultValue() {
        return nullConst;
    }

    @Override
    public String toString() {
        return "%struct." + name;
    }

}
