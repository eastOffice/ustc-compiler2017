parser grammar C1Parser;
options { tokenVocab = C1Lexer; }

compilationUnit:
    (decl | funcdef)
    |compilationUnit (decl | funcdef);
decl: 
    constdecl | vardecl;
constdecl: 
    Const Int constdef (Comma constdef)* SemiColon;
constdef: 
    Identifier Assign exp 
    |Identifier LeftBracket exp? RightBracket Assign LeftBrace exp (Comma exp)* RightBrace;
vardecl: 
    Int vardef (Comma vardef)* SemiColon;
vardef: 
    Identifier | Identifier LeftBracket exp RightBracket
    |Identifier Assign exp
    |Identifier LeftBracket exp? RightBracket Assign LeftBrace exp (Comma exp)* RightBrace;
funcdef: 
    Void Identifier LeftParen RightParen block;
block: 
    LeftBrace blockitem* RightBrace;
blockitem:
    decl | stmt;
stmt: 
    lval Assign exp SemiColon
    |Identifier LeftParen RightParen SemiColon
    |block
    |If LeftParen cond RightParen stmt (Else stmt)?
    |While LeftParen cond RightParen stmt
    |SemiColon;
lval: 
    Identifier | Identifier LeftBracket exp RightBracket;
relop:
    Equal | NonEqual | Less | Greater | LessEqual | GreaterEqual;
binop:
    Plus | Minus | Multiply | Divide | Modulo;
unaryop:
    Plus | Minus;
cond: 
    exp relop exp;
exp:
    exp binop exp
    | unaryop exp
    | LeftParen exp RightParen
    | lval
    | Number
;
