LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;
USE IEEE.STD_LOGIC_UNSIGNED.ALL;
USE IEEE.STD_LOGIC_ARITH.ALL;

ENTITY P8 IS 
PORT(
    CLK_50MHz, CLR : IN STD_LOGIC;
    B : IN STD_LOGIC_VECTOR(2 DOWNTO 0);
    E : IN STD_LOGIC_VECTOR(7 DOWNTO 0);
    ECD, ECI : IN STD_LOGIC;
    Q : INOUT STD_LOGIC_VECTOR(7 DOWNTO 0)
);
END P8;

ARCHITECTURE A_P8 OF P8 IS
    SIGNAL GRAY_COUNT : STD_LOGIC_VECTOR(7 DOWNTO 0) := (OTHERS => '0');
    SIGNAL clk_1Hz   : STD_LOGIC := '0';
    SIGNAL div_counter: INTEGER RANGE 0 TO 24999999 := 0;  -- 50,000,000 / 2 = 25,000,000
BEGIN
    -- Divisor de frecuencia: 50 MHz → 1 Hz (periodo de 1 segundo)
    PROCESS(CLK_50MHz, CLR)
    BEGIN
        IF CLR = '0' THEN
            div_counter <= 0;
            clk_1Hz <= '0';
        ELSIF RISING_EDGE(CLK_50MHz) THEN
            IF div_counter = 24999999 THEN  -- 50MHz/1Hz = 50,000,000 → medio periodo = 25,000,000
                div_counter <= 0;
                clk_1Hz <= NOT clk_1Hz;
            ELSE
                div_counter <= div_counter + 1;
            END IF;
        END IF;
    END PROCESS;

    -- Lógica principal del registro operando a 1Hz
    PROCESS(clk_1Hz, CLR)
    BEGIN
        IF CLR = '0' THEN
            Q <= "00000000";
            GRAY_COUNT <= (OTHERS => '0');
        ELSIF RISING_EDGE(clk_1Hz) THEN
            CASE B IS 
                WHEN "000" => Q <= E;                         -- Carga paralela
                WHEN "001" => Q <= Q + 1;                     -- Incremento binario
                WHEN "010" => Q <= Q - 1;                     -- Decremento binario
                WHEN "011" => Q <= ECD & Q(7 DOWNTO 1);       -- Desplazamiento derecha
                WHEN "100" => Q <= Q(6 DOWNTO 0) & ECI;       -- Desplazamiento izquierda
                WHEN "101" => Q <= Q;                         -- Mantener valor
                WHEN "110" => Q <= Q(0) & Q(7 DOWNTO 1);      -- Rotación derecha
                WHEN OTHERS =>                                -- Contador Gray
                    GRAY_COUNT <= GRAY_COUNT + 1;
                    Q <= GRAY_COUNT XOR ('0' & GRAY_COUNT(7 DOWNTO 1));
            END CASE;
        END IF;
    END PROCESS;
END A_P8;