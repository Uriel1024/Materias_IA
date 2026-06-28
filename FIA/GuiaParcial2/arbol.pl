/*hechos */ 

es_hombre(abraham).
es_hombre(clancy).
es_hombre(herb).
es_hombre(homero).
es_hombre(bart).

es_mujer(mona).
es_mujer(jackie).
es_mujer(marge).
es_mujer(patty).
es_mujer(selma).
es_mujer(lisa).
es_mujer(maggie).
es_mujer(ling).

/*relaciones de progenitor */

es_progenitor(abraham,homero).
es_progenitor(abraham,herb).
es_progenitor(mona,homero).

es_progenitor(clancy,marge).
es_progenitor(clancy,patty).
es_progenitor(clancy,selma).

es_progenitor(jackie,marge).
es_progenitor(jackie,patty).
es_progenitor(jackie,selma).

es_progenitor(homero,bart).
es_progenitor(homero,lisa).
es_progenitor(homero,maggie).

es_progenitor(marge,bart).
es_progenitor(marge,lisa).
es_progenitor(marge,maggie).

es_progenitor(selma,ling).


/*relaciones de matrimonio */

es_matrimonio(clancy,jackie).
es_matrimonio(homero,marge).

%Reglas auxiliares

es_padre(X,Y):- es_hombre(X), es_progenitor(X,Y).
es_madre(X,Y):- es_mujer(X), es_progenitor(X,Y).

es_hermano(X,Y):- 
	es_progenitor(P,Y),
	es_progenitor(P,Y),
	X \= Y, 
	es_hombre(X).


es_hermana(X,Y):- 
	es_progenitor(P,Y),
	es_progenitor(P,Y),
	X \= Y, 
	es_mujer(X).

/*reglas */

es_tio(Tio,Sobrino) :- 
	(es_hermano(Tio,Padre), es_progenitor(Padre,Sobrino)); 
	(es_hermano(Tio,Madre), es_progenitor(Madre,Sobrino)).

es_tia(Tia,Sobrino) :-
	(es_hermana(Tia,Padre), es_progenitor(Padre,Sobrino));
	(es_hermano(Tia,Madre), es_progenitor(Madre,Sobrino)).

es_primo(Primo, Persona) :- 
	es_progenitor(P1, Primo),
	es_progenitor(P2,Persona),
	es_hermano(P1,P2),
	es_hombre(Primo).

es_prima(Prima, Persona) :- 
	es_progenitor(P1, Prima),
	es_progenitor(P2,Persona),
	es_hermano(P1,P2),
	es_mujer(Prima).


%Reglas extra para consuegro y concunio

es_consuegro(X, Y) :- 
	es_progenitor(X, Hijo1), 
	es_progenitor(Y, Hijo2),
	es_matrimonio(Hijo1, Hijo2),
	X \= Y. 



es_concuno(X,Y):- 
	es_matrimonio(X, Pareja1),
	es_matrimonio(Y, Pareja2),
	(es_hermana(pareja1,pareja2) ; es_hermano(pareja1,pareja2) ),
	x \= y.