grammar UnsupportedLeftRecursive;

End : 'e';
WhiteSpace: [ \t\n\r]+ -> skip;

S : A | End;
A : B | End;
B : S | End;