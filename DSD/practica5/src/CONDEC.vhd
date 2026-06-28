library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity condec is 
    port (
        clk, clr : in std_logic;
        c : in std_logic_vector(1 downto 0);
        e: in std_logic_vector(9 downto 0); 
        display : inout std_logic_vector(6 downto 0)
    );
end entity;

architecture a_condec of condec is
    constant dig0 : std_logic_vector(6 downto 0) := "1111110";
    constant dig1 : std_logic_vector(6 downto 0) := "0110000";
    constant dig2 : std_logic_vector(6 downto 0) := "1101101";
    constant dig3 : std_logic_vector(6 downto 0) := "1111001";
    constant dig4 : std_logic_vector(6 downto 0) := "0110011";
    constant dig5 : std_logic_vector(6 downto 0) := "1011011";
    constant dig6 : std_logic_vector(6 downto 0) := "1011111";
    constant dig7 : std_logic_vector(6 downto 0) := "1110000";
    constant dig8 : std_logic_vector(6 downto 0) := "1111111";
    constant dig9 : std_logic_vector(6 downto 0) := "1111011";
    constant max_count : natural := 50000000;
    signal enable : std_logic := '0';
    signal count  : natural range 0 to max_count := 0;

    signal priority_value : std_logic_vector(6 downto 0) := "0000000"; 
begin
    
    process(clk)
    begin
        if rising_edge(clk) then
            if count = max_count - 1 then
                enable <= '1';
                count <= 0;
            else
                enable <= '0';
                count <= count + 1;
            end if;
        end if;
    end process;

    -- Codificador de prioridad
    process (e)
    begin
        if e(9) = '1' then
            priority_value <= dig9;
        elsif e(8) = '1' then
            priority_value <= dig8;
        elsif e(7) = '1' then
            priority_value <= dig7;
        elsif e(6) = '1' then
            priority_value <= dig6;
        elsif e(5) = '1' then
            priority_value <= dig5;
        elsif e(4) = '1' then
            priority_value <= dig4;
        elsif e(3) = '1' then
            priority_value <= dig3;
        elsif e(2) = '1' then
            priority_value <= dig2;
        elsif e(1) = '1' then
            priority_value <= dig1;
        elsif e(0) = '1' then
            priority_value <= dig0;
        else
            priority_value <= "0000000"; 
        end if;
    end process;

    -- divisor
    process (clr, enable)
    begin
        if (clr = '0') then
            display <= dig0;
        elsif (rising_edge(enable)) then
            case c is
                when "00" =>  -- Contador ascendente
                    case display is
                        when dig0 => display <= dig1;
                        when dig1 => display <= dig2;
                        when dig2 => display <= dig3;
                        when dig3 => display <= dig4;
                        when dig4 => display <= dig5;
                        when dig5 => display <= dig6;
                        when dig6 => display <= dig7;
                        when dig7 => display <= dig8;
                        when dig8 => display <= dig9;
                        when dig9 => display <= dig0;
                        when others => display <= "0000000"; -- apagar 
                    end case;

                when "01" =>  -- Contador descendente
                    case display is
                        when dig0 => display <= dig9;
                        when dig1 => display <= dig0;
                        when dig2 => display <= dig1;
                        when dig3 => display <= dig2;
                        when dig4 => display <= dig3;
                        when dig5 => display <= dig4;
                        when dig6 => display <= dig5;
                        when dig7 => display <= dig6;
                        when dig8 => display <= dig7;
                        when dig9 => display <= dig8;
                        when others => display <= "0000000"; -- apagar 
                    end case;

                when "10" =>  -- mostrar valor del codificador de prioridad
                    display <= priority_value;

                when others =>  -- mantener el valor actual en el display
                    display <= display;
            end case;
        end if;
    end process;
end architecture a_condec;