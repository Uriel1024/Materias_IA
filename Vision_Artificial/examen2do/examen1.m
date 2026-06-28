% práctica 8 - Generación Geométrica de Clase 1, Clase 2 y Clase 3
clc
clear
close all
format rational

% Función de Clasificación y Evaluación
function [precision, clasificacion_correcta] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl, modelo)
medias = cell(1, num_cl);
if modelo == 2
    covarianzas = cell(1, num_cl);
end

% 1. Entrenamiento: Calcular medias y covarianzas
for i = 1:num_cl
    datos_clase = X_entrenamiento(Y_entrenamiento == i, :);
    if isempty(datos_clase)
        warning('Clase %d no tiene datos en el conjunto de entrenamiento.', i);
        medias{i} = NaN(size(X_entrenamiento, 2), 1);
        if modelo == 2
             covarianzas{i} = NaN(size(X_entrenamiento, 2));
        end
        continue;
    end
    
    medias{i} = mean(datos_clase, 1)'; % Centroide D x 1
    
    if modelo == 2
        covarianzas{i} = cov(datos_clase); % Matriz D x D
        if det(covarianzas{i}) < 1e-9 
            covarianzas{i} = covarianzas{i} + 1e-6 * eye(size(covarianzas{i}, 1));
        end
    end
end

% 2. Clasificación: Predecir etiquetas
N_test = size(X_test, 1);
Y_predicha = zeros(N_test, 1);

for k = 1:N_test
    vector_prueba = X_test(k, :)';
    distancias = zeros(num_cl, 1);
    
    % Inicializar distancias de clases no entrenadas a Inf
    for idx_clase = 1:num_cl
        if isnan(medias{idx_clase}(1))
             distancias(idx_clase) = Inf;
        end
    end
    
    for i = 1:num_cl
        if isnan(medias{i}(1))
            continue;
        end
        
        media_clase = medias{i};
        
        if modelo == 1 % Distancia Euclidiana (Modelo 1)
            distancias(i) = norm(vector_prueba - media_clase);
            
        elseif modelo == 2 % Distancia de Mahalanobis (Modelo 2)
            matriz_cov = covarianzas{i};
            diff_vector = vector_prueba - media_clase;
            
            try
                sigma_inv = inv(matriz_cov); 
                distancia_cad = diff_vector' * sigma_inv * diff_vector;
                
                if distancia_cad < 0
                    distancias(i) = 0;
                else
                    distancias(i) = sqrt(distancia_cad);
                end
            catch
                distancias(i) = Inf; 
            end
        end
    end
    
    % La clase predicha es la que tiene la min distancia (ignorando Inf)
    [~, clase_pred] = min(distancias);
    Y_predicha(k) = clase_pred;
end

% 3. Evaluación
clasificados_correctamente = sum(Y_predicha == Y_test);
precision = clasificados_correctamente / N_test;

if N_test == 1
    clasificacion_correcta = (Y_predicha == Y_test);
else
    clasificacion_correcta = -1;
end
end

% --- PARÁMETROS GLOBALES ---
Num_puntos = 200; % Puntos por clase (C1, C2 y C3)

figure(1);
hold on;
axis equal;
title('Generación de Puntos Aleatorios - Clases Geométrica (C1, C2, C3)');
xlabel('Dimensión X');
ylabel('Dimensión Y');
xlim([0 100]);
ylim([0 100]);

% --- 1. Generación de Clase 1: Triángulo Equilátero hacia Arriba (Etiqueta 1) ---
v1_up = [20, 20];
v2_up = [50, 80];
v3_up = [80, 20];
vertices_clase1 = [v1_up; v2_up; v3_up];
X_clase1 = zeros(Num_puntos, 2);
count = 0;
while count < Num_puntos
    x_rand = 20 + 60 * rand();
    y_rand = 20 + 60 * rand();
    if inpolygon(x_rand, y_rand, vertices_clase1(:, 1), vertices_clase1(:, 2))
        count = count + 1;
        X_clase1(count, :) = [x_rand, y_rand];
    end
