:- consult('knowledge.pl').

% --- RELACIONES DE JERARQUÍA ---

subc(C1, C2) :- frame(C1, subclase_de(C2), _, _).
subc(C1, C2) :- frame(C1, subclase_de(C3), _, _), subc(C3, C2).

subclase(X) :- frame(X, subclase_de(_), _, _).

% --- CONSULTAS DE PROPIEDADES HEREDADAS ---

propiedadesc(top, []) :- !.
propiedadesc(X, Z) :-
    frame(X, subclase_de(Y), propiedades(P), _),
    propiedadesc(Y, P1),
    append(P1, P, W),
    list_to_set(W, Z).

% --- CONSULTAS DE CLASES ---

clases(L) :- findall(X, frame(X, subclase_de(_), propiedades(_), descripcion(_)), L).

subclases_de(X, L) :- findall(C1, subc(C1, X), L).

superclases_de(X, L) :- findall(C1, subc(X, C1), S), reverse(S, L).

% --- CONSULTA DE OBJETOS POR PROPIEDAD ---

tiene_propiedad(P, Objs) :-
    findall(Clase,
        (frame(Clase, _, propiedades(Props), _),
         member(P, Props)),
        Clases),
    sort(Clases, Objs).

% --- CONSULTA DE TODAS LAS PROPIEDADES ---

todas_propiedades(Unicas) :-
    findall(Ps, frame(_, _, propiedades(Ps), _), PropList),
    flatten(PropList, All),
    sort(All, Unicas).

% --- DESCRIPCIÓN DE UNA CLASE ---

obtiene_descripcion(C, D) :- frame(C, _, _, descripcion(D)).

% --- CONSULTA POR MÚLTIPLES PROPIEDADES ---

consulta_por_propiedades(Ps, ClasesUnicas) :-
    consulta(Ps, Clases),
    sort(Clases, ClasesUnicas).

consulta([], []).
consulta([H | T], R) :-
    consulta(T, R1),
    tiene_propiedad(H, R2),
    append(R1, R2, R).

% --- DETERMINA SI UNA CLASE ES HOJA (no tiene subclases) ---

es_hoja(C) :-
    subclases_de(C, L),
    L = [].
