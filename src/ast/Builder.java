package ast;

import utils.*;
import grammar.*;
import grammar.MxParser.*;
import utils.MyError;

public class Builder extends MxParserBaseVisitor<Node> {
    @Override
    public Node visitProgram(ProgramContext ctx) {
        ProgramNode programNode = new ProgramNode(new Position(ctx));
        for (var def : ctx.children) {
            if (def instanceof ClassDefContext) {
                programNode.defList.add((ClassDefNode) visit(def));
            } else if (def instanceof VarDefContext) {
                programNode.defList.add((VarDefNode) visit(def));
            } else if (def instanceof FuncDefContext) {
                programNode.defList.add((FuncDefNode) visit(def));
            }
        }
        return programNode;
    }

    @Override
    public Node visitFuncDef(FuncDefContext ctx) {
        FuncDefNode funcDefNode = new FuncDefNode(new Position(ctx), ctx.Identifier().getText());
        funcDefNode.returnType = (TypeNode) visit(ctx.returnType());
        if (ctx.parameterList() != null) funcDefNode.parameterListNode = (ParameterListNode) visit(ctx.parameterList());
        funcDefNode.stmts = ((SuiteNode) visit(ctx.suite())).stmts;
        return funcDefNode;
    }

    @Override
    public Node visitClassDef(ClassDefContext ctx) {
        ClassDefNode classDefNode = new ClassDefNode(new Position(ctx), ctx.Identifier().getText());
        boolean hasConstructor = false;
        for (var def : ctx.children) {
            if (def instanceof FuncDefContext) {
                classDefNode.funcDefList.add((FuncDefNode) visit(def));
            } else if (def instanceof VarDefContext) {
                classDefNode.varDefList.add((VarDefNode) visit(def));
            } else if (def instanceof ClassBuildContext) {
                if (hasConstructor) throw new MyError(new Position(ctx), "Multiple constructors");
                hasConstructor = true;
                classDefNode.classBuild = (ClassBuildNode) visit(def);
            }
        }
        return classDefNode;
    }

    @Override
    public Node visitVarDef(VarDefContext ctx) {
        VarDefNode varDefNode = new VarDefNode(new Position(ctx));
        TypeNode typeNode = (TypeNode) visit(ctx.type());
        for (var unit : ctx.varDefUnit()) {
            varDefNode.units.add(new VarDefUnitNode(new Position(unit), typeNode, unit.Identifier().getText(), unit.expr() == null ? null : (ExprNode) visit(unit.expr())));
        }
        return varDefNode;
    }

    @Override
    public Node visitReturnType(ReturnTypeContext ctx) {
        if (ctx.Void() != null) {
            return new TypeNode(new Position(ctx), ctx.getText());
        } else {
            return (TypeNode) visit(ctx.type());
        }
    }

    @Override
    public Node visitParameterList(ParameterListContext ctx) {
        ParameterListNode parameterListNode = new ParameterListNode(new Position(ctx));
        for (int i = 0; i < ctx.type().size(); ++i) {
            parameterListNode.units.add(new VarDefUnitNode(new Position(ctx.type(i)), (TypeNode) visit(ctx.type(i)), ctx.Identifier(i).getText(), null));
        }
        return parameterListNode;
    }

    @Override
    public Node visitSuite(SuiteContext ctx) {
        SuiteNode suiteNode = new SuiteNode(new Position(ctx));
        ctx.statement().forEach(stmt -> suiteNode.stmts.add((StmtNode) visit(stmt)));
        return suiteNode;
    }

    @Override
    public Node visitClassBuild(ClassBuildContext ctx) {
        return new ClassBuildNode(new Position(ctx), ctx.Identifier().getText(), (SuiteNode) visit(ctx.suite()));
    }

    @Override
    public Node visitType(TypeContext ctx) {
        return new TypeNode(new Position(ctx), ctx.typeName().getText(), ctx.LBracket().size());
    }

    @Override
    public Node visitStatement(StatementContext ctx) {
        if (ctx.suite() != null) {
            return visit(ctx.suite());
        } else if (ctx.varDef() != null) {
            return visit(ctx.varDef());
        } else if (ctx.exprStmt() != null) {
            return visit(ctx.exprStmt());
        } else if (ctx.ifStmt() != null) {
            return visit(ctx.ifStmt());
        } else if (ctx.forStmt() != null) {
            return visit(ctx.forStmt());
        } else if (ctx.continueStmt() != null) {
            return visit(ctx.continueStmt());
        } else if (ctx.breakStmt() != null)  {
            return visit(ctx.breakStmt());
        } else if (ctx.returnStmt() != null) {
            return visit(ctx.returnStmt());
        } else if (ctx.whileStmt() != null) {
            return visit(ctx.whileStmt());
        }
        return visitChildren(ctx);
    }

    @Override // todo
    public Node visitIfStmt(IfStmtContext ctx) {
        IfStmtNode ifStmtNode = new IfStmtNode(new Position(ctx), (ExprNode) visit(ctx.expr()));
        // todo
        if (ctx.statement(0).suite() != null) {
            ifStmtNode.thenStmts = ((SuiteNode) visit(ctx.statement(0).suite())).stmts;
        } else {
            ifStmtNode.thenStmts.add((StmtNode) visit(ctx.statement(0)));
        }
        if (ctx.Else() != null) {
            if (ctx.statement(1).suite() != null) {
                ifStmtNode.elseStmts = ((SuiteNode) visit(ctx.statement(1).suite())).stmts;
            } else {
                ifStmtNode.elseStmts.add((StmtNode) visit(ctx.statement(1)));
            }
        }
        return ifStmtNode;
    }

