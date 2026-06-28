%Programa que clasifica un vector respecto a n clases 
clc
clear
close all

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

while true
    disper_x = input('Ingresa la dispersion en x: ');
    disper_y = input('Ingresa la dispersion en y: ');
    if (disper_x <= 0 || disper_y <= 0)
        disp('La dispersion debe de ser diferente de 0.');
    else   
        break
    end
end

for i = 1:num_cl    
    fprintf('Ingresa el centroide de x para la clase %d\n ',i);
    centro_x = input(":");
    fprintf('Ingresa el centroide de y para la clase %d\n',i);
    centro_y = input(":");

    matrices{i} = [(centro_x + randn(1, num_de_objetos)*disper_x); ...
                   (centro_y + randn(1, num_de_objetos)*disper_y)];

end 
    


op = 0 ;
while (op ~= -1)
    %pedimos el vector al usuario
    vx=input('dame el valor de la coord en x=');
    vy=input('dame el valor de la coord en y=');
    
    vector=[vx;vy];
    medias = cell(1,num_cl);

    % Obtenemos la media de cada clase 
    for j = 1:num_cl
        medias{j} = mean(matrices{j},2);
    end
    
    % Calcular la distancia de cada vector a las medias de las clases
    distancias = zeros(1, num_cl);
    for j = 1:num_cl
        distancias(j) = norm(vector - medias{j});
    end
    
    % Determinar la clase más cercana
    [~, clase_asignada] = min(distancias);
    minimo = min(distancias);
    if minimo > 800
        disp(['El vector no pertenece a ninguna clase debido a que ' num2str(minimo)]);
        disp('es mayor al umbral de 100 puntos para poder pertenecer a alguna clase')
    else
        disp(['El vector pertenece a la clase: ' num2str(clase_asignada)]);
        disp(['Con una distancia de ' num2str(minimo)])
            
        %para poder graficar
        leyendas = cell(1,num_cl);
        figure;
        hold on;
    
        %graficamos las clsaes 
        for i = 1: num_cl
            x_coords = matrices{i}(1,:);
            y_coords = matrices{i}(2,:);
            
            %crea el color de manear aleatoria
            color_rgb = rand(1,3);

            plot(x_coords,y_coords,'o','Color',color_rgb, "MarkerSize",8,"lineWidth",1.5);
            leyendas{i} = ['Clase ' num2str(i)];
        end
        leyendas{num_cl + 1} = ['Vector'];
    
    
    
        plot(vector(1,:),vector(2,:),'go','MarkerSize',10,'MarkerFaceColor','g')
        xlabel('Eje X');
        ylabel('Eje Y');
        title('Clasificación de vectores por clase');
        legend(leyendas);
        grid on;
        hold off;

    end
    op = input('Deseas continuar con el programa (-1 para salir): ');
end 
disp('Fin de programa, ahi nos vemos. ');