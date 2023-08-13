lexer grammar MxLexer;

@header { package grammar; }

Void: 'void';
Bool: 'bool';
Int: 'int';
String: 'string';
New: 'new';
Class: 'class';
Null: 'null';
True: 'true';
False: 'false';
This: 'this';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';

Add: '+';
Sub: '-';
Mul: '*';
Div: '/';
Mod: '%';

GThan: '>';
LThan: '<';
GEqual: '>=';
LEqual: '<=';
EEqual: '==';
NEqual: '!=';

LAnd: '&&';
LOr: '||';
LNot: '!';

LShift: '<<';
RShift: '>>';
BAnd: '&';
BOr: '|';
BNot: '~';
BXor: '^';

Assign: '=';

SelfAdd: '++';
SelfSub: '--';

Member: '.';
Arrow: '->';

Quote: '"';

LBracket: '[';
RBracket: ']';
LParen: '(';
RParen: ')';
LBrace: '{';
RBrace: '}';

Semi: ';';
Comma: ',';
Colon: ':';
QMark: '?';

Identifier: [A-Za-z][0-9A-Za-z_]*;

IntConst: [1-9][0-9]* | '0';
StringConst: Quote (PChar)*? Quote;
fragment PChar: '\\n' | '\\\\' | '\\"' | [ -~];

WhiteSpace: [ \t\r\n]+ -> skip;
Newline: ('\r' '\n'? | '\n') -> skip;
LineComment: '//' ~[\r\n]* -> skip;
BlockComment: '/*' .*? '*/' -> skip;
