clc
clases = cell(1,8);

for i = 1:8
    limite_inferior = (i-1)*3 + 1;
    limite_superior = i*3;
    
    % Generar la matriz aleatoria
    clases{i} = randi([limite_inferior, limite_superior], 2, 5)
    
end


plot(clases{1}(1,:),c1(2,:),'ro','MarkerSize',10,'MarkerFaceColor','r')