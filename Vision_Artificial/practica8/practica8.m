%practica 8
clc
clear
close all
format rational

function [precision, clasificacion_correcta] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl, modelo)

medias = cell(1, num_cl);
if modelo == 2
    covarianzas = cell(1, num_cl);
end

for i = 1:num_cl

    datos_clase = X_entrenamiento(Y_entrenamiento == i, :);

    medias{i} = mean(datos_clase, 1)'; % Centroide D x 1
    
    if modelo == 2

        covarianzas{i} = cov(datos_clase); % Matriz D x D
   
        if det(covarianzas{i}) < 1e-6
            covarianzas{i} = covarianzas{i} + 1e-6 * eye(size(covarianzas{i}, 1));
        end
    end
end
N_test = size(X_test, 1);
Y_predicha = zeros(N_test, 1);

for k = 1:N_test
    vector_prueba = X_test(k, :)'; % Punto de prueba D x 1
    distancias = zeros(num_cl, 1);
    
    for i = 1:num_cl
        media_clase = medias{i};
        
        if modelo == 1 
            distancias(i) = norm(vector_prueba - media_clase);
            
        elseif modelo == 2 
            matriz_cov = covarianzas{i};
            diff_vector = vector_prueba - media_clase;

            sigma_inv = inv(matriz_cov); 
            
            distancia_cad = diff_vector' * sigma_inv * diff_vector;
            
            if distancia_cad < 0
                distancias(i) = 0;
            else
                distancias(i) = sqrt(distancia_cad);
            end
        end
    end
    
    % La clase predicha es la que tiene la min distancia
    [~, clase_pred] = min(distancias);
    Y_predicha(k) = clase_pred;
end

clasificados_correctamente = sum(Y_predicha == Y_test);
precision = clasificados_correctamente / N_test;

% Para el caso de LOO (un solo punto de prueba)
if N_test == 1
    clasificacion_correcta = (Y_predicha == Y_test);
else
    % Se asume que la función se usa para calcular la precisión total en 100% y CV
    clasificacion_correcta = -1; % Valor por defecto
end

end


% Solicitar al usuario que ingrese el número de clases
while true
    fprintf('Ingresa el numero de clases a generar: ')
    num_cl = input('');
    if (num_cl <= 0 || num_cl > 10)
        disp('Ingresa un número entre 1 y 9.')
    else
        break
    end
end




%matriz para guardar las clases 
matrices = cell(1,num_cl);


while true
    fprintf('Ingresa el numero de objetos por clase: ')
    Num_puntos = input('');
    if Num_puntos <= 0
        disp('El numero de objetos debe ser mayor que 0.')
    else
        break
    end
end


% Crear clases
img=imread('prueba.jpg');
figure(1)
imshow(img)
title("Practica 8")
leyendas = cell(1,num_cl);


for i = 1:num_cl    
    % SELECCIONAR CON EL MOUSE LOS LIMITES DE LA VENTANA
    [x,y]=ginput(2);

    %CALCULAR LAS DIMENSIONES DEL RECTANGULO
    x_min=min(x);
    y_min=min(y);
    ancho=abs(x(2)-x(1));
    alto=abs(y(2)-y(1));

    %dibujando la ventana
    hold on
    rectangle('Position',[x_min y_min,ancho alto],'LineWidth',2)

    disp('ventana dibujada con éxito')

    %generando los puntos sobre el rectángulo
    x_rand= x_min + ancho * rand(1,Num_puntos);
    y_rand= y_min + alto * rand(1,Num_puntos);

    matrices{i} = [impixel(img, x_rand, y_rand)]';
    leyendas{i} = ['Clase ' num2str(i)];

    %ploteando los puntos
    hold on
    p = plot(x_rand(:),y_rand(:),'o','MarkerSize',10,'MarkerEdgeColor','auto');
    [p.MarkerFaceColor] = deal(p.Color);
    %hold off
end

