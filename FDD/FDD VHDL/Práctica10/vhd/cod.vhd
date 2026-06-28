
--  CYPRESS NOVA XVL Structural Architecture

--  JED2VHD Reverse Assembler - 6.3 IR 35


--    VHDL File: cod.vhd

--    Date: Sun Dec 22 23:02:52 2024

--  Disassembly from Jedec file for: c22v10

--  Device Ordercode is: PALCE22V10-25PC/PI
library ieee;
use ieee.std_logic_1164.all;

library primitive;
use primitive.primitive.all;


-- Beginning Test Bench Header

ENTITY cod IS
    PORT (
	                   e :    in std_logic_vector (8 downto 0) ;
	                   s : inout std_logic_vector (2 downto 0) ;
	                 con :    in std_logic ;
	                   y : inout std_logic
    );
END cod;

-- End of Test Bench Header

ARCHITECTURE DSMB of cod is

	signal jed_node1	: std_logic:='0' ; -- e(0)
	signal jed_node2	: std_logic:='0' ; -- e(1)
	signal jed_node3	: std_logic:='0' ; -- e(2)
	signal jed_node4	: std_logic:='0' ; -- e(3)
	signal jed_node5	: std_logic:='0' ; -- e(4)
	signal jed_node6	: std_logic:='0' ; -- e(5)
	signal jed_node7	: std_logic:='0' ; -- e(6)
	signal jed_node8	: std_logic:='0' ; -- e(7)
	signal jed_node9	: std_logic:='0' ; -- con
	signal jed_node10	: std_logic:='0' ;
	signal jed_node11	: std_logic:='0' ;
	signal jed_node12	: std_logic:='0' ;
	signal jed_node13	: std_logic:='0' ;
	signal jed_node18	: std_logic:='0' ;
	signal jed_node19	: std_logic:='0' ;
	signal jed_node20	: std_logic:='0' ;
	signal jed_node21	: std_logic:='0' ;
	signal jed_node22	: std_logic:='0' ;
	signal jed_node23	: std_logic:='0' ;
	signal jed_node24	: std_logic:='0' ;

	for all: c22v10m use entity primitive.c22v10m (sim);

SIGNAL  one:std_logic:='1';
SIGNAL  zero:std_logic:='0';
SIGNAL  jed_oept_1:std_logic:='0';
--Attribute PIN_NUMBERS of e(0):SIGNAL is "0001";

SIGNAL  jed_oept_2:std_logic:='0';
--Attribute PIN_NUMBERS of e(1):SIGNAL is "0002";

SIGNAL  jed_oept_3:std_logic:='0';
--Attribute PIN_NUMBERS of e(2):SIGNAL is "0003";

SIGNAL  jed_oept_4:std_logic:='0';
--Attribute PIN_NUMBERS of e(3):SIGNAL is "0004";

SIGNAL  jed_oept_5:std_logic:='0';
--Attribute PIN_NUMBERS of e(4):SIGNAL is "0005";

SIGNAL  jed_oept_6:std_logic:='0';
--Attribute PIN_NUMBERS of e(5):SIGNAL is "0006";

SIGNAL  jed_oept_7:std_logic:='0';
--Attribute PIN_NUMBERS of e(6):SIGNAL is "0007";

SIGNAL  jed_oept_8:std_logic:='0';
--Attribute PIN_NUMBERS of e(7):SIGNAL is "0008";

SIGNAL  jed_oept_9:std_logic:='0';
--Attribute PIN_NUMBERS of con:SIGNAL is "0009";

SIGNAL  jed_oept_14:std_logic:='0';
SIGNAL  jed_sum_14,jed_fb_14:std_logic:='0';
--Attribute PIN_NUMBERS of s(0):SIGNAL is "0014";

SIGNAL  jed_oept_15:std_logic:='0';
SIGNAL  jed_sum_15,jed_fb_15:std_logic:='0';
--Attribute PIN_NUMBERS of s(1):SIGNAL is "0015";

SIGNAL  jed_oept_16:std_logic:='0';
SIGNAL  jed_sum_16,jed_fb_16:std_logic:='0';
--Attribute PIN_NUMBERS of s(2):SIGNAL is "0016";

SIGNAL  jed_oept_17:std_logic:='0';
SIGNAL  jed_sum_17,jed_fb_17:std_logic:='0';
--Attribute PIN_NUMBERS of y:SIGNAL is "0017";

