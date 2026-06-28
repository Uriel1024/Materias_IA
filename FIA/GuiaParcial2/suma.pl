%Suma de todos los numeros de una lista

%Condicion inicial, si la lisa esta vacia la suma es 0.
suma_lista([], 0).


suma_lista([X|Y], Suma) :-
    suma_lista(Y, Suma_restante),
    Suma is X + Suma_restante.