package ast;

import utils.*;
import java.util.ArrayList;

public class VarDefNode extends StmtNode {
    public ArrayList <VarDefUnitNode> units = new ArrayList<VarDefUnitNode>();

    public VarDefNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
