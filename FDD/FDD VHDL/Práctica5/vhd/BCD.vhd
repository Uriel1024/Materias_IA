
--  CYPRESS NOVA XVL Structural Architecture

--  JED2VHD Reverse Assembler - 6.3 IR 35


--    VHDL File: BCD.vhd

--    Date: Thu Nov 07 11:26:39 2024

--  Disassembly from Jedec file for: c22v10

--  Device Ordercode is: PALCE22V10-25PC/PI
library ieee;
use ieee.std_logic_1164.all;

library primitive;
use primitive.primitive.all;


-- Beginning Test Bench Header

ENTITY bcd IS
    PORT (
	                   a :    in std_logic ;
	                   b :    in std_logic ;
	                   c :    in std_logic ;
	                   d :    in std_logic ;
	                   f : inout std_logic ;
	                   g : inout std_logic ;
	                   h : inout std_logic ;
	                   i : inout std_logic ;
	                   j : inout std_logic ;
	                   k : inout std_logic ;
	                   l : inout std_logic
    );
END bcd;

-- End of Test Bench Header

ARCHITECTURE DSMB of bcd is

	signal jed_node1	: std_logic:='0' ; -- d
	signal jed_node2	: std_logic:='0' ; -- c
	signal jed_node3	: std_logic:='0' ; -- b
	signal jed_node4	: std_logic:='0' ; -- a
	signal jed_node5	: std_logic:='0' ;
	signal jed_node6	: std_logic:='0' ;
	signal jed_node7	: std_logic:='0' ;
	signal jed_node8	: std_logic:='0' ;
	signal jed_node9	: std_logic:='0' ;
	signal jed_node10	: std_logic:='0' ;
	signal jed_node11	: std_logic:='0' ;
	signal jed_node12	: std_logic:='0' ;
	signal jed_node13	: std_logic:='0' ;
	signal jed_node21	: std_logic:='0' ;
	signal jed_node22	: std_logic:='0' ;
	signal jed_node23	: std_logic:='0' ;
	signal jed_node24	: std_logic:='0' ;

	for all: c22v10m use entity primitive.c22v10m (sim);

SIGNAL  one:std_logic:='1';
SIGNAL  zero:std_logic:='0';
SIGNAL  jed_oept_1:std_logic:='0';
--Attribute PIN_NUMBERS of d:SIGNAL is "0001";

SIGNAL  jed_oept_2:std_logic:='0';
--Attribute PIN_NUMBERS of c:SIGNAL is "0002";

SIGNAL  jed_oept_3:std_logic:='0';
--Attribute PIN_NUMBERS of b:SIGNAL is "0003";

SIGNAL  jed_oept_4:std_logic:='0';
--Attribute PIN_NUMBERS of a:SIGNAL is "0004";

SIGNAL  jed_oept_14:std_logic:='0';
SIGNAL  jed_sum_14,jed_fb_14:std_logic:='0';
--Attribute PIN_NUMBERS of f:SIGNAL is "0014";

SIGNAL  jed_oept_15:std_logic:='0';
SIGNAL  jed_sum_15,jed_fb_15:std_logic:='0';
--Attribute PIN_NUMBERS of g:SIGNAL is "0015";

SIGNAL  jed_oept_16:std_logic:='0';
SIGNAL  jed_sum_16,jed_fb_16:std_logic:='0';
--Attribute PIN_NUMBERS of h:SIGNAL is "0016";

SIGNAL  jed_oept_17:std_logic:='0';
SIGNAL  jed_sum_17,jed_fb_17:std_logic:='0';
--Attribute PIN_NUMBERS of i:SIGNAL is "0017";

SIGNAL  jed_oept_18:std_logic:='0';
SIGNAL  jed_sum_18,jed_fb_18:std_logic:='0';
--Attribute PIN_NUMBERS of j:SIGNAL is "0018";

SIGNAL  jed_oept_19:std_logic:='0';
SIGNAL  jed_sum_19,jed_fb_19:std_logic:='0';
--Attribute PIN_NUMBERS of k:SIGNAL is "0019";

SIGNAL  jed_oept_20:std_logic:='0';
SIGNAL  jed_sum_20,jed_fb_20:std_logic:='0';
--Attribute PIN_NUMBERS of l:SIGNAL is "0020";

SIGNAL  jed_oept_25:std_logic:='0';
SIGNAL  jed_node25,jed_sum_25:std_logic:='0';
SIGNAL  jed_oept_26:std_logic:='0';
SIGNAL  jed_node26,jed_sum_26:std_logic:='0';

BEGIN
jed_node1 <= d ;
jed_node2 <= c ;
jed_node3 <= b ;
jed_node4 <= a ;
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
     y=>f,
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
     y=>g,
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
     y=>h,
     fb=>jed_fb_16
   );

Mcell_17:c22v10m
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
     d=>jed_sum_17,
     clk=>jed_node1,
     oe=>jed_oept_17,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>i,
     fb=>jed_fb_17
   );

Mcell_18:c22v10m
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
     d=>jed_sum_18,
     clk=>jed_node1,
     oe=>jed_oept_18,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>j,
     fb=>jed_fb_18
   );

Mcell_19:c22v10m
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
     d=>jed_sum_19,
     clk=>jed_node1,
     oe=>jed_oept_19,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>k,
     fb=>jed_fb_19
   );

Mcell_20:c22v10m
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
     d=>jed_sum_20,
     clk=>jed_node1,
     oe=>jed_oept_20,
     ss=>jed_sum_26,
     ar=>jed_sum_25,
     y=>l,
     fb=>jed_fb_20
   );

jed_node25<=jed_sum_25;
jed_node26<=jed_sum_26;
jed_oept_14<=(one);

jed_sum_14<= (((jed_node1) and not(jed_node2) and not(jed_node3) and not(jed_node4)
) or
(not(jed_node1) and not(jed_node2) and (jed_node3) and not(jed_node4)
));

jed_oept_15<=(one);

jed_sum_15<= (((jed_node1) and not(jed_node2) and (jed_node3)) or
(not(jed_node1) and (jed_node2) and (jed_node3)));

jed_oept_16<=(one);

jed_sum_16<= ((not(jed_node1) and (jed_node2) and not(jed_node3)));

jed_oept_17<=(one);

jed_sum_17<= (((jed_node1) and not(jed_node2) and not(jed_node3) and not(jed_node4)
) or
(not(jed_node1) and not(jed_node2) and (jed_node3) and not(jed_node4)
) or
((jed_node1) and (jed_node2) and (jed_node3) and not(jed_node4)
));

jed_oept_18<=(one);

jed_sum_18<= ((not(jed_node1) and (jed_node2)) or
(not(jed_node1) and not(jed_node3)));

jed_oept_19<=(one);

jed_sum_19<= (((jed_node1) and (jed_node2) and not(jed_node4)) or
((jed_node1) and not(jed_node3) and not(jed_node4)) or
((jed_node2) and not(jed_node3) and not(jed_node4)));

jed_oept_20<=(one);

jed_sum_20<= (((jed_node1) and (jed_node2) and (jed_node3) and not(jed_node4)
) or
(not(jed_node2) and not(jed_node3) and not(jed_node4)
));

end DSMB;
