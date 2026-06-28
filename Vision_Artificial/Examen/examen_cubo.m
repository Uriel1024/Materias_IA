function clasificador_interactivo_modificado()
    % Tabla de verdad solo con Clase 1 (colores puros) y Clase 2 (mezclas)
    tabla_verdad = [
        % Clase 1 (colores puros)
        0, 0, 0, 1;   % Negro
        1, 0, 0, 1;   % Rojo
        0, 1, 0, 1;   % Verde
        0, 0, 1, 1;   % Azul

        % Clase 2 (mezclas)
        1, 1, 0, 2;   % Amarillo
        1, 0, 1, 2;   % Magenta
        0, 1, 1, 2;   % Cyan
        1, 1, 1, 2;   % Blanco
    ];
    fprintf('\n');

    % Solicitar valores RGB al usuario
    fprintf('Ingrese valores RGB (0-1):\n');
    r = input('Rojo: ');
    g = input('Verde: ');
    b = input('Azul: ');
    vector = [r, g, b];

    % Verificar si el vector está fuera del cubo RGB
    if any(vector < 0) || any(vector > 1)
        fprintf('\nResultado:\n');
        fprintf('Vector [%.2f, %.2f, %.2f] está fuera del cubo RGB (0-1)\n', vector(1), vector(2), vector(3));
        fprintf('No pertenece a ninguna clase\n');
        visualizar_clasificacion(tabla_verdad, vector, -1); % Usamos -1 para indicar fuera del cubo
        return;
    end

    % Clasificar por Distancia de Mahalanobis
    [clase, distancia] = clasificar_mahalanobis(vector, tabla_verdad);

    fprintf('\nMétodo: Distancia de Mahalanobis\n');
    fprintf('Distancia mínima: %.4f\n', distancia);

    % Mostrar resultados
    fprintf('\nResultado:\n');
    fprintf('Vector [%.2f, %.2f, %.2f]\n', vector(1), vector(2), vector(3));
    fprintf('Clase: %d\n', clase);

    % Visualización
    visualizar_clasificacion(tabla_verdad, vector, clase);
end

function [clase, min_distancia] = clasificar_mahalanobis(vector, tabla_verdad)
    % Separar por clases (solo 1 y 2)
    clases = unique(tabla_verdad(:,4));
    distancias = zeros(length(clases), 1);

    for c = 1:length(clases)
        puntos_clase = tabla_verdad(tabla_verdad(:,4) == clases(c), 1:3);
        media = mean(puntos_clase);
        % Regularización: + eye(3)*0.001 ayuda a asegurar que la matriz no sea singular
        cov_mat = cov(puntos_clase) + eye(3)*0.001;

        diff = vector - media;
        % Fórmula de la distancia de Mahalanobis: sqrt( (x - mu)' * Sigma^-1 * (x - mu) )
        distancias(c) = sqrt(diff * inv(cov_mat) * diff');
    end

    [min_distancia, idx] = min(distancias);
    clase = clases(idx);
end

function visualizar_clasificacion(tabla_verdad, vector, clase)
    figure;
    hold on;
    grid on;

    % Colores para los puntos de referencia (Clase 1 y Clase 2)
    colores = {'k', 'r', 'g', 'b', 'y', 'm', 'c', 'w'};

    % Graficar puntos de referencia
    for i = 1:size(tabla_verdad, 1)
        scatter3(tabla_verdad(i,1), tabla_verdad(i,2), tabla_verdad(i,3), ...
                100, colores{i}, 'filled');
    end

    % Graficar vector de usuario
    if clase == -1
        % Vector fuera del cubo: se marca con 'x' roja
        scatter3(vector(1), vector(2), vector(3), 150, 'r', 'x', 'LineWidth', 2);
    else
        % Vector dentro del cubo: se marca con un pentagrama negro 'p'
        scatter3(vector(1), vector(2), vector(3), 150, 'k', 'p', 'filled');
    end

    % Configuración de la gráfica
    xlabel('Rojo'); ylabel('Verde'); zlabel('Azul');

    if clase == -1
        title('Vector fuera del cubo RGB - No pertenece a ninguna clase');
    else
        title(sprintf('Clasificación por Mahalanobis: Clase %d', clase));
    end

    axis([0 1 0 1 0 1]);
    view(3);
    hold off;
end