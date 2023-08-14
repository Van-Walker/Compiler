package semantic;

import ast.*;
import utils.*;
import grammar.*;
import java.util.HashMap;

public class SemanticCheck implements Visitor, BuiltinElements {
    private GlobalScope globalScope;
    private Scope currentScope;

    public SemanticCheck(GlobalScope globalScope) {
        this.globalScope = globalScope;
        currentScope = globalScope;
    }

    public void visit(ProgramNode node) {
        FuncDefNode mainFunc = globalScope.getFuncDef("main");
        if (mainFunc == null || !mainFunc.returnType.type.equals(IntType) || mainFunc.parameterListNode != null)
            throw new MyError(node.position, "main function error");
        for (var def : node.defList) {
            def.accept(this);
        }
    }

    public void visit(VarDefUnitNode node) {
        node.typeNode.accept(this);
        if (node.init != null) node.init.accept(this);
        if (currentScope.isRepeatedName(node.varName)) throw new MyError(node.position, "redefinition of variable " + node.varName);
        currentScope.addVar(node.varName, node.typeNode.type);
    }

    public void visit(VarDefNode node) {
        node.units.forEach(unit -> unit.accept(this));
    }

    public void visit(ParameterListNode node) {
        node.units.forEach(param -> param.accept(this));
    }

    public void visit(TypeNode node) {
        switch (node.type.typeName) {
            case "int", "bool", "string", "void", "null", "this" -> {}
            default -> {
                if (globalScope.getClassDef(node.type.typeName) == null)
                    throw new MyError(node.position, "undefined type " + node.type.typeName);
            }
        }
    }

    public void visit(FuncDefNode node) {
        node.returnType.accept(this);
        currentScope = new Scope(currentScope, node.returnType.type);
        if (node.parameterListNode != null) node.parameterListNode.accept(this);
        node.stmts.forEach(stmt -> stmt.accept(this));
        if (!VoidType.equals(node.returnType.type) && !node.funcName.equals("main") && !currentScope.returned)
            throw new MyError(node.position, "Function " + node.funcName + " without return value");
        currentScope = currentScope.parentScope;
    }

    // todo: add var and func to currentScope or SemanticCheck
    public void visit(ClassDefNode node) {
        currentScope = new Scope(currentScope, node);
        node.varDefList.forEach(varDef -> varDef.accept(this));
        // todo
        if (node.classBuild != null) {
            if (node.name.equals(node.classBuild.name)) {
                node.classBuild.accept(this);
            } else {
                throw new MyError(node.classBuild.position, "Class name mismatch");
            }
        }
        node.funcDefList.forEach(funcDef -> funcDef.accept(this));
        currentScope = currentScope.parentScope;
    }

    // todo
    public void visit(ClassBuildNode node) {
        currentScope = new Scope(currentScope, VoidType);
        node.suite.accept(this);
        currentScope = currentScope.parentScope;
    }

    public void visit(SuiteNode node) {
        currentScope = new Scope(currentScope);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }

    public void visit(IfStmtNode node) {
        node.condition.accept(this);
        if (!BoolType.equals(node.condition.type)) throw new MyError(node.position, "invalid condition expression");
        currentScope = new Scope(currentScope);
        node.thenStmts.forEach(stmtNode -> stmtNode.accept(this));
        currentScope = currentScope.parentScope;
        if (node.elseStmts != null) {
            currentScope = new Scope(currentScope);
            node.elseStmts.forEach(stmtNode -> stmtNode.accept(this));
            currentScope = currentScope.parentScope;
        }
    }

    public void visit(ForStmtNode node) {
        currentScope = new Scope(currentScope, true);
        if (node.varDefNode != null) node.varDefNode.accept(this);
        if (node.init != null) node.init.accept(this);
        if (node.condition != null) {
            node.condition.accept(this);
            if (!BoolType.equals(node.condition.type)) throw new MyError(node.position, "Invalid condition expression");
        }
        if (node.step != null) node.step.accept(this);
        node.stmts.forEach(stmtNode -> stmtNode.accept(this));
        currentScope = currentScope.parentScope;
    }

    public void visit(WhileStmtNode node) {
        node.condition.accept(this);
        if (!BoolType.equals(node.condition.type)) throw new MyError(node.position, "invalid condition expression");
        currentScope = new Scope(currentScope, true);
        node.stmts.forEach(stmtNode -> stmtNode.accept(this));
        currentScope = currentScope.parentScope;
    }

    // todo: inLoop initialize
    public void visit(ContinueStmtNode node) {
        if (!currentScope.inLoop) throw new MyError(node.position, "CONTINUE not in a loop");
    }

