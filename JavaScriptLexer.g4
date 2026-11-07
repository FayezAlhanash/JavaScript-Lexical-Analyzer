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

fragment CODE_POINT
    : '0'* ( HEX_DIGIT HEX_DIGIT? HEX_DIGIT? HEX_DIGIT? HEX_DIGIT?
           | '10' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
    ;
fragment ESC_SEQ
    : '\\'
      ( ['"\\bfnrtv]                       // \'  \"  \\  \b  \f  \n  \r  \t  \v
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
fragment TPL_BODY
    : ( '\\' .
      | '$'* TPL_SUBST
      | ~[`\\$]
      | '$'+ ~[`\\${]
      | '$'+ '\\' .
      )* '$'*
    ;
fragment TPL_ANY   : ( '\\' . | TPL_SUBST | ~[`\\] )* ;

fragment TPL_SUBST : '${' TPL_CODE* '}' ;
fragment TPL_CODE
    : '`' TPL_BODY '`'
    | '"'  ( ~["\\\r\n] | '\\' . )* '"'
    | '\'' ( ~['\\\r\n] | '\\' . )* '\''
    | '{' TPL_CODE* '}'
    | ~[{}`"']
    ;
TEMPLATE              : '`' TPL_BODY '`' ;
INVALID_TEMPLATE      : '`' TPL_ANY '`' ;
UNTERMINATED_TEMPLATE : '`' TPL_ANY '\\'? ;
LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
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
        | '{'
        | '}'
        | '['
        | ']'
        | ';'
        | ','
        | '.'
        | ':'
        ;
WS: [ \t\r\n]+ -> skip;
