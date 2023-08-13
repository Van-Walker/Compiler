package ast;

import utils.*;
import java.util.ArrayList;

public class ProgramNode extends Node {
    public ArrayList <Node> defList = new ArrayList<Node>();

    public ProgramNode(Position position) {
        super(position);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
