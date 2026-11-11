lexer grammar JavaScriptLexer;

tokens { TEMPLATE, UNTERMINATED_TEMPLATE }

IF: 'if';
LET: 'let';
BREAK: 'break';
CASE: 'case';
CATCH: 'catch';
CLASS: 'class';
CONST: 'const';
CONTINUE: 'continue';
DEBUGGER: 'debugger';
DEFAULT: 'default';
DELETE: 'delete';
DO: 'do';
ELSE: 'else';
ENUM: 'enum';
EXPORT: 'export';
EXTENDS: 'extends';
FINALLY: 'finally';
FOR: 'for';
FUNCTION: 'function';
IMPORT: 'import';
IN: 'in';
INSTANCEOF: 'instanceof';
NEW: 'new';
RETURN: 'return';
SUPER: 'super';
SWITCH: 'switch';
THIS: 'this';
THROW: 'throw';
TRY: 'try';
TYPEOF: 'typeof';
VAR: 'var';
VOID: 'void';
WHILE: 'while';
WITH: 'with';
YIELD: 'yield';
STATIC: 'static';
AWAIT: 'await';
IMPLEMENTS: 'implements';
INTERFACE: 'interface';
PACKAGE: 'package';
PRIVATE: 'private';
PROTECTED: 'protected';
PUBLIC: 'public';
// KEYWORD
//     : 'break' | 'case' | 'catch' | 'class' | 'const' | 'continue'
//     | 'debugger' | 'default' | 'delete' | 'do' | 'else' | 'enum'
//     | 'export' | 'extends' | 'finally' | 'for' | 'function'
//     | 'import' | 'in' | 'instanceof' | 'new' | 'return' | 'super'
//     | 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void'
//     | 'while' | 'with' | 'yield' | 'static' | 'await'
//     | 'implements' | 'interface' | 'package' | 'private'
//     | 'protected' | 'public'
//     ;
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

fragment CODE_POINT
    : '0'* ( HEX_DIGIT HEX_DIGIT? HEX_DIGIT? HEX_DIGIT? HEX_DIGIT?
           | '10' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
    ;
fragment ESC_SEQ
    : '\\'
      ( ['"\\bfnrtv]
      | 'x' HEX_DIGIT HEX_DIGIT
      | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
      | 'u{' CODE_POINT '}'
      | [0-3] [0-7] [0-7]
      | [0-7] [0-7]?
      | [89]
      | '\r' '\n' | '\r' | '\n'
      | '\u2028' | '\u2029'
      | ~['"\\bfnrtvxu0-9\r\n\u2028\u2029]
      )
    ;
STRING
    : '"'  ( ~["\\\r\n] | ESC_SEQ )* '"'
    | '\'' ( ~['\\\r\n] | ESC_SEQ )* '\''
    ;
UNTERMINATED_STRING
    : '"'  ( ~["\\\r\n] | ESC_SEQ )* '\\'?
    | '\'' ( ~['\\\r\n] | ESC_SEQ )* '\\'?
    ;
INVALID_ESCAPE
    : '"'  ( ~["\\\r\n] | ESC_SEQ )* '\\' [xu] ( ~["\\\r\n] | '\\' ( '\r\n' | . ) )* '"'?
    | '\'' ( ~['\\\r\n] | ESC_SEQ )* '\\' [xu] ( ~['\\\r\n] | '\\' ( '\r\n' | . ) )* '\''?
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;
UNTERMINATED_BLOCK_COMMENT
    : '/*' ( ~[*] | '*'+ ~[/] )* '*'* EOF
    ;

OPERATOR
    : '>>>='
    | '**='
    | '==='
    | '!=='
    | '>>>'
    | '<<='
    | '>>='
    | '&&='
    | '||='
    | '??='
    | '...'
    | '=='
    | '!='
    | '<='
    | '>='
    | '++'
    | '--'
    | '&&'
    | '||'
    | '??'
    | '?.'
    | '?'
    | '<<'
    | '>>'
    | '**'
    | '+='
    | '-='
    | '*='
    | '/='
    | '%='
    | '&='
    | '|='
    | '^='
    | '=>'
    | '+'
    | '-'
    | '*'
    | '/'
    | '%'
    | '='
    | '>'
    | '<'
    | '!'
    | '~'
    | '&'
    | '|'
    | '^'
    ;

PUNCTUATION
    : '('
    | ')'
    | '['
    | ']'
    | ';'
    | ','
    | '.'
    | ':'
    ;


LBRACE : '{' -> type(PUNCTUATION), pushMode(DEFAULT_MODE) ;
RBRACE : '}' { if (!_modeStack.isEmpty()) popMode(); } -> type(PUNCTUATION) ;


TPL_START : '`' -> more, pushMode(TPL_MODE) ;

WS: [ \t\r\n]+ -> skip;

mode TPL_MODE;

TPL_ESC   : '\\' . -> more ;
TPL_SUBST : '${'   -> type(TEMPLATE), pushMode(DEFAULT_MODE) ;
TPL_END   : '`'    -> type(TEMPLATE), popMode ;
TPL_EOF   : EOF    -> type(UNTERMINATED_TEMPLATE), popMode ;
TPL_CHAR  : .      -> more ;