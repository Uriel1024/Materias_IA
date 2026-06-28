%Clase principal

frame(guitarra, subclase_de(top),
    propiedades([instrumento_de_cuerdas, tiene(cuerpo), tiene(mastil)]),
    descripcion('Instrumento de cuerda con cuerpo resonante y mástil')).

%Modelos de guitarra (subclases de guitarras)

frame(guitarra_semihueca, subclase_de(guitarra),
    propiedades([tiene(cuerpo_semihueco), usa(humbuckers)]),
    descripcion('Cuerpo parcialmente hueco, tono cálido y resonante')).

frame(guitarra_hueca, subclase_de(guitarra),
    propiedades([tiene(cuerpo_hueco), usa(humbuckers)]),
    descripcion('Cuerpo completamente hueco, ideal para jazz y blues')).

frame(guitarra_solida, subclase_de(guitarra),
    propiedades([tiene(cuerpo_sólido)]),
    descripcion('Cuerpo sólido sin cámaras acústicas, excelente sustain')).

%subclases de una guitarra sólida

frame(guitarra_strat, subclase_de(guitarra_solida),
    propiedades([usa(single_coils)]),
    descripcion('Stratocaster: pastillas single coil, tonos brillantes y limpios')).

frame(guitarra_les_paul, subclase_de(guitarra_solida),
    propiedades([usa(humbuckers)]),
    descripcion('Les Paul: grosor de tono, sustain largo y cuerpo pesado')).

frame(guitarra_sg, subclase_de(guitarra_solida),
    propiedades([usa(humbuckers), circuito(simple)]),
    descripcion('SG: más ligera que la Les Paul, acceso superior a trastes')).

frame(guitarra_mustang, subclase_de(guitarra_solida),
    propiedades([usa(single_coils), escala(corta)]),
    descripcion('Mustang: escala corta, sonido crujiente, rock alternativo')).

frame(guitarra_jaguar, subclase_de(guitarra_solida),
    propiedades([usa(single_coils), circuito(complejo)]),
    descripcion('Jaguar: circuitos complejos, tono brillante')).

frame(guitarra_meteora, subclase_de(guitarra_solida),
    propiedades([usa(humbuckers), circuito(complejo)]),
    descripcion('Meteora: diseño moderno con pastillas potentes')).

%subclases de cada tipo de modelo

frame(fender_strat_deluxe, subclase_de(guitarra_strat),
    propiedades([trastes(jumbo)]),
    descripcion('Fender Strat Deluxe con trastes jumbo para mayor comodidad')).

frame(gibson_les_paul_custom, subclase_de(guitarra_les_paul),
    propiedades([acabado(lujoso)]),
    descripcion('Les Paul Custom con acabado premium y electrónica mejorada')).

frame(epiphone_sg_special, subclase_de(guitarra_sg),
    propiedades([version(economica)]),
    descripcion('Epiphone SG Special: versión asequible de la SG clásica')).

frame(fender_mustang_pj, subclase_de(guitarra_mustang),
    propiedades([pastillas(pj)]),
    descripcion('Mustang PJ con combinación Precision + Jazz para versatilidad')).

frame(gretsch_white_falcon, subclase_de(guitarra_hueca),
    propiedades([estilo(vintage)]),
    descripcion('Gretsch White Falcon: icónica hueca de estilo vintage')).

frame(ibanez_artcore, subclase_de(guitarra_semihueca),
    propiedades([orientacion(jazz)]),
    descripcion('Ibanez Artcore: semihueca enfocada en jazz y tonos cálidos')).

frame(squier_jaguar_vintage, subclase_de(guitarra_jaguar),
    propiedades([reproduccion(vintage)]),
    descripcion('Squier Jaguar Vintage: reproducción fiel del modelo original')).

frame(fender_meteora_ultra, subclase_de(guitarra_meteora),
    propiedades([acabado(moderno)]),
    descripcion('Fender Meteora Ultra: ergonomía mejorada y estética moderna')).
