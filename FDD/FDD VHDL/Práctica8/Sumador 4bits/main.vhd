library ieee;
use ieee.std_logic_1164.all;
		
entity sum_res is
	port (
			c1: in std_logic;
			op: in std_logic;
		    A, B : in std_logic_vector(3 downto 0); 
		    S : out std_logic_vector(3 downto 0); 
		    cout : out std_logic;
			sel : out std_logic
		);	
		attribute pin_numbers of sum_res: entity is
		"A(0):1 A(1):2 A(2):3 A(3):4 B(0):5 B(1):6 B(2):7 B(3):8 op:9 "
		& " S(0):14 S(1):15 S(2):16 S(3):17 cout:18 sel:19 ";		
		
end sum_res;
		

architecture asum_res of sum_res is
		signal c: std_logic_vector(43 downto 0);
		ATTRIBUTE synthesis_off OF c : SIGNAL IS true;
		begin
			process(op,A,B,S,cout,C,c1)
			begin
				if op = '1' then 
					sel <= '1';			

        			S(0) <= C1 xor B(0) xor A(0);
        			C(1) <= (not(A(0)) and C1) or (B(0) and C1) or (not(A(0)) and B(0));
        
			        S(1) <= C(1) xor B(1) xor A(1);
			        C(2) <= (not(A(1)) and C(1)) or (B(1) and C(1)) or (not(A(1)) and B(1));
			        
			        S(2) <= C(2) xor B(2) xor A(2);
			        C(3) <= (not(A(2)) and C(2)) or (B(2) and C(2)) or (not(A(2)) and B(2));
			        
			        S(3) <= C(3) xor B(3) xor A(3);
			        cout <= (not(A(3)) and C(3)) or (B(3) and C(3)) or (not(A(3)) and B(3));										
			
				elsif op = '0' then  
					
					sel <= '0';		
			
					S(0) <= A(0) xor B(0);
					C(0) <= A(0) and B(0);
					S(1) <= (A(1) xor B(1) xor C(0));
					C(1) <= (A(1) and B(1)) or (C(0) and (A(1) xor B(1)));
				    S(2) <= (A(2) xor B(2) xor C(1));
					C(2) <= (A(2) and B(2)) or (C(1) and (A(2) xor B(2)));
					S(3) <= (A(3) xor B(3)) xor C(2);
					cout <= (A(3) and B(3)) or (C(2) and (A(3)  xor B(3)));
	
	
				end if;
			  end process;
	end asum_res;
	
		
		
	
	

