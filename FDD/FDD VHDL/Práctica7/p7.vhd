library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
--MAIN

entity SumadorDosBits is
    Port (
        A     : in  STD_LOGIC_VECTOR(1 downto 0); 
        B     : in  STD_LOGIC_VECTOR(1 downto 0); 
        Cin   : in  STD_LOGIC;                   
        Sum   : out STD_LOGIC_VECTOR(1 downto 0); 
        Cout  : out STD_LOGIC         
    );

    attribute pin_numbers of SumadorDosBits: entity is
	"Sum(0):14 Sum(1):15 Cout:16 "
	& "A(0):1 A(1):2 B(0):3 B(0):4 ";

end SumadorDosBits;

architecture arch_SumadorDosBits of SumadorDosBits is

   
    component MS 
        Port (
            A     : in  STD_LOGIC;
            B     : in  STD_LOGIC;
            Sum   : out STD_LOGIC;
            Cout  : out STD_LOGIC
        );
    end component;

    component SC
        Port (
            A     : in  STD_LOGIC;
            B     : in  STD_LOGIC;
            Cin   : in  STD_LOGIC;
            Sum   : out STD_LOGIC;
            Cout  : out STD_LOGIC
        );
    end component;

    
    signal Carry : STD_LOGIC_VECTOR(1 downto 0);

begin

   
    HA: MS
        port map (
            A     => A(0),
            B     => B(0),
            Sum   => Sum(0),
            Cout  => Carry(0)
        );

    FA: SC
        port map (
            A     => A(1),
            B     => B(1),
            Cin   => Carry(0),
            Sum   => Sum(1),
            Cout  => Cout
        );

end arch_SumadorDosBits;