    @Override // todo
    public Node visitForStmt(ForStmtContext ctx) {
        ForStmtNode forStmtNode = new ForStmtNode(new Position(ctx));
        if (ctx.statement().suite() != null) {
            forStmtNode.stmts = ((SuiteNode) visit(ctx.statement().suite())).stmts;
        } else {
            forStmtNode.stmts.add((StmtNode) visit(ctx.statement()));
        }
        if (ctx.forInit().varDef() != null) {
            forStmtNode.varDefNode = (VarDefNode) visit(ctx.forInit().varDef());
        } else {
            forStmtNode.init = ((ExprStmtNode) visit(ctx.forInit().exprStmt())).exprNode;
        }
        forStmtNode.condition = ((ExprStmtNode) visit(ctx.exprStmt())).exprNode;
        if (ctx.expr() != null) forStmtNode.step = (ExprNode) visit(ctx.expr());
        return forStmtNode;
    }

    @Override // todo
    public Node visitWhileStmt(WhileStmtContext ctx) {
        WhileStmtNode whileStmtNode = new WhileStmtNode(new Position(ctx), (ExprNode) visit(ctx.expr()));
        if (ctx.statement().suite() != null) {
            whileStmtNode.stmts = ((SuiteNode) visit(ctx.statement().suite())).stmts;
        } else {
            whileStmtNode.stmts.add((StmtNode) visit(ctx.statement()));
        }
        return whileStmtNode;
    }

    @Override
    public Node visitBreakStmt(BreakStmtContext ctx) {
        return new BreakStmtNode(new Position(ctx));
    }

    @Override
    public Node visitContinueStmt(ContinueStmtContext ctx) {
        return new ContinueStmtNode(new Position(ctx));
    }

    @Override
    public Node visitReturnStmt(ReturnStmtContext ctx) {
        return new ReturnStmtNode(new Position(ctx), ctx.expr() == null ? null : (ExprNode) visit(ctx.expr()));
    }

    @Override
    public Node visitExprStmt(ExprStmtContext ctx) {
        return new ExprStmtNode(new Position(ctx), ctx.expr() == null ? null : (ExprNode) visit(ctx.expr()));
    }

    @Override // todo
    public Node visitNewExpr(NewExprContext ctx) {
        NewExprNode newExprNode = new NewExprNode(new Position(ctx), ctx.typeName().getText());
        newExprNode.dim = ctx.newArrayUnit().size();
        boolean isEmpty = false;
        for (var unit : ctx.newArrayUnit()) {
            if (unit.expr() == null) {
                isEmpty = true;
            } else if (isEmpty) {
                throw new MyError(new Position(ctx), "Empty array dimension");
            } else {
                newExprNode.sizeList.add((ExprNode) visit(unit.expr()));
            }
        }
        return newExprNode;
    }

    @Override
    public Node visitUnaryExpr(UnaryExprContext ctx) {
        return new UnaryExprNode(new Position(ctx), ctx.op.getText(), (ExprNode) visit(ctx.expr()));
    }

    @Override
    public Node visitPreSelfExpr(PreSelfExprContext ctx) {
        return new PreSelfExprNode(new Position(ctx), ctx.op.getText(), (ExprNode) visit(ctx.expr()));
    }

    @Override // todo
    public Node visitArrayExpr(ArrayExprContext ctx) {
        return new ArrayExprNode(new Position(ctx), (ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)));
    }

    @Override // todo
    public Node visitFuncExpr(FuncExprContext ctx) {
        FuncExprNode funcExprNode = new FuncExprNode(new Position(ctx), (ExprNode) visit(ctx.expr()));
        if (ctx.exprList() != null) funcExprNode.args = (ExprListNode) visit(ctx.exprList());
        return funcExprNode;
    }

    @Override
    public Node visitAtomExpr(AtomExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Node visitMemberExpr(MemberExprContext ctx) {
        return new MemberExprNode(new Position(ctx), (ExprNode) visit(ctx.expr()), ctx.Identifier().getText());
    }

    @Override
    public Node visitBinaryExpr(BinaryExprContext ctx) {
        return new BinaryExprNode(new Position(ctx), (ExprNode) visit(ctx.expr(0)), ctx.op.getText(), (ExprNode) visit(ctx.expr(1)));
    }

    @Override
    public Node visitConditionalExpr(ConditionalExprContext ctx) {
        return new ConditionalExprNode(new Position(ctx), (ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)), (ExprNode) visit(ctx.expr(2)));
    }

    @Override
    public Node visitAssignExpr(AssignExprContext ctx) {
        return new AssignExprNode(new Position(ctx), (ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)));
    }

    @Override
    public Node visitParenExpr(ParenExprContext ctx) {
        return (ExprNode) visit(ctx.expr());
    }

    @Override
    public Node visitPrimary(PrimaryContext ctx) {
        return ctx.Identifier() == null ? new AtomExprNode(new Position(ctx), ctx.getText()) : new VarExprNode(new Position(ctx), ctx.getText());
    }

    @Override
    public Node visitExprList(ExprListContext ctx) {
        ExprListNode exprListNode = new ExprListNode(new Position(ctx));
        ctx.expr().forEach(expr -> exprListNode.exprs.add((ExprNode) visit(expr)));
        return exprListNode;
    }
}