%para almacenar los datos de cada elemento por clase
X_todos = []; 
Y_etiquetas = []; 
for i = 1:num_cl
    X_todos = [X_todos; matrices{i}'];
    Y_etiquetas = [Y_etiquetas; i*ones(size(matrices{i},2), 1)];
end

disp('Primer Metodo: RECONSTRUCCIÓN (100% de los miembros)');

[precision_100] = clasificar_y_evaluar(X_todos, Y_etiquetas, X_todos, Y_etiquetas, num_cl, 1);
fprintf('Precision usando distancia euclidiana (100%%): %.4f \n', precision_100);

[precision_100] = clasificar_y_evaluar(X_todos, Y_etiquetas, X_todos, Y_etiquetas, num_cl, 2);

fprintf('Precision usando mohalanobis (100%%): %.4f \n', precision_100);

fprintf('\n\n\nSegundo metodo 50/50 Cross-Validation \n')
X_entrenamiento = [];
Y_entrenamiento = [];
X_test = [];
Y_test = [];


for i = 1:num_cl
    clase_data = matrices{i}';
    N_clase = size(clase_data,1);
    
    % Permutar los índices para una selección aleatoria
    indices = randperm(N_clase);
    mitad = floor(N_clase / 2);
    
    % Datos de entrenamiento
    X_entrenamiento = [X_entrenamiento; clase_data(indices(1:mitad),:)];
    Y_entrenamiento = [Y_entrenamiento; i*ones(mitad,1)]; 
    
    % Datos de prueba (el resto)
    X_test = [X_test; clase_data(indices(mitad+1:end),:)];
    Y_test = [Y_test; i*ones(N_clase - mitad, 1)]; 
end



[precision_cv] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl, 1);
fprintf('Precision de 50/50 Cross Validation usando dist euclidiana %.2f', precision_cv)

[precision_cv] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl, 2);
fprintf('\nPrecision de 50/50 Cross Validation usando mohalanobis %.2f \n\n', precision_cv)




disp('Tercer metodo leav One out LOO ');

num_total_puntos = size(X_todos,1);

num_clasificados_bien = 0;

% Iteramos sobre cada miembro de la clase para usarlo como punto de prueba
for j = 1:num_total_puntos
    % Conjunto de Prueba: El punto 'j'
    X_test_loo = X_todos(j, :);
    Y_test_loo = Y_etiquetas(j);
   
    indices_entrenamiento = [1:j-1, j+1:num_total_puntos];
    X_entrenamiento_loo = X_todos(indices_entrenamiento, :);
    Y_entrenamiento_loo = Y_etiquetas(indices_entrenamiento);
 
    [precision_punto, ~] = clasificar_y_evaluar(X_entrenamiento_loo, Y_entrenamiento_loo, X_test_loo, Y_test_loo, num_cl, 1);
    
    if precision_punto == 1 % 1 si se clasificó correctamente, 0 si no
        num_clasificados_bien = num_clasificados_bien + 1;
    end
end
precision_loo = num_clasificados_bien / num_total_puntos;
fprintf('\n\nPresicion de LOO usando dist euclidiana: %.2f', precision_loo)


%para evitar guardar la basura del entrenamiento pasado
num_total_puntos = size(X_todos,1);

num_clasificados_bien = 0;



%lo mismo que arriba pero para mohalanobis xd 
for j = 1:num_total_puntos
    % Conjunto de Prueba: El punto 'j'
    X_test_loo = X_todos(j, :);
    Y_test_loo = Y_etiquetas(j);
    
    % Todos los otros puntos 
    indices_entrenamiento = [1:j-1, j+1:num_total_puntos];
    X_entrenamiento_loo = X_todos(indices_entrenamiento, :);
    Y_entrenamiento_loo = Y_etiquetas(indices_entrenamiento);
    
    % Clasificar y evaluar este punto
    % La función clasificar_y_evaluar funciona para un solo punto
    [precision_punto, ~] = clasificar_y_evaluar(X_entrenamiento_loo, Y_entrenamiento_loo, X_test_loo, Y_test_loo, num_cl, 2);
    
    if precision_punto == 1 % 1 si se clasificó correctamente, 0 si no
        num_clasificados_bien = num_clasificados_bien + 1;
    end
end
precision_loo = num_clasificados_bien / num_total_puntos;
fprintf('\nPresicion de LOO usando mohalanobis: %.2f', precision_loo)


fprintf('\n\nFin de programa, ahi nos vemos. ');


