package ir;

import ast.*;
import utils.*;

import java.util.ArrayList;
import java.util.HashMap;

public class IRBuilder implements BuiltinElements, Visitor {
    public IRProgram irProgram;
    public GlobalScope globalScope;
    public Scope currentScope;
    int idx = -1; // for parameters

    public IRBlock currentBlock = null;
    public IRFunction currentFunc = null;
    public StructType currentClass = null;
    public HashMap<String, StructType> classMap = new HashMap<>();

    public IRBuilder(GlobalScope globalScope, IRProgram program) {
        irProgram = program;
        currentScope = globalScope;
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        for (var def : node.defList) {
            if (def instanceof ClassDefNode) {
                classMap.put(((ClassDefNode) def).name, new StructType(((ClassDefNode) def).name));
            }
        }
        node.defList.forEach(def -> {
            if (def instanceof ClassDefNode) def.accept(this);
        });
        node.defList.forEach(def -> {
            if (def instanceof VarDefNode) def.accept(this);
        });
        node.defList.forEach(def -> {
            if (def instanceof FuncDefNode) def.accept(this);
        });
        if (irProgram.initBlock.instructionList.isEmpty()) {
            irProgram.initFunc = null;
            return;
        }
        irProgram.initFunc.finish();
        irProgram.functions.addFirst(irProgram.initFunc);
        IRBlock mainEntry = irProgram.mainFunc.blocks.get(0);
        mainEntry.instructionList.addFirst(new CallInstruction(mainEntry, irVoidType, "__program_var_init"));
    }

    @Override
    public void visit(FuncDefNode node) {
        IRBlock.blockCnt = 0;
        node.returnType.irType = toIRType(node.returnType.type);
        String funcName = currentClass != null ? currentClass.name + "." + node.funcName : node.funcName;
        currentFunc = new IRFunction(funcName, node.returnType.irType);
        irProgram.functions.add(currentFunc);
        currentBlock = currentFunc.appendBlock(new IRBlock(currentFunc, "entry_"));
        currentScope = new Scope(currentScope, node.returnType.type);
        // todo
        if (currentClass != null) {
            IRPointerType classPtrType = new IRPointerType(currentClass);
            IRRegister thisVal = new IRRegister("this", classPtrType);
            currentFunc.params.add(thisVal);
            IRRegister thisAddr = new IRRegister("this.addr", new IRPointerType(classPtrType));
            currentBlock.add(new AllocaInstruction(currentBlock, classPtrType, thisAddr));
            currentBlock.add(new StoreInstruction(currentBlock, thisVal, thisAddr));
            currentScope.addIRVar("this", thisAddr);
        }
        if (node.parameterListNode != null) node.parameterListNode.accept(this);
        currentFunc.exit = new IRBlock(currentFunc, "return_");
        currentBlock.terminal = new JumpInstruction(currentBlock, currentFunc.exit);
        if (node.returnType.type.equals(VoidType)) {
            currentFunc.exit.terminal = new RetInstruction(currentFunc.exit, voidConst);
        } else {
            IRType retType = node.returnType.irType;
            currentFunc.retAddr = new IRRegister("retval", new IRPointerType(retType));
            currentFunc.exit.add(new AllocaInstruction(currentBlock, retType, currentFunc.retAddr));
            IRRegister retVal = new IRRegister("ret", retType);
            currentFunc.exit.add(new LoadInstruction(currentBlock, retVal, currentFunc.retAddr));
            currentFunc.exit.terminal = new RetInstruction(currentFunc.exit, retVal);
        }
        if (funcName.equals("main")) irProgram.mainFunc = currentFunc;
        // todo
        node.stmts.forEach(stmt -> stmt.accept(this));
        node.irFunction = currentFunc;
        currentFunc.finish(); // ?
        currentScope = currentScope.parentScope;
        currentFunc = null;
        currentBlock = null;
    }