    public void visit(BreakStmtNode node) {
        if (!currentScope.inLoop) throw new MyError(node.position, "BREAk not in a loop");
    }

    // todo
    public void visit(ReturnStmtNode node) {
        for (var nowScope = currentScope; nowScope != null; nowScope = nowScope.parentScope) {
            if (nowScope.returnType == null) continue;
            if (node.exprNode == null) {
                if (!VoidType.equals(nowScope.returnType)) throw new MyError(node.position, "Return type mismatch");
            } else {
                node.exprNode.accept(this);
                // todo: null or void
                if (!nowScope.returnType.equals(node.exprNode.type) && (!nowScope.returnType.isReferenceType() || !NullType.equals(node.exprNode.type)))
                    throw new MyError(node.position, "Return type mismatch");
            }
            nowScope.returned = true;
            return;
        }
        throw new MyError(node.position, "RETURN not in a function");
    }

    public void visit(ExprStmtNode node) {
        if (node.exprNode != null) node.exprNode.accept(this);
    }

    public void visit(ExprListNode node) {
        node.exprs.forEach(exprNode -> exprNode.accept(this));
    }

    // todo
    public void visit(NewExprNode node) {
        for (var size : node.sizeList) {
            size.accept(this);
            if (size.type == null || !size.type.equals(IntType))
                throw new MyError(node.position, "invalid expression");
        }
        new TypeNode(node.position, node.typeName).accept(this);
        node.type = new Type(node.typeName, node.dim);
    }

    public void visit(AtomExprNode node) {
        if (node.str.equals("null")) {
            node.type = NullType;
        } else if (node.str.equals("true") || node.str.equals("false")) {
            node.type = BoolType;
        } else if (node.str.equals("this")) {
            if (currentScope.inWhichClass == null) throw new MyError(node.position, "THIS not in a class");
            node.type = new Type(currentScope.inWhichClass.name);
        } else if (node.str.matches("\".*\"")) {
            node.type = StringType;
        } else node.type = IntType;
    }

    // todo: VarExpr?
    public void visit(VarExprNode node) {
        node.type = currentScope.getVarType(node.str);
        if (currentScope.inWhichClass != null && currentScope.inWhichClass.getFuncDef(node.str) != null) {
            node.funcDef = currentScope.inWhichClass.getFuncDef(node.str);
        } else {
            node.funcDef = globalScope.getFuncDef(node.str);
        }
    }

    public void visit(PreSelfExprNode node) {
        node.expr.accept(this);
        if (node.expr.type == null) throw new MyError(node.position, "Invalid expression");
        if (!node.expr.isLeftValue() || !node.expr.type.equals(IntType)) throw new MyError(node.position, "Int left value expected");
        node.type = new Type(IntType);
    }

    public void visit(UnaryExprNode node) {
        node.expr.accept(this);
        if (node.expr.type == null) throw new MyError(node.position, "invalid unary expression");
        if (node.op.equals("!")) {
            if (!BoolType.equals(node.expr.type)) throw new MyError(node.position, "Unary expression: not bool type");
            node.type = new Type(BoolType);
        } else if (node.op.equals("++") || node.op.equals("--")) {
            if (!node.expr.isLeftValue() || !node.expr.type.equals(IntType)) throw new MyError(node.position, "Unary expression: int left value needed");
            node.type = new Type(IntType);
        } else {
            if (!IntType.equals(node.expr.type)) throw new MyError(node.position, "Unary expression: not int type");
            node.type = new Type(IntType);
        }
    }

    // todo: order
    public void visit(AssignExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (node.lhs.type == null || node.rhs.type == null) throw new MyError(node.position, "Invalid expression");
        if (VoidType.equals(node.lhs.type) || VoidType.equals(node.rhs.type)) throw new MyError(node.position, "Invalid expression");
        if (!node.lhs.type.equals(node.rhs.type) && (!node.lhs.type.isReferenceType() || !NullType.equals(node.rhs.type))) throw new MyError(node.position, "Assign expression: type mismatch");
        node.type = new Type(node.lhs.type);
        if (!node.lhs.isLeftValue()) throw new MyError(node.position, "Assign expression: left value for lhs expected");
    }

