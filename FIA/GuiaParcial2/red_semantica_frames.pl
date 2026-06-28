%Clases generales

frame(mammal,[
	needs(food),
	can(move)
]).

frame(cattle,[
	isa(mammal),
	are(herbivores)
]).

frame(human_being, [
	isa(mammal),
	has(head)
]).

%Instancias

frame(daisy,[
	instancia_of(cattle),
	has(calf)
]).

frame(fred_jones,[
	instancia_of(human_being),
	hair_colour(blonde)
]).

frame(john_smith, [
	instancia_of(human_being),
	hair_colour(black)
]).