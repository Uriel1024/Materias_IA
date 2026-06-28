clc
clear
%leemos la img 
img = imread('img5.jpeg');
figure, imshow(img);

height = size(img, 1);
width = size(img, 2); 
third_width = floor(width/3);

% 1. SEPARAR LA IMAGEN EN TRES TIRAS VERTICALES
part1 = img(:, 1:third_width, :);                       
part2_original = img(:, third_width+1:floor(2*width/3), :); 
part3 = img(:, floor(2*width/3)+1:end, :);              

part1_colored = zeros(size(part1), 'uint8');
part1_colored(:, :, 1) = part1(:, :, 1); % Canal rojo

[h2, w2, ~] = size(part2_original);
part2_colored = zeros(h2, w2, 3, 'uint8');

[X, Y] = meshgrid(1:w2, 1:h2); 

diagonal_gradient = (Y / h2) + (X / w2);

% Definir los umbrales para la división
T1 = 0.8; % Umbral inferior
T2 = 1.2; % Umbral superior

% 1. BLANCO (Triángulo superior-izquierda, donde el gradiente es bajo)
mask_white = diagonal_gradient < T1; 
part2_colored(repmat(mask_white, 1, 1, 3)) = 0; % Aplicar blanco (255)

mask_gray = (diagonal_gradient >= T1) & (diagonal_gradient < T2); 
gray_part = rgb2gray(part2_original); 
gray_part_colored = repmat(gray_part, [1, 1, 3]);
part2_colored(repmat(mask_gray, 1, 1, 3)) = gray_part_colored(repmat(mask_gray, 1, 1, 3));


mask_black = diagonal_gradient >= T2; 
part2_colored(repmat(mask_black, 1, 1, 3)) = 255; 

part3_colored = zeros(size(part3), 'uint8');
part3_colored(:, :, 2) = part3(:, :, 2); % Canal verde
part3_colored(:, :, 3) = part3(:, :, 3); % Canal azul

% ----------------------------------------------------
% 2. COMBINAR LAS PARTES
% ----------------------------------------------------
final_img = [part1_colored, part2_colored, part3_colored]; 

% Mostrar la imagen resultante
figure, imshow(final_img);
title('Tira Central Dividida Diagonalmente Invertida (Blanco/Gris/Negro)');
    
fprintf("\n\nPrograma terminado.\n\n")