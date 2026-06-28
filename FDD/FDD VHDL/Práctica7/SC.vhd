library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

--SUMADOR COMPLETO
entity SC is
    Port (
        A     : in  STD_LOGIC;
        B     : in  STD_LOGIC;
        Cin   : in  STD_LOGIC;
        Sum   : out STD_LOGIC;
        Cout  : out STD_LOGIC
    );
end SC;

architecture arch_SC of SC is
begin
    
    Sum  <= A xor B xor Cin;
    Cout <= (A and B) or (B and Cin) or (A and Cin);
end arch_SC;
