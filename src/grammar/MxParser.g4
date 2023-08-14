parser grammar MxParser;

@header { package grammar; }

options { tokenVocab = MxLexer; }

program: (funcDef | classDef | varDef)* EOF;

funcDef: returnType Identifier '(' parameterList? ')' '{' suite '}';
returnType: type | Void;
parameterList: (type Identifier) (Comma type Identifier)*;
suite: statement*;

classDef: Class Identifier '{' (varDef | classBuild | funcDef)* '}' Semi;
classBuild: Identifier '(' ')' '{' suite '}';

varDef: type varDefUnit (Comma varDefUnit)* Semi;
varDefUnit: Identifier (Assign expr)?;
type: typeName ('[' ']')*;
typeName: baseType | Identifier;
baseType: Int | Bool | String;

statement: '{' suite '}' | varDef | ifStmt | whileStmt | forStmt | breakStmt | continueStmt | returnStmt | exprStmt;

ifStmt: If '(' expr ')' statement (Else statement)?;

whileStmt: While '(' expr ')' statement;

forStmt: For '(' forInit exprStmt expr? ')' statement;
forInit: varDef | exprStmt;

breakStmt: Break Semi;

continueStmt: Continue Semi;

returnStmt: Return expr? Semi;

exprStmt: expr? Semi;
expr
    : '(' expr ')'                                                               #parenExpr
    | New typeName (newArrayUnit)* ('(' ')')?                                    #newExpr
    | expr op=Member Identifier                                                  #memberExpr
    | expr '[' expr ']'                                                          #arrayExpr
    | expr '(' exprList? ')'                                                     #funcExpr
    | <assoc=right> expr op=(SelfAdd | SelfSub)                                  #unaryExpr
    | op=(SelfAdd |SelfSub) expr                                                 #preSelfExpr
    | <assoc=right> op=(LNot | BNot | Add | Sub) expr                            #unaryExpr
    | expr op=(Mul | Div | Mod) expr                                             #binaryExpr
    | expr op=(Add | Sub) expr                                                   #binaryExpr
    | expr op=(LShift | RShift) expr                                             #binaryExpr
    | expr op=(LThan | GThan | LEqual | GEqual) expr                             #binaryExpr
    | expr op=(EEqual | NEqual) expr                                             #binaryExpr
    | expr op=(BAnd | BXor | BOr | LAnd | LOr) expr                              #binaryExpr
    | <assoc=right> expr op=Assign expr                                          #assignExpr
    | <assoc=right> expr '?' expr ':' expr                                       #conditionalExpr
    | primary                                                                    #atomExpr
    ;

newArrayUnit: '[' expr? ']';

primary: IntConst | StringConst | True | False | Null | Identifier | This;

exprList: expr (Comma expr)*;