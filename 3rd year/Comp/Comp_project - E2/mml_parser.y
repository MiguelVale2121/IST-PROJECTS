%{
#include <stdio.h>
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <algorithm>
#include <memory>
#include <cstring>
#include <cdk/compiler.h>
#include <cdk/types/types.h>
#include ".auto/all_nodes.h"
#define LINE                         compiler->scanner()->lineno()
#define yylex()                      compiler->scanner()->scan()
#define yyerror(compiler, s)         compiler->scanner()->error(s)
//-- don't change *any* of these --- END!
%}

%parse-param {std::shared_ptr<cdk::compiler> compiler}

%union {
  //--- don't change *any* of these: if you do, you'll break the compiler.
  YYSTYPE() : type(cdk::primitive_type::create(0, cdk::TYPE_VOID)) {}
  ~YYSTYPE() {}
  YYSTYPE(const YYSTYPE &other) { *this = other; }
  YYSTYPE& operator=(const YYSTYPE &other) { type = other.type; return *this; }

  std::shared_ptr<cdk::basic_type> type;        /* expression type */
  //-- don't change *any* of these --- END!

  int                  i;	/* integer value */
  double               d;
  std::string          *s;	/* symbol name or string literal */
  cdk::basic_node      *node;	/* node pointer */
  mml::declaration_node *decl_node; /* variable declaration node */
  cdk::sequence_node   *sequence;
  cdk::expression_node *expression; /* expression nodes */
  cdk::lvalue_node     *lvalue;
  std::shared_ptr<cdk::basic_type> t;

  std::vector<std::shared_ptr<cdk::basic_type>> *types;

  mml::block_node *block;
};

%token <i> tINTEGER
%token <d> tREAL
%token <s> tIDENTIFIER tSTRING
%token <expression> tNULL
%token tINTD tREALD tSTRINGD tAUTO tVOIDD
%token tWHILE tIF tPRINT tPRINTLN tREAD tBEGIN tEND tRETURN tSIZEOF tNEXT tSTOP
%token tPUBLIC tFORWARD tFOREIGN

%nonassoc tIFX
%nonassoc tIF
%nonassoc tELIF
%nonassoc tELSE
%nonassoc '?'

%right '='
%left tAND tOR
%left tGE tLE tEQ tNE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc tUNARY

%type <node> stmt program
%type <s> string
%type <node> top_var_decl block_var_decl fdecl cond_instr elif_instr loop_instr func_arg
%type <decl_node> var_decl
%type <sequence> statements exprs file fdecls block_var_decls func_args
%type <expression> expr
%type <lvalue> lval
%type <t> type func_type
%type <block> block block_instr

%type <types> types

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

file : fdecls  { compiler->ast(new mml::program_node(LINE, $1)); }
     ;

fdecls :        fdecl  { $$ = new cdk::sequence_node(LINE, $1); }
       | fdecls fdecl  { $$ = new cdk::sequence_node(LINE, $2, $1); }
       ;

fdecl : top_var_decl ';' { $$ = $1; }
      | program    { $$ = $1; }
      ;

program	: tBEGIN block_instr tEND { $$ = $2; }
	     ;

/* Top vars can use qualifiers */
top_var_decl : tPUBLIC var_decl                   { $$ = new mml::declaration_node(LINE, false, false, true, $2->isAuto(), $2->type(), $2->identifier(), $2->expression()); }
             | tPUBLIC tIDENTIFIER '=' expr       { $$ = new mml::declaration_node(LINE, false, false, false, false, cdk::primitive_type::create(0, cdk::typename_type::TYPE_UNSPEC), *$2, $4); delete $2; }
             | tFORWARD var_decl                  { $$ = new mml::declaration_node(LINE, true, false, false, $2->isAuto(), $2->type(), $2->identifier(), $2->expression()); }
             | tFOREIGN var_decl                  { $$ = new mml::declaration_node(LINE, false, true, false, $2->isAuto(), $2->type(), $2->identifier(), $2->expression()); }
             | var_decl                           { $$ = $1; }
             ;

