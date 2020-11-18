node top
  (x: bool)
returns
  (blocage: bool);

var
  V4_a: bool;
  V5_b: bool;

let
  blocage = (V4_a or V5_b);
  V4_a = (if x then x else (pre x));
  V5_b = (not V4_a);
tel

