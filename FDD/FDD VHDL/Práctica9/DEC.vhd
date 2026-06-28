--DEC 4x10

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity DEC is
    PORT (
        E : in std_logic_vector(4 downto 0);
        D : out std_logic_vector(9 downto 0)
    );
    attribute pin_numbers of DEC: entity is 
    " E(0):1 E(1):2 E(2):3 E(3):4 E(4):5 " 
    & " D(0):14 D(1):15 D(2):16 D(3):17 D(4):18 "
    & "D(5):19 D(6):20 D(7):21 D(8):22 D(9):23 ";
END;

architecture ADEC of DEC is
begin
    pdec : process(E)
    begin
        if E(0) = '1' then
          D<= "0000000000";
        else 
            case E(4 downto 1) is
				when "0000" => D <= "0000000001";
                when "0001" => D <= "0000000011"; -- BCD = 0 
                when "0010" => D <= "0000000101"; -- BCD = 2
                when "0011" => D <= "0000001001"; -- BCD = 3
                when "0100" => D <= "0000010001"; -- BCD = 4
                when "0101" => D <= "0000100001"; -- BCD = 5
                when "0110" => D <= "0001000001"; -- BCD = 6
                when "0111" => D <= "0010000001"; -- BCD = 7
                when "1000" => D <= "0100000001"; -- BCD = 8
                when "1001" => D <= "1000000001"; -- BCD = 9
                when others => D <= "1111111111"; -- BCD > 9							wh
            end case;
        end if;
    end process pdec;
end ADEC;