    @Override
    public void visit(ClassDefNode node) {
        currentScope = new Scope(currentScope, node);
        currentClass = classMap.get(node.name);
        irProgram.classes.add(currentClass);
        node.varDefList.forEach(def -> def.accept(this));
        if (node.classBuild != null) {
            currentClass.hasBuild = true;
            node.classBuild.accept(this);
        }
        node.funcDefList.forEach(def -> def.className = node.name);
        node.funcDefList.forEach(def -> def.accept(this));
        currentClass = null;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(VarDefNode node) {
        node.units.forEach(unit -> unit.accept(this));
    }

    @Override
    public void visit(VarDefUnitNode node) {
        node.typeNode.accept(this);
        if (currentFunc != null) {
            IRRegister definingPtr = new IRRegister(node.varName + ".addr", new IRPointerType(node.typeNode.irType));
            currentScope.addIRVar(node.varName, definingPtr);
            currentBlock.add(new AllocaInstruction(currentBlock, node.typeNode.irType, definingPtr, idx == -1 ? -1 : idx + (currentClass == null ? 0 : 1)));
            if (node.init != null) {
                node.init.accept(this);
                addStore(definingPtr, node.init);
            } else if (node.typeNode.type.isReferenceType() && idx == -1)
                currentBlock.add(new StoreInstruction(currentBlock, new NullConst(node.typeNode.irType), definingPtr));
        } else if (currentClass != null) {
            currentClass.addMember(node.varName, node.typeNode.irType);
        } else {
            GlobalVariable globalVariable = new GlobalVariable(node.varName, node.typeNode.irType);
            if (node.init instanceof AtomExprNode && !node.init.type.equals(StringType) && !node.init.str.equals("this")) {
                node.init.accept(this);
                globalVariable.init = node.init.value;
                globalScope.addIRVar(node.varName, globalVariable);
            } else {
                globalVariable.init = node.typeNode.irType.defaultValue();
                globalScope.addIRVar(node.varName, globalVariable);
                if (node.init != null) {
                    // todo
                    IRBlock tmpBlock = currentBlock;
                    IRFunction tmpFunc = currentFunc;
                    currentFunc = irProgram.initFunc;
                    currentBlock = irProgram.initBlock;
                    node.init.accept(this);
                    addStore(globalVariable, node.init);
                    irProgram.initBlock = currentBlock;
                    currentBlock = tmpBlock;
                    currentFunc = tmpFunc;
                }
            }
            irProgram.globalVariables.add(globalVariable);
        }
    }

    @Override
    public void visit(ParameterListNode node) {
        for (idx = 0; idx < node.units.size(); idx++) {
            var unit = node.units.get(idx);
            unit.accept(this);
            IRRegister input = new IRRegister("", unit.typeNode.irType);
            currentFunc.params.add(input);
            currentBlock.add(new StoreInstruction(currentBlock, input, currentScope.getIRVarPtr(unit.varName), idx + (currentClass == null ? 0 : 1)));
        }
        idx = -1;
    }

    @Override
    public void visit(TypeNode node) {
        node.irType = toIRType(node.type);
    }

    @Override
    public void visit(ClassBuildNode node) {
        node.toFunc().accept(this);
    }

    @Override
    public void visit(SuiteNode node) {
        currentScope = new Scope(currentScope);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(IfStmtNode node) {
        // todo
        node.condition.accept(this);
        IRBase cond = getVal(node.condition);
        IRBlock lastBlock = currentBlock, nextBlock = new IRBlock(currentFunc, "if.end_");
        nextBlock.terminal = currentBlock.terminal;
        IRBlock thenBlock = new IRBlock(currentFunc, "if.then_", nextBlock);
        currentScope = new Scope(currentScope);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(thenBlock);
        node.thenStmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
        if (node.elseStmts != null) {
            IRBlock elseBlock = new IRBlock(currentFunc, "if.else_", nextBlock);
            currentScope = new Scope(currentScope);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(elseBlock);
            node.elseStmts.forEach(stmt -> stmt.accept(this));
            currentScope = currentScope.parentScope;
            lastBlock.terminal = new BrInstruction(lastBlock, cond, thenBlock, elseBlock);
        } else {
            lastBlock.terminal = new BrInstruction(lastBlock, cond, thenBlock, nextBlock);
        }
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(nextBlock);
        // todo
    }

    @Override
    public void visit(ForStmtNode node) {
        currentScope = new Scope(currentScope, node);
        if (node.varDefNode != null) node.varDefNode.accept(this);
        if (node.init != null) node.init.accept(this);
        node.condBlock = new IRBlock(currentFunc, "for.cond_");
        node.loopBlock = new IRBlock(currentFunc, "for.loop_");
        node.stepBlock = new IRBlock(currentFunc, "for.step_");
        node.nextBlock = new IRBlock(currentFunc, "for.end_");
        node.nextBlock.terminal = currentBlock.terminal;
        currentBlock.terminal = new JumpInstruction(currentBlock, node.condBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(node.condBlock);
        if (node.condition != null) {
            node.condition.accept(this);
            currentBlock.terminal = new BrInstruction(currentBlock, getVal(node.condition), node.loopBlock, node.nextBlock);
        } else {
            currentBlock.terminal = new JumpInstruction(currentBlock, node.loopBlock);
        }
        currentBlock.isFinished = true;
        currentScope = new Scope(currentScope);
        currentBlock = currentFunc.appendBlock(node.loopBlock);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentBlock.terminal = new JumpInstruction(currentBlock, node.stepBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(node.stepBlock);
        currentScope = currentScope.parentScope;
        if (node.step != null) node.step.accept(this);
        currentBlock.terminal = new JumpInstruction(currentBlock, node.condBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(node.nextBlock);
        currentScope = currentScope.parentScope; // ?
        // todo
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.condBlock = new IRBlock(currentFunc, "while.cond_");
        node.loopBlock = new IRBlock(currentFunc, "while.loop_");
        node.nextBlock = new IRBlock(currentFunc, "while.end_");
        node.nextBlock.terminal = currentBlock.terminal;
        currentBlock.terminal = new JumpInstruction(currentBlock, node.condBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(node.condBlock);
        node.condition.accept(this);
        currentBlock.terminal = new BrInstruction(currentBlock, getVal(node.condition), node.loopBlock, node.nextBlock);
        currentBlock = currentFunc.appendBlock(node.loopBlock);
        currentScope = new Scope(currentScope, node);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
        currentBlock.terminal = new JumpInstruction(currentBlock, node.condBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(node.nextBlock);
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if (currentScope.inWhichLoop instanceof ForStmtNode) currentBlock.terminal = new JumpInstruction(currentBlock, ((ForStmtNode) currentScope.inWhichLoop).stepBlock);
        else currentBlock.terminal = new JumpInstruction(currentBlock, currentScope.inWhichLoop.condBlock);
        currentBlock.isFinished = true;
    }

    @Override
    public void visit(BreakStmtNode node) {
        currentBlock.terminal = new JumpInstruction(currentBlock, currentScope.inWhichLoop.nextBlock);
        currentBlock.isFinished = true;
    }

    @Override
    public void visit(ReturnStmtNode node) {
        if (node.exprNode != null) {
            node.exprNode.accept(this);
            addStore(currentFunc.retAddr, node.exprNode);
        }
        currentBlock.terminal = new JumpInstruction(currentBlock, currentFunc.exit);
        currentBlock.isFinished = true;
    }

    @Override
    public void visit(ExprStmtNode node) {
        if (node.exprNode != null) node.exprNode.accept(this);
    }

    @Override
    public void visit(AtomExprNode node) {
        if (node.type.equals(IntType)) {
            node.value = new IntConst(Integer.parseInt(node.str));
        } else if (node.type.equals(BoolType)) {
            node.value = new BoolConst(node.str.equals("true"));
        } else if (node.type.equals(NullType)) {
            node.value = new NullConst();
        } else if (node.type.equals(StringType)) {
            node.value = irProgram.addString(node.str.substring(1, node.str.length() - 1));
        } else {
            node.store = currentScope.getIRVarPtr("this");
        }
    }

    @Override
    public void visit(VarExprNode node) {
        node.store = currentScope.getIRVarPtr(node.str);
        if (node.store == null) {
            IRRegister thisAddr = currentScope.getIRVarPtr("this");
            if (thisAddr != null) {
                IRType objPtr = ((IRPointerType) thisAddr.irType).toType();
                IRType objType = ((IRPointerType) objPtr).toType();
                IRRegister thisVal = new IRRegister("this", objPtr);
                if (((StructType) objType).findMember(node.str)) {
                    currentBlock.add(new LoadInstruction(currentBlock, thisVal, thisAddr));
                    node.store = new IRRegister("this." + node.str, new IRPointerType(((StructType) objType).getMemberType(node.str)));
                    currentBlock.add(new GetElementPtrInstruction(currentBlock, thisVal, node.store, intConstZero, new IntConst(((StructType) objType).memberMap.get(node.str))));
                }
            }
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.rhs.accept(this);
        node.lhs.accept(this);
        node.store = node.lhs.store;
        node.value = getVal(node.rhs);
        addStore(node.store, node.rhs);
    }

    @Override
    public void visit(UnaryExprNode node) {
        node.expr.accept(this);
        IRRegister dest;
        String op;
        switch (node.op) {
            case "++" -> {
                op = "add";
                node.value = getVal(node.expr);
                dest = new IRRegister("", irIntType);
                currentBlock.add(new BinaryInstruction(currentBlock, dest, node.value, intConstOne, op));
                currentBlock.add(new StoreInstruction(currentBlock, dest, node.expr.store));
            } case "--" -> {
                op = "sub";
                node.value = getVal(node.expr);
                dest = new IRRegister("", irIntType);
                currentBlock.add(new BinaryInstruction(currentBlock, dest, node.value, intConstOne, op));
                currentBlock.add(new StoreInstruction(currentBlock, dest, node.expr.store));
            } case "-" -> { // "+" need no change
                op = "sub";
                dest = new IRRegister("", irIntType);
                currentBlock.add(new BinaryInstruction(currentBlock, dest, intConstZero, getVal(node.expr), op));
                node.value = dest;
            } case "~" -> {
                op = "xor";
                dest = new IRRegister("", irIntType);
                currentBlock.add(new BinaryInstruction(currentBlock, dest, getVal(node.expr), intConstNegativeOne, op));
                node.value = dest;
            } case "!" -> {
                assert node.expr.type.equals(BoolType);
                op = "xor";
                dest = new IRRegister("", irBoolType);
                currentBlock.add(new BinaryInstruction(currentBlock, dest, getVal(node.expr), trueConst, op));
                node.value = dest;
            }
        }
    }

    @Override
    public void visit(PreSelfExprNode node) {
        node.expr.accept(this);
        IRRegister dest;
        String op;
        if (node.op.equals("++")) op = "add";
        else op = "sub";
        dest = new IRRegister("", irIntType);
        currentBlock.add(new BinaryInstruction(currentBlock, dest, getVal(node.expr), intConstOne, op));
        currentBlock.add(new StoreInstruction(currentBlock, dest, node.expr.store));
        node.value = dest;
        node.store = node.expr.store;
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        IRType tmp = getVal(node.obj).irType;
        node.objAddress = (IRRegister) node.obj.value;
        tmp = ((IRPointerType) tmp).toType();
        if (tmp instanceof StructType) {
            IRType memberType = ((StructType) tmp).getMemberType(node.member);
            if (memberType != null) {
                node.store = new IRRegister("", new IRPointerType(memberType));
                currentBlock.add(new GetElementPtrInstruction(currentBlock, getVal(node.obj), node.store, intConstZero, new IntConst(((StructType) tmp).memberMap.get(node.member))));
            }
        }
    }

    @Override
    public void visit(ArrayExprNode node) {
        node.array.accept(this);
        node.index.accept(this);
        IRRegister dest = new IRRegister("", getVal(node.array).irType);
        currentBlock.add(new GetElementPtrInstruction(currentBlock, getVal(node.array), dest, getVal(node.index)));
        node.store = dest;
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.lhs.accept(this);
        // todo: short-circuit
        if (node.op.equals("&&") || node.op.equals("||")) {
            IRRegister tmp = new IRRegister(".shortCirTmp", new IRPointerType(irBoolType));
            currentBlock.add(new AllocaInstruction(currentBlock, irBoolType, tmp));
            IRBlock rhsBlock = new IRBlock(currentFunc, "rhsBlock_");
            IRBlock trueBlock = new IRBlock(currentFunc, "trueBlock_");
            IRBlock falseBlock = new IRBlock(currentFunc, "falseBlock_");
            IRBlock nextBlock = new IRBlock(currentFunc, "shortCir.end_");
            nextBlock.terminal = currentBlock.terminal;
            currentBlock.terminal = node.op.equals("&&")
                    ? new BrInstruction(currentBlock, getVal(node.lhs), rhsBlock, falseBlock)
                    : new BrInstruction(currentBlock, getVal(node.lhs), trueBlock, rhsBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(rhsBlock);
            node.rhs.accept(this);
            currentBlock.terminal = new BrInstruction(currentBlock, getVal(node.rhs), trueBlock, falseBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(trueBlock);
            currentBlock.add(new StoreInstruction(currentBlock, trueConst, tmp));
            currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(falseBlock);
            currentBlock.add(new StoreInstruction(currentBlock, falseConst, tmp));
            currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(nextBlock);
//            IRRegister loadTmp = new IRRegister(".loadTmp", irBoolType);
//            currentBlock.add(new LoadInstruction(currentBlock, loadTmp, tmp));
            node.value = new IRRegister("", irBoolType);
            currentBlock.add(new LoadInstruction(currentBlock, (IRRegister) node.value, tmp));
            return;
        }
        node.rhs.accept(this);
        String op = null;
        IRType opType;
        IRRegister dest = null;
        if (node.lhs.type.equals(StringType) || node.rhs.type.equals(StringType)) {
            switch (node.op) {
                case "+" -> {
                    node.value = new IRRegister("", irStringType);
                    currentBlock.add(new CallInstruction(currentBlock, (IRRegister) node.value, irStringType, "__string_add", getVal(node.lhs), getVal(node.rhs)));
                }
                case "<" -> node.value = stringCmp("__string_less", getVal(node.lhs), getVal(node.rhs));
                case ">" -> node.value = stringCmp("__string_greater", getVal(node.lhs), getVal(node.rhs));
                case "<=" -> node.value = stringCmp("__string_lessOrEqual", getVal(node.lhs), getVal(node.rhs));
                case ">=" -> node.value = stringCmp("__string_greaterOrEqual", getVal(node.lhs), getVal(node.rhs));
                case "==" -> node.value = stringCmp("__string_equal", getVal(node.lhs), getVal(node.rhs));
                case "!=" -> node.value = stringCmp("__string_notEqual", getVal(node.lhs), getVal(node.rhs));
            }
        } else {
            // todo
            IRBase lhsVal = getVal(node.lhs), rhsVal = getVal(node.rhs);
            switch (node.op) {
                case "+" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val + ((IntConst) rhsVal).val);
                    op = "add";
                } case "-" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val - ((IntConst) rhsVal).val);
                    op = "sub";
                } case "*" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val * ((IntConst) rhsVal).val);
                    op = "mul";
                } case "/" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst && ((IntConst) rhsVal).val != 0)
                        node.value = new IntConst(((IntConst) lhsVal).val / ((IntConst) rhsVal).val);
                    op = "sdiv";
                } case "%" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val % ((IntConst) rhsVal).val);
                    op = "srem";
                } case "<<" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val << ((IntConst) rhsVal).val);
                    op = "shl";
                } case ">>" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val >> ((IntConst) rhsVal).val);
                    op = "ashr";
                } case "&" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val & ((IntConst) rhsVal).val);
                    op = "and";
                } case "|" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val | ((IntConst) rhsVal).val);
                    op = "or";
                } case "^" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new IntConst(((IntConst) lhsVal).val ^ ((IntConst) rhsVal).val);
                    op = "xor";
                } case "<" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val < ((IntConst) rhsVal).val);
                    op = "slt";
                } case "<=" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val <= ((IntConst) rhsVal).val);
                    op = "sle";
                } case ">" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val > ((IntConst) rhsVal).val);
                    op = "sgt";
                } case ">=" -> {
                    if (lhsVal instanceof IRConst && rhsVal instanceof IRConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val >= ((IntConst) rhsVal).val);
                    op = "sge";
                } case "==" -> {
                    if (lhsVal instanceof IntConst && rhsVal instanceof IntConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val == ((IntConst) rhsVal).val);
                    op = "eq";
                } case "!=" -> {
                    if (lhsVal instanceof IntConst && rhsVal instanceof IntConst)
                        node.value = new BoolConst(((IntConst) lhsVal).val != ((IntConst) rhsVal).val);
                    op = "ne";
                }
            }
            if (node.value != null) return;
            switch (node.op) {
                case "+", "-", "*", "/", "%", "<<", ">>", "&", "|", "^" -> {
                    dest = new IRRegister("", irIntType);
                    currentBlock.add(new BinaryInstruction(currentBlock, dest, lhsVal, rhsVal, op));
                } case "<", "<=", ">", ">=" -> {
                    opType = irIntType;
                    dest = new IRRegister("", irBoolType);
                    currentBlock.add(new IcmpInstruction(currentBlock, opType, dest, lhsVal, rhsVal, op));
                } case "==", "!=" -> {
                    opType = node.lhs.type.equals(NullType) ? node.rhs.getIRType() : node.lhs.getIRType();
                    dest = new IRRegister("tmp", irBoolType);
                    currentBlock.add(new IcmpInstruction(currentBlock, opType, dest, lhsVal, rhsVal, op));
                }
            }
            node.value = dest;
        }
    }

    @Override // todo
    public void visit(ConditionalExprNode node) {
        node.condition.accept(this);
        IRBlock thenBlock = new IRBlock(currentFunc, "condExpr.then_");
        IRBlock elseBlock = new IRBlock(currentFunc, "condExpr.else_");
        IRBlock nextBlock = new IRBlock(currentFunc, "condExpr.next_");
        IRType irType = toIRType(node.type);
        IRBase condition = getVal(node.condition);
        // todo
        if (irType.equals(irVoidType)) {
            nextBlock.terminal = currentBlock.terminal;
            currentBlock.terminal = new BrInstruction(currentBlock, condition, thenBlock, elseBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(thenBlock);
            node.thenExpr.accept(this);
            currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(elseBlock);
            node.elseExpr.accept(this);
            currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(nextBlock);
            node.value = null;
            node.store = null;
            return;
        }
        node.store = new IRRegister("", new IRPointerType(irType));
        currentBlock.add(new AllocaInstruction(currentBlock, irType, node.store));
        nextBlock.terminal = currentBlock.terminal;
        currentBlock.terminal = new BrInstruction(currentBlock, condition, thenBlock, elseBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(thenBlock);
        node.thenExpr.accept(this);
        addStore(node.store, node.thenExpr);
        currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(elseBlock);
        node.elseExpr.accept(this);
        addStore(node.store, node.elseExpr);
        currentBlock.terminal = new JumpInstruction(currentBlock, nextBlock);
        currentBlock.isFinished = true;
        currentBlock = currentFunc.appendBlock(nextBlock);
        node.value = getVal(node);
    }

    @Override
    public void visit(FuncExprNode node) {
        node.funcName.accept(this);
        FuncDefNode funcDef = node.funcName.funcDef;
        String funcName = funcDef.className == null ? funcDef.funcName : funcDef.className + "." + funcDef.funcName;
        funcDef.returnType.irType = toIRType(funcDef.returnType.type);
        CallInstruction call = new CallInstruction(currentBlock, funcDef.returnType.irType, funcName);
        if (funcDef == ArraySizeFunc) {
            IRRegister array = ((MemberExprNode) node.funcName).objAddress;
            node.value = new IRRegister("", irIntType);
            currentBlock.add(new CallInstruction(currentBlock, (IRRegister) node.value, irIntType, "__array_size", array));
        } else {
            if (funcDef == StringLengthFunc) call.funcName = "strlen";
            else if (funcDef == StringSubStringFunc) call.funcName = "__string_subString";
            else if (funcDef == StringParseIntFunc) call.funcName = "__string_parseInt";
            else if (funcDef == StringOrdFunc) call.funcName = "__string_ord";
            if (funcDef.className != null) {
                //System.out.println(funcDef.className + call.funcName);

                if (node.funcName instanceof MemberExprNode) {

                    call.args.add(((MemberExprNode) node.funcName).objAddress);
                } else {
                    IRRegister thisPtr = currentScope.getIRVarPtr("this");
                    IRRegister thisVal = new IRRegister("", ((IRPointerType) thisPtr.irType).toType());
                    currentBlock.add(new LoadInstruction(currentBlock, thisVal, thisPtr));
                    call.args.add(thisVal);
                }
            } else {
                //System.out.println("empty." + call.funcName);
            }
            // todo
            if (node.args != null) {
                node.args.accept(this);
                node.args.exprs.forEach(arg -> call.args.add(getVal(arg)));
            }
            if (funcDef.returnType.irType != irVoidType)
                call.reg = new IRRegister("", funcDef.returnType.irType);
            currentBlock.add(call);
            node.value = call.reg;
            // System.out.println(call.funcName + call.args);
        }


    }

    @Override
    public void visit(NewExprNode node) {
        IRType irType = toIRType(node.type);
        if (node.dim > 0) {
            node.value = !node.sizeList.isEmpty() ? newArray(irType, 0, node.sizeList) : new NullConst(irType);
            return;
        }
        StructType classType = (StructType) ((IRPointerType) irType).toType();
        // todo
        node.value = new IRRegister("", irType);
        currentBlock.add(new CallInstruction(currentBlock, (IRRegister) node.value, irType, "malloc", new IntConst(classType.size)));
        if (classType.hasBuild) currentBlock.add(new CallInstruction(currentBlock, null, irVoidType, classType.name + "." + classType.name, node.value));
    }

    @Override
    public void visit(ExprListNode node) {
        node.exprs.forEach(expr -> expr.accept(this));
    }

    public IRBase getVal(ExprNode node) {
        if (node.value != null) return node.value;
        assert node.store != null;
        IRRegister val = new IRRegister("", ((IRPointerType) node.store.irType).toType());
        currentBlock.add(new LoadInstruction(currentBlock, val, node.store));
        return node.value = val;
    }

    public IRType toIRType(Type type) {
        IRType ret = switch (type.typeName) {
            case "void" -> irVoidType;
            case "int" -> irIntType;
            case "bool" -> irBoolType;
            case "string" -> irStringType;
            default -> new IRPointerType(classMap.get(type.typeName), 1);
        };
        if (type.dim > 0) ret = new IRPointerType(ret, type.dim);
        return ret;
    }

    public IRRegister stringCmp(String cmpName, IRBase lhs, IRBase rhs) {
        IRRegister cmp = new IRRegister("", irBoolType);
        currentBlock.add(new CallInstruction(currentBlock, cmp, irBoolType, cmpName, lhs, rhs));
        return cmp;
    }

    public IRBase newArray(IRType type, int at, ArrayList<ExprNode> sizeList) {
        sizeList.get(at).accept(this);
        IRBase size;
        IRBase cnt = getVal(sizeList.get(at));
        int sizeOfType = ((IRPointerType) type).toType().size;
        if (cnt instanceof IntConst) {
            size = new IntConst(((IntConst) cnt).val * sizeOfType + 4);
        } else {
            IntConst typeSize = new IntConst(sizeOfType);
            IRRegister tmpSize = new IRRegister("", irIntType);
            currentBlock.add(new BinaryInstruction(currentBlock, tmpSize, cnt, typeSize, "mul"));
            size = new IRRegister("", irIntType);
            currentBlock.add(new BinaryInstruction(currentBlock, (IRRegister) size, tmpSize, intConstFour, "add"));
        }
        IRRegister ptr = new IRRegister("",type);
        currentBlock.add(new CallInstruction(currentBlock, ptr, new IRPointerType(type), "__newPtrArray", size, cnt));
        if (at + 1 < sizeList.size()) {
            IRRegister _idx = new IRRegister("", irIntPtrType);
            currentBlock.add(new AllocaInstruction(currentBlock, irIntType, _idx));
            currentBlock.add(new StoreInstruction(currentBlock, intConstZero, _idx));
            IRBlock condBlock = new IRBlock(currentFunc, "for.cond_");
            IRBlock loopBlock = new IRBlock(currentFunc, "for.loop_");
            IRBlock stepBlock = new IRBlock(currentFunc, "for.step_");
            IRBlock nextBlock = new IRBlock(currentFunc, "for.end_");
            nextBlock.terminal = currentBlock.terminal;
            currentBlock.terminal = new JumpInstruction(currentBlock, condBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(condBlock);
            IRRegister cond = new IRRegister("", irBoolType);
            IRRegister iVal = new IRRegister("", irIntType);
            currentBlock.add(new LoadInstruction(currentBlock, iVal, _idx));
            currentBlock.add(new IcmpInstruction(currentBlock, irIntType, cond, iVal, cnt, "slt"));
            currentBlock.terminal = new BrInstruction(currentBlock, cond, loopBlock, nextBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(loopBlock);
            IRBase iPtrVal = newArray(((IRPointerType) type).toType(), at + 1, sizeList);
            IRRegister iPtr = new IRRegister("", type);
            IRRegister iVal2 = new IRRegister("", irIntType);
            currentBlock.add(new LoadInstruction(nextBlock, iVal2, _idx));
            currentBlock.add(new GetElementPtrInstruction(currentBlock, ptr, iPtr, iVal2));
            currentBlock.add(new StoreInstruction(currentBlock, iPtrVal, iPtr));
            currentBlock.terminal = new JumpInstruction(currentBlock, stepBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(stepBlock);
            IRRegister iVal3 = new IRRegister("", irIntType), iRes = new IRRegister("", irIntType);
            currentBlock.add(new LoadInstruction(nextBlock, iVal3, _idx));
            currentBlock.add(new BinaryInstruction(currentBlock, iRes, iVal3, intConstOne, "add"));
            currentBlock.add(new StoreInstruction(currentBlock, iRes, _idx));
            currentBlock.terminal = new JumpInstruction(currentBlock, condBlock);
            currentBlock.isFinished = true;
            currentBlock = currentFunc.appendBlock(nextBlock);
        }
        return ptr;
    }

    public void addStore(IRRegister ptr, ExprNode rhs) {
        getVal(rhs);
        if (rhs.value instanceof NullConst) rhs.value = new NullConst(((IRPointerType) ptr.irType).toType());
        else currentBlock.add(new StoreInstruction(currentBlock, rhs.value, ptr));

    }

}