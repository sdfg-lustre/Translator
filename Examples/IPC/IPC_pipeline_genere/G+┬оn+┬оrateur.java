package stage;

/**
 *
 * @author Adhéss
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Générateur {

    public static void main(final String[] args) {
        /*
         * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
         */
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            /*
             * Etape 2 : création d'un parseur
             */
            final DocumentBuilder builder = factory.newDocumentBuilder();

            /*
	     * Etape 3 : création d'un Document
             */
           final Document document = builder.parse(new File("../IPC/IPC_pipeline_genere/IPC_pipeline.xml"));
 //final Document document = builder.parse(new File(args[1]));
            /*
	     * Etape 4 : récupération de l'Element racine
             */
            final Element racine = document.getDocumentElement();

            int nberBit = Integer.parseInt(racine.getAttribute("nberBit"));
            
            String maxValue = racine.getAttribute("maxValue");
            //System.out.println("nberBit= " + nberBit + " maxAutoConcurrences= " + maxAutoConcurrences);
            //Affichage de l'élément racine
            //System.out.println("\n*************TYPE DE GRAPHE************");
            //System.out.println(racine.getNodeName());

            /*
	     * Etape 5 : Récupération de données
             */
            final NodeList racineNoeuds = racine.getChildNodes();
            final int nbRacineNoeuds = racineNoeuds.getLength();
            if (!BaliseErroné(racine)) {

                int nberNode = 0;
                int nberChannel = 0;
                int nberDriver = 0;
                int nberMoniteur = 0;

                for (int i = 0; i < nbRacineNoeuds; i++) {
                    if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        if (racineNoeuds.item(i).getNodeName().equals("actor")) {
                            nberNode++;
                        }
                        if (racineNoeuds.item(i).getNodeName().equals("channel")) {
                            nberChannel++;
                        }
                        if (racineNoeuds.item(i).getNodeName().equals("driver")) {
                            nberDriver++;
                        }
                        if (racineNoeuds.item(i).getNodeName().equals("moniteur")) {
                            nberMoniteur++;
                        }
                    }
                }

                NodePersonnaliser[] arrNodes = new NodePersonnaliser[nberNode];
                Channel[] arrChannel = new Channel[nberChannel];
                Driver[] arrDriver = new Driver[nberDriver];
                Moniteur[] arrMoniteur = new Moniteur[nberMoniteur];

                nberNode = -1;
                nberChannel = -1;
                nberDriver = -1;
                nberMoniteur = -1;

                for (int i = 0; i < nbRacineNoeuds; i++) {
                    if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        switch (racineNoeuds.item(i).getNodeName()) {
                            case "actor":
                                nberNode++;
                                NodeList Ports = racineNoeuds.item(i).getChildNodes();
                                int nberPorts = 0;
                                for (int j = 0; j < Ports.getLength(); j++) {
                                    if (Ports.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        nberPorts++;
                                    }
                                }
                                Element p = (Element) racineNoeuds.item(i);
                                arrNodes[nberNode] = new NodePersonnaliser(p.getAttribute("name"), p.getAttribute("duration"), nberPorts,Integer.parseInt(p.getAttribute("maxAutoConcurrences")));
                                for (int j = 0; j < Ports.getLength(); j++) {
                                    if (Ports.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        Element q = (Element) Ports.item(j);
                                        arrNodes[nberNode].addPort(q.getAttribute("name"), q.getAttribute("type"), q.getAttribute("rate"));
                                    }
                                }
                                break;
                            case "channel": {
                                nberChannel++;
                                Element c = (Element) racineNoeuds.item(i);
                                arrChannel[nberChannel] = new Channel(c.getAttribute("name"), c.getAttribute("srcActor"),
                                        c.getAttribute("srcPort"), c.getAttribute("dstActor"), c.getAttribute("dstPort"),
                                        c.getAttribute("initialTokens"));
                                break;
                            }
                            case "driver": {
                                nberDriver++;
                                Element c = (Element) racineNoeuds.item(i);
                                arrDriver[nberDriver] = new Driver(c.getAttribute("name"), c.getAttribute("actorDest"),
                                        c.getAttribute("portDest"));
                                break;
                            }
                            case "moniteur": {
                                nberMoniteur++;
                                Element c = (Element) racineNoeuds.item(i);
                                arrMoniteur[nberMoniteur] = new Moniteur(c.getAttribute("name"), c.getAttribute("srcActor"),
                                        c.getAttribute("portSrc"));
                                break;
                            }
                            default:
                                break;
                        }
                    }

                }
                int maxAutoConcurrences = NodePersonnaliser.getMaxAutoConcurrences();
                /**
                 * ************************************************
                 ********* Verification du fichier XML ************
                 * *************************************************
                 */
                VerifyXML verifyXML = new VerifyXML(arrChannel, arrNodes, arrDriver, arrMoniteur, racine);
                if (verifyXML.fichierOK()) {
                    System.out.println("fichier Ok");
                    /*************************************************************************************************
                     ************************************ output.lus *************************************************
                     ************************************************************************************************/
                    try {
                        PrintWriter writer = new PrintWriter("../IPC/IPC_pipeline_genere/IPC.lus", "UTF-8");
                        /**
                         * **************** const ****************
                         */
                        writer.println("include \"./toInclude_BitBlasting_ok.lus\"\n");
                        writer.println("const maxValue = " + NodePersonnaliser.getBinairRate(maxValue, nberBit));
                        writer.println("\n--**************** Const Rates ****************--\n");
                        for (int i = 0; i <= nberNode; i++) {

                            String[][] arrPort = arrNodes[i].getArrPort();
                            for (String[] s : arrPort) {
                                writer.println("const " + arrNodes[i].getName() + "_" + s[0] + "_rate_" + s[1] + " = " + NodePersonnaliser.getBinairRate(s[2], nberBit));
                            }

                        }
                        writer.println("\n--**************** Init Channel ****************--\n");
                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println("const " + arrChannel[i].getName() + "_I = " + NodePersonnaliser.getBinairRate(arrChannel[i].getInitialTokens(), nberBit));
                        }
                        writer.println();
                        for (Driver driver : arrDriver) {
                            writer.println("const " + driver.getName() + "_I = " + NodePersonnaliser.getBinairRate("0", nberBit));
                        }
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println("const " + moniteur.getName() + "_I = " + NodePersonnaliser.getBinairRate("0", nberBit));
                        }
                        /**
                         * **************** Nodes *****************
                         */
                        writer.println("\n--**************** Nodes Declaration ****************--\n");
                        for (int i = 0; i < nberNode + 1; i++) {
                            NodePersonnaliser node = arrNodes[i];
                            writer.print("node " + node.getName() + "(" + node.getInput() + ")");
                            writer.println(" returns (" + node.getOutput() + "; " + node.getName() + "nbrFired: UsrInt );\n");
                            if (Integer.parseInt(node.getDuree()) != 1) {
                                writer.println("var");
                                writer.println(node.getOutputProdiusDelay() + " :UsrInt;");
                            }

                            writer.println("let");
                            String str = node.getDoFireString(node.getMaxAutoConc());

                            writer.println(node.getName() + "nbrFired= " + str + "\n");
                            String st = node.getOutputConsium();
                            if (node.getOutputConsium().equals("")) {
                                st = node.getOutputProdiusDelay();
                            } else {
                                if (Integer.parseInt(node.getDuree()) != 1) {
                                    st += "," + node.getOutputProdiusDelay();
                                } else {
                                    st += "," + node.getOutputProdius();
                                }
                            }
                            writer.println("(" + st + ") = " + node.getMultFired() + "\n");
                            if (Integer.parseInt(node.getDuree()) != 1) {
                                writer.println(node.getProdiusAfterDelay());
                            }
                            writer.println("tel;");

                            writer.println("------------------");
                            writer.println("node " + node.getName() + "DoFire("
                                    + node.getInput().replaceAll(" :UsrInt", ",") + "times:UsrInt) returns ( yes: bool);");
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
                        String topDriverInput = "";
                        for (Driver driver : arrDriver) {
                            topDriverInput += driver.getName() + "_P";
                        }
                        deleteComma(topDriverInput);
                        topDriverInput += ": UsrInt";
                        writer.println("node top (" + topDriverInput + ") returns ("
                                + getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur) + ");\n");
                        writer.println("-------------- Les variables -------------- \n");
                        writer.println("var\n");

                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println(arrChannel[i].getName() + "_C, " + arrChannel[i].getName() + "_P: UsrInt; ");
                        }

                        for (Driver driver : arrDriver) {
                            writer.println(driver.getName() + "_C : UsrInt;");
                        }
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println(moniteur.getName() + "_P : UsrInt;");
                        }

                        writer.println("\nlet\n");
                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println(arrChannel[i].getName() + " = " + arrChannel[i].getName()
                                    + "_I -> pre UsrIntPlus( UsrIntMinus( " + arrChannel[i].getName() + ","
                                    + arrChannel[i].getName() + "_C), " + arrChannel[i].getName() + "_P);");
                        }
                        writer.println();
                        for (Driver driver : arrDriver) {
                            writer.println(driver.getName() + " = " + driver.getName()
                                    + "_I -> pre if UsrIntLt(UsrIntPlus( UsrIntMinus( "
                                    + driver.getName() + "," + driver.getName() + "_C), "
                                    + driver.getName() + "_P), maxValue) \n"
                                    + "\t\t\t\tthen UsrIntPlus( UsrIntMinus( " + driver.getName() + ","
                                    + driver.getName() + "_C), " + driver.getName() + "_P)\n"
                                    + "\t\t\t\telse UsrIntMinus( " + driver.getName() + ","
                                    + driver.getName() + "_C);");
                        }
                        writer.println();
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println(moniteur.getName() + " = " + moniteur.getName()
                                    + "_I -> pre if UsrIntLt(UsrIntPlus(" + moniteur.getName()
                                    + ", " + moniteur.getName() + "_P), maxValue) \n"
                                    + "\t\t\t\tthen UsrIntPlus(" + moniteur.getName() + ", " + moniteur.getName() + "_P)\n"
                                    + "\t\t\t\telse " + moniteur.getName() + ";");
                        }
                        writer.println();
                        for (NodePersonnaliser node : arrNodes) {
                            String[][] arrPort = node.getArrPort();
                            String outputNode = "";
                            String inputNode = "";
                            for (String[] port : arrPort) {
                                if (port[1].equals("out")) {
                                    outputNode += searchOut(node.getName(), port[0], arrChannel, arrMoniteur) + "_P, ";
                                } else {
                                    outputNode += searchIn(node.getName(), port[0], arrChannel, arrDriver) + "_C, ";
                                    inputNode += searchIn(node.getName(), port[0], arrChannel, arrDriver) + ", ";
                                }
                            }

                            writer.println("(" + outputNode + node.getName() + "nbrFired) = " + node.getName() + "(" + deleteComma(inputNode) + ");");

                        }
                        writer.println();

                        writer.println("\ntel;");
                        writer.println();
                        writer.println();
                        writer.println("--**************** Node Harness Declaration ****************--\n");
                        writer.println("node Harness (" + topDriverInput + ") returns (noError :bool)\n");
                        writer.println("var");
                        writer.println();
                        writer.println(getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur) + ";\n");
                        writer.println("positiveValues: bool;\n\nlet\n");
                        writer.println("(" + getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur).replaceAll(":UsrInt", "")
                                + ") = top(" + topDriverInput.replaceAll(": UsrInt", "") + ");");
                        writer.println("\npositiveValues = " + positiveValuesString(arrChannel, arrNodes, arrDriver, arrMoniteur) + ";");
                        writer.println("\nnoError= positiveValues and true;\n\ntel");

                        /**
                         *  **************** close writer ****************
                         */
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("ERROR output.lus");
                    }
                    /*************************************************************************************************
                     ******************************** outputDeadlock.lus *********************************************
                     ************************************************************************************************/
                    try {
                        PrintWriter writer = new PrintWriter("../IPC/IPC_pipeline_genere/IPCoutputDeadlock.lus", "UTF-8");
                        /**
                         * **************** const ****************
                         */
                        writer.println("include \"./toInclude_BitBlasting_ok.lus\"\n");
                        writer.println("const maxValue = " + NodePersonnaliser.getBinairRate(maxValue, nberBit));
                        writer.println("\n--**************** Const Rates ****************--\n");
                        for (int i = 0; i <= nberNode; i++) {

                            String[][] arrPort = arrNodes[i].getArrPort();
                            for (String[] s : arrPort) {
                                writer.println("const " + arrNodes[i].getName() + "_" + s[0] + "_rate_" + s[1] + " = " + NodePersonnaliser.getBinairRate(s[2], nberBit));
                            }

                        }
                        writer.println("\n--**************** Init Channel ****************--\n");
                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println("const " + arrChannel[i].getName() + "_I = " + NodePersonnaliser.getBinairRate(arrChannel[i].getInitialTokens(), nberBit));
                        }
                        writer.println();
                        for (Driver driver : arrDriver) {
                            writer.println("const " + driver.getName() + "_I = " + NodePersonnaliser.getBinairRate("0", nberBit));
                        }
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println("const " + moniteur.getName() + "_I = " + NodePersonnaliser.getBinairRate("0", nberBit));
                        }
                        /**
                         * **************** Nodes *****************
                         */
                        writer.println("\n--**************** Nodes Declaration ****************--\n");
                        for (int i = 0; i < nberNode + 1; i++) {
                            NodePersonnaliser node = arrNodes[i];
                            writer.print("node " + node.getName() + "(" + node.getInput() + ")");
                            writer.println(" returns (" + node.getOutput() + "; " + node.getName() + "nbrFired: UsrInt );\n");
                            

                            writer.println("let");
                            String str = node.getDoFireString(node.getMaxAutoConc());

                            writer.println(node.getName() + "nbrFired= " + str + "\n");
                            String st = node.getOutputConsium();
                            if (node.getOutputConsium().equals("")) {
                                st = node.getOutputProdiusDelay();
                            } else {
                                
                                    st += "," + node.getOutputProdius();
                                
                            }
                            writer.println("(" + st + ") = " + node.getMultFired() + "\n");
                            
                            writer.println("tel;");

                            writer.println("------------------");
                            writer.println("node " + node.getName() + "DoFire("
                                    + node.getInput().replaceAll(" :UsrInt", ",") + "times:UsrInt) returns ( yes: bool);");
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
                        String topDriverInput = "";
                        for (Driver driver : arrDriver) {
                            topDriverInput += driver.getName() + "_P";
                        }
                        deleteComma(topDriverInput);
                        topDriverInput += ": UsrInt";
                        writer.println("node top (" + topDriverInput + ") returns ("
                                + getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur) + ");\n");
                        writer.println("-------------- Les variables -------------- \n");
                        writer.println("var\n");

                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println(arrChannel[i].getName() + "_C, " + arrChannel[i].getName() + "_P: UsrInt; ");
                        }

                        for (Driver driver : arrDriver) {
                            writer.println(driver.getName() + "_C : UsrInt;");
                        }
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println(moniteur.getName() + "_P : UsrInt;");
                        }

                        writer.println("\nlet\n");
                        for (int i = 0; i <= nberChannel; i++) {
                            writer.println(arrChannel[i].getName() + " = " + arrChannel[i].getName()
                                    + "_I -> pre UsrIntPlus( UsrIntMinus( " + arrChannel[i].getName() + ","
                                    + arrChannel[i].getName() + "_C), " + arrChannel[i].getName() + "_P);");
                        }
                        writer.println();
                        for (Driver driver : arrDriver) {
                            writer.println(driver.getName() + " = " + driver.getName()
                                    + "_I -> pre if UsrIntLt(UsrIntPlus( UsrIntMinus( "
                                    + driver.getName() + "," + driver.getName() + "_C), "
                                    + driver.getName() + "_P), maxValue) \n"
                                    + "\t\t\t\tthen UsrIntPlus( UsrIntMinus( " + driver.getName() + ","
                                    + driver.getName() + "_C), " + driver.getName() + "_P)\n"
                                    + "\t\t\t\telse UsrIntMinus( " + driver.getName() + ","
                                    + driver.getName() + "_C);");
                        }
                        writer.println();
                        for (Moniteur moniteur : arrMoniteur) {
                            writer.println(moniteur.getName() + " = " + moniteur.getName()
                                    + "_I -> pre if UsrIntLt(UsrIntPlus(" + moniteur.getName()
                                    + ", " + moniteur.getName() + "_P), maxValue) \n"
                                    + "\t\t\t\tthen UsrIntPlus(" + moniteur.getName() + ", " + moniteur.getName() + "_P)\n"
                                    + "\t\t\t\telse " + moniteur.getName() + ";");
                        }
                        writer.println();
                        for (NodePersonnaliser node : arrNodes) {
                            String[][] arrPort = node.getArrPort();
                            String outputNode = "";
                            String inputNode = "";
                            for (String[] port : arrPort) {
                                if (port[1].equals("out")) {
                                    outputNode += searchOut(node.getName(), port[0], arrChannel, arrMoniteur) + "_P, ";
                                } else {
                                    outputNode += searchIn(node.getName(), port[0], arrChannel, arrDriver) + "_C, ";
                                    inputNode += searchIn(node.getName(), port[0], arrChannel, arrDriver) + ", ";
                                }
                            }

                            writer.println("(" + outputNode + node.getName() + "nbrFired) = " + node.getName() + "(" + deleteComma(inputNode) + ");");

                        }
                        writer.println();

                        writer.println("\ntel;");
                        writer.println();
                        writer.println();
                        writer.println("--**************** Node Harness Declaration ****************--\n");
                        writer.println("node Harness (" + topDriverInput + ") returns (noError :bool)\n");
                        writer.println("var");
                        writer.println();
                        writer.println(getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur) + ";\n");
                        writer.println("noDeadlock : bool;\n\nlet\n");
                        writer.println("(" + getOutputNodeTop(arrChannel, arrNodes, arrDriver, arrMoniteur).replaceAll(":UsrInt", "")
                                + ") = top(" + topDriverInput.replaceAll(": UsrInt", "") + ");");
                        writer.println("\nnoDeadlock = " + deadlockString(arrNodes) + ";");
                        writer.println("\nnoError= noDeadlock;\n\ntel");

                        /**
                         *  **************** close writer ****************
                         */
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("ERROR output.lus");
                    }
                    /*************************************************************************************************
                     ************************************ bibléothéque ***********************************************
                     ************************************************************************************************/
                    try {
                        PrintWriter writer = new PrintWriter("lustre_test_ouput/bibléothéque.lus", "UTF-8");
                        writer.println("const BIN_SZ = " + nberBit + ";\n");
                        for (int i = 0; i <= maxAutoConcurrences; i++) {
                            writer.println("const UsrInt_" + i + " = " + NodePersonnaliser.getBinairRate(Integer.toString(i), nberBit));
                        }
                        /**
                         * NFG = nombre des fonctions à générée.
                         */
                        int NFG = (int) (Math.log10(nberBit) / Math.log10(2));
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("type UsrInt = bool^BIN_SZ ;\n"
                                + "\n"
                                + "node UsrIntIf (c : bool ; x : UsrInt ; y : UsrInt) returns (s : UsrInt);\n"
                                + "let\n"
                                + "s = if c^BIN_SZ then x else y ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        for (int i = 0; i < NFG - 3; i++) {
                            writer.println("node UsrIntPlus_"+(int) Math.pow(2, i + 3)+" ( x, y : bool^"+(int) Math.pow(2, i + 3)
                                    + " ) returns ( s : bool^"+(int) Math.pow(2, i + 3)
                                    + " ) ;\n"
                                    + "var c : bool^"+((int) Math.pow(2, i + 3)+1)
                                    + " ;\n"
                                    + "let\n"
                                    + "c [ 0 ] = false ;\n"
                                    + "c[1.."+((int) Math.pow(2, i + 3))
                                    + "]= if c [0.."+((int) Math.pow(2, i + 3))
                                    + " -1] then (x or y) else (x and y);\n"
                                    + "s= x xor y xor c[0.."+((int) Math.pow(2, i + 3))
                                    + " -1];\n"
                                    + "tel");
                            writer.println("\n------------------------------------------------------\n");
                        }
                        writer.println("node UsrIntPlus ( x, y : UsrInt ) returns ( s : UsrInt ) ;\n"
                                + "var c : bool ^( BIN_SZ +1) ;\n"
                                + "let\n"
                                + "c [ 0 ] = false ;\n"
                                + "c[1..BIN_SZ]= if c [0..BIN_SZ -1] then (x or y) else (x and y);\n"
                                + "s= x xor y xor c[0..BIN_SZ -1];\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntChs ( x : UsrInt ) returns ( s : UsrInt ) ;\n"
                                + "var c : bool ^( BIN_SZ +1) ;\n"
                                + "let\n"
                                + "c [ 0 ] = false ;\n"
                                + "c [1..BIN_SZ]= c [0..BIN_SZ -1] or x ;\n"
                                + "s = c [0..BIN_SZ -1] xor x ;\n"
                                + "tel");

                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntIncr ( x : UsrInt ) returns ( s : UsrInt ) ;\n"
                                + "var c : bool ^( BIN_SZ +1) ;\n"
                                + "let\n"
                                + "c [ 0 ] = true ;\n"
                                + "c [ 1..BIN_SZ ] = c [ 0..BIN_SZ -1] and x ;\n"
                                + "s = c [ 0..BIN_SZ -1] xor x ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntDecr ( x : UsrInt ) returns ( s : UsrInt ) ;\n"
                                + "var c : bool ^( BIN_SZ +1) ;\n"
                                + "let\n"
                                + "c [ 0 ] = true ;\n"
                                + "c [ 1..BIN_SZ ] = c [ 0..BIN_SZ -1] and not x ;\n"
                                + "s = c [ 0..BIN_SZ -1] xor x ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntMinus ( x : UsrInt ; y : UsrInt ) returns ( s : UsrInt ) ;\n"
                                + "let\n"
                                + "s = UsrIntPlus ( x , UsrIntChs ( y ) ) ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntGt ( x : UsrInt ; y : UsrInt ) returns ( o : bool ) ;\n"
                                + "var\n"
                                + "z : UsrInt ;\n"
                                + "let\n"
                                + "-- x > y <=> y - x < 0\n"
                                + "z = UsrIntPlus ( y , UsrIntChs ( x ) ) ;\n"
                                + "o = z [ BIN_SZ - 1 ] ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntLt ( x : UsrInt ; y : UsrInt ) returns ( o : bool ) ;\n"
                                + "let\n"
                                + "-- x < y <=> y > x\n"
                                + "o = UsrIntGt ( y , x ) ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntGte ( x : UsrInt ; y : UsrInt ) returns ( o : bool ) ;\n"
                                + "let\n"
                                + "-- x >= y <=> not y > x\n"
                                + "o = not UsrIntGt ( y , x ) ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntLte ( x : UsrInt ; y : UsrInt ) returns ( o : bool ) ;\n"
                                + "let\n"
                                + "-- x <= y <=> not x > y\n"
                                + "o = not UsrIntGt ( x , y ) ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node UsrIntEq ( x : UsrInt ; y : UsrInt ) returns ( o : bool ) ;\n"
                                + "var\n"
                                + "a : bool^BIN_SZ +1;\n"
                                + "let\n"
                                + "a [ 0 ] = true ;\n"
                                + "a [ 1..BIN_SZ ] = a [ 0..BIN_SZ -1] and ( x = y ) ;\n"
                                + "o = a [ BIN_SZ - 1 ] ;\n"
                                + "tel");
                        writer.println("\n------------------------------------------------------\n");
                        writer.println("node mult2Bit(a, b: bool^2) returns (q: bool^4)\n"
                                + "let\n"
                                + " \n"
                                + "	q[0]= a[0] and b[0];\n"
                                + "	q[1]= a[1] and b[0] xor a[0] and b[1];\n"
                                + "	q[2]= a[1] and b[0] and a[0] and b[1] xor a[1] and b[1];\n"
                                + "	q[3]= a[1] and b[0] and a[0] and b[1] and a[1] and b[1];\n"
                                + "\n"
                                + "tel");
                        for (int i = 0; i < NFG - 2; i++) {
                            writer.println("\n------------------------------------------------------\n");

                            writer.print("node ");
                            if (i == NFG - 3) {
                                writer.print("UsrIntMult");
                                writer.println(" ( a, b: bool^" + (int) Math.pow(2, i + 3)
                                    + ") returns ( q: bool^" + (int) Math.pow(2, i + 3)
                                    + ")");
                            } else {
                                writer.print("mult" + (int) Math.pow(2, i + 2)
                                        + "Bit");
                            

                            writer.println(" ( a, b: bool^" + (int) Math.pow(2, i + 2)
                                    + ") returns ( q: bool^" + (int) Math.pow(2, i + 3)
                                    + ")");}
                            writer.println("var\n"
                                    + " \n"
                                    + "a0, a1, b0, b1 : bool^" + (int) Math.pow(2, i + 1)
                                    + ";\n"
                                    + "outMult1, outMult2, outMult3, outMult4 : bool^" + (int) Math.pow(2, i + 2)
                                    + ";\n"
                                    + "in1Add1, in2Add1, in1Add2, in2Add2,  outLastAdd: bool^" + (int) Math.pow(2, i + 3)
                                    + ";" + "\n\nlet\n");
                            writer.println("a0[0.." + ((int) Math.pow(2, i + 1) - 1) + "]= a[0.."
                                    + ((int) Math.pow(2, i + 1) - 1) + "];\n"
                                    + "a1[0.." + ((int) Math.pow(2, i + 1) - 1) + "]= a["
                                    + (int) Math.pow(2, i + 1) + ".." + (2 * (int) Math.pow(2, i + 1) - 1) + "];\n"
                                    + "b0[0.." + ((int) Math.pow(2, i + 1) - 1) + "]= b[0.." + ((int) Math.pow(2, i + 1) - 1) + "];\n"
                                    + "b1[0.." + ((int) Math.pow(2, i + 1) - 1) + "]= b["
                                    + (int) Math.pow(2, i + 1) + ".." + (2 * (int) Math.pow(2, i + 1) - 1) + "];");
                            writer.println("\noutMult1 = mult" + (int) Math.pow(2, i + 1) + "Bit (a0, b0);\n"
                                    + "outMult2 = mult" + (int) Math.pow(2, i + 1) + "Bit (a1, b0);\n"
                                    + "outMult3 = mult" + (int) Math.pow(2, i + 1) + "Bit (a0, b1);\n"
                                    + "outMult4 = mult" + (int) Math.pow(2, i + 1) + "Bit (a1, b1);\n");
                            writer.println("in1Add1[0.." + ((int) Math.pow(2, i + 1) - 1)
                                    + "]= outMult1[" + (int) Math.pow(2, i + 1) + ".."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "]; in1Add1["
                                    + (int) Math.pow(2, i + 1) + ".." + ((int) Math.pow(2, i + 3) - 1) + "]= false^("
                                    + (((int) Math.pow(2, i + 3) - 1) - (int) Math.pow(2, i + 1) + 1) + ");\n"
                                    + "in2Add1[0.."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "]=outMult2[0.."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "];  in2Add1["
                                    + (2 * (int) Math.pow(2, i + 1)) + ".."
                                    + (2 * (int) Math.pow(2, i + 2) - 1) + "]= false^("
                                    + ((2 * (int) Math.pow(2, i + 2) - 1) - (2 * (int) Math.pow(2, i + 1)) + 1) + ");\n"
                                    + "\n"
                                    + "in1Add2[0.."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "]=outMult3[0.."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "];  in1Add2["
                                    + (2 * (int) Math.pow(2, i + 1)) + ".."
                                    + (2 * (int) Math.pow(2, i + 2) - 1) + "]= false^("
                                    + ((2 * (int) Math.pow(2, i + 2) - 1) - (2 * (int) Math.pow(2, i + 1)) + 1) + ");\n"
                                    + "in2Add2[" + ((int) Math.pow(2, i + 1)) + ".." + ((int) Math.pow(2, i + 1) + (int) Math.pow(2, i + 2) - 1) + "]=outMult4[0.."
                                    + (2 * (int) Math.pow(2, i + 1) - 1) + "];  in2Add2[0.." + ((int) Math.pow(2, i + 1) - 1)
                                    + "]= false^" + ((int) Math.pow(2, i + 1)) + "; in2Add2[" + ((int) Math.pow(2, i + 1)
                                    + (int) Math.pow(2, i + 2)) + ".." + ((int) Math.pow(2, i + 3) - 1) + "]= false^("
                                    + ((int) Math.pow(2, i + 3) - ((int) Math.pow(2, i + 1) + (int) Math.pow(2, i + 2))) + ");\n"
                                    + "");
                            if (i == NFG - 3) {
                                writer.println("outLastAdd=UsrIntPlus(UsrIntPlus(in1Add1, in2Add1), UsrIntPlus(in1Add2, in2Add2));");
                            } else {
                                writer.println("outLastAdd=UsrIntPlus_" + ((int) Math.pow(2, i + 3)) + " (UsrIntPlus_" + ((int) Math.pow(2, i + 3)) + "(in1Add1, in2Add1), UsrIntPlus_" + ((int) Math.pow(2, i + 3)) + "(in1Add2, in2Add2));");

                            }
                            writer.println("q[0.." + ((int) Math.pow(2, i + 1) - 1) + "]=outMult1[0.." + ((int) Math.pow(2, i + 1) - 1) + "];\n"
                                    + "q[" + ((int) Math.pow(2, i + 1)) + ".." + ((int) Math.pow(2, i + 3) - 1) + "]= outLastAdd[0.."
                                    + ((int) Math.pow(2, i + 3) - ((int) Math.pow(2, i + 1)) - 1) + "];\n"
                                    + "\n"
                                    + "\n"
                                    + "tel\n"
                                    + "");
                        }
                        writer.println("\n------------------------------------------------------\n");

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

    private static String getOutputNodeTop(Channel[] arrChannel, NodePersonnaliser[] arrNodes, Driver[] arrDriver, Moniteur[] arrMoniteur) {
        String str = "";

        for (Channel channel : arrChannel) {
            str += channel.name + ", ";
        }
        for (Driver driver : arrDriver) {
            str += driver.getName() + ", ";
        }
        for (Moniteur moniteur : arrMoniteur) {
            str += moniteur.getName() + ", ";
        }
        for (NodePersonnaliser arrNode : arrNodes) {
            str += arrNode.getName() + "nbrFired, ";
        }

        return deleteComma(str) + ":UsrInt";
    }

    private static String positiveValuesString(Channel[] arrChannel, NodePersonnaliser[] arrNodes, Driver[] arrDriver, Moniteur[] arrMoniteur) {
        String str = "";

        for (Channel channel : arrChannel) {
            str += "UsrIntGte( " + channel.name + ", UsrInt_0) \n\tand ";
        }
        for (Driver driver : arrDriver) {
            str += "UsrIntGte( " + driver.getName() + ", UsrInt_0) \n\tand ";
        }
        for (Moniteur moniteur : arrMoniteur) {
            str += "UsrIntGte( " + moniteur.getName() + ", UsrInt_0) \n\tand ";
        }
        for (NodePersonnaliser arrNode : arrNodes) {
            str += "UsrIntGte( " + arrNode.getName() + "nbrFired " + ", UsrInt_0) \n\tand ";
        }
        str = str.trim();
        str = str.substring(0, str.length() - 5);
        return str;
    }
    private static String deadlockString(NodePersonnaliser[] arrNodes) {
        String str = "";

        
        for (NodePersonnaliser arrNode : arrNodes) {
            str += "UsrIntGte( " + arrNode.getName() + "nbrFired " + ", UsrInt_1) \n\tor ";
        }
        str = str.trim();
        str = str.substring(0, str.length() - 5);
        return str;
    }

    public static String searchOut(String nodeName, String portName, Channel[] arrChannel, Moniteur[] arrMoniteur) {
        String str = "";
        for (Channel channel : arrChannel) {
            if (channel.srcActor.equals(nodeName) && channel.srcPort.equals(portName)) {
                return channel.getName();
            }
        }
        for (Moniteur moniteur : arrMoniteur) {
            if (moniteur.getSrcActor().equals(nodeName) && moniteur.getPortSrc().equals(portName)) {
                return moniteur.getName();
            }
        }
        return str;
    }

    public static String searchIn(String nodeName, String portName, Channel[] arrChannel, Driver[] arrDriver) {
        String str = "";
        for (Channel channel : arrChannel) {
            if (channel.getDstActor().equals(nodeName) && channel.getDstPort().equals(portName)) {
                return channel.getName();
            }
        }
        for (Driver driver : arrDriver) {
            if (driver.getActorDest().equals(nodeName) && driver.getPortDest().equals(portName)) {
                return driver.getName();
            }
        }
        return str;
    }

    private static boolean BaliseErroné(Element racine) {
        NodeList racineNoeuds = racine.getChildNodes();
        for (int i = 0; i < racineNoeuds.getLength(); i++) {
            if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                String s = racineNoeuds.item(i).getNodeName();
                if (!s.equals("actor") && !s.equals("port") && !s.equals("channel")
                        && !s.equals("driver") && !s.equals("moniteur")) {
                    System.out.println(" vous avez le balise " + s + " Erronée...");
                    return true;
                }

            }
        }
        for (int i = 0; i < racineNoeuds.getLength(); i++) {
            if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (racineNoeuds.item(i).getNodeName().equals("actor")) {
                    NodeList Ports = racineNoeuds.item(i).getChildNodes();
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
