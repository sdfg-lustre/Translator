package SDFG2LUSTRE_cycle_cycle;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All rights reserved
 */
public class Actor {

    private final String name;
    private final String duration;
    private final String[][] PortsArray;
    private final int maxAutoConc;
    private static int maxAutoConcurrency;
    private int indice;

    public Actor(String name, String duree, int nberPort, int maxAutoConc) {
        this.name = name;
        this.duration = duree;
        this.PortsArray = new String[nberPort][3];
        this.maxAutoConc = maxAutoConc;
        if (maxAutoConc > maxAutoConcurrency) {
            maxAutoConcurrency = maxAutoConc;
        }
    }
    
    public static int getMaxAutoConcurrences() {
        return maxAutoConcurrency;
    }

    public int getMaxAutoConc() {
        return maxAutoConc;
    }
    
    public void addPort(String name, String type, String rate) {
        if (indice < PortsArray.length) {
            PortsArray[indice][0] = name;
            PortsArray[indice][1] = type;
            PortsArray[indice][2] = rate;
            indice++;
        }
    }

    public String[][] getArrPort() {
        return PortsArray;
    }

    public String[] getPort(String name) {
        for (String[] arrPort1 : PortsArray) {
            if (arrPort1[0].equals(name)) {
                return arrPort1;
            }
        }
        return null;
    }

    public String getRatePort(String name) {
        return getPort(name)[2];
    }

    @Override
    public String toString() {
        String str = "name = " + name + ", duree = " + duration + ", numberPorts=" + PortsArray.length;
        for (int i = 0; i < PortsArray.length; i++) {
            str += " arrPort[" + i + "][0]= " + PortsArray[i][0] + " arrPort[" + i + "][1]= " + PortsArray[i][1] + " arrPort[" + i + "][2]= " + PortsArray[i][2];
        }
        return str;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getInput() {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("in")) {
                str += s[0] + ", ";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
            str += " :int";
        }

        return str;
    }

    public String getFiredString(String name) {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("in")) {
                str += " and   (" + s[0] + ">=" + name + "_" + s[0] + "_rate_" + s[1] + ")";
            }
        }
        str = str.replaceFirst("and ", "");
        return str;
    }

    public String getOutput() {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("in")) {
                str += s[0] + "_C" + ", ";
            } else if (s[1].equals("out")) {
                str += s[0] + "_P" + ", ";
            }
        }

        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
            str += " :int";
        }
        return str;
    }

    public String getOutputConsume() {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("in")) {
                str += s[0] + "_C" + ", ";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public String getOutputProduceDelayed() {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("out")) {
                str += s[0] + "_PD" + ", ";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    String getOutputProduce() {
        String str = "";
        for (String[] s : PortsArray) {
            if (s[1].equals("out")) {
                str += s[0] + "_P" + ", ";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    

    public String gateNameConsts() {
        String str = "";
        for (String[] s : PortsArray) {
            str += name + "_" + s[0] + "_rate_" + s[1] + ", ";
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
            str += " :int";
        }
        return str;
    }

    String getDoFireString(int maxAutoConcurrences) {
        String[] str = new String[maxAutoConcurrences];
        for (int i = 0; i < maxAutoConcurrences; i++) {
            str[i] = "";
            str[i] = "if " + name + "DoFire(" + getInput().replace(" :int", "");
            if (!getInput().equals("")) {
                str[i] += ",";
            }
            str[i] +=  (maxAutoConcurrences - i) + ") then "  + (maxAutoConcurrences - i);
        }
        String s = "";
        for (String string : str) {
            s += string + "\n\t\telse ";
        }
        return s + "0;";
    }

    public String getMultFired() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("in")) {
                str += " (" + name + "nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += " (" + name + "nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
    }
    
        public String getConsMultFired() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("in")) {
                str += " (" + name + "nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
    }
            public String getProdMultFired() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += " (" + name + "nbrEnd * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
    }

    public String getProduceAfterDelay() {
        String str  ;
  str= getZeroPreRecur(Integer.parseInt(duration), name + "nbrEnd") + ";\n\n";
           
        return str;
    }

    private String getZeroPreRecurOld(int duree, String st) {
        if (duree > 0) {
            return "0 -> pre (" + getZeroPreRecur(duree - 1, st) + ")";
        } else {
            return st;
        }
    }
  String getZeroPreRecur(int duree, String st) {
    String str="";
     for (int i = 0; i < duree; i++) {
                           str+= "0 -> pre (";
      }
     str+=st;
      for (int i = 0; i < duree; i++) {
                           str+= ")";
      }
      return str;
 }
                        
    String DoFireNodeString() {
        String str = "yes = if ";

        for (String[] port : PortsArray) {
            if (port[1].equals("in")) {
                str += "and  (" + port[0] + ">=  (times * " + name + "_" + port[0] + "_rate_" + port[1] + ")) \n\t";
            }
        }
        str = str.replaceFirst("and ", "");
        return str + "then true \n\telse false;\n";
    }

}
