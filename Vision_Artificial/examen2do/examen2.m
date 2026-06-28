function analisis_color_areas_completo(ruta_imagen)

    % 1. Configuración inicial
    if nargin < 1
        ruta_imagen = 'colore.png'; 
    end
    
    % 2. Cargar la imagen
    imagen = imread(ruta_imagen);
    if isempty(imagen)
        error('Error: No se pudo cargar la imagen en la ruta: %s. Asegúrese de que el archivo exista.', ruta_imagen);
    end

    
    % --- 3. Segmentación de la imagen usando K-Means ---
    % Usamos 5 clusters para separar las 4 regiones principales de color más la región central gris.
    num_clusters = 5; 
    
    % Convertir la imagen RGB a un espacio de color (L*a*b*) para mejor agrupación por color
    lab_image = rgb2lab(imagen);
    ab = lab_image(:,:,2:3);
    ab = im2single(ab); 
    pixel_labels = imsegkmeans(ab, num_clusters);

    % Convertir la imagen a double (0-255) para un cálculo de media preciso
    imagen_double = im2double(imagen) * 255; 
    
    % 4. Análisis y Cálculo de Áreas
    figura_area = struct('ID', {}, 'Color', {}, 'Area_px', {}, 'Etiqueta', {});
    area_total_px = numel(imagen(:,:,1));
    area_centro_px = 0; 
    
    disp(' ');
    disp('--- Resultados de Segmentación y Área ---');
    
    % Separar los canales de la imagen 'double' para evitar el error de reshape
    R_canal = imagen_double(:,:,1);
    G_canal = imagen_double(:,:,2);
    B_canal = imagen_double(:,:,3);
    
    % Iterar sobre cada cluster (región de color)
    for k = 1:num_clusters
        % Crear una máscara binaria para la región actual
        mascara_region = (pixel_labels == k);
        
        % Calcular el área de la región (número de píxeles)
        area_px = sum(mascara_region(:));
        
        % Descartar clusters muy pequeños (ruido)
        if area_px < 500 
            continue;
        end
        
        % CORRECCIÓN: Obtener el color promedio aplicando la máscara a cada canal
        R_valores = R_canal(mascara_region);
        G_valores = G_canal(mascara_region);
        B_valores = B_canal(mascara_region);
        
        % Calcular la media para cada canal y formar el vector de color
        color_promedio = [mean(R_valores), mean(G_valores), mean(B_valores)];
        color_rgb = uint8(color_promedio);
        
        % 5. Clasificación y Etiquetado (Heurística)
        etiqueta = sprintf('Región %d', k);
        
        % Heurística para identificar la región GRIS (centro)
        % (R, G, B similares, típicamente en el rango medio 100-200)
        if abs(color_rgb(1) - color_rgb(2)) < 15 && abs(color_rgb(1) - color_rgb(3)) < 15 && color_rgb(1) > 100 && color_rgb(1) < 200
            etiqueta = '**Región Central (Gris)**';
            area_centro_px = area_px; % Almacenar el área central
        end
        
        % Guardar el resultado
        figura_area(end+1) = struct( ...
            'ID', k, ...
            'Color', sprintf('#%02X%02X%02X', color_rgb(1), color_rgb(2), color_rgb(3)), ...
            'Area_px', area_px, ...
            'Etiqueta', etiqueta ...
        );
        
        fprintf('ID %d | Etiqueta: %s | Área: %.0f px² (%.1f%% del total)\n', ...
                k, etiqueta, area_px, (area_px / area_total_px) * 100);
    end
    
    % 6. Mostrar Conclusiones
    disp(' ');
    disp('--- Conclusiones ---');
    if area_centro_px > 0
        fprintf('El área de la **Región Central (Gris)** es: **%.0f píxeles cuadrados (px²)**.\n', area_centro_px);
    else
        disp('No se pudo identificar la región central gris basándose en la heurística de color.');
    end
    
    % 7. Mostrar la imagen segmentada 
    figure('Name', 'Imagen Segmentada por Color (K-Means)');
    B = labeloverlay(imagen, pixel_labels);
    imshow(B);


end