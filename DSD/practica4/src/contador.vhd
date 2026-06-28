LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;
USE IEEE.NUMERIC_STD.ALL;

ENTITY CONTADOR IS
    PORT(
        CLK    : IN STD_LOGIC;
        Q      : INOUT STD_LOGIC_VECTOR(9 DOWNTO 0);  -- Cambiado a 10 bits
        CLR, E : IN STD_LOGIC
    );
END CONTADOR;

ARCHITECTURE P4 OF CONTADOR IS
    signal contador  : unsigned(31 downto 0) := (others => '0'); 
    signal DIV_CLK   : std_logic := '0';
    constant DIVISOR_05S : integer := 13500000;
    signal jk : std_logic_vector(9 downto 0);  -- Cambiado a 10 bits
    signal q_sig : std_logic_vector(9 downto 0);  -- Cambiado a 10 bits

BEGIN
    process (clk)
    begin
        if rising_edge(clk) then
            if contador >= to_unsigned(DIVISOR_05S, 32) then  
                contador <= (others => '0');
                DIV_CLK <= not DIV_CLK;
            else
                contador <= contador + 1;
            end if;
        end if;
    end process;

    jk(0) <= E;
    jk(1) <= (E AND q_sig(0));
    jk(2) <= (E AND q_sig(0) AND q_sig(1));
    jk(3) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2));
    jk(4) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3));
    jk(5) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3) AND q_sig(4));
    jk(6) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3) AND q_sig(4) AND q_sig(5));
    jk(7) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3) AND q_sig(4) AND q_sig(5) AND q_sig(6));
    jk(8) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3) AND q_sig(4) AND q_sig(5) AND q_sig(6) AND q_sig(7));
    jk(9) <= (E AND q_sig(0) AND q_sig(1) AND q_sig(2) AND q_sig(3) AND q_sig(4) AND q_sig(5) AND q_sig(6) AND q_sig(7) AND q_sig(8));

    process(CLR, DIV_CLK)
    begin
        if CLR = '0' then
            q_sig <= (others => '0');
        elsif rising_edge(DIV_CLK) and E = '1' then
            q_sig(0) <= (not q_sig(0) and jk(0)) or (q_sig(0) and not jk(0));
            q_sig(1) <= (not q_sig(1) and jk(1)) or (q_sig(1) and not jk(1));
            q_sig(2) <= (not q_sig(2) and jk(2)) or (q_sig(2) and not jk(2));
            q_sig(3) <= (not q_sig(3) and jk(3)) or (q_sig(3) and not jk(3));
            q_sig(4) <= (not q_sig(4) and jk(4)) or (q_sig(4) and not jk(4));
            q_sig(5) <= (not q_sig(5) and jk(5)) or (q_sig(5) and not jk(5));
            q_sig(6) <= (not q_sig(6) and jk(6)) or (q_sig(6) and not jk(6));
            q_sig(7) <= (not q_sig(7) and jk(7)) or (q_sig(7) and not jk(7));
            q_sig(8) <= (not q_sig(8) and jk(8)) or (q_sig(8) and not jk(8));
            q_sig(9) <= (not q_sig(9) and jk(9)) or (q_sig(9) and not jk(9));
        end if;
    end process;

    Q <= q_sig;

END P4;