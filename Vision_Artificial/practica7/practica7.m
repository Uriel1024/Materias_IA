%practica 7
clc
clear
close all
format rational
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
img=imread('descarga.jpeg');
figure(1)
imshow(img)

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
   
%vector=[1, 2];
op = 0 ;
while (op ~= -1)
    %pedimos el vector al usuario
    [vx,vy,vector]=impixel();
    vector = vector';
    legend(leyendas);
    vPlot = plot(vx,vy,'bo','MarkerSize',10,'MarkerFaceColor','g');
    %hold off

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
            
            disp("Has escogido la distancia Mohalanobis. ")
            for i = 1:num_cl
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
        disp('es mayor al umbral de 400 puntos para poder pertenecer a alguna clase')
    else
        disp(['El vector pertenece a la clase: ' num2str(clase_asignada)]);
        disp(['Con una distancia de ' num2str(minimo)])
    end

    fprintf('Deseas continuar con el programa (-1 para salir): ')
    op = input('');
    delete(vPlot)
end 
disp('Fin de programa, ahi nos vemos. ');


