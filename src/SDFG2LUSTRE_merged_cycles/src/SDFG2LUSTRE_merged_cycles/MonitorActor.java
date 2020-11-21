package SDFG2LUSTRE_merged_cycles;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All rights reserved
 */
public class MonitorActor {

    private final String name;
    private final   String srcActor;
    private final   String portSrc;

    public MonitorActor(String name, String srcActor, String portSrc) {
        this.name = name;
        this.srcActor = srcActor;
        this.portSrc = portSrc;
    }

    public String getName() {
        return name;
    }

    public String getSrcActor() {
        return srcActor;
    }

    public String getPortSrc() {
        return portSrc;
    }
    
}