/* Base variable declarations */
var_decl : type tIDENTIFIER           { $$ = new mml::declaration_node(LINE, false, false, false, false, $1, *$2, nullptr); delete $2; }
         | type tIDENTIFIER '=' expr  { $$ = new mml::declaration_node(LINE, false, false, false, false, $1, *$2, $4); delete $2; }
         | tAUTO tIDENTIFIER '=' expr { $$ = new mml::declaration_node(LINE, false, false, false, true, cdk::primitive_type::create(0, cdk::typename_type::TYPE_UNSPEC), *$2, $4); delete $2; }
         ;

types  : type                         { $$ = new std::vector<std::shared_ptr<cdk::basic_type>>(); $$->push_back($1); }
       | types ',' type               { $$ = $1; $$->push_back($3); }
       | %empty                       { $$ = new std::vector<std::shared_ptr<cdk::basic_type>>(); }
       ;

type   : tINTD                        { $$ = cdk::primitive_type::create(4, cdk::typename_type::TYPE_INT); }
       | tREALD                       { $$ = cdk::primitive_type::create(8, cdk::typename_type::TYPE_DOUBLE); }
       | tSTRINGD                     { $$ = cdk::primitive_type::create(4, cdk::typename_type::TYPE_STRING); }
       | tVOIDD                       { $$ = cdk::primitive_type::create(2, cdk::typename_type::TYPE_VOID); }
       | '[' type ']'                 { $$ = cdk::reference_type::create(4, std::shared_ptr<cdk::basic_type>($2)); }
       | func_type                    { $$ = $1; }
       ;

func_type : type '<' types '>'    { auto output = new std::vector<std::shared_ptr<cdk::basic_type>>(); output->push_back($1); $$ = cdk::functional_type::create(*$3, *output); }
          ;



func_arg : type tIDENTIFIER           { $$ = new mml::declaration_node(LINE, false, false, false, false, $1, *$2, nullptr); delete $2; }
         ;

func_args : func_arg               { $$ = new cdk::sequence_node(LINE, $1); }
          | func_args ',' func_arg { $$ = new cdk::sequence_node(LINE, $3, $1); }
          | %empty                 { $$ = new cdk::sequence_node(LINE); }
          ;

block : '{' block_instr '}'   { $$ = $2; }
      ;

block_instr : block_var_decls statements { $$ = new mml::block_node(LINE, $1, $2); }
            | statements                 { $$ = new mml::block_node(LINE, nullptr, $1); }
            | block_var_decls            { $$ = new mml::block_node(LINE, $1, nullptr); }
            | %empty                     { $$ = new mml::block_node(LINE, nullptr, nullptr); }
            ;

block_var_decls : block_var_decl  { $$ = new cdk::sequence_node(LINE, $1); }
                | block_var_decls block_var_decl  { $$ = new cdk::sequence_node(LINE, $2, $1); }
                ;

block_var_decl : var_decl ';' { $$ = $1; }
               ;

statements : stmt	           { $$ = new cdk::sequence_node(LINE, $1); }
           | statements stmt   { $$ = new cdk::sequence_node(LINE, $2, $1); }
	      ;

stmt : expr ';'                         { $$ = new mml::evaluation_node(LINE, $1); }
     | exprs tPRINTLN                   { $$ = new mml::print_node(LINE, true, $1); }
     | exprs tPRINT                     { $$ = new mml::print_node(LINE, false, $1); }
     | tREAD ';'                        { $$ = new mml::read_node(LINE); }
     | tRETURN ';'                      { $$ = new mml::return_node(LINE); }
     | tRETURN expr ';'                 { $$ = new mml::return_node(LINE, $2); }
     | tNEXT tINTEGER ';'               { $$ = new mml::next_node(LINE, $2);}
     | tNEXT ';'                        { $$ = new mml::next_node(LINE);}
     | tSTOP tINTEGER ';'               { $$ = new mml::stop_node(LINE, $2);}
     | tSTOP ';'                        { $$ = new mml::stop_node(LINE);}
     | cond_instr             { $$ = $1; }
     | loop_instr             { $$ = $1; }
     | block                  { $$ = $1; }
     ;

loop_instr : tWHILE '(' expr ')' block   { $$ = new mml::while_node(LINE, $3, $5); }
           ;

