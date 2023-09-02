%option c++ prefix="mml_scanner_" outfile="mml_scanner.cpp"
%option stack noyywrap yylineno 8bit debug
%{ 
// make relevant includes before including the parser's tab file
#include <string>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "mml_parser.tab.h"

// don't change this
#define yyerror LexerError
%}
%x X_STRING X_COMMENT X_IGNORE
%%
  yydebug=1; set_debug(1);

"//".*                 ; /* comments */

"/*"                   yy_push_state(X_COMMENT);
<X_COMMENT>"*/"        yy_pop_state();
<X_COMMENT>"/*"        yy_push_state(X_COMMENT);
<X_COMMENT>.|\n         ; /* comments */

">="                   return tGE; 
"<="                   return tLE; 
"=="                   return tEQ; 
"!="                   return tNE; 

"&&"                   return tAND;
"||"                   return tOR;

"auto"                 return tAUTO;
"int"                  return tINTD;
"double"               return tREALD;
"string"               return tSTRINGD;
"void"                 return tVOIDD;
"null"                 return tNULL;

"public"               return tPUBLIC;
"forward"              return tFORWARD;
"foreign"              return tFOREIGN;

"while"                return tWHILE; 
"if"                   return tIF; 
"elif"                 return tELIF;
"else"                 return tELSE; 
"!"                    return tPRINT;
"!!"                   return tPRINTLN;
"read"                 return tREAD;
"return"               return tRETURN;
"next"                 return tNEXT;
"stop"                 return tSTOP;
"begin"                return tBEGIN; 
"end"                  return tEND; 
"sizeof"               return tSIZEOF;

[A-Za-z][A-Za-z0-9_]*  yylval.s = new std::string(yytext); return tIDENTIFIER;

\"                           yy_push_state(X_STRING); yylval.s = new std::string("");
<X_STRING>\"                 yy_pop_state(); return tSTRING;
<X_STRING>\\\"               *yylval.s += yytext + 1;
<X_STRING>\\\\               *yylval.s += yytext + 1;
<X_STRING>\\n                *yylval.s += '\n';
<X_STRING>\\r                *yylval.s += '\r';
<X_STRING>\\t                *yylval.s += '\t';
<X_STRING>\\0                yy_push_state(X_IGNORE);
<X_STRING>\\[0-7]{1,3}       *yylval.s += (char) strtol(yytext+1, NULL, 8);
<X_STRING>\0                 yyerror("nullbyte in string");
<X_STRING>.|\n               *yylval.s += yytext;

<X_IGNORE>\0                 yyerror("null byte in string");
<X_IGNORE>\"                 yy_pop_state(); yy_pop_state(); return tSTRING;
<X_IGNORE>.|\n               ; /* ignore everything after /\\0/ */

0[0-7]+                              yylval.i = strtol(yytext, nullptr, 8); return tINTEGER;
[0-9]+                               yylval.i = strtol(yytext, nullptr, 10); return tINTEGER;
[0-9]*\.[0-9]+([Ee][-+]?[0-9]+)?     yylval.d = std::stod(yytext, NULL); return tREAL;
[0-9]+\.[0-9]*([Ee][-+]?[0-9]+)?     yylval.d = std::stod(yytext, NULL); return tREAL;
[0-9]+([Ee][-+]?[0-9]+)              yylval.d = std::stod(yytext, NULL); return tREAL;

[-()<>=+?*/%;{}.~,@\[\]]       return *yytext;

[ \t\n]+               ; /* ignore whitespace */

.                      yyerror("Unknown character");

%%