end
plot([vertices_clase1(:, 1); v1_up(1)], [vertices_clase1(:, 2); v1_up(2)], 'b', 'LineWidth', 2, 'DisplayName', 'Clase 1 (Límite)');
scatter(X_clase1(:, 1), X_clase1(:, 2), 20, 'b', 'filled', 'DisplayName', 'Clase 1 Puntos');
Y_clase1 = 1 * ones(Num_puntos, 1);

% --- 2. Generación de Clase 2: Triángulo Equilátero Invertido (Etiqueta 2) ---
v1_down = [20, 80];
v2_down = [50, 20];
v3_down = [80, 80];
vertices_clase2 = [v1_down; v2_down; v3_down];
X_clase2 = zeros(Num_puntos, 2);
count = 0;
while count < Num_puntos
    x_rand = 20 + 60 * rand(); 
    y_rand = 20 + 60 * rand();
    if inpolygon(x_rand, y_rand, vertices_clase2(:, 1), vertices_clase2(:, 2))
        count = count + 1;
        X_clase2(count, :) = [x_rand, y_rand];
    end
end
plot([vertices_clase2(:, 1); v1_down(1)], [vertices_clase2(:, 2); v1_down(2)], 'r-', 'LineWidth', 2, 'DisplayName', 'Clase 2 (Límite)');
scatter(X_clase2(:, 1), X_clase2(:, 2), 20, 'r', 'filled', 'DisplayName', 'Clase 2 Puntos');
Y_clase2 = 2 * ones(Num_puntos, 1);

centro_c3 = [90, 10]; 
radio_c3 = 8; 

X_clase3 = zeros(Num_puntos, 2);
count = 0;
while count < Num_puntos
    x_rand = centro_c3(1) - radio_c3 + 2 * radio_c3 * rand();
    y_rand = centro_c3(2) - radio_c3 + 2 * radio_c3 * rand();
    
    dist_al_centro = norm([x_rand, y_rand] - centro_c3);
    
    % Verificar que NO esté en el cuadrante superior derecho
    es_cuadrante_excluido = (x_rand >= centro_c3(1) && y_rand >= centro_c3(2));
    
    if dist_al_centro <= radio_c3 && ~es_cuadrante_excluido
        count = count + 1;
        X_clase3(count, :) = [x_rand, y_rand];
    end
end
Y_clase3 = 3 * ones(Num_puntos, 1); 
theta = linspace(0, 2*pi, 100);
x_circulo = centro_c3(1) + radio_c3 * cos(theta);
y_circulo = centro_c3(2) + radio_c3 * sin(theta);

idx_excluido = find(x_circulo >= centro_c3(1) & y_circulo >= centro_c3(2));
x_circulo(idx_excluido) = NaN;
y_circulo(idx_excluido) = NaN;

plot(x_circulo, y_circulo, 'm', 'LineWidth', 2, 'DisplayName', 'Clase 3 (Límite)');

plot([centro_c3(1) centro_c3(1)], [centro_c3(2) centro_c3(2)+radio_c3], 'm', 'LineWidth', 2, 'HandleVisibility', 'off');
plot([centro_c3(1) centro_c3(1)+radio_c3], [centro_c3(2) centro_c3(2)], 'm', 'LineWidth', 2, 'HandleVisibility', 'off'); 

scatter(X_clase3(:, 1), X_clase3(:, 2), 20, 'm', 'filled', 'DisplayName', 'Clase 3 Puntos');


legend('show', 'Location', 'northeast');
hold off;

X_todos = [X_clase1; X_clase2; X_clase3];
Y_etiquetas = [Y_clase1; Y_clase2; Y_clase3];

num_cl_evaluacion = 3; % Clase 1 (1), Clase 2 (2), Clase 3 (3)
num_total_puntos = size(X_todos, 1); % Total: 200 + 200 + 200 = 600 puntos


