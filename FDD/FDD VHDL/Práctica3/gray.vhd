library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
	
entity gray is
	    port (
	        A, B, C, D : in std_logic;  
	        F, G, H, I : out std_logic
	    );
end gray;

architecture agray of gray is
	begin
	    F <= C xor D; 
	    G <= C xor B;
	    H <= B xor A;
	    I <= A;
	end agray;