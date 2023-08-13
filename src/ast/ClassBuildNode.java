package ast;

import utils.*;

public class ClassBuildNode extends Node {
    public String name;
    public SuiteNode suite;

    public ClassBuildNode(Position position, String name, SuiteNode suite) {
        super(position);
        this.name = name;
        this.suite = suite;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