    // todo: two kind of Null
    public void visit(BinaryExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (node.lhs.type == null || node.rhs.type == null) {
//            if (node.lhs.type != null) System.out.println("lhs" + node.lhs.type.name() + node.lhs.str);
//            if (node.rhs.type != null) System.out.println("rhs" + node.rhs.type.name());
            throw new MyError(node.position, "Invalid expression");
        }
        if (NullType.equals(node.lhs.type) || NullType.equals(node.rhs.type)) {
            if ((node.op.equals("==") || node.op.equals("!=")) && (node.lhs.type.isReferenceType() || node.rhs.type.isReferenceType())) {
                node.type = BoolType;
                return;
            }
        }
        if (VoidType.equals(node.lhs.type) || VoidType.equals(node.rhs.type)) throw new MyError(node.position, "Invalid expression");
        if (!node.lhs.type.equals(node.rhs.type)) throw new MyError(node.position, "Type mismatch");
        switch (node.op) {
            case "+", "<", ">", "<=", ">=" -> {
                if (!IntType.equals(node.lhs.type) && !StringType.equals(node.lhs.type)) throw new MyError(node.position, "Type mismatch with op");
                node.type = node.op.equals("+") ? new Type(node.lhs.type) : BoolType;
            }
            case "-", "*", "/", "%", ">>", "<<", "&", "|", "^" -> {
                if (!IntType.equals(node.lhs.type)) throw new MyError(node.position, "Type mismatch with op");
                node.type = IntType;
            }
            case "&&", "||" -> {
                if (!node.lhs.type.equals(BoolType))  throw new MyError(node.position, "Type mismatch with op");
                node.type = BoolType;
            }
            default -> node.type = BoolType; // "==" or "!="
        }
    }

    // todo
    public void visit(ConditionalExprNode node) {
        node.condition.accept(this);
        node.thenExpr.accept(this);
        node.elseExpr.accept(this);
        if (!node.condition.type.equals(BoolType)) throw new MyError(node.position, "Condition is not bool type ");
        if (node.thenExpr.type == null || node.elseExpr.type == null) throw new MyError(node.position, "Invalid expression");
        if (!node.thenExpr.type.equals(node.elseExpr.type) && !(node.thenExpr.type.equals(NullType) && node.elseExpr.type.isReferenceType()) && !(node.thenExpr.type.isReferenceType() && node.elseExpr.type.equals(NullType)))
            throw new MyError(node.position, "ConditionalExpr: thenExpr mismatch with elseExpr");
        node.type = !NullType.equals(node.thenExpr.type) ? new Type(node.thenExpr.type) : new Type(node.elseExpr.type);
    }

    public void visit(ArrayExprNode node) {
        node.array.accept(this);
        node.index.accept(this);
        if (node.array.type == null || node.index.type == null || !node.index.type.equals(IntType)) throw new MyError(node.position, "Invalid expression");
        node.type = new Type(node.array.type);
        --node.type.dim;
        if (node.type.dim < 0) throw new MyError(node.position, "Array: negative dimension");
    }

    // todo
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        if (node.obj.type == null) throw new MyError(node.position, "Invalid expression");
        // todo
        if (!node.obj.type.isReferenceType() && !ThisType.equals(node.obj.type) && !StringType.equals(node.obj.type)) throw new MyError(node.position, "Type mismatch");
        var classDef = ThisType.equals(node.obj.type) ? currentScope.inWhichClass : globalScope.getClassDef(node.obj.type.typeName);
        // todo
        if (node.obj.type.dim > 0) {
            if (classDef == null) throw new MyError(node.position, "Type mismatch");
            if (node.member.equals("size")) node.funcDef = ArraySizeFunc;
        } else {
            if (classDef == null) throw new MyError(node.position, "Class " + node.obj.type.typeName + " is undefined");
            node.type = classDef.getVarType(node.member);
            node.funcDef = classDef.getFuncDef(node.member);
        }
    }

    public void visit(FuncExprNode node) {
        node.funcName.accept(this);
        if (node.funcName.funcDef == null) throw new MyError(node.position, "Function " + node.funcName.str + " is undefined");
        var funcDef = node.funcName.funcDef;
        if (node.args != null) {
            node.args.accept(this);
            if (funcDef.parameterListNode == null || funcDef.parameterListNode.units.size() != node.args.exprs.size()) throw new MyError(node.position, "Parameters mismatch with args");
            for (int i = 0; i < funcDef.parameterListNode.units.size(); ++i) {
                var param = funcDef.parameterListNode.units.get(i);
                var arg = node.args.exprs.get(i);
                if (!param.typeNode.type.equals(arg.type) && (!param.typeNode.type.isReferenceType() || !NullType.equals(arg.type))) throw new MyError(node.position, "Parameters mismatch with args");
            }
        } else {
            if (funcDef.parameterListNode != null) throw new MyError(node.position, "Parameters mismatch with args");
        }
        node.type = new Type(funcDef.returnType.type);
    }
}