cond_instr : tIF '(' expr ')' stmt %prec tIFX       { $$ = new mml::if_node(LINE, $3, $5); }
           | tIF '(' expr ')' stmt elif_instr       { $$ = new mml::if_else_node(LINE, $3, $5, $6); }
           ;

elif_instr : tELSE stmt                         { $$ = $2; }
           | tELIF '(' expr ')' stmt %prec tIFX { $$ = new mml::if_node(LINE, $3, $5); }
           | tELIF '(' expr ')' stmt elif_instr { $$ = new mml::if_else_node(LINE, $3, $5, $6); }
           ;

exprs : expr           { $$ = new cdk::sequence_node(LINE, $1); }
      | exprs ',' expr { $$ = new cdk::sequence_node(LINE, $3, $1); }
      | %empty         { $$ = new cdk::sequence_node(LINE); }
      ;

expr : tINTEGER                   { $$ = new cdk::integer_node(LINE, $1); }
     | tREAL                      { $$ = new cdk::double_node(LINE, $1);  }
	| string                     { $$ = new cdk::string_node(LINE, *$1); delete $1; }
     | tNULL                      { $$ = new mml::null_node(LINE); }
     | '+' expr %prec tUNARY      { $$ = new mml::identity_node(LINE, $2); }
     | '-' expr %prec tUNARY      { $$ = new cdk::neg_node(LINE, $2);     }
     | '~' expr %prec tUNARY      { $$ = new cdk::not_node(LINE, $2);     }
     | expr '+' expr	         { $$ = new cdk::add_node(LINE, $1, $3); }
     | expr '-' expr	         { $$ = new cdk::sub_node(LINE, $1, $3); }
     | expr '*' expr	         { $$ = new cdk::mul_node(LINE, $1, $3); }
     | expr '/' expr	         { $$ = new cdk::div_node(LINE, $1, $3); }
     | expr '%' expr	         { $$ = new cdk::mod_node(LINE, $1, $3); }
     | expr '<' expr	         { $$ = new cdk::lt_node(LINE, $1, $3); }
     | expr '>' expr	         { $$ = new cdk::gt_node(LINE, $1, $3); }
     | expr tGE expr	         { $$ = new cdk::ge_node(LINE, $1, $3); }
     | expr tLE expr              { $$ = new cdk::le_node(LINE, $1, $3); }
     | expr tNE expr	         { $$ = new cdk::ne_node(LINE, $1, $3); }
     | expr tEQ expr	         { $$ = new cdk::eq_node(LINE, $1, $3); }
     | expr tOR expr              { $$ = new cdk::or_node(LINE, $1, $3);  }
     | expr tAND expr             { $$ = new cdk::and_node(LINE, $1, $3); }
     | tSIZEOF '(' expr ')'       { $$ = new mml::sizeof_node(LINE, $3); }
     | '(' func_args ')' '-' '>' type block { $$ = new mml::function_node(LINE, $2, $6, $7);}
     | '(' expr ')'               { $$ = $2; }
     | '[' expr ']'               { $$ = new mml::memory_alloc_node(LINE, $2); }
     | lval '(' exprs ')'         { $$ = new mml::function_call_node(LINE, new cdk::rvalue_node(LINE, $1), $3); }
     | '(' expr ')' '(' exprs ')' { $$ = new mml::function_call_node(LINE, $2, $5); }
     | '@' '(' exprs ')'          { $$ = new mml::function_call_node(LINE,nullptr, $3);}
     | lval                       { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
     | lval '=' expr              { $$ = new cdk::assignment_node(LINE, $1, $3); }
     | lval '?'                   { $$ = new mml::address_of_node(LINE,$1);}
     ;

lval : tIDENTIFIER             { $$ = new cdk::variable_node(LINE, $1); }
     | lval '[' expr ']'       { $$ = new mml::indexing_ptr_node(LINE, new cdk::rvalue_node(LINE, $1), $3); }
     | '(' expr ')' '[' expr ']' { $$ = new mml::indexing_ptr_node(LINE, $2, $5); }
     ;

string : tSTRING              { $$ = $1; }
       | string tSTRING       { $$ = new std::string(*$1 + *$2); delete $1; delete $2; }
       ;

%%