SIGNAL  jed_oept_25:std_logic:='0';
SIGNAL  jed_node25,jed_sum_25:std_logic:='0';
SIGNAL  jed_oept_26:std_logic:='0';
SIGNAL  jed_node26,jed_sum_26:std_logic:='0';

BEGIN
jed_node1 <= e(0) ;
jed_node2 <= e(1) ;
jed_node3 <= e(2) ;
jed_node4 <= e(3) ;
jed_node5 <= e(4) ;
jed_node6 <= e(5) ;
jed_node7 <= e(6) ;
jed_node8 <= e(7) ;
jed_node9 <= con ;
Mcell_14:c22v10m
generic map(comb,
   invt,
   xpin,
   	25 ns, --tpd
	25 ns, --tea
	25 ns, --ter
	15 ns, --tco
	18 ns, --ts
	0 ns, --th
	14 ns, --twh
	14 ns, --twl
	13 ns, --tcf
	25 ns, --taw
	25 ns, --tar
	25 ns, --tap
	25 ns  --tspr
)
port map(
     d=>jed_sum_14,
     clk=>jed_node1,
     oe=>jed_oept_14,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>s(0),
     fb=>jed_fb_14
   );

Mcell_15:c22v10m
generic map(comb,
   invt,
   xpin,
   	25 ns, --tpd
	25 ns, --tea
	25 ns, --ter
	15 ns, --tco
	18 ns, --ts
	0 ns, --th
	14 ns, --twh
	14 ns, --twl
	13 ns, --tcf
	25 ns, --taw
	25 ns, --tar
	25 ns, --tap
	25 ns  --tspr
)
port map(
     d=>jed_sum_15,
     clk=>jed_node1,
     oe=>jed_oept_15,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>s(1),
     fb=>jed_fb_15
   );

Mcell_16:c22v10m
generic map(comb,
   invt,
   xpin,
   	25 ns, --tpd
	25 ns, --tea
	25 ns, --ter
	15 ns, --tco
	18 ns, --ts
	0 ns, --th
	14 ns, --twh
	14 ns, --twl
	13 ns, --tcf
	25 ns, --taw
	25 ns, --tar
	25 ns, --tap
	25 ns  --tspr
)
port map(
     d=>jed_sum_16,
     clk=>jed_node1,
     oe=>jed_oept_16,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>s(2),
     fb=>jed_fb_16
   );

Mcell_17:c22v10m
generic map(comb,
   ninv,
   xpin,
   	25 ns, --tpd
	25 ns, --tea
	25 ns, --ter
	15 ns, --tco
	18 ns, --ts
	0 ns, --th
	14 ns, --twh
	14 ns, --twl
	13 ns, --tcf
	25 ns, --taw
	25 ns, --tar
	25 ns, --tap
	25 ns  --tspr
)
port map(
     d=>jed_sum_17,
     clk=>jed_node1,
     oe=>jed_oept_17,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>y,
     fb=>jed_fb_17
   );

jed_node25<=jed_sum_25;
jed_node26<=jed_sum_26;
jed_oept_14<=(one);

jed_sum_14<= (((jed_node3) and not(jed_node4) and not(jed_node6) and not(jed_node8)
 and (jed_node9)) or
(not(jed_node2) and not(jed_node4) and not(jed_node6)
 and not(jed_node8) and (jed_node9)) or
((jed_node5) and not(jed_node6) and not(jed_node8) and (jed_node9)
) or
((jed_node7) and not(jed_node8) and (jed_node9)));

jed_oept_15<=(one);

jed_sum_15<= ((not(jed_node3) and not(jed_node4) and not(jed_node7)
 and not(jed_node8) and (jed_node9)) or
((jed_node6) and not(jed_node7) and not(jed_node8) and (jed_node9)
) or
((jed_node5) and not(jed_node7) and not(jed_node8) and (jed_node9)
));

jed_oept_16<=(one);

jed_sum_16<= ((not(jed_node5) and not(jed_node6) and not(jed_node7)
 and not(jed_node8) and (jed_node9)));

jed_oept_17<=(one);

jed_sum_17<= ((not(jed_node1) and not(jed_node2) and not(jed_node3)
 and not(jed_node4) and not(jed_node5) and not(jed_node6)
 and not(jed_node7) and not(jed_node8)) or
(not(jed_node9)));

end DSMB;
