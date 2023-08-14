package semantic;

import ast.*;
import utils.*;

public class SymbolCollect implements Visitor {
    private GlobalScope globalScope;

    public SymbolCollect(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    public void visit(ProgramNode node) {
        node.defList.forEach(def -> def.accept(this));
    }

    public void visit(FuncDefNode node) {
        if (globalScope.getFuncDef(node.funcName) != null) throw new MyError(node.position, "Function " + node.funcName + " is repeatedly defined");
        if (globalScope.getClassDef(node.funcName) != null) throw new MyError(node.position, "Function " + node.funcName + " is previously defined as a class");
        //System.out.println("FuncDef success: " + node.funcName);
        globalScope.addFunc(node.funcName, node);
    }

    public void visit(ClassDefNode node) {
        if (globalScope.getFuncDef(node.name) != null) throw new MyError(node.position, "Class " + node.name + " is previously defined as a function");
        if (globalScope.getClassDef(node.name) != null) throw new MyError(node.position, "Class " + node.name + " is repeatedly defined");
        globalScope.addClass(node.name, node);
        for (var func : node.funcDefList) {
            if (node.funcMap.containsKey(func.funcName)) throw new MyError(node.position, "Function " + func.funcName + " is repeatedly defined");
            node.funcMap.put(func.funcName, func);
        }
        for (var variable : node.varDefList) {
            for (var unit : variable.units) {
                if (node.varMap.containsKey(unit.varName)) throw new MyError(unit.position, "Variable " + unit.varName + " is repeatedly defined");
                node.varMap.put(unit.varName, unit);
            }
        }
    }

    public void visit(VarDefUnitNode node) {}

    public void visit(VarDefNode node) {}

    public void visit(TypeNode node) {}

    public void visit(ParameterListNode node) {}

    public void visit(ClassBuildNode node) {}

    public void visit(StmtNode node) {}

    public void visit(SuiteNode node) {}

    public void visit(IfStmtNode node) {}

    public void visit(ForStmtNode node) {}

    public void visit(WhileStmtNode node) {}

    public void visit(ContinueStmtNode node) {}

    public void visit(BreakStmtNode node) {}

    public void visit(ReturnStmtNode node) {}

    public void visit(ExprStmtNode node) {}

    public void visit(ExprNode node) {}

    public void visit(AtomExprNode node) {}

    public void visit(UnaryExprNode node) {}

    public void visit(BinaryExprNode node) {}

    public void visit(ConditionalExprNode node) {}

    public void visit(NewExprNode node) {}

    public void visit(VarExprNode node) {}

    public void visit(ExprListNode node) {}

    public void visit(PreSelfExprNode node) {}

    public void visit(AssignExprNode node) {}

    public void visit(ArrayExprNode node) {}

    public void visit(FuncExprNode node) {}

    public void visit(MemberExprNode node) {}
}
