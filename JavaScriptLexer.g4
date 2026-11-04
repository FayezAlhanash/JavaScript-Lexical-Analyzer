lexer grammar JavaScriptLexer;
IF: 'if';
LET: 'let';
KEYWORD
    : 'break' | 'case' | 'catch' | 'class' | 'const' | 'continue'
    | 'debugger' | 'default' | 'delete' | 'do' | 'else' | 'enum'
    | 'export' | 'extends' | 'finally' | 'for' | 'function'
    | 'import' | 'in' | 'instanceof' | 'new' | 'return' | 'super'
    | 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void'
    | 'while' | 'with' | 'yield' | 'static' | 'await'
    | 'implements' | 'interface' | 'package' | 'private'
    | 'protected' | 'public'
    ;
BOOLEAN      : 'true' | 'false' ;
NULL_LITERAL : 'null' ;
BIGINT: (DIGITS | '0' [xX] HEX_DIGIT+ | '0' [oO] [0-7]+ | '0' [bB] [01]+) 'n';
HEX_NUMBER: '0' [xX] HEX_DIGIT+;
OCTAL_NUMBER: '0' [oO] [0-7]+;
BINARY_NUMBER: '0' [bB] [01]+;
FLOAT: DIGITS '.' DIGITS? EXP? | '.' DIGITS EXP? | DIGITS EXP;
INTEGER: DIGITS;
INVALID_NUMBER: (DIGITS ('.' DIGITS?)? EXPONENT? | '.' DIGITS EXPONENT?) ID_START ID_PART* | '0' [xXoObB];
fragment DIGITS: [0-9] ('_'? [0-9])*;
fragment HEX_DIGIT: [0-9a-fA-F];
fragment EXP: [eE] [+\-]? DIGITS;
fragment EXPONENT: [eE] [+\-]? [0-9]+;
fragment ID_START: [\p{L}_$];
fragment ID_PART: [\p{L}_$0-9];
IDENTIFIER: ID_START ID_PART*;
PRIVATE_NAME: '#' ID_START ID_PART*;
WS: [ \t\r\n]+ -> skip;
