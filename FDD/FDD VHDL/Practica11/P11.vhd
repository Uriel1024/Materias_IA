library ieee;
use ieee.std_logic_1164.all;

entity P11_MUX is port (
	A,B,C,D,X,Y,Z: in std_logic;
	F : out std_logic);
end P11_MUX;

architecture P11 of P11_MUX is

component MUX_8a1 port (
	I0, I1, I2, I3, I4, I5, I6, I7 : in std_logic;
	S : in std_logic_vector (0 to 2);
	Y : out std_logic);
end component;

begin

	Bloque1: MUX_8a1 port map(I0=>X ,I1=>Z ,I2=>X ,I3=>A ,I4=>Y ,I5=>Z ,I6=>Y ,I7=>A ,S(2)=>D ,S(1)=>C ,S(0)=>B ,Y=>F);

end P11;
