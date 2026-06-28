:- dynamic si/1,no/1.

%Practica 6 

/**************************************************
Implementacion de un pequenio sistema eXperto en cerati
***************************************************/


main:-repeat,
      nl,nl,write('EXPERTO en Cerati'),nl,
      nl,write('Menu'),nl,
      nl,write('1 Consulta al experto'),
      nl,write('2 Salir'),
      nl,nl,write('Ingresa tu opcion:'),
      read(Opcion),nl,
      ( (Opcion=1,motor,fail);
    (Opcion=2,!)).

%Motor 

motor:-borraResp,(deduce(Objeto,_) ->
      (nl,write('Eres la cancion : '),write(Objeto),
       nl,writeln('\nLlegue a esa conclusion porque te identificas con las emociones/sentimientos: '), findall(X, si(X), L), writeln(L));
      (nl,write('\nNo eres ninguna cancion de Cerati :( '))).



satisface(Atributo,_) :-
   (si(Atributo)  -> true ;                %if   si(Atributo) then true
    (no(Atributo) -> fail ;        %else if no(Atributo) then fail
                     pregunta(Atributo))). %else pregunta(Atributo)

pregunta(A) :-
    write('Te identificas con las emociones/sentimientos '),
    write(A), write('?'),
    read(Resp),
    nl,
    ((Resp = s ; Resp = si)                 %if Resp = s or Resp = S then
      -> assert(si(A));                     %     assert(si(A))
         assert(no(A)), fail).          %else assert(no(A))


borraResp:-retractall(si(_)),retractall(no(_)).

%Base del conocimiento para canciones 


%album ahi vamos
deduce(adios,X) :- adios(X).
deduce(me_quedo_aqui,X) :- me_quedo_aqui(X).
deduce(lago_en_el_cielo,X) :- lago_en_el_cielo(X).
deduce(crimen,X) :- crimen(X).

%album amor amarillo
deduce(amor_amarillo,X) :- amor_amarillo(X).
deduce(te_llevo_para_que_me_lleves,X) :- te_llevo_para_que_me_lleves(X).

%album bocanada
deduce(tabu,X):- tabu(X).
deduce(engania,X):- engania(X).
deduce(bocanada,X):- bocanada(X).
deduce(puente,X):- puente(X).
deduce(rio_babel,X):- rio_babel(X).
deduce(beautiful,X):-  beautiful(X).
deduce(perdonar_es_divino,X):- perdonar_es_divino(X).
deduce(verbo_carne,X):- verbo_carne(X).
deduce(raiz,X):- raiz(X).
deduce(y_si_el_humo_esta_en_foco,X):- y_si_el_humo_esta_en_foco(X).
deduce(paseo_inmoral,X):- paseo_inmoral(X).
deduce(aqui_y_ahora_primeros_3min,X):- aqui_y_ahora_primeros_3min(X).
deduce(aqui_y_ahora_y_despues,X):- aqui_y_ahora_y_despues(X).
deduce(alma,X):- alma(X).
deduce(balsa,X):- balsa(X).

%Singles
deduce(tu_locura,X):- tu_locura(X).

%Album Fuerza natural
deduce(deja_vu,X):- deja_vu(X).
deduce(cactus,X):- cactus(X).


%Album siempre es hoy
deduce(karaoke,X):- karaoke(X).
deduce(fantasma,X):- fantasma(X).
deduce(vivo,X):- vivo(X).



adios(X):-
    satisface(melancolico_nostalgico,X),
    satisface(liberado_aceptado,X),
    satisface(solo_reflexivo,X).

me_quedo_aqui(X):- 
    satisface(determinado_obstinado,X),
    satisface(desafiante_rebelde,X),
    satisface(con_intencidad_emocional,X).

lago_en_el_cielo(X):- 
    satisface(eXtasis_trasendencia_emocional,X),
    satisface(vulnerabilidad_y_negociacion_afectiva,X),
    satisface(redencion_a_traves_del_amor,X),
    satisface(paciencia_y_conexion,X).

crimen(X):-
    satisface(obsesion_y_culpa,X),
    satisface(traicion_y_enganio,X),
    satisface(celos,X).

amor_amarillo(X):- 
    satisface(amor,X),
    satisface(conexon,X),
    satisface(vitalidad,X).

te_llevo_para_que_me_lleves(X):-
    satisface(amor,X),
    satisface(disposicion_entrega,X),
    satisface(reciproco,X).

tabu(X):-
    satisface(deseo_prohibido,X),
    satisface(sacrificio_valentia,X),
    satisface(trascendencia_renacimiento,X).

engania(X):-
    satisface(paranoia,X),
    satisface(decepcion,X),
    satisface(ruptura,X).

bocanada(X):-
    satisface(transicion,X),
    satisface(conexion,X),
    satisface(desvanecimiento_memorias,X).

puente(X):-
    satisface(conexion,X),
    satisface(fe_en_el_amor,X),
    satisface(comunicacion,X).

rio_babel(X):-
    satisface(comprension,X),
    satisface(fluido_libre,X).

beautiful(X):-
    satisface(construccion_de_identidad,X),
    satisface(paz_mental,X),
    satisface(perdida_y_liberacion,X).

perdonar_es_divino(X):-
    satisface(introspeccion_arrepentimiento,X),
    satisface(refleXion,X),
    satisface(superar_olvidar,X).

verbo_carne(X):-
    satisface(introspeccion,X),
    satisface(espiritualidad,X),
    satisface(culpa_y_redencion,X).

raiz(X):-
    satisface(amor_conexion,X),
    satisface(vinculo_profundo,X),
    satisface(conexion,X).

y_si_el_humo_esta_en_foco(X):-
    satisface(indeciso_ambiguo,X),
    satisface(desvaneserse,X).

paseo_inmoral(X):-
    satisface(desafiante,X),
    satisface(libertad_autonomia,X),
    satisface(busqueda_de_la_esencia,X).

aqui_y_ahora_primeros_3min(X):-
    satisface(pensante,X),
    satisface(refleXivo,X),
    satisface(cosmico,X).

aqui_y_ahora_y_despues(X):-
    satisface(pensando_en_el_futuro,X),
    satisface(estar_presente,X),
    satisface(refleXivo,X).

alma(X):-
    satisface(intimidad_emocional,X),
    satisface(conexion_con_la_vida,X),
    satisface(soniando_despierto,X).

balsa(X):-
    satisface(vulnerabilidad,X),
    satisface(dependencia_amorosa,X),
    satisface(fragil,X).

tu_locura(X):-
    satisface(normalura,X),
    satisface(conexion_profunda,X),
    satisface(empatia,X).

deja_vu(X):-
    satisface(haberlo_vivido_antes,X),
    satisface(ciclo_infinito,X),
    satisface(atrapado_en_un_suenio,X).

cactus(X):-
    satisface(busqueda_de_algo,X),
    satisface(refleXion,X),
    satisface(soledad,X).

karaoke(X):-
    satisface(traicion_y_enganio,X),
    satisface(transicion_y_olvido,X),
    satisface(tratar_de_cambiar_algo,X).

fantasma(X):-
    satisface(remordimiento,X),
    satisface(valle_emocional,X),
    satisface(lucha_interna,X).

vivo(X):-
    satisface(sentirse_muerto,X),
    satisface(sin_alma,X),
    satisface(sin_motivacion,X),
    satisface(roto,X).
