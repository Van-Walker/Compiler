package ast;

import utils.*;

public class ClassBuildNode extends Node {
    public String name;
    public SuiteNode suite;
    public FuncDefNode func;

    public ClassBuildNode(Position position, String name, SuiteNode suite) {
        super(position);
        this.name = name;
        this.suite = suite;
    }

    public FuncDefNode toFunc() {
        FuncDefNode funcDefNode = new FuncDefNode(position, name);
        funcDefNode.returnType = new TypeNode(position, "void");
        funcDefNode.stmts = suite.stmts;
        return func = funcDefNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
