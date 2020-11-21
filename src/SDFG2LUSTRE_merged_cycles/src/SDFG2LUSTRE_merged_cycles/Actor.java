package SDFG2LUSTRE_merged_cycles;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All
 * rights reserved
 */
public class Actor {

    private final String name;
    private final String duration;
    static String maxDuration;
    final String[][] PortsArray;
    private final int maxAutoConc;
    private final int maxStages;
    private static int maxAutoConcurrency;
    private int indice;

    public Actor(String name, String duree, int nberPort, int maxAutoConc, int maxStages) {
        this.name = name;
        this.duration = duree;
        this.PortsArray = new String[nberPort][3];
        this.maxAutoConc = maxAutoConc;
        this.maxStages = maxStages;
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

    public int getMaxStages() {
        return maxStages;
    }

    public void addPort(String name, String type, String rate) {
        if (indice < PortsArray.length) {
            PortsArray[indice][0] = name;
            PortsArray[indice][1] = type;
            PortsArray[indice][2] = rate;
            indice++;
        }
    }

    public String[][] getPortsArray() {
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
                str += " and e (" + s[0] + ">= " + name + "_" + s[0] + "_rate_" + s[1] + ")";
            }
        }
        str = str.replaceFirst("and ", "");
        return str;
    }

    public String getConsMultFired() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("in")) {
                str += " (nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }

        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
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

    public String getProdMultFired() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += " (nbrEnd * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }

        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
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

    public static String getBinaryRate(String s, int nberBit) {
        Integer a = Integer.parseInt(s);
        String str = "";
        for (int i = nberBit - 1; i >= 0; i--) {
            str += a % 2 + ",";
            a /= 2;
        }
        str = str.trim();
        str = str.substring(0, str.length() - 1);
        return "[" + str + "];";
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
            str[i] += (maxAutoConcurrences - i) + ") then " + (maxAutoConcurrences - i);
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
                str += "(nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += "(nbrFired * " + name + "_" + port[0] + "_rate_" + port[1] + "),";
            }
        }
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return "(" + str + ");";
    }

    public String getProduceAfterDelay() {
        String str = "";
        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += port[0] + "_P = " + getZeroPreRecur(Integer.parseInt(duration) - 1, port[0] + "_PD") + ";\n\n";
            }
        }
        return str;
    }

    private String getZeroPreRecur(int duree, String st) {
        if (duree > 0) {
            return "0 -> pre (" + getZeroPreRecur(duree - 1, st) + ")";
        } else {
            return st;
        }
    }

    String DoFireNodeString() {
        String str = "yes = if ";

        for (String[] port : PortsArray) {
            if (port[1].equals("in")) {
                str += "and (" + port[0] + ">=  (times * " + name + "_" + port[0] + "_rate_" + port[1] + ")) \n\t";
            }
        }
        str = str.replaceFirst("and ", "");
        return str + "then true \n\telse false;\n";
    }

    String getZeroAsOutput() {
        String str = "";

        for (String[] s : PortsArray) {
            if (s[1].equals("out")) {
                str += ", 0";
            }
        }
        return (str.replaceFirst(", ", ""));
    }

    String transOutPort2PreOutPort() {
        String str = "";

        for (String[] port : PortsArray) {
            if (port[1].equals("out")) {
                str += ", pre " + port[0] + "_PDBuffer";
            }
        }
        str = str.replaceFirst(",", "");
        return str;
    }

    String StageNodesString(int i) {
        String str = " const numStage = " + i + "; \n";
        str += "var" + " \n";
        str += getOutputProduceDelayed().replaceAll("_PD", "_PDBuffer") + ", rsteps  : int; \n";
        str += "running : bool; \n";

        str += "\n let \n";
        str += "running = if  (stage = numStage) then true \n ";
        str += "        else if  (pre running) and ((pre rTime - clock)= 0) then false \n";
        str += "              else pre running; \n";

        str += "rTime = if  (stage = numStage) then " + name + "_duration \n ";
        str += "        else if  (pre running) then (pre rTime -clock) \n";
        str += "             else 0 ; \n";

        str += "(" + getOutputProduceDelayed().replaceAll("_PD", "_PDBuffer") + ") =  if  (stage = numStage) then (" + getOutputProduceDelayed() + ") \n";

        str += "                               else if (pre running) and ((pre rTime - clock)= 0) "
                + "then (" + getZeroAsOutput() + ")\n";
        str += "                                        else (" + transOutPort2PreOutPort() + "); \n";

        str += "(" + getOutputProduce() + ") =   if (pre running) and ((pre rTime - clock) = 0) "
                + "then (" + getOutputProduceDelayed().replaceAll("_PD", "_PDBuffer") + ")\n";
        str += "        else (" + getZeroAsOutput() + ");g\n";

        return (str + "tel; \n");
    }

    String getNextstageString() {
        String st = "";

        if (maxStages == 1) {
            st += "stage = if (nbrFired = 0) then 0 \n";
            st += "         else   1 ;\n";
        } else {

            st += "stage = (if (nbrFired = 0) then 0 \n";
            st += "         else   1) \n";
            st += " -> if (nbrFired = 0) then 0 \n";
            st += "         else if (pre lastStage = " + name + "_maxStages) then  1 \n";
            st += "         else  (pre lastStage + 1) ; \n \n";

            st += "lastStage = (if (nbrFired = 0) then 0 \n";
            st += "         else   1) \n";
            st += " -> if (nbrFired = 0) then (pre lastStage) \n";
            st += "         else stage; \n \n";
        }
        return (st);
    }

    String callStageNodesString() {
        String st = "";
        if (maxStages > 1) { //case many stages

            //calling the stages
            for (int j = 1; j <= maxStages; j++) {
                st += "(rTime" + Integer.toString(j) + ", nbrEnd" + Integer.toString(j) + ") =" + name + "Stage" + "(clock, stage, "
                        + Integer.toString(j) + ", nbrFired); \n";
            }

            //the end of firings
            st += " \n nbrEnd = ";
            for (int j = 1; j < maxStages; j++) {
                st += "( nbrEnd" + Integer.toString(j) + " + ";
            }

            st += "nbrEnd" + Integer.toString(maxStages);
            for (int j = 1; j < (maxStages); j++) {
                st += ")";
            }
            st += ";\n \n";

            //producing the tokens
            if (getOutputProduce() != "") { //the actor has out  edges 
                // st = node.getOutputProduce();
                st += "(" + getOutputProduce() + ") = " + getProdMultFired() + "\n";

            }

            //computing the next clock
            st += "\n nextClock= ";
            for (int j = 1; j < maxStages; j++) {
                st += "Min(rTime" + Integer.toString(j) + " , ";
            }
            st += "rTime" + Integer.toString(maxStages);
            for (int j = 1; j < maxStages; j++) {
                st += ")";
            }
            st += ";\n \n";

        } else {//maxstage==1
            st += "(nextClock, nbrEnd) = " + name + "Stage (clock, stage, 1, nbrFired ); \n";
            //producing the tokens
            if (getOutputProduce() != "") { //the actor has out  edges 
                // st = node.getOutputProduce();
                st += "(" + getOutputProduce() + ") = " + getProdMultFired() + "\n";

            }
        }

        return (st);

    }

    String StageNodesString() {
        String str = "var" + " \n";
        // str += getOutputProduceDelayed().replaceAll("_PD", "_PDBuffer") + ": int; \n";
        str += "nbrFiredBuf : int; \n";

        str += "selected, endExecution, running : bool; \n";

        //node body
        str += "\n let";
        str += "\n selected = if  (stage = numStage) then true else false ; \n ";

        str += "\n endExecution= false -> if not pre (running)  then false else pre  ((rTime - clock) = 0); \n";

        str += "\n running = if  selected then true else false -> \n ";
        str += "          if  selected then true \n ";
        str += "        else if  endExecution then false else pre running; \n";

        //buffering  the nbrFired
        str += "\n nbrFiredBuf = ";
        str += "if selected then nbrFired  else 0 -> \n ";
        str += "                       if selected then  nbrFired  else if endExecution then 0 else pre nbrFiredBuf ; \n";

        // computing the  nbrEnd
        str += "\n nbrEnd = 0 -> if endExecution then pre nbrFiredBuf else 0; \n";

        // the remaining time
        str += "\n rTime = if  selected then " + name + "_duration  else maxDuration -> \n ";
        str += "         if  selected then " + name + "_duration \n ";
        str += "         else if  (pre running) then if  (pre (rTime > clock) )then  pre (rTime - clock) \n";
        str += "          else maxDuration   else maxDuration ; \n";

        return (str + "\n tel; \n");
    }

}
