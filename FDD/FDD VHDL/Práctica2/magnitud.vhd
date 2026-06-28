library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity magnitud is
	port( 
		a,b,c,d : in std_logic; 
		f,g,h : out std_logic
	);
end magnitud; 



 architecture amagnitud of magnitud is  
 begin 
 	f <= (a and not b) or (a and not d) or (a and not c) or (b and not c and not d); 
	g <= (not a and c) or (not b and c and d) or (not a and not b and d);
	h <= (a and not b and c and not d) or (a and b and c and d) or (not a and b and not c and d) or (not a and not b and not c and not d);
end amagnitud; 
