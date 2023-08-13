package ast;

import utils.*;
import java.util.ArrayList;

public class NewExprNode extends ExprNode {
    public String typeName;
    public int dim = 0;
    public ArrayList <ExprNode> sizeList = new ArrayList<ExprNode>();

    public NewExprNode(Position position, String typeName) {
        super(position);
        this.typeName = typeName;
    }

    @Override
    public boolean isLeftValue() {
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
