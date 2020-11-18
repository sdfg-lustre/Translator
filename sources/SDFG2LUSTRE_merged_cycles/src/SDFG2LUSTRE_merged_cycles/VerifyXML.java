package SDFG2LUSTRE_merged_cycles;

import org.w3c.dom.Element;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All
 * rights reserved
 */
public class VerifyXML {

    private final Channel[] channelsArray;
    private final Actor[] actorsArray;
    private final DriverActor[] driversArray;
    private final MonitorActor[] MonitorArray;
    private String ERROR = "";
    Element racine;

    public VerifyXML(Channel[] channelsArray, Actor[] actorsArray, DriverActor[] driversArray, MonitorActor[] MonitorArray, Element racine) {
        this.channelsArray = channelsArray;
        this.actorsArray = actorsArray;
        this.driversArray = driversArray;
        this.MonitorArray = MonitorArray;
        this.racine = racine;
    }

    boolean fichierOK() {
        if (falseAtributPort() || NodeSameName() || NodePortSameName() || ChannelSameName()
                || ChannelDestSrcNotCorrect() || !durationRateInitialTokensFalse()
                || !PortNoConnect() || nbrStagesHigh()) {
            return false;
        }
        return true;

    }

    String getERRORXML() {
        return "message d'ERROR : " + ERROR;
    }

    /**
     * tester si deux noeuds ont le même nom.
     *
     * @return true si vrai sinon false
     */
    private boolean NodeSameName() {
        for (int i = 0; i < actorsArray.length; i++) {
            Actor nodeI = actorsArray[i];
            for (int j = i + 1; j < actorsArray.length; j++) {
                Actor nodeJ = actorsArray[j];
                if (nodeI.getName().equals(nodeJ.getName())) {
                    ERROR += "Le noeud " + nodeI.getName() + " se répète plusieurs fois !! ";
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * tester si une noeud a deux portes ont le même nom.
     *
     * @return true si vrai sinon false
     */
    private boolean NodePortSameName() {
        for (Actor node : actorsArray) {
            String[][] arrPort = node.getPortsArray();
            for (int i = 0; i < arrPort.length; i++) {
                for (int j = i + 1; j < arrPort.length; j++) {
                    if (arrPort[i][0].equals(arrPort[j][0])) {
                        ERROR += "le noeud " + node.getName() + " comporte plusieur porte du même nom : " + arrPort[i][0];
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * tester si deux channels ont le même nom.
     *
     * @return true si vrai sinon false
     */
    private boolean ChannelSameName() {
        for (int i = 0; i < channelsArray.length; i++) {
            for (int j = i + 1; j < channelsArray.length; j++) {
                if (channelsArray[i].getName().equals(channelsArray[j].getName())) {
                    ERROR += "le nom du channel ce repete plusieur fois : " + channelsArray[i].getName();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * tester si le destination et le source des tous les edges bien definie.
     *
     * @return true si il a une mouvaise destination ou mouvaise source
     */
    private boolean ChannelDestSrcNotCorrect() {
        for (Channel channel : channelsArray) {
            String srcActor = channel.getSrcActor();
            String destActor = channel.getDstActor();
            boolean goodDest = false;
            boolean goodSrc = false;
            for (Actor node : actorsArray) {
                if (node.getName().equals(srcActor)) {
                    goodSrc = true;
                }
                if (node.getName().equals(destActor)) {
                    goodDest = true;
                }

            }
            if (!goodDest || !goodSrc) {
                ERROR += "problem channel : " + channel.getName();
                return true;
            }

        }
        return false;
    }

    /**
     * tester si on a le durée, le poid et initialToken correcte.
     *
     * @return false s'il y'a une problem
     */
    private boolean durationRateInitialTokensFalse() {
        return durationFalse() && rateFalse() && InitialTokenFalse();
    }

    /**
     * tester si le durée est supérieurs ou egale à 1.
     *
     * @return
     */
    private boolean durationFalse() {
        for (Actor node : actorsArray) {
            if (Integer.parseInt(node.getDuration()) < 1) {
                System.out.println("resultat du test : false");
                ERROR += "Le durée du noeud : " + node.getName() + " n'est pas supérieurs ou egale à 1";
                return false;
            }
        }
        return true;
    }

    /**
     * tester si le rate est supérieurs ou egale à 1.
     *
     * @return
     */
    private boolean rateFalse() {
        for (Actor node : actorsArray) {
            for (String[] port : node.getPortsArray()) {
                if (Integer.parseInt(port[2]) < 1) {
                    System.out.println("resultat du test : false");
                    ERROR += "Le rate du port " + port[0] + " du noeud : " + node.getName() + " n'est pas supérieurs ou egale à 1";
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * tester si le InitialToken est supérieurs ou egale à 0.
     *
     * @return
     */
    private boolean InitialTokenFalse() {
        for (Channel channel : channelsArray) {
            if (Integer.parseInt(channel.getInitialTokens()) < 0) {
                System.out.println("resultat du test : false");
                ERROR += "Le rate du channel " + channel.getName() + " n'est pas supérieurs ou egale à 0";
                return false;
            }
        }
        return true;
    }

    public Boolean searchOutBool(String nodeName, String portName, Channel[] channelsArray, MonitorActor[] MonitorArray) {

        for (Channel channel : channelsArray) {
            if (channel.srcActor.equals(nodeName) && channel.srcPort.equals(portName)) {
                return true;
            }
        }
        for (MonitorActor moniteur : MonitorArray) {
            if (moniteur.getSrcActor().equals(nodeName) && moniteur.getPortSrc().equals(portName)) {
                return true;
            }
        }
        return false;
    }

    public Boolean searchInBool(String nodeName, String portName, Channel[] channelsArray, DriverActor[] driversArray) {

        for (Channel channel : channelsArray) {
            if (channel.getDstActor().equals(nodeName) && channel.getDstPort().equals(portName)) {
                return true;
            }
        }
        for (DriverActor driver : driversArray) {
            if (driver.getActorDest().equals(nodeName) && driver.getPortDest().equals(portName)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true si tous les portes de chaque noeud sont connecter.
     */
    public Boolean PortNoConnect() {
        for (Actor node : actorsArray) {
            String[][] arrPort = node.getPortsArray();

            for (String[] port : arrPort) {
                if (port[1].equals("out")) {
                    if (!searchOutBool(node.getName(), port[0], channelsArray, MonitorArray)) {
                        ERROR += "Le port " + port[0] + " de la node " + node.getName() + " n'est pas connecter.";
                        return false;
                    }
                } else {
                    if (!searchInBool(node.getName(), port[0], channelsArray, driversArray)) {
                        ERROR += "Le port " + port[0] + " de la node " + node.getName() + " n'est pas connecter.";
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean falseAtributPort() {
        for (Actor node : actorsArray) {
            String[][] arrPort = node.getPortsArray();
            for (String[] port : arrPort) {
                if (!port[1].equals("in") && !port[1].equals("out")) {
                    ERROR += "l'atribut type du port n'est pas correcte : " + port[1];
                    return true;
                }
            }
        }
        return false;
    }

    private boolean nbrStagesHigh() {
        for (Actor nodeI : actorsArray) {
            if (nodeI.getMaxStages() > Integer.parseInt(nodeI.getDuration()) || nodeI.getMaxStages()==0) {
                ERROR += "Dans le noeud " + nodeI.getName() + " le nombre d'étages est soit égale à zéro soit supérieur à la durée !! ";
                return true;
            }
        }

        return false;
    }
}
