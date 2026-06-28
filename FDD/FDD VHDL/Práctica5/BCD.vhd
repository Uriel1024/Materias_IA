library IEEE;
use IEEE.STD_LOGIC_1164.ALL;


entity bcd is
    PORT (
        A, B, C, D : IN std_logic;   -- Entradas
        F, G, H, I, J, K, L : OUT std_logic  -- Salida
	);

	attribute pin_numbers of bcd: entity is
	"A:4 B:3 C:2 D:1 F:14 G:15 H:16 I:17 J:18 K:19 L:20 ";

    
END bcd;

 



 architecture abcd of bcd is  
 begin 
 	f <= (c or a or (b and d) or (not b and not d));
	g <= not b or (not c and not d) or (c and d);
	h <= not c or d or b;
	i <= (not b and c) or a or (not b and not d) or (c and not d) or (b and not c and d);
	j <= (not b and not d) or (c and not d);
	k <= (not c and not d) or a or (b and not c) or (b and not d);
	l <= ((c and not d) or (b and not c) or (not b and c) or a);
end abcd; 
