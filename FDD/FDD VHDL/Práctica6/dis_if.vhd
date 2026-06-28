library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

	
entity display is
    PORT (
		E : in std_logic_vector(4 downto 0);
		DIS : out std_logic_vector(6 downto 0)
	);
	attribute pin_numbers of display: entity is 
	" E(0):1 E(1):2 E(2):3 E(3):4 E(4):5" 
	& " DIS(0):20 DIS(1):19 DIS(2):18 DIS(3):17 DIS(4):16  " 
    & " DIS(5):15 DIS(6):14";
END display;


architecture adisplay of display is	
begin
	pdisplay : process(E)
	begin
		case (E) is        --ABCDEFG
			when "00000" => DIS<="1111110"; --0
			when "00001" => DIS<="0110000"; --1
			when "00010" => DIS<="1101101"; --2
			when "00011" => DIS<="1111001"; --3
			when "00100" => DIS<="0110011"; --4
			when "00101" => DIS<="1011011"; --5
 	    	when "00110" => DIS<="1011111"; --6
			when "00111" => DIS<="1110000"; --7
			when "01000" => DIS<="1111111"; --8
			when "01001" => DIS<="1111011"; --9
			when "01010" => DIS<="1110111"; --A
			when "01011" => DIS<="0011111"; --b
			when "01100" => DIS<="1001110"; --C
			when "01101" => DIS<="0111101"; --d
			when "01110" => DIS<="1001111"; --E
			when "01111" => DIS<="1000111"; --F 
			when "10000" => DIS<="1111110"; --0
			when "10001" => DIS<="0110000"; --1
			when "10010" => DIS<="1101101"; --2
			when "10011" => DIS<="1111001"; --3
			when "10100" => DIS<="0110011"; --4
			when "10101" => DIS<="1011011"; --5
 	    	when "10110" => DIS<="1011111"; --6
			when "10111" => DIS<="1110000"; --7
			when "11000" => DIS<="1111111"; --8
			when "11001" => DIS<="1111011"; --9
			when others => DIS<="0000001" ; --void
			end case;

		end process pdisplay;
	end adisplay;
