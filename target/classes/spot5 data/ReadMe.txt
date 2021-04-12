
Warning!  Original  SPOT5 instances  number  1401-1506  have a  memory
limitation (n-ary constraint) which is ignored when translating in ds,
cp, and wcsp formats.

----
---- Original README file from  ftp.cert.fr/pub/lemaitre/LVCSP/Pbs/SPOT5.zip
----

This README file explains the `spot5' problems and their representation.

Look at the file "8.spot" for a small example (in the same directory,
and see below).

These   files   represent  some earth  observation   daily  management
problems, which have been  produced at the  CNES (French Space Agency)
by a simulator of the future order book of the satellite SPOT5.

The  management problems  to  be solved  can  be roughly  described as
follows (see [1] for more details):

 - given a set S of photographs which  can be taken  the next day from
at   least  one of   the    three instruments,  w.r.t. the   satellite
trajectory;  given,  for  each   photograph, a  weight  expressing its
importance;

 - given a set of imperative constraints:  non overlapping and minimal
transition   time  between two  successive    photographs on the  same
instrument,  limitation  on the instantaneous  data   flow through the
satellite telemetry and on the recording capacity on board;

 -  find  an admissible subset S'  of  S (imperative  constraints met)
which maximizes the sum of the weights of the photographs in S'.


These problems can  be casted as additive  CSPs with  valued variables
(where the objective is to produce a partial assignment of the problem
variables which satisfies all the imperative constraints and maximizes
the sum of the weights of the assigned variables) by:

 - associating  a variable v  with each photograph  p, and associating
with v a domain d to express the different ways of achieving p;

 -  translating  as imperative   constraints (binary and  ternary) the
constraints of non overlapping and minimal transition time between two
photographs   on the  same   instrument,  and of   limitation  on  the
instantaneous data flow;

 -  translating  as an n-ary  imperative  constraint the constraint of
limitation on the recording capacity;

The files suffixed by ".spot" are the result of a preprocessing, which
computes all the  variables with their associated  domain and all  the
binary and  ternary  imperative  constraints with   their  explicitely
defined associated  relation  (all  the forbidden tuples).    Only the
n-ary constraint  associated   with the  limitation on   the recording
capacity remains  implicitely  defined     (the sum of     the  memory
consumptions of the assigned  variables must be  less than or equal to
the memory  limitation).  Looking at them, you  can  forget what  they
represent and just consider them as additive CSPs.

The   used BNF-like syntax  can   be  described as follows  (something
following by an asterisk indicates zero or more  of them, something in
square brackets indicates zero  or one of  them and curly brackets are
used for grouping):

file ::=
   variables constraints


variables ::=
   number-of-variables {variable}*

number-of-variables ::= number \newline

variable ::=
   variable-ident variable-weight domain-size 
   {value-ident memory-consumption}* \newline

variable-ident ::= number

variable-weight ::= number

domain-size ::= number

value-ident ::= number

memory-consumption ::= number


constraints ::=
   explicitly-defined-constraints [implicitly-defined-constraints]

explicitly-defined-constraints ::=
   number-of-constraints {constraint}*

number-of-constraints ::=
   number \newline

constraint ::=
   arity {variable-ident}* {forbidden-tuple}* \newline

arity ::= number

forbidden-tuple ::=
   {value-ident}*

implicitly-defined-constraints ::=
   memory-limitation \newline

memory-limitation ::= number


For   example,  the  file "8.spot"   represents  a small  size problem
including 8 variables and 7 constraints, without memory limitation.

-----------------
8
0 1 3 1 0 2 0 3 0 
1 1 3 1 0 2 0 3 0 
2 1 3 1 0 2 0 3 0 
3 1 3 1 0 2 0 3 0 
4 2 1 13 0 
5 2 1 13 0 
6 2 1 13 0 
7 2 1 13 0 
7
2 1 0 3 3 2 2 1 1 
2 2 0 3 3 2 2 1 1 
2 3 0 3 3 2 2 1 1 
2 5 4 13 13 
2 5 6 13 13 
2 2 1 3 3 2 2 1 1 
2 3 1 3 3 2 2 1 1
-----------------
 
The first variable (line 2) has the following characteristics:
 - its ident is 0;
 - its weight is equal to 1;
 - its domain size is equal to 3;
 - its  possible values are 1, 2   and 3, all  of them  without memory
consumption.


The first constraint (line 11) has the following characteristics:
 - its arity is equal to 2;
 - it links the variables 1 and 0;
 - the forbidden pairs  of values are (3  3)  (2 2) (1 1)  (inequality
constraint).

The following table shows the results which have been obtained on some
of these problems, using an exact Branch-and-Bound-like method, called
"Russian Doll Search"  (see [2]).   

pb is the problem number,  n the number of variables,  e the number of
constraints, v  the optimal valuation (the maximum  of the sum  of the
weights of  the assigned variables) and  t the cpu  time in seconds to
get this result (to  get an optimal valuation  assignment and to prove
its optimality). Algorithms have been written in Common Lisp and tests
have  been  performed  with  the  CMUCL  implementation  on  a Sparc 5
workstation with 32Mb of memory).

--------------------------------
 pb  & n   &  e   & v     & t

 404 & 100 &  610 & 49    & 0.5
 408 & 199 & 2032 & 3082  & 14
 412 & 300 & 4048 & 16102 & 29
 414 & 364 & 9744 & 22120 & 86

 503 & 105 &  403 & 9096  & 2.5
 505 & 240 & 2002 & 13100 & 15
 507 & 311 & 5421 & 15137 & 55
 509 & 348 & 8276 & 19125 & 106
--------------------------------

Note that problems number 11 and 414 are the same.


[1]:  "Exact  and Approximate Methods  for the  Daily Management of an
Earth Observation Satellite", J.C.  Agnese, N.  Bataille, E.  Bensana,
D.  Blumstein and  G.  Verfaillie, Proc.  of the  5th ESA  Workshop on
Artificial  Intelligence and    Knowledge  Based Systems    for Space,
Noordwijk,              The              Netherlands,            1995,
ftp://ftp.cert.fr/pub/verfaillie/estec95.ps

[2]:  "Russian   Doll   Search for  Solving   Constraint  Optimization
Problems",  G.  Verfaillie, M.   Lemaitre and  T.  Schiex,   Proc.  of
AAAI-96,            Portland,           Oregon,       USA,       1996,
ftp://ftp.cert.fr/pub/verfaillie/rds-aaai96.ps

