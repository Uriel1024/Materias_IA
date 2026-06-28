library ieee;
use ieee.std_logic_1164.all;

entity sum_res is
    port (
		cam : in std_logic;
        A, B : in std_logic_vector(3 downto 0); 
        S : out std_logic_vector(3 downto 0); 
        cout : out std_logic                    
    );	
	attribute pin_numbers of sum_res: entity is
	"A(0):1 A(1):2 A(2):3 A(3):4 B(0):5 B(1):6 B(2):7 B(3):8"
	& " S(0):14 S(1):15 S(2):16 S(3):17 cout:18 cam:9 ";		
	
		
end sum_res;

architecture asum_res of sum_res is
signal c: std_logic_vector(4 downto 0);
signal bcom : std_logic_vector(3 downto 0); --para el complemento a 2
ATTRIBUTE synthesis_off OF c : SIGNAL IS true;
begin
	process(A,B,cam,c,bcom)
	begin 
		if cam = '1' then
			--operaciones para el sumador 
			S(0) <= A(0) xor B(0);
			C(0) <= A(0) and B(0);
			S(1) <= (A(1) xor B(1) xor C(0));
			C(1) <= (A(1) and B(1)) or (C(0) and (A(1) xor B(1)));
		    S(2) <= (A(2) xor B(2) xor C(1));
			C(2) <= (A(2) and B(2)) or (C(1) and (A(2) xor B(2)));
			S(3) <= (A(3) xor B(3)) xor C(2);
			cout <= (A(3) and B(3)) or (C(2) and (A(3)  xor B(3)));
		else
			c(0) <= '1';
			bcom(0) <= not b(0) xor c(0);
			c(1) <= not B(0) and C(0);
			

			bcom(1) <= not b(1) xor c(1);
			c(2) <= not B(1) and C(1);

			
			bcom(2) <= not b(2) xor c(2);
			c(3) <= not B(2) and C(2);

			
			bcom(3) <= not b(3) xor c(3);
			c(4) <= not B(3) and C(3);
			
			S(0) <= A(0) xor bcom(0);
			C(0) <= A(0) and bcom(0);

			S(1) <= (A(1) xor bcom(1)) xor c(0);
			c(1) <= (A(1) and bcom(1)) or (c(0) and (A(1) xor bcom(1)));

			S(2) <= (A(2) xor bcom(2)) xor c(1);
		    c(2) <= (A(2) and bcom(2)) or (c(1) and (A(2) xor bcom(2)));

			S(3) <= (A(3) xor bcom(3)) xor c(2);
		    Cout <= (A(3) and bcom(3)) or (c(2) and (A(3) xor bcom(3)));

		end if;
	end process;
 end asum_res;





