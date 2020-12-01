package SDFG2LUSTRE_merged_cycles;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All
 * rights reserved
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] args) throws IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            /*
             * Step 1 : Parsing the XML file containing the SDFG"
             */
            final DocumentBuilder builder = factory.newDocumentBuilder();
             final Document document = builder.parse(new File(args[0] + ".xml"));

            final Element racine = document.getDocumentElement(); 
            String SDFname = racine.getAttribute("name");

            final NodeList racineNodes = racine.getChildNodes();
            final int nbRacineNodes = racineNodes.getLength();
            if (!BaliseErroné(racine)) {

                int nbrActors = 0;
                int nbrChannels = 0;
                int nbrDriverActors = 0;
                int nbrMonitorActors = 0;

                for (int i = 0; i < nbRacineNodes; i++) {
                    if (racineNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        if (racineNodes.item(i).getNodeName().equals("actor")) {
                            nbrActors++;
                        }
                        if (racineNodes.item(i).getNodeName().equals("channel")) {
                            nbrChannels++;
                        }
                        if (racineNodes.item(i).getNodeName().equals("driver")) {
                            nbrDriverActors++;
                        }
                        if (racineNodes.item(i).getNodeName().equals("monitor")) {
                            nbrMonitorActors++;
                        }
                    }
                }

                Actor[] actorsArray = new Actor[nbrActors];
                Channel[] channelsArray = new Channel[nbrChannels];
                DriverActor[] driversArray = new DriverActor[nbrDriverActors];
                MonitorActor[] monitorsArray = new MonitorActor[nbrMonitorActors];

                nbrActors = -1;
                nbrChannels = -1;
                nbrDriverActors = -1;
                nbrMonitorActors = -1;

                for (int i = 0; i < nbRacineNodes; i++) {
                    if (racineNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        switch (racineNodes.item(i).getNodeName()) {
                            case "actor":
                                nbrActors++;
                                NodeList Ports = racineNodes.item(i).getChildNodes();
                                int nbrPorts = 0;
                                for (int j = 0; j < Ports.getLength(); j++) {
                                    if (Ports.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        nbrPorts++;
                                    }
                                }
                                Element p = (Element) racineNodes.item(i);
                                actorsArray[nbrActors] = new Actor(p.getAttribute("name"), p.getAttribute("duration"), nbrPorts, Integer.parseInt(p.getAttribute("maxAutoConcurrences")), Integer.parseInt(p.getAttribute("maxStages")));
                                for (int j = 0; j < Ports.getLength(); j++) {
                                    if (Ports.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        Element q = (Element) Ports.item(j);
                                        actorsArray[nbrActors].addPort(q.getAttribute("name"), q.getAttribute("type"), q.getAttribute("rate"));
                                    }
                                }
                                break;
                            case "channel": {
                                nbrChannels++;
                                Element c = (Element) racineNodes.item(i);
                                channelsArray[nbrChannels] = new Channel(c.getAttribute("name"), c.getAttribute("srcActor"),
                                        c.getAttribute("srcPort"), c.getAttribute("dstActor"), c.getAttribute("dstPort"),
                                        c.getAttribute("initialTokens"));
                                break;
                            }
                            case "driver": {
                                nbrDriverActors++;
                                Element c = (Element) racineNodes.item(i);
                                driversArray[nbrDriverActors] = new DriverActor(c.getAttribute("name"), c.getAttribute("dstActor"),
                                        c.getAttribute("dstPort"));
                                break;
                            }
                            case "monitor": {
                                nbrMonitorActors++;
                                Element c = (Element) racineNodes.item(i);
                                monitorsArray[nbrMonitorActors] = new MonitorActor(c.getAttribute("name"), c.getAttribute("srcActor"),
                                        c.getAttribute("srcPort"));
                                break;
                            }
                            default:
                                break;
                        }
                    }

                }
                /*
                 * Step 2 : Checking  the XML file "
                 */
                int maxAutoConcurrences = Actor.getMaxAutoConcurrences();
                VerifyXML verifyXML = new VerifyXML(channelsArray, actorsArray, driversArray, monitorsArray, racine);
                if (verifyXML.fichierOK()) {
                    System.out.println("fichier Ok");
                    /*
                     * Step 3 : starting the code generatig"
                     */
                    try {
                        Date date = new Date();
                        SimpleDateFormat dateFormatComp;

                        dateFormatComp = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        PrintWriter writer = new PrintWriter(args[0] + "_merged_cycles.lus", "UTF-8");
                        writer.println("--** This lustre code is generated automatically on " + dateFormatComp.format(date));

                        writer.println("--** It corresponds to the translation of an SDFG model (" + SDFname + ")  to Lustre. This version produce a Lustre code having a reduced number of steps.");

                        writer.println();
                        writer.println();
                        writer.println();
                        /**
                         *******Step 3.1: Generating all the constants*********
                         */
                        writer.println("\n--**************** Const Rates ****************--\n");
                         for (int i = 0; i <= nbrActors; i++) {

                            String[][] arrPort = actorsArray[i].getPortsArray();
                            for (String[] s : arrPort) {
                                writer.println("const " + actorsArray[i].getName() + "_" + s[0] + "_rate_" + s[1] + " = " + s[2] + ";");
                            }

                        }

                        writer.println("\n--****************  Actors' durations ****************--\n");
                        int maxDuration = 0;
                        for (int i = 0; i <= nbrActors; i++) {
                            writer.println("const " + actorsArray[i].getName() + "_maxStages " + " = " + Integer.toString(actorsArray[i].getMaxStages()) + ";");

                            writer.println("const " + actorsArray[i].getName() + "_duration " + " = " + actorsArray[i].getDuration() + ";");
                            if (maxDuration < Integer.parseInt(actorsArray[i].getDuration())) {
                                maxDuration = Integer.parseInt(actorsArray[i].getDuration());
                            }
                        }
                        Actor.maxDuration = Integer.toString(maxDuration);
                        writer.println("const  maxDuration " + " = " + Actor.maxDuration + ";");

                        writer.println("\n--**************** Init Channel ****************--\n");
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println("const " + channelsArray[i].getName() + "_I = " + channelsArray[i].getInitialTokens() + ";");
                        }
                        writer.println();
                        for (DriverActor driver : driversArray) {
                            writer.println("const " + driver.getName() + "_I = " + "0" + ";");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println("const " + monitor.getName() + "_I = " + "0" + ";");
                        }

                        /**
                         ******************Step 3.2: Translation of Actors
                         */
                        for (int i = 0; i < nbrActors + 1; i++) {
                            Actor actor = actorsArray[i];
                            writer.println("\n--************************************************--");
                            writer.println("--******* Translation of Actor: " + actor.getName() + "***************--");
                            writer.println("--************************************************--");

                            // generating the IO interface of the node
                            writer.print("node " + actor.getName() + "(clock, " + actor.getInput() + ")");
                            writer.println(" returns (nextClock, " + actor.getOutput() + "; nbrFired, nbrEnd: int );\n");

                            // generating variable's declarations 
                            if (actor.getOutputProduceDelayed() != "") {
                                 if (Integer.parseInt(actor.getDuration()) != 1) {    
                                    writer.println("var"); 
                                    writer.println("stage :int;");
                                    if (actor.getMaxStages() > 1) {
                                        writer.println(" lastStage  :int;");
                                        for (int j = 1; j <= actor.getMaxStages(); j++) {
                                            String st = "";
                                            st += "rTime" + Integer.toString(j);
                                            st += ", nbrEnd" + Integer.toString(j);                                       
                                            st += " : int; ";
                                            writer.println(st);

                                        }
                                    }
                                } 

                            }

                            // the body of the actor
                            writer.println("let");

                            // computing  the number of firings of  the actor
                            String str = actor.getDoFireString(actor.getMaxAutoConc());
                            writer.println("nbrFired= " + str + "\n");

                            // generating the consumed tokens
                            if (actor.getOutputConsume() != "") { //the actor has inputs  edges 
                                writer.println("(" + actor.getOutputConsume() + ") = " + actor.getConsMultFired() + "\n");
                            }
                            
                            //generating the produced data  
                            String st;
                       
                                 if (Integer.parseInt(actor.getDuration()) != 1) { //case of multistage
                                    writer.println(actor.getNextstageString());  // computing  the id of the next active stage                                
                                    writer.println(actor.callStageNodesString());// calling the Stage node

                                } else {        //case  the actor duration equals one i.e no stage
                                    if (actor.getOutputProduce() != "") { //the actor has out  edges 
                                        writer.println("(" + actor.getOutputProduce() + ") = " + actor.getProdMultFired() + "\n");
                                    }
                                    writer.println( "nbrEnd = 0 -> pre nbrFired  ;");

                                     writer.println("nextClock = if (nbrFired = 0) then maxDuration else 1;");
                                }
                             
                            writer.println("tel;");

                            /*
                             ****************************************************************
                             * **************** Generating subnode DoFire of the current  Actor
                             ***************************************************************
                             */
                            writer.println("------------------subnode Dofire of Actor" + actor.getName() + "-----------------");

                            // generating the IO interface of the node
                            writer.println("node " + actor.getName() + "DoFire("
                                    + actor.getInput().replaceAll(" :int", ",") + "times:int) returns ( yes: bool);");
                            writer.println("let\n");
                            writer.println(actor.DoFireNodeString());
                            writer.println("tel;");

                            /*
                             ****************************************************************
                             * **************** Generating subnode Stage 
                             ***************************************************************
                             */
                            String s1 = actor.getOutputProduceDelayed();
                            if (!s1.equals("") && (Integer.parseInt(actor.getDuration()) != 1)) {
                                writer.println("------------------subnode Stage of Actor" + actor.getName() + "-----------------");
                                writer.print("node " + actor.getName() + "Stage");

                                writer.println("(clock,  stage, numStage, nbrFired : int) returns (rTime, nbrEnd : int);\n");

                                writer.println(actor.StageNodesString());

                            }
                        }

                        /*
                         ****************************************************************
                         * **************** Generating node Min 
                         ***************************************************************
                         */
                        writer.println("--******* Node Min***************--");
                        writer.println("node Min (x, y :int) returns ( r: int);");
                        writer.println("let");
                        writer.println("r= if  (x < y) then x else y;");
                        writer.println("tel;");

                        /*
                         ***************************************************************
                         * **************** Generating node Top 
                         ***************************************************************
                         */
                        writer.println("\n--************************************************--");
                        writer.println("--******* Node Top***************--");
                        writer.println("--************************************************--");

                        // generatin the IO interface of the node
                        String topDriverInput = "";
                        for (DriverActor driver : driversArray) {
                              topDriverInput += driver.getName() + "_P, ";
                        }
                        Boolean withDriverInput = false;
                        if (topDriverInput != "") {
                            withDriverInput = true;
                            deleteComma(topDriverInput);
                             writer.println( "node top (" + topDriverInput.substring(0, topDriverInput.length()-2)  + " : int)");
                        } else {
                            writer.print("node top (_: bool) ");

                        }

                        writer.println("returns ( adv, "
                                + getOutputNodeTop(channelsArray, actorsArray, driversArray, monitorsArray) + ");\n");

                        // generating variable's declarations 
                        writer.println("var\n");

                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + "_C, " + channelsArray[i].getName() + "_P: int; ");
                        }

                        for (DriverActor driver : driversArray) {
                            writer.println(driver.getName() + "_C : int;");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + "_P : int;");
                        }
                        for (Actor node : actorsArray) {
                            writer.println(node.getName() + "nextClock : int;  ");
                        }
                        
                            String str="";
                        for (Actor arrNode : actorsArray) {
                          str += arrNode.getName() + "nbrEnd, ";
                        }
                                              
                         writer.println(  str.substring(0, str.length()-2)  + " : int; \n");
                        
                                                 str="";
  
                     for (Actor arrNode : actorsArray) {
                         str += arrNode.getName() + "nbrRun, ";  
                         }
                
                         writer.println(  str.substring(0, str.length()-2)  + " : int; \n");     
                         writer.println(" noDeadlock: bool; ");
                    
                        
                        //******************generation the node body;
                        //updating the channel value
                        writer.println("\nlet\n");

                       
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + " = " + channelsArray[i].getName()
                                    + "_I ->  ( pre ( " + channelsArray[i].getName() + " - "
                                    + channelsArray[i].getName() + "_C) + " + channelsArray[i].getName() + "_P);");
                        }
                        writer.println();

                        writer.println();
                        for (DriverActor driver : driversArray) {
                            writer.println(driver.getName() + " = " + driver.getName()
                                    + "_P -> pre  ( " + driver.getName() + "-"
                                    + driver.getName() + "_C)+ " + driver.getName() + "_P ;");
                        }
                        writer.println();
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + " = " + monitor.getName()
                                    + "_I -> pre (" + monitor.getName() + "+ " + monitor.getName() + "_P);");
                        }
                        writer.println();

                        //calling the Actor nodes
                        for (Actor node : actorsArray) {
                            String[][] arrPort = node.getPortsArray();
                            String outputNode = "";
                            String inputNode = "";
                            for (String[] port : arrPort) {
                                if (port[1].equals("out")) {
                                    outputNode += searchOut(node.getName(), port[0], channelsArray, monitorsArray) + "_P, ";
                                } else {
                                    outputNode += searchIn(node.getName(), port[0], channelsArray, driversArray) + "_C, ";
                                    inputNode += searchIn(node.getName(), port[0], channelsArray, driversArray) + ", ";
                                }
                            }

                            writer.println("(" + node.getName() + "nextClock, " + outputNode + node.getName() + "nbrFired, " + node.getName() + "nbrEnd ) = " 
                                                                                     + node.getName() + "(adv, " + deleteComma(inputNode) + ");");

                              }
                        writer.println();

                        writer.print("adv= ");
                        for (int i = 0; i < nbrActors; i++) {
                            writer.print("Min(" + actorsArray[i].getName() + "nextClock , ");
                        }
                        writer.print(actorsArray[nbrActors].getName() + "nextClock");
                        for (int i = 0; i < nbrActors; i++) {
                            writer.print(")");
                        }
                    writer.println(";\n\n");
                       
                      for (Actor arrNode : actorsArray) {
                            writer.println(arrNode.getName() + "nbrRun=  " + arrNode.getName() + "nbrFired  -> (pre " + arrNode.getName() + "nbrRun) +" + arrNode.getName() + "nbrFired - " + arrNode.getName() + "nbrEnd ;" + "\n");
                        }
                        writer.println("\nnoDeadlock = " + deadlockStringNew(actorsArray) + ";");
            
                        for (DriverActor driver : driversArray) {
                            writer.println("assert " + driver.getName() + "_P  =1; \n ");
                        }
                           writer.println("--%MAIN;\n");
          
                        writer.println("--%PROPERTY  \"no deadlock \"  noDeadlock;\n");
                     writer.println("-- --%PROPERTY  ....");
                        writer.println("\ntel;");
                        writer.println();
                        writer.println();

                        /**
                         *  **************** close writer ****************
                         */
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("ERROR output.lus");
                    }

                } else {
                    System.out.println("fichier not Ok");
                    System.out.println(verifyXML.getERRORXML());
                }

            }
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    public static String deleteComma(String str) {
        if (!str.equals("")) {
            str = str.trim();
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String getOutputNodeTop(Channel[] channelsArray, Actor[] actorsArray, DriverActor[] driversArray, MonitorActor[] monitorsArray) {
        String str = "";

        for (Channel channel : channelsArray) {
            str += channel.name + ", ";
        }
        for (DriverActor driver : driversArray) {
            str += driver.getName() + ", ";
        }
        for (MonitorActor monitor : monitorsArray) {
            str += monitor.getName() + ", ";
        }
        for (Actor arrNode : actorsArray) {
            str += arrNode.getName() + "nbrFired, ";
        }

        return deleteComma(str) + ":int";
    }

    private static String positiveValuesString(Channel[] channelsArray, Actor[] actorsArray, DriverActor[] driversArray, MonitorActor[] monitorsArray) {
        String str = "";

        for (Channel channel : channelsArray) {
            str += "( " + channel.name + " >= 0) \n\tand ";
        }
        for (DriverActor driver : driversArray) {
            str += "( " + driver.getName() + " >= 0) \n\tand ";
        }
        for (MonitorActor monitor : monitorsArray) {
            str += "( " + monitor.getName() + ">= 0) \n\tand ";
        }
        for (Actor arrNode : actorsArray) {
            str += "( " + arrNode.getName() + "nbrFired " + " >=  0) \n\tand ";
        }
        str = str.trim();
        str = str.substring(0, str.length() - 5);
        return str;
    }

    private static String deadlockStringNew(Actor[] actorsArray) {
        String str = "";

        for (Actor arrNode : actorsArray) {
            str += " ( " + arrNode.getName() + "nbrRun " + ">= 1) \n\tor ";
        }
        str = str.trim();
        str = str.substring(0, str.length() - 5);
        return str;
    }
    private static String deadlockString(Actor[] actorsArray) {
        String str = "";

        for (Actor arrNode : actorsArray) {
            str += "( " + arrNode.getName() + "nbrFired " + " >= 1) \n\tor ";
        }
        str = str.trim();
        str = str.substring(0, str.length() - 5);
        return str;
    }

    public static String searchOut(String nodeName, String portName, Channel[] channelsArray, MonitorActor[] monitorsArray) {
        String str = "";
        for (Channel channel : channelsArray) {
            if (channel.srcActor.equals(nodeName) && channel.srcPort.equals(portName)) {
                return channel.getName();
            }
        }
        for (MonitorActor monitor : monitorsArray) {
            if (monitor.getSrcActor().equals(nodeName) && monitor.getPortSrc().equals(portName)) {
                return monitor.getName();
            }
        }
        return str;
    }

    public static String searchIn(String nodeName, String portName, Channel[] channelsArray, DriverActor[] driversArray) {
        String str = "";
        for (Channel channel : channelsArray) {
            if (channel.getDstActor().equals(nodeName) && channel.getDstPort().equals(portName)) {
                return channel.getName();
            }
        }
        for (DriverActor driver : driversArray) {
            if (driver.getActorDest().equals(nodeName) && driver.getPortDest().equals(portName)) {
                return driver.getName();
            }
        }
        return str;
    }

    private static boolean BaliseErroné(Element racine) {
        NodeList racineNodes = racine.getChildNodes();
        for (int i = 0; i < racineNodes.getLength(); i++) {
            if (racineNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                String s = racineNodes.item(i).getNodeName();
                if (!s.equals("actor") && !s.equals("port") && !s.equals("channel")
                        && !s.equals("driver") && !s.equals("monitor")) {
                    System.out.println(" vous avez le balise " + s + " Erronée...");
                    return true;
                }

            }
        }
        for (int i = 0; i < racineNodes.getLength(); i++) {
            if (racineNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (racineNodes.item(i).getNodeName().equals("actor")) {
                    NodeList Ports = racineNodes.item(i).getChildNodes();
                    for (int j = 0; j < Ports.getLength(); j++) {
                        if (Ports.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            String s = Ports.item(j).getNodeName();
                            if (!s.equals("port")) {
                                System.out.println(" vous avez le balise " + s + " Erronée... qu'lle doit être normalement <port...>");
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
