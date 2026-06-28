library IEEE;
use ieee.std_logic_1164.all;

	entity cod is 
port(
	e : in std_logic_vector(8 downto 0);
	s : out std_logic_vector(2 downto 0);	
	con: in std_logic;
	y : out std_logic
);
	 attribute pin_numbers of cod: entity is	
	"e(0):1 e(1):2 e(2):3 e(3):4 e(4):5 e(5):6 e(6):7 e(7):8 con:9  "
	& "s(0):14 s(1):15 s(2):16 y:17 ";

end cod;

architecture acod of cod is
begin 
	
	pdec: process(e,con)
	begin
			if con = '0' then
				s <="111";
				y <= '1';
			else 
				if(e(7) = '1') then
			 		s <= "111"; 
					y <= '0';
				elsif(e(6) = '1') then
					s <= "110";
					y <= '0';
				elsif(e(5) = '1') then
					s <= "101";
					y <= '0';
				elsif(e(4) = '1') then
					s <= "100";
					y <= '0';
				elsif(e(3) = '1') then
					s <= "011";
					y <= '0';
				elsif(e(2) = '1') then
					s <= "010";
					y <= '0';
				elsif(e(1) = '1') then
					s <= "001";
					y <= '0';
				elsif(e(0) = '1') then
					s <= "000";
					y <= '0';
				else s <= "000";
					y <= '1';	
				end if;
			end if;
		end process;
	 end 	acod;
