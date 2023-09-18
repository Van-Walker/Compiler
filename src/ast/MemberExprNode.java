package ast;

import ir.IRRegister;
import utils.*;

public class MemberExprNode extends ExprNode implements BuiltinElements {
    public ExprNode obj;
    public String member;
    public IRRegister objAddress;

    public MemberExprNode(Position position, ExprNode obj, String member) {
        super(position);
        this.obj = obj;
        this.member = member;
    }

    @Override
    public boolean isLeftValue() {
        return true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
