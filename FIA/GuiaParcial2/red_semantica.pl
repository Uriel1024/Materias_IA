%Relaciones is-a 

es_un(cattle,mammal).
es_un(mammal,animal).
es_un(human_being,mammal).

%Relaciones instance-of

instancia_de(daisy,cattle).
instancia_de(john_smith, human_being).
instancia_de(fred_jones,human_being).

%Relaciones has_a 

tiene(human_being,head).
tiene(daisy,calf).

%Relaciones needs

necesita(mammal, food).

%Relaciones can

puede(mammal,moverse).

%Caracteristicas 

Caracteristica(fred_jones,hair_color,blonde).
Caracteristica(john_smith,hair_color, black).