disp('Primer Metodo: RECONSTRUCCIÓN (100% - Clase 1, Clase 2 y Clase 3)');
[precision_100_euclidiana] = clasificar_y_evaluar(X_todos, Y_etiquetas, X_todos, Y_etiquetas, num_cl_evaluacion, 1);
fprintf('Precision usando distancia euclidiana (100%%): **%.4f** \n', precision_100_euclidiana);
[precision_100_mahalanobis] = clasificar_y_evaluar(X_todos, Y_etiquetas, X_todos, Y_etiquetas, num_cl_evaluacion, 2);
fprintf('Precision usando Mahalanobis (100%%): **%.4f** \n', precision_100_mahalanobis);

disp('Segundo metodo: 50/50 Cross-Validation (Clase 1, Clase 2 y Clase 3)');
X_entrenamiento = [];
Y_entrenamiento = [];
X_test = [];
Y_test = [];
datos_a_usar = {X_clase1, X_clase2, X_clase3}; % Solo C1, C2 y C3

for i = 1:num_cl_evaluacion
    clase_data = datos_a_usar{i};
    N_clase = size(clase_data,1);
    
    indices = randperm(N_clase);
    mitad = floor(N_clase / 2); 
    
    X_entrenamiento = [X_entrenamiento; clase_data(indices(1:mitad),:)];
    Y_entrenamiento = [Y_entrenamiento; i*ones(mitad,1)]; 
    
    X_test = [X_test; clase_data(indices(mitad+1:end),:)];
    Y_test = [Y_test; i*ones(N_clase - mitad, 1)]; 
end

[precision_cv_euclidiana] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl_evaluacion, 1);
fprintf('Precision de 50/50 Cross Validation usando dist euclidiana: **%.4f**', precision_cv_euclidiana)
[precision_cv_mahalanobis] = clasificar_y_evaluar(X_entrenamiento, Y_entrenamiento, X_test, Y_test, num_cl_evaluacion, 2);
fprintf('\nPrecision de 50/50 Cross Validation usando Mahalanobis: **%.4f** \n', precision_cv_mahalanobis)


disp('Tercer metodo: Leave One Out (LOO) (Clase 1, Clase 2 y Clase 3)');
% LOO con Distancia Euclidiana (Modelo 1)
num_clasificados_bien = 0;
for j = 1:num_total_puntos
    X_test_loo = X_todos(j, :);
    Y_test_loo = Y_etiquetas(j);
   
    indices_entrenamiento = [1:j-1, j+1:num_total_puntos];
    X_entrenamiento_loo = X_todos(indices_entrenamiento, :);
    Y_entrenamiento_loo = Y_etiquetas(indices_entrenamiento);
 
    [precision_punto, ~] = clasificar_y_evaluar(X_entrenamiento_loo, Y_entrenamiento_loo, X_test_loo, Y_test_loo, num_cl_evaluacion, 1);
    
    if precision_punto == 1
        num_clasificados_bien = num_clasificados_bien + 1;
    end
end
precision_loo_euclidiana = num_clasificados_bien / num_total_puntos;
fprintf('Presicion de LOO usando dist euclidiana: **%.4f**', precision_loo_euclidiana)

% LOO con Distancia de Mahalanobis (Modelo 2)
num_clasificados_bien = 0;
for j = 1:num_total_puntos
    X_test_loo = X_todos(j, :);
    Y_test_loo = Y_etiquetas(j);
    
    indices_entrenamiento = [1:j-1, j+1:num_total_puntos];
    X_entrenamiento_loo = X_todos(indices_entrenamiento, :);
    Y_entrenamiento_loo = Y_etiquetas(indices_entrenamiento);
    
    [precision_punto, ~] = clasificar_y_evaluar(X_entrenamiento_loo, Y_entrenamiento_loo, X_test_loo, Y_test_loo, num_cl_evaluacion, 2);
    
    if precision_punto == 1
        num_clasificados_bien = num_clasificados_bien + 1;
    end
end
precision_loo_mahalanobis = num_clasificados_bien / num_total_puntos;
fprintf('\nPresicion de LOO usando Mahalanobis: **%.4f**', precision_loo_mahalanobis)
fprintf('\nFin de programa. ');