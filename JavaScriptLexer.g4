lexer grammar JavaScriptLexer;

IF : 'if';
LET : 'let';
NUMBER : [0-9]+;
IDENTIFIER : [a-zA-Z]+;
WS : [ \t\r\n]+ -> skip;