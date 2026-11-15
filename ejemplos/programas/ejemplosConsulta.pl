%%Test de eqStr
mismoTexto(X,Y):-eqStr(X,Y).

%%Test len con asignacion mediante =.
longitud(T,L):-L=len(T).

%%Test igualdad
identidad(X,Y):-X=Y.

%%Test variable anonima
padre(daniel,ana).
padre(daniel,pedro).

%%Test anidados
resultado(X):-X=add(2,add(6,1)).
calculo(X):-X=mult(add(3,2),sub(6,2)).

%%Test false true
usa_blanco().
usa_negro():-false.
usa_rojo():-true.
puede_usar_gris():-usa_blanco(),usa_negro().
puede_usar_rosa():-usa_blanco(),usa_rojo().

%%Test gt
mayor(X,Y):-gt(X,Y).

%%Test notEq
distintos(X,Y):-not(eq(X,Y)).