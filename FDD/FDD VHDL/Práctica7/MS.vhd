library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
--MEDIO SUMADOR
entity MS is
    Port (
        A     : in  STD_LOGIC;
        B     : in  STD_LOGIC;
        Sum   : out STD_LOGIC;
        Cout  : out STD_LOGIC
    );
end MS;


architecture arch_MS of MS is
begin
    
    Sum  <= A xor B;
    Cout <= A and B;
end arch_MS;

