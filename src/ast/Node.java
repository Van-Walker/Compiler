package ast;

import utils.*;

public abstract class Node {
    public Position position;

    public Node(Position position) {
        this.position = position;
    }

    public abstract void accept(Visitor visitor);
}