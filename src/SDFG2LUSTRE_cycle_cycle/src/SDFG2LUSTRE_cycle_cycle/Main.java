package SDFG2LUSTRE_cycle_cycle;

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
            /**
                 * ************************************************
                 ********* Parsing the XML file ************
                 * *************************************************
                 */
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
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
                DriverActor[] sinkArray = new DriverActor[nbrDriverActors];
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
                                actorsArray[nbrActors] = new Actor(p.getAttribute("name"), p.getAttribute("duration"), nbrPorts, Integer.parseInt(p.getAttribute("maxAutoConcurrences")));
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
                                sinkArray[nbrDriverActors] = new DriverActor(c.getAttribute("name"), c.getAttribute("dstActor"),
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
                int maxAutoConcurrences = Actor.getMaxAutoConcurrences();
                
                // verif of the XML file ************
                VerifyXML verifyXML = new VerifyXML(channelsArray, actorsArray, sinkArray, monitorsArray, racine);
                if (verifyXML.fichierOK()) {
                    System.out.println("file Ok");
                    /**
                     * ***********************************************************************************************
                     **************Begin of Lustre code generation
                     * *************************************************
                     * **********************************************************************************************
                     */
                    try {
                        Date date = new Date();
                        SimpleDateFormat dateFormatComp;

                        dateFormatComp = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        PrintWriter writer = new PrintWriter(args[0] + "_cycle_by_cycle.lus", "UTF-8");
                        writer.println("--** This lustre code is generated automatically on " + dateFormatComp.format(date));

                        writer.println("--**It corresponds to a translation to Lustre of the  SDFG model  of the design \"" + SDFname + "\" ");

                        writer.println();
                        writer.println();

                        /**
                         * ********generating the constants
                         * ****************
                         */
                        writer.println("\n--**************** Const Rates ****************--\n");
                        for (int i = 0; i <= nbrActors; i++) {

                            String[][] arrPort = actorsArray[i].getArrPort();
                            for (String[] s : arrPort) {
                                writer.println("const " + actorsArray[i].getName() + "_" + s[0] + "_rate_" + s[1] + " = " + s[2] + ";");
                            }

                        }
                        writer.println("\n--**************** Init Channel ****************--\n");
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println("const " + channelsArray[i].getName() + "_I = " + channelsArray[i].getInitialTokens() + " ;");
                        }
                        writer.println();
                        for (DriverActor driver : sinkArray) {
                            writer.println("const " + driver.getName() + "_I = 0 ;");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println("const " + monitor.getName() + "_I = 0 ;");
                        }
                        /**
                         * **************** Translating actors to Lustre nodes
                         * *****************
                         */
                        writer.println("\n--**************** Actors Translation ****************--\n");
                        for (int i = 0; i < nbrActors + 1; i++) {

                            //1. the actor's signature
                            Actor node = actorsArray[i];
                            writer.print("node " + node.getName() + "(" + node.getInput() + ")");
                            writer.println(" returns (" + node.getOutput() + "; " + node.getName() + "nbrFired, "+  node.getName() + "nbrEnd : int );\n");

                            //2. variable declaration
                           
                    //                                        writer.println("var");


                            writer.println("let");

                            //3. counting the number of simultanous firings
                            String str = node.getDoFireString(node.getMaxAutoConc());
                            writer.println(node.getName() + "nbrFired= " + str + "\n");

                            writer.println(node.getName() + "nbrEnd= " + node.getZeroPreRecur(Integer.parseInt(node.getDuration()), node.getName() + "nbrFired") + ";\n\n"+ "\n");
 
                            //4. generating the output streams of the node
                             if (node.getOutputConsume() != "") { //the actor has inputs  edges 
                                 writer.println("(" + node.getOutputConsume() + ") = " + node.getConsMultFired() + "\n");
                            }

                            if (node.getOutputProduce() != "") { //the actor has out  edges 
                               // st = node.getOutputProduce();
                                writer.println("(" +  node.getOutputProduce() + ") = " + node.getProdMultFired() + "\n");

                            }
 
                            writer.println("tel;");

                            
                            
                            /* **************** generating the subnode DofFire  *****
                           ************                                     *****
                             */
                            writer.println("------------------");
                            writer.println("node " + node.getName() + "DoFire("
                                    + node.getInput().replaceAll(" : int", ",") + "; times: int) returns ( yes: bool);");
                            writer.println("let\n");
                            writer.println(node.DoFireNodeString());
                            writer.println("tel;");

                            writer.println("------------------");

                        }

                        /* **************** generating the node top  *****
                           ************                                     *****
                         */
                        // generating the IO interface of the node top
                        String topDriverInput = "";
                        for (DriverActor driver : sinkArray) {
                            topDriverInput += driver.getName() + "_P, ";
                        }
                        Boolean withDriverInput = false;
                        if (topDriverInput != "") { // the SDFG has input edges
                            withDriverInput = true;
                            deleteComma(topDriverInput);
                             writer.println( "node top (" + topDriverInput.substring(0, topDriverInput.length()-2)  + " : int)");

                         } else {
                            writer.print("node top (_: bool) ");

                        }

                        writer.println("returns ("
                                + getOutputNodeTop(channelsArray, actorsArray, sinkArray, monitorsArray) + ");\n");
///

                        writer.println("-------------- Les variables -------------- \n");
                        writer.println("var\n");

                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + "_C, " + channelsArray[i].getName() + "_P: int; ");
                        }

                        for (DriverActor driver : sinkArray) {
                            writer.println(driver.getName() + "_C : int;");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + "_P : int;");
                        }
                        String str="";
                        for (Actor arrNode : actorsArray) {
                          str += arrNode.getName() + "nbrEnd, ";
                        }
                                              
                         writer.println(  str.substring(0, str.length()-2)  + " : int; \n");
                         str="";
                    /*               
                         for (Actor arrNode : actorsArray) {
                         str += arrNode.getName() + "nbrRun, ";
                    
                         }
                
                          writer.println(  str.substring(0, str.length()-2)  + " : int; \n");
                        writer.println("noDeadlock: bool;\n");

                       */    

                        //generating the node-top body
                        writer.println("\nlet\n");
                                        
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + " = " + channelsArray[i].getName()
                                    + "_I -> pre  (   " + channelsArray[i].getName() + "-"
                                    + channelsArray[i].getName() + "_C)+ " + channelsArray[i].getName() + "_P;");
                        }
                        writer.println();
                        for (DriverActor driver : sinkArray) {
                            writer.println(driver.getName() + " = " + driver.getName()
                                    + "_P -> pre  ( " + driver.getName() + "-"
                                    + driver.getName() + "_C)+ " + driver.getName() + "_P ;");
                        }
                        writer.println();
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + " = " + monitor.getName()
                                    + "_I -> pre " + monitor.getName() + "+ " + monitor.getName() + "_P;");
                        }

                        writer.println();
                        for (Actor node : actorsArray) {
                            String[][] arrPort = node.getArrPort();
                            String outputNode = "";
                            String inputNode = "";
                            for (String[] port : arrPort) {
                                if (port[1].equals("out")) {
                                    outputNode += searchOut(node.getName(), port[0], channelsArray, monitorsArray) + "_P, ";
                                } else {
                                    outputNode += searchIn(node.getName(), port[0], channelsArray, sinkArray) + "_C, ";
                                    inputNode += searchIn(node.getName(), port[0], channelsArray, sinkArray) + ", ";
                                }
                            }

                            writer.println("(" + outputNode + node.getName() + "nbrFired, " + node.getName() + "nbrEnd ) = " + node.getName() + "(" + deleteComma(inputNode) + ");");

                        }
                        writer.println();

                     /*  for (Actor arrNode : actorsArray) {
                        writer.println(arrNode.getName() + "nbrRun=  " + arrNode.getName() + "nbrFired  -> (pre " + arrNode.getName() + "nbrRun) +" + arrNode.getName() + "nbrFired - " + arrNode.getName() + "nbrEnd ;" + "\n");
                         writer.println("\nnoDeadlock = " + deadlockStringNew(actorsArray) + ";");
                        }*/

                        writer.println();
                              for (DriverActor driver : sinkArray) {
                          writer.println("assert " + driver.getName() + "_P  =1; \n ");
                        }
                                         


                        writer.println("--%MAIN;\n");
                  //        writer.println("--%PROPERTY  \"Deadlock free \"  noDeadlock;\n");
                        writer.println("-- --%PROPERTY  .....\n");
                  
                        writer.println("\ntel;");
                        writer.println();

                        /**
                         *  **************** close writer ****************
                         */
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("ERROR output.lus");
                    }
                    /**
                     * ***********************************************************************************************
                     ******************************** production of the  Utimed Lustre model
                     * *********************************************
                     * **********************************************************************************************
                     */
                    try {
                        Date date = new Date();
                        SimpleDateFormat dateFormatComp;

                        dateFormatComp = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                        PrintWriter writer = new PrintWriter(args[0] + "_untimed.lus", "UTF-8");
                        writer.println("--** This lustre code is generated automatically on " + dateFormatComp.format(date));

                        writer.println("--**It corresponds to a translation to Lustre of the Untimed SDFG model  of the design \"" + SDFname + "\" ");

                        writer.println();
                        writer.println();
                        writer.println();
                        /**
                         * **************** const ****************
                         */
                        //       writer.println("const maxValue = " + maxValue + ";");
                        writer.println("\n--**************** Const Rates ****************--\n");
                        for (int i = 0; i <= nbrActors; i++) {

                            String[][] arrPort = actorsArray[i].getArrPort();
                            for (String[] s : arrPort) {
                                writer.println("const " + actorsArray[i].getName() + "_" + s[0] + "_rate_" + s[1] + " = " + s[2] + " ;");
                            }

                        }
                        writer.println("\n--**************** Init Channel ****************--\n");
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println("const " + channelsArray[i].getName() + "_I = " + channelsArray[i].getInitialTokens() + " ;");
                        }
                        writer.println();
                        for (DriverActor driver : sinkArray) {
                            writer.println("const " + driver.getName() + "_I = 0; ");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println("const " + monitor.getName() + "_I = 0; ");
                        }
                        /**
                         *
                         * **************** Nodes *****************
                         */
                        writer.println("\n--**************** Nodes Declaration ****************--\n");
                        for (int i = 0; i < nbrActors + 1; i++) {
                            Actor node = actorsArray[i];
                            writer.print("node " + node.getName() + "(" + node.getInput() + ")");
                            writer.println(" returns (" + node.getOutput() + "; " + node.getName() + "nbrFired: int );\n");
                            
                              //2. variable declaration
                           
                                                            writer.println("var");

                                writer.println(node.getName() + "nbrEnd: int;");

                            writer.println("let");
                            String str = node.getDoFireString(node.getMaxAutoConc());
                            writer.println(node.getName() + "nbrFired= " + str + "\n");
                            
                                                     
                            
                           writer.println(node.getName() + "nbrEnd= " + node.getZeroPreRecur(1, node.getName() + "nbrFired") + ";\n\n"+ "\n");

                            //4. generating the output streams of the node
                          //  String st;
                            if (node.getOutputConsume() != "") { //the actor has inputs  edges 
                             //   st = node.getOutputConsume();
                                writer.println("(" + node.getOutputConsume() + ") = " + node.getConsMultFired() + "\n");

                            }

                            if (node.getOutputProduce() != "") { //the actor has out  edges 
                               // st = node.getOutputProduce();
                                writer.println("(" +  node.getOutputProduce() + ") = " + node.getProdMultFired() + "\n");

                            }


 
                            writer.println("tel;");

                    
                            //********************generating the Dofire
                            writer.println("------------------");
                            writer.println("node " + node.getName() + "DoFire("
                                    + node.getInput().replaceAll(" : int", ",") + "; times: int) returns ( yes: bool);");
                            writer.println("let\n");
                            writer.println(node.DoFireNodeString());
                            writer.println("tel;");

                            writer.println("------------------");

                        }

                        /**
                         * **************** Node Top Declaration
                         * ****************
                         */
                        writer.println("\n--**************** Node Top Declaration ****************--\n");

                        // generatin the IO interface of the node
                        String topDriverInput = "";
                        for (DriverActor driver : sinkArray) {
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

                        writer.println("returns ("
                                + getOutputNodeTop(channelsArray, actorsArray, sinkArray, monitorsArray) + ");\n");

                        writer.println("var\n");

                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + "_C, " + channelsArray[i].getName() + "_P: int; ");
                        }

                        for (DriverActor driver : sinkArray) {
                            writer.println(driver.getName() + "_C : int;");
                        }
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + "_P : int;");
                        }

                       writer.println("noDeadlock : bool;");

                        //generation the node-top body
                        writer.println("\nlet\n");

                        for (DriverActor driver : sinkArray) {
                            writer.println("assert " + driver.getName() + "_P  = 1; \n ");
                        }
                        for (int i = 0; i <= nbrChannels; i++) {
                            writer.println(channelsArray[i].getName() + " = " + channelsArray[i].getName()
                                    + "_I -> pre ( " + channelsArray[i].getName() + "-"
                                    + channelsArray[i].getName() + "_C)+ " + channelsArray[i].getName() + "_P;");
                        }
                        writer.println();
                        for (DriverActor driver : sinkArray) {
                            writer.println(driver.getName() + " = " + driver.getName()
                                    + "_P -> pre  ( " + driver.getName() + "-"
                                    + driver.getName() + "_C)+ " + driver.getName() + "_P ;");
                        }
                        writer.println();
                        for (MonitorActor monitor : monitorsArray) {
                            writer.println(monitor.getName() + " = " + monitor.getName()
                                    + "_I -> pre " + monitor.getName() + "+ " + monitor.getName() + "_P;");
                        }
                        writer.println();
                        for (Actor node : actorsArray) {
                            String[][] arrPort = node.getArrPort();
                            String outputNode = "";
                            String inputNode = "";
                            for (String[] port : arrPort) {
                                if (port[1].equals("out")) {
                                    outputNode += searchOut(node.getName(), port[0], channelsArray, monitorsArray) + "_P, ";
                                } else {
                                    outputNode += searchIn(node.getName(), port[0], channelsArray, sinkArray) + "_C, ";
                                    inputNode += searchIn(node.getName(), port[0], channelsArray, sinkArray) + ", ";
                                }
                            }

                            writer.println("(" + outputNode + node.getName() + "nbrFired) = " + node.getName() + "(" + deleteComma(inputNode) + ");");

                        }
                        writer.println();

                    writer.println("\nnoDeadlock = " + deadlockString(actorsArray) + ";");
                        writer.println("--%MAIN;\n");
                     writer.println("--%PROPERTY  \"Deadlock free \"  noDeadlock;\n");
                        writer.println("\ntel;");
                        writer.println();
                        writer.println();

                        /**
                         * u * **************** close writer ****************
                         */
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("ERROR outputs");
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

        return deleteComma(str) + ": int";
    }

    private static String positiveValuesString(Channel[] channelsArray, Actor[] actorsArray, DriverActor[] driversArray, MonitorActor[] monitorsArray) {
        String str = "";

        for (Channel channel : channelsArray) {
            str += " ( " + channel.name + ">=   0) \n\tand ";
        }
        for (DriverActor driver : driversArray) {
            str += " ( " + driver.getName() + ">=  0) \n\tand ";
        }
        for (MonitorActor monitor : monitorsArray) {
            str += " ( " + monitor.getName() + ">=  0) \n\tand ";
        }
        for (Actor arrNode : actorsArray) {
            str += " ( " + arrNode.getName() + "nbrFired " + ">= 0) \n\tand ";
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
            str += " ( " + arrNode.getName() + "nbrFired " + ">= 1) \n\tor ";
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
