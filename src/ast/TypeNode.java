package ast;

import utils.*;
import ir.*;

public class TypeNode extends Node {
    public Type type;
    public IRType irType;

    public TypeNode(Position position) {
        super(position);
    }

    public TypeNode(Position position, String name) {
        super(position);
        this.type = new Type(name);
    }

    public TypeNode(Position position, String name, int dim) {
        super(position);
        this.type = new Type(name, dim);
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
