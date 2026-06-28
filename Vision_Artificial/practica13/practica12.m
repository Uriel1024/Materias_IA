% 1. Definir los datos de entrada (coordenadas de los 5 puntos)
Puntos = [
    1, 4;   % X1
    2, 4;   % X2
    1, 3;   % X3
    3, 1;   % X4
    4, 3    % X5
];
N = size(Puntos, 1);

function d = distancia_euclidiana(p1, p2)
    % Calcula la distancia euclidiana entre dos puntos.
    d = sqrt(sum((p1 - p2).^2));
end

% Inicialización de la matriz de distancias D (Tabla A)
D = zeros(N, N);
for i = 1:N
    for j = 1:N
        if i ~= j
            D(i, j) = distancia_euclidiana(Puntos(i, :), Puntos(j, :));
        else
            D(i, j) = inf; % Distancia a sí mismo es infinita
        end
    end
end

% Matriz para almacenar los resultados del clustering (Z), 
% [Grupo_1, Grupo_2, Altura_Union, Numero_Elementos]
Z = zeros(N - 1, 4); 

% Etiqueta de los grupos. Inicialmente, cada punto es un grupo.
% En este código, Grupos almacena la ETIQUETA real del grupo/punto, 
% no solo el índice en D.
Grupos = 1:N; 

% Bucle principal del AHC (N-1 pasos de unión)
for k = 1:(N - 1)
    
    % 3.1. Encontrar la mínima distancia (enlace simple)
    % Buscamos el mínimo en la sub-matriz triangular superior para evitar 
    % las distancias a sí mismo (inf) y la duplicación.
    min_dist = inf;
    r = 0; % Fila del mínimo
    c = 0; % Columna del mínimo
    
    % Recorremos solo la parte superior triangular (i < j)
    for i = 1:size(D, 1)
        for j = i + 1:size(D, 2)
            if D(i, j) < min_dist
                min_dist = D(i, j);
                r = i;
                c = j;
            end
        end
    end
    
    grupo_r = Grupos(r);
    grupo_c = Grupos(c);
    
    % Contar elementos (el código de conteo original estaba correcto)
    num_r = 1;
    if grupo_r > N
        % El grupo fue creado en un paso anterior (g - N) y el conteo está en Z.
        num_r = Z(grupo_r - N, 4); 
    end
    
    num_c = 1;
    if grupo_c > N
        num_c = Z(grupo_c - N, 4);
    end
    
    % Registro de la nueva unión Z(k)
    % Las etiquetas de grupo deben ser ordenadas (g1 < g2)
    if grupo_r > grupo_c
        Z(k, :) = [grupo_c, grupo_r, min_dist, num_r + num_c];
    else
        Z(k, :) = [grupo_r, grupo_c, min_dist, num_r + num_c];
    end
    
    % 3.3. Actualizar la matriz de distancias D
    
    % Cálculo de la distancia del nuevo grupo (r y c) a todos los demás grupos (i)
    % Usamos el índice del nuevo grupo, que será la *posición* actual en D 
    % de uno de los grupos unidos (e.g., 'r').
    
    % Recorremos todas las demás columnas 'i' que NO sean 'r' o 'c'.
    for i = 1:size(D, 2)
        if i ~= r && i ~= c 
            % Distancia del punto 'i' al grupo 'r' y 'c'
            dist_r = D(r, i);
            dist_c = D(c, i);
            
            % Se usa 'min' para el enlace simple (Single-Linkage)
            nueva_distancia = min(dist_r, dist_c); 
            
            % Actualizamos la fila 'r' (que será el nuevo grupo) 
            % y su simétrico en la columna 'r'.
            D(r, i) = nueva_distancia; 
            D(i, r) = nueva_distancia; 
        end
    end
    
    D(r, r) = inf;
    D(c, :) = []; % Eliminar la fila 'c'
    D(:, c) = []; % Eliminar la columna 'c'
    
    % 3.5. Actualizar las etiquetas de los grupos
    nuevo_grupo_idx = N + k; 
    
    % El grupo 'r' ahora tiene la etiqueta del nuevo grupo unido.
    Grupos(r) = nuevo_grupo_idx; 
    
    % Se elimina la etiqueta del grupo 'c'
    Grupos(c) = [];
    
