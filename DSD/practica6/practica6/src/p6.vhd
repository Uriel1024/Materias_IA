LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;
USE IEEE.NUMERIC_STD.ALL;  

ENTITY P6 IS 
PORT(
    CLK, CLR, CON : IN  STD_LOGIC;
    DISPLAY       : OUT STD_LOGIC_VECTOR(6 DOWNTO 0)
);
END P6;

ARCHITECTURE A_P6 OF P6 IS

    SIGNAL DIV_CLK  : STD_LOGIC := '0';  
    SIGNAL Q        : UNSIGNED(3 DOWNTO 0) := (others => '0'); 
    SIGNAL DIV_CON  : UNSIGNED(24 DOWNTO 0) := (others => '0');

    CONSTANT UNO     : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1001111";
    CONSTANT DOS     : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0010010";
    CONSTANT TRES    : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0000110";
    CONSTANT CUATRO  : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1001100";
    CONSTANT CINCO   : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0100100";
    CONSTANT SEIS    : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0100000";
    CONSTANT SIETE   : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0001110";
    CONSTANT OCHO    : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0000000";
    CONSTANT NUEVE   : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0001100";
    CONSTANT CERO    : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0000001";

    CONSTANT A : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0001000";
    CONSTANT H : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1001000";
    CONSTANT I : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1001111";
    CONSTANT L : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1110001";
    CONSTANT N : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0001001";
    CONSTANT O : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0000001";
    CONSTANT S : STD_LOGIC_VECTOR(6 DOWNTO 0):= "0100100";
    CONSTANT T : STD_LOGIC_VECTOR(6 DOWNTO 0):= "1110000";

BEGIN


DIVISOR: PROCESS(CLK)
BEGIN
    IF rising_edge(CLK) THEN
        IF DIV_CON = 26999999 THEN
            DIV_CON  <= (others => '0');
            DIV_CLK  <= '1';
        ELSE
            DIV_CON  <= DIV_CON + 1;
            DIV_CLK  <= '0';
        END IF;
    END IF;
END PROCESS;

CONTADOR: PROCESS(DIV_CLK, CLR)
BEGIN
    IF CLR = '0' THEN
        Q <= (others => '0');
    ELSIF rising_edge(DIV_CLK) THEN
        IF Q = "1001" THEN
            Q <= (others => '0');
        ELSE
            Q <= Q + 1;
        END IF;
    END IF;
END PROCESS;

DECODIFICADOR: PROCESS(Q, CON)
BEGIN
    IF CON = '0' THEN
        CASE Q IS
            WHEN "0000" => DISPLAY <= DOS;
            WHEN "0001" => DISPLAY <= CERO;
            WHEN "0010" => DISPLAY <= DOS;
            WHEN "0011" => DISPLAY <= CUATRO;
            WHEN "0100" => DISPLAY <= SEIS;
            WHEN "0101" => DISPLAY <= TRES;
            WHEN "0110" => DISPLAY <= CERO;
            WHEN "0111" => DISPLAY <= SEIS;
            WHEN "1000" => DISPLAY <= CERO;
            WHEN "1001" => DISPLAY <= CERO;
            WHEN OTHERS => DISPLAY <= "1111110";
        END CASE;
    ELSE
        CASE Q IS
            WHEN "0000" => DISPLAY <= H;
            WHEN "0001" => DISPLAY <= O;
            WHEN "0010" => DISPLAY <= L;
            WHEN "0011" => DISPLAY <= A;
            WHEN "0100" => DISPLAY <= "1111111";
            WHEN "0101" => DISPLAY <= S;
            WHEN "0110" => DISPLAY <= A;
            WHEN "0111" => DISPLAY <= N;
            WHEN "1000" => DISPLAY <= T;
            WHEN "1001" => DISPLAY <= I;
            WHEN OTHERS => DISPLAY <= "1111110";
        END CASE;
    END IF;
END PROCESS;

END A_P6;
