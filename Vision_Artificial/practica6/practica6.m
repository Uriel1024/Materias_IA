    %practica n6 
%Programa que clasifica un vector respecto a n clases 
clc
clear
close all
format rational
% Solicitar al usuario que ingrese el número de clases
while true
    num_cl = input('Ingresa el numero de clases a generar: ');
    if (num_cl <= 0 || num_cl > 49)
        disp('NO es posible generar más de 49 clases, por favor ingresa un número válido.')
    else
        break
    end
end


%matriz para guardar las clases 
matrices = cell(1,num_cl);

while true
    num_de_objetos = input('Ingresa el numero de objetos por clase: ');
    if num_de_objetos <= 0
        disp('El numero de objetos debe ser mayor que 0.')
    else
        break
    end
end


for i = 1:num_cl    
    
    while true
        fprintf('Ingresa la dispersion en x para la clase %d\n ',i);
        disper_x = input(': ');
        fprintf('Ingresa la dispersion en y para la clase %d\n ',i);
        disper_y = input(': ');
        fprintf('Ingresa la dispersion en z para la clase %d\n', i);
        disper_z = input(': ');
        if (disper_x <= 0 || disper_y <= 0 || disper_z <= 0)
            disp('La dispersion debe de ser diferente de 0.');
        else   
            break
        end
    end
   
    fprintf('Ingresa el centroide de x para la clase %d\n ',i);
    centro_x = input(":");
    fprintf('Ingresa el centroide de y para la clase %d\n',i);
    centro_y = input(":");
    fprintf('Ingresa el centroide de z para la clase %d\n',i);
    centro_z = input(":");

    matrices{i} = [(centro_x + randn(1, num_de_objetos)*disper_x); ...
                   (centro_y + randn(1, num_de_objetos)*disper_y); ...
                   (centro_z + randn(1, num_de_objetos)*disper_z)];
end 
   
op = 0 ;
while (op ~= -1)
    %pedimos el vector al usuario
    vx=input('dame el valor de la coord en x=');
    vy=input('dame el valor de la coord en y=');
    vz=input('dame el valor de la coord en z=');
    vector=[vx;vy;vz];

        medias = cell(1,num_cl);
    distancias = zeros(1, num_cl);
    while true
        disp("Que modelo deseas usar para calcular las distancias a las clases");
        disp("1.Distancia Euclidiana.")
        disp("2.Mohalanobis.")
        modelo = input("Tu opcion: ");
        if modelo ~= 1 && modelo ~= 2
            disp('Opción no válida, por favor elige 1 o 2.');
        else    
            break
        end
    end
    vector1 = cell(1,num_cl);
    %obtenemos la media de cada clase
    for j = 1:num_cl
        medias{j} = mean(matrices{j}, 2);
    end

    if modelo == 1
            % Calcular la distancia de cada vector a las medias de las clases
            disp("Has escogido la distancia euclidiana. ")
            for j = 1:num_cl
                distancias(j) = norm(vector - medias{j});
            end
    else 
            %genera el vector con la suma entre el numero de objetos
            %totales
            
            for i = 1:num_cl
                disp("Has escogido la distancia Mohalanobis. ")
                matriz_clase = matrices{i};
                media_clase = medias{i};
                matriz_cov = cov(matriz_clase');
                sigma_inv = inv(matriz_cov);
                diff_vector = vector - media_clase;
                distancia_cad = (diff_vector' * sigma_inv) * diff_vector;
                distancias(i) = sqrt(distancia_cad);

            end 
 
    end

    % Determinar la clase más cercana
    [~, clase_asignada] = min(distancias);
    minimo = min(distancias);
    if minimo > 400
        disp(['El vector no pertenece a ninguna clase debido a que ' num2str(minimo)]);
        disp('es mayor al umbral de 100 puntos para poder pertenecer a alguna clase')
    else
        disp(['El vector pertenece a la clase: ' num2str(clase_asignada)]);
        disp(['Con una distancia de ' num2str(minimo)])
    end
    
    %para poder graficar
    leyendas = cell(1,num_cl);
    figure;
    hold on;

    %graficamos las clsaes 
    for i = 1: num_cl
        x_coords = matrices{i}(1,:);
        y_coords = matrices{i}(2,:);
        z_coords = matrices{i}(3,:);
        %crea el color de manear aleatoria
        color_rgb = rand(1,3);

        plot3(x_coords,y_coords,z_coords,'o','Color',color_rgb, "MarkerSize",8,"lineWidth",1.5);
        leyendas{i} = ['Clase ' num2str(i)];
    end
    leyendas{num_cl + 1} = ['Vector'];



    plot3(vector(1,:),vector(2,:),vector(3,:),'go','MarkerSize',10,'MarkerFaceColor','g')
    xlabel('Eje X');
    ylabel('Eje Y');
    zlabel('Eje Z');
    title('Clasificación de vectores por clase');
    legend(leyendas);
    grid on;
    view(3);
    hold off;

    
    op = input('Deseas continuar con el programa (-1 para salir): ');
end 
disp('Fin de programa, ahi nos vemos. ');