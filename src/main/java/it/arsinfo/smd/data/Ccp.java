package it.arsinfo.smd.data;

public enum Ccp {
    UNO("000063470009");
    private String ccp; 
    
    public static Ccp getByCcp(String ccp) {
        for (Ccp l : Ccp.values()) {
            if (l.ccp.equals(ccp)) return l;
        }
        throw new IllegalArgumentException("Ccp not found.");
    }

    private Ccp(String ccp) {
        this.ccp = ccp;
    }
    
    public String getCcp() {
        return ccp;
    }
}