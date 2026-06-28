library ieee;
use ieee.std_logic_1164.all;
USE IEEE.STD_LOGIC_ARITH.ALL;
USE IEEE.STD_LOGIC_UNSIGNED.ALL;
USE IEEE.NUMERIC_STD.ALL;  

entity p3 is
	port(CLK, CLR, PRE, T, D : in std_logic;
			SEL, SR, JK : in std_logic_vector(1 downto 0);
			Q : inout std_logic_vector(1 downto 0));
end entity;

architecture practica of p3 is

SIGNAL CONTADOR: STD_LOGIC_VECTOR (24 DOWNTO 0):=(OTHERS => '0');
CONSTANT DIVISOR1: INTEGER:= 27000000;
SIGNAL CRISTAL: STD_LOGIC;
SIGNAL CLKOUT: STD_LOGIC;

begin
    RELOJ: PROCESS (CLK)
    BEGIN
        IF (CLK'EVENT AND CLK='1') THEN
                CRISTAL <= CLK;
            IF (CONTADOR=DIVISOR1) THEN
                CONTADOR <= (OTHERS=>'0');
                CLKOUT <= NOT CLKOUT;
            ELSE
                CONTADOR<= CONTADOR + 1;
            END IF;
        END IF;
    END PROCESS RELOJ;
    
    
    CUERPO:process(CLKOUT, CLR, PRE, SEL, SR, Q, JK, T, D)
    begin
        -- condicion del PRESET
        if(CLR = '0') then
            Q(1) <= '1';
            Q(0)<='0';

        elsif RISING_EDGE(CLKOUT) then
         
            if(PRE='1') then
            
            Q(1)<='1';
            Q(0)<='0';
            
            else
            -- MUX
            case SEL is
                -- SR
                when "00" => Q(1) <= SR(1) or ( Q(1) and not SR(0) );
                             Q(0) <= NOT (SR(1) OR (NOT SR(0) AND Q(1)));
                -- JK
			    when "01" => Q(1) <= ( JK(1) and Q(0) ) or ( Q(1) and not JK(0) );
                             Q(0) <= NOT ((NOT JK(0) AND Q(1)) OR (JK(1) AND Q(0)));
                -- T
			    when "10" => Q(1) <= ( T xor Q(1) );
                             Q(0) <= NOT ((NOT T AND Q(1)) OR (T AND Q(0)));
                -- D+
			    when others => Q(1) <= D;
                               Q(0) <= NOT D;
            end case;
        -- reloj inactivo
        end if;
        if (SEL = "00" AND SR = "11" ) THEN Q <= "11";
        END IF;
        End if;

    end process CUERPO;
end practica;