clc
format rational
%problema de ejemplo en clase gg
%definimos las clases
clase_1 = [0 1 1 1;
           0 0 1 0;
           0 0 0 1];
%cambie la matriz del ejemplo debido a que no era invertible, r2 y r3 eran
%iguales por lo que no se podia obtener la inversa
clase_2 = [1 0 0 0; 
           1 1 0 1;
           1 0 0 1];

v1 = sum(clase_1, 2);
v2 = sum(clase_2, 2);
s1 = size(clase_1,2);
s2 = size(clase_2,2);
s1 = 1/s1;
s2 = 1/s2;
v1 = v1 * s1;
v2 = v2 * s2;
%Resta el elemento de v1[i] por la matriz clase_1 en cada columna
for i = 1:length(v1)
    for j = 1:size(clase_1, 2)
        clase_1(i, j) = clase_1(i, j) - v1(i);
    end
end
clase_1t = clase_1';
clase_n = s1*clase_1*clase_1t;
clase_n = inv(clase_n);
vector_us = [-1/4; 1/4; 3/4]';

d1 = (vector_us*clase_n)*vector_us';
%Calcular la distancia cuadrática de Mahalanobis
mahalDist1 = sqrt(d1)

for i = 1:length(v2)
    for j = 1:size(clase_2, 2)
        clase_2(i, j) = clase_2(i, j) - v2(i);
    end
end

clase_2t = clase_2';
clase_2n = s2 * clase_2 * clase_2t
clase_2n = inv(clase_2n);
d2 = (vector_us * clase_2n) * vector_us';
mahalDist2 = sqrt(d2)
% Compare the Mahalanobis distances to determine class membership
if mahalDist1 < mahalDist2
    disp("El vector pertenece a la clase 1")
else
    disp("El vector pertenece a la clase 2")
end