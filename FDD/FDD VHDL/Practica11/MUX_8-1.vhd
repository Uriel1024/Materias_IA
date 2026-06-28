library ieee;
use ieee.std_logic_1164.all;

entity MUX_8a1 is port (
	I0, I1, I2, I3, I4, I5, I6, I7 : in std_logic;
	S : in std_logic_vector (0 to 2);
	Y : out std_logic);
end MUX_8a1;

architecture AMUX_8a1 of MUX_8a1 is
begin
with S select
	Y <=I0 when "000",
		I1 when "001",
		I2 when "010",
		I3 when "011",
		I4 when "100",
		I5 when "101",
		I6 when "110",
		I7 when OTHERS;
end AMUX_8a1;
