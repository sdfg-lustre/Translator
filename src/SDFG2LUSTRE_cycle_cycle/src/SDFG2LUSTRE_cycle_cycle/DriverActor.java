package SDFG2LUSTRE_cycle_cycle;

/**
 *
 * Copyright 2020, Imed E. Bennour, National Engineering School at Sousse, All rights reserved
 */
public class DriverActor {

    private final String name;
    private final String actorDest;
    private final String portDest;

    public DriverActor(String name, String actorDest, String portDest) {
        this.name = name;
        this.actorDest = actorDest;
        this.portDest = portDest;
    }

    public String getName() {
        return name;
    }

    public String getActorDest() {
        return actorDest;
    }

    public String getPortDest() {
        return portDest;
    }
    
}
