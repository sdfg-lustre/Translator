package SDFG2LUSTRE_cycle_cycle;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All rights reserved
 */

public class Channel {

    String name;
    String srcActor;
    String srcPort;
    String dstActor;
    String dstPort;
    String initialTokens;

    public Channel(String name, String srcActor, String srcPort, String dstActor, String dstPort, String initialTokens) {
        this.name = name;
        this.srcActor = srcActor;
        this.srcPort = srcPort;
        this.dstActor = dstActor;
        this.dstPort = dstPort;
        this.initialTokens = initialTokens;
    }

    public String getName() {
        return name;
    }

    public String getSrcActor() {
        return srcActor;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public String getDstActor() {
        return dstActor;
    }

    public String getDstPort() {
        return dstPort;
    }

    public String getInitialTokens() {
        return initialTokens;
    }

    @Override
    public String toString() {
        return "channel{" + "name=" + name + ", srcActor=" + srcActor + ", srcPort=" + srcPort + ", dstActor=" + dstActor + ", dstPort=" + dstPort + ", initialTokens=" + initialTokens + '}';
    }
    public String getInputChannelName(String name){
        if(dstActor.equals(name))
            return this.name+", ";
        return "";
    }
    public String getOutputChannelNameConsum(String name){
        if(dstActor.equals(name))
            return this.name+"_C, ";
        return "";
    }
    public String getOutputChannelNameProdus(String name){
        if(srcActor.equals(name))
          return this.name+"_P, ";
        return "";
    }
}
