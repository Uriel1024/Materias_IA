-- Practica 7
LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;
USE IEEE.NUMERIC_STD.ALL;

ENTITY p7 IS
    PORT(
        CLK, CLR, SICO, SICI : IN STD_LOGIC;
        SEL : IN STD_LOGIC_VECTOR (1 DOWNTO 0);
        PI : IN STD_LOGIC_VECTOR (7 DOWNTO 0);
        Q : INOUT STD_LOGIC_VECTOR (7 DOWNTO 0);
        CLKOUT : BUFFER STD_LOGIC
    );
END ENTITY;

ARCHITECTURE a_p7 OF p7 IS
    SIGNAL D : STD_LOGIC_VECTOR (7 DOWNTO 0);
    SIGNAL CRISTAL : STD_LOGIC;
    SIGNAL CONTADOR : UNSIGNED (24 DOWNTO 0) := (OTHERS => '0');
    CONSTANT DIVISOR1 : INTEGER := 27000000;
BEGIN

    PROCESS (CLK)
    BEGIN
        IF RISING_EDGE(CLK) THEN
            CRISTAL <= CLK;
            IF CONTADOR = DIVISOR1 THEN
                CONTADOR <= (OTHERS => '0');
                CLKOUT <= NOT CLKOUT;
            ELSE
                CONTADOR <= CONTADOR + 1;
            END IF;
        END IF;
    END PROCESS;

    MUX: PROCESS(SEL, Q, SICO, SICI, PI)
    BEGIN
        CASE SEL IS
            WHEN "00" =>
                D(7) <= SICO;
                FOR I IN 6 DOWNTO 0 LOOP
                    D(I) <= Q(I + 1);
                END LOOP;

            WHEN "01" =>
                D <= PI;

            WHEN "10" =>
                D(0) <= SICI;
                FOR I IN 7 DOWNTO 1 LOOP
                    D(I) <= Q(I - 1);
                END LOOP;

            WHEN OTHERS =>
                D <= Q;
        END CASE;
    END PROCESS;

    PROCESS (CLKOUT, CLR)
    BEGIN
        IF CLR = '0' THEN
            Q <= (OTHERS => '0');  -- Reiniciar Q a ceros
        ELSIF RISING_EDGE(CLKOUT) THEN
            Q <= D;
        END IF;
    END PROCESS;

END ARCHITECTURE a_p7;