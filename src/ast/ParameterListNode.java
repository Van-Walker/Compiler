package ast;

import utils.*;
import java.util.ArrayList;

public class ParameterListNode extends Node {
    public ArrayList <VarDefUnitNode> units = new ArrayList<>();

    public ParameterListNode(Position position) {
        super(position);
    }

    public ParameterListNode(Position position, Type type, int cnt) {
        super(position);
        for (int i = 0; i < cnt; ++i) {
            units.add(new VarDefUnitNode(position, new TypeNode(position, type.typeName, type.dim), "p" + i));
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
