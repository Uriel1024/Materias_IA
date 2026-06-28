%practica 9 
clc
clear
close all 
clases = [
        % Clase 1 
        % x,y,z, clase a la q pertenece (0/1)
        0, 0, 0, 0;  
        1, 0, 0, 0;   
        0, 1, 0, 0;   
        0, 0, 1, 0;   
        % Clase 2 
        1, 1, 0, 1;   
        1, 0, 1, 1;  
        0, 1, 1, 1;  
        1, 1, 1, 1;  
    ];
b= input("Ingresa el valro del bias: ");
z = input("Ingresa los valores iniciales en z: ");
y = input("Ingresa los valores iniciales en y: ");
x = input("Ingresa los valores iniciales en x: ");
%vector inicial 
vector = [1,x,y,z]; 
contador = 0;
converge = false;
fprintf("inicio del entrenamiento del perceptron. ")
while ~converge
    converge = true;
    contador = contador + 1;
    %asumimos que el plano original encaja
    for j = 1:size(clases,1)
        % fsal = [1,clases(j,1:3)] -> [1, x_j, y_j, z_j]
        fsal = [1,clases(j,1:3)];
        y_real = clases(j,4); 
        x_net = vector  * fsal';
        if y_real == 0
            if x_net >= 0
                vector = vector - fsal;
                converge = false;
            end
        else
            if x_net<=0
                vector = vector + fsal;
                converge = false;
            end
        end
    end
    %para ver cuantas iteraciones tardo en encontrar el plano optimo
    fprintf("%d iteracion con el sig vector:", contador)
    disp(vector)
    
    if converge
        break;
    end
end
fprintf("\n\nLa neurona alcanzo el plano optimo despues de %d  iteraciones con el vector", contador);
disp(vector)
% vector = [b, wx, wy, wz]
b = vector(1);
wx = vector(2); % Corresponde a la 1ra característica (x)
wy = vector(3); % Corresponde a la 2da característica (y)
wz = vector(4); % Corresponde a la 3ra característica (z)
% --- 5. Graficado de Clases y Plano Separador ---
% Crear figura 3D
figure;
ax = axes('Parent', gcf, 'NextPlot', 'add');
view(ax, 3);
grid on;
xlabel('$x$', 'Interpreter', 'latex');
ylabel('$y$', 'Interpreter', 'latex');
zlabel('$z$', 'Interpreter', 'latex');
title(sprintf('Clases y Plano Separador del Perceptrón: %.2f + %.2fx + %.2fy + %.2fz = 0', b, wx, wy, wz));
axis equal;
xL = [-0.2, 1.2];
yL = [-0.2, 1.2];
zL = [-0.2, 1.2];
xlim(xL);
ylim(yL);
zlim(zL);
% 5.1. Plotear los puntos de las clases
clase_0_data = clases(clases(:,4) == 0, 1:3);
clase_1_data = clases(clases(:,4) == 1, 1:3);
scatter3(clase_0_data(:,1), clase_0_data(:,2), clase_0_data(:,3), ...
    100, 'b', 'o', 'filled', 'DisplayName', 'Clase 0 (Target 0)');
scatter3(clase_1_data(:,1), clase_1_data(:,2), clase_1_data(:,3), ...
    100, 'r', '^', 'filled', 'DisplayName', 'Clase 1 (Target 1)');



if all(abs([wx, wy, wz]) < 1e-6) && abs(b) < 1e-6
    fprintf("Advertencia: El plano no está bien definido (todos los pesos son cero).\n");
else
    % Define la función implícita f(x,y,z) = 0
    plano_separador = @(x_var, y_var, z_var) b + wx*x_var + wy*y_var + wz*z_var;
    
    % Usa fimplicit3 para graficar la superficie definida por f(x,y,z) = 0
    h_plano = fimplicit3(plano_separador, [xL, yL, zL], ...
        'FaceColor', 'g', 'FaceAlpha', 0.5, 'EdgeColor', 'none');
    
    % Asigna el DisplayName
    h_plano.DisplayName = 'Plano Separador';
end


% Añadir Leyenda
legend('Location', 'best');
fprintf("\n\nFinal del programa, ahi nos voidmos");