end
disp('Matriz de Uniones Z (Resultado del AHC):');
disp(Z);

function graficar_dendograma_manual(Z, N)
    
    % Pos_X ahora almacenará la posición x de cada etiqueta de grupo.
    Pos_X = zeros(N * 2 - 1, 1);
    Pos_X(1:N) = 1:N; % Puntos originales: 1 a N
    
    % Altura_Y almacenará la altura de unión de cada etiqueta de grupo.
    Altura_Y = zeros(N * 2 - 1, 1);
    
    segmentos_horizontales = [];
    segmentos_verticales = [];
    
    % Bucle sobre las uniones en Z
    for k = 1:size(Z, 1)
        
        g1 = Z(k, 1); % Etiqueta Grupo 1 (e.g., 1, 2, 6)
        g2 = Z(k, 2); % Etiqueta Grupo 2 (e.g., 3, 7)
        h  = Z(k, 3); % Altura de la unión (distancia)
        
        % El índice de la nueva etiqueta de grupo es N + k
        nuevo_grupo_idx = N + k; 
        
        % Posiciones X: Se obtienen de la etiqueta.
        x1 = Pos_X(g1); 
        x2 = Pos_X(g2);
        
        % Alturas Y: Se obtienen de la etiqueta.
        h1 = Altura_Y(g1);
        h2 = Altura_Y(g2);
        
        % 4.1. Dibujar el segmento horizontal (la rama de unión)
        segmentos_horizontales = [segmentos_horizontales; [x1, x2, h]];
        
        % 4.2. Dibujar segmentos verticales 
        segmentos_verticales = [segmentos_verticales; [x1, x1, h1, h]]; % Vertical para g1
        segmentos_verticales = [segmentos_verticales; [x2, x2, h2, h]]; % Vertical para g2
        
        % 4.3. Actualizar la posición X y Altura Y del nuevo grupo
        nueva_pos_x = (x1 + x2) / 2;
        
        Pos_X(nuevo_grupo_idx) = nueva_pos_x;
        Altura_Y(nuevo_grupo_idx) = h;
        
    end
    
    % 4.4. Dibujar en la figura
    figure;
    hold on;
    
    % Dibujar puntos originales en h=0 (opcional)
    plot(Pos_X(1:N), Altura_Y(1:N), 'ko', 'MarkerFaceColor', 'k', 'MarkerSize', 5);
    
    % Dibujar líneas horizontales
    for i = 1:size(segmentos_horizontales, 1)
        plot(segmentos_horizontales(i, 1:2), [segmentos_horizontales(i, 3), segmentos_horizontales(i, 3)], 'r', 'LineWidth', 1.5);
    end
    
    % Dibujar líneas verticales
    for i = 1:size(segmentos_verticales, 1)
        plot([segmentos_verticales(i, 1), segmentos_verticales(i, 2)], [segmentos_verticales(i, 3), segmentos_verticales(i, 4)], 'b', 'LineWidth', 1.5);
    end
    
    title('Dendrograma (Implementación Manual) - Single Linkage');
    xlabel('Puntos / Grupos');
    ylabel('Distancia de Unión (Altura)');
    
    % Configurar etiquetas de los ejes X
    xticks(1:N);
    xticklabels({'X1', 'X2', 'X3', 'X4', 'X5'});
    
    % Establecer límites del eje Y
    max_h = max(Z(:, 3));
    ylim([0, max_h * 1.1]); 
    xlim([0.5, N + 0.5]);
    
    grid on;
    hold off;
end

% Llamar a la función de graficado
graficar_dendograma_manual(Z, N);