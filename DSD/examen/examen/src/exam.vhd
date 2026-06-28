library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity parity_detector_mealy is
    port (
        clk          : in  std_logic;     -- Reloj de 27 MHz
        clr          : in  std_logic;     -- Reset asincrónico
        X            : in  std_logic;     -- Entrada serial
        Parity_Out   : out std_logic;     -- Salida de paridad (1 si impar, 0 si par)
        Input_Enable : out std_logic;     -- Indica cuándo ingresar dato
        Cycle_End    : out std_logic      -- Pulso al finalizar grupo de 3 bits
    );
end entity parity_detector_mealy;

architecture Behavioral of parity_detector_mealy is

    -- Estados de la máquina de Mealy
    type state_type is (S0, S1, S2);
    signal current_state : state_type := S0;
    signal next_state    : state_type := S0;

    signal current_parity : std_logic := '0';
    signal next_parity    : std_logic := '0';

    -- División de reloj para generar pulso más lento (DIV_CLK)
    signal contador : unsigned(31 downto 0) := (others => '0');
    signal div_clk  : std_logic := '0';
    constant divisor : integer := 27000000;  -- Divide a ~1 Hz si clk = 27 MHz

    -- Contador de pulsos para detectar 3 bits
    signal pulse_count    : integer range 0 to 2 := 0;

    -- Señal para generar pulso de fin de ciclo
    signal cycle_end_reg : std_logic := '0';

begin

    -- Salida final del pulso
    Cycle_End <= cycle_end_reg;

    -- División de reloj: genera pulso lento div_clk
    process(clk)
    begin
        if rising_edge(clk) then
            if contador >= to_unsigned(divisor, 32) then
                contador <= (others => '0');
                div_clk <= not div_clk;
            else
                contador <= contador + 1;
            end if;
        end if;
    end process;

    -- Proceso secuencial: actualiza estado, paridad, contador, pulso de ciclo
    process(div_clk, clr)
    begin
        if clr = '1' then
            current_state  <= S0;
            current_parity <= '0';
            pulse_count    <= 0;
            cycle_end_reg  <= '0';
        elsif rising_edge(div_clk) then
            current_state  <= next_state;
            current_parity <= next_parity;

            -- Pulso de ciclo: se activa cada 3 entradas
            if pulse_count = 2 then
                pulse_count   <= 0;
                cycle_end_reg <= '1';  -- Pulso de 1 ciclo
            else
                pulse_count   <= pulse_count + 1;
                cycle_end_reg <= '0';
            end if;
        end if;
    end process;

    -- Proceso combinacional: lógica de transición de estados y salidas
    process(current_state, X, current_parity)
    begin
        -- Valores por defecto
        next_state    <= current_state;
        next_parity   <= current_parity;
        Parity_Out    <= '0';
        Input_Enable  <= '0';

        case current_state is
            when S0 =>
                Input_Enable <= '1';  -- Activar entrada
                if X = '0' then
                    next_state  <= S1;
                    next_parity <= '0';
                else
                    next_state  <= S1;
                    next_parity <= '1';
                end if;

            when S1 =>
                if X = '0' then
                    next_state  <= S2;
                    next_parity <= current_parity xor '0';
                else
                    next_state  <= S2;
                    next_parity <= current_parity xor '1';
                end if;

            when S2 =>
                next_state  <= S0;
                next_parity <= current_parity xor X;
                Parity_Out  <= current_parity xor X;
                -- Nota: Cycle_End ahora se genera de forma síncrona fuera
        end case;
    end process;

end architecture Behavioral;
