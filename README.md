# Calculator

a java-implemented recursive-descent interpreter

only support `arithmetic` and `assignment` operations

# language

\<calculation\> = \<expression\> | "define" \<identifier\> "=" \<expression\>
\<identifier\> = \<alphanumericstring\>
\<alphanumericstring\> = [\<alphastring\>{\<digitstring\>}]
\<alphastring\> = [\<letter\>]
\<letter\> = 'a'|…|'z'| 'A'|…|'Z'
\<digitstring\> = [\<digit\>]
\<digit\> = '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
\<number\> = {'+'|'-'}\<digitstring\>{'.'\<digitsrting\>}
\<expression\> = \<term\>
\<expression\> = \<term\> { '+' |'-' \<term\>}
\<term\> = \<factor\>
\<term\> = \<factor\> {'*' |'/' \<factor\>}
\<factor\> = \<identifier\>
\<factor\> = \<number\>
\<factor\> = \<identifier\>|\<number\> '^' \<identifier\>|\<number\>
\<factor\> ='(' \<expression\>')' { '^' \<identifier\>|\<number\>}
\<factor\> ='(' \<expression\>')' '^' '(' \<expression\> ')'