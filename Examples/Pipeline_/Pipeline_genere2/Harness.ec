node Harness
  (e1_P: int)
returns
  (noError: bool);

var
  V80_e2: int;
  V81_e3: int;
  V82_e4: int;
  V83_e5: int;
  V84_e1: int;
  V85_a1nbrFired: int;
  V86_a2nbrFired: int;
  V87_endnbrFired: int;
  V88_positiveValues: bool;
  V256_e2_C: int;
  V257_e2_P: int;
  V258_e3_C: int;
  V259_e3_P: int;
  V260_e4_C: int;
  V261_e4_P: int;
  V262_e5_C: int;
  V263_e5_P: int;
  V264_e1_C: int;
  V265_e5out_PD: int;
  V266_e4_PD: int;
  V267_e2_PD: int;
  V268_e3_PD: int;

let
  noError = (V88_positiveValues and true);
  V80_e2 = (0 -> (pre ((V80_e2 - V256_e2_C) + V257_e2_P)));
  V81_e3 = (5 -> (pre ((V81_e3 - V258_e3_C) + V259_e3_P)));
  V82_e4 = (0 -> (pre ((V82_e4 - V260_e4_C) + V261_e4_P)));
  V83_e5 = (1 -> (pre ((V83_e5 - V262_e5_C) + V263_e5_P)));
  V84_e1 = (0 -> (pre (if (((V84_e1 - V264_e1_C) + e1_P) < 8) then ((V84_e1 - 
  V264_e1_C) + e1_P) else (V84_e1 - V264_e1_C))));
  V85_a1nbrFired = (if (if (((V84_e1 >= (1 * 1)) and (V81_e3 >= (1 * 1))) and (
  V83_e5 >= (1 * 1))) then true else false) then 1 else 0);
  V86_a2nbrFired = (if (if (V82_e4 >= (3 * 1)) then true else false) then 3 
  else (if (if (V82_e4 >= (2 * 1)) then true else false) then 2 else (if (if (
  V82_e4 >= (1 * 1)) then true else false) then 1 else 0)));
  V87_endnbrFired = (if (if (V80_e2 >= (1 * 1)) then true else false) then 1 
  else 0);
  V88_positiveValues = ((((((((V80_e2 >= 0) and (V81_e3 >= 0)) and (V82_e4 >= 0
  )) and (V83_e5 >= 0)) and (V84_e1 >= 0)) and (V85_a1nbrFired >= 0)) and (
  V86_a2nbrFired >= 0)) and (V87_endnbrFired >= 0));
  V256_e2_C = (V87_endnbrFired * 1);
  V257_e2_P = (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre 
  (0 -> (pre (0 -> (pre (0 -> (pre V267_e2_PD))))))))))))))))));
  V258_e3_C = (V85_a1nbrFired * 1);
  V259_e3_P = (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre (0 -> (pre 
  (0 -> (pre (0 -> (pre (0 -> (pre V268_e3_PD))))))))))))))))));
  V260_e4_C = (V86_a2nbrFired * 1);
  V261_e4_P = (0 -> (pre V266_e4_PD));
  V262_e5_C = (V85_a1nbrFired * 1);
  V263_e5_P = (0 -> (pre V265_e5out_PD));
  V264_e1_C = (V85_a1nbrFired * 1);
  V265_e5out_PD = (V85_a1nbrFired * 1);
  V266_e4_PD = (V85_a1nbrFired * 1);
  V267_e2_PD = (V86_a2nbrFired * 1);
  V268_e3_PD = (V86_a2nbrFired * 1);
tel

