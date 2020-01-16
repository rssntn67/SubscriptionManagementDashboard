package it.arsinfo.smd.data;

public enum Ccp {
    UNO("000063470009","Poste"),
    DUE("000063470010","Mps"),
    TRE("000063470011","Cassa"),
    QUATTRO("000063470011","BPE");
    private String ccp; 
    private String cc; 
    
    public static Ccp getByCc(String cc) {
        for (Ccp l : Ccp.values()) {
            if (l.cc.equals(cc)) return l;
        }
        throw new IllegalArgumentException("Ccp not found.");
    }

    private Ccp(String cc, String ccp) {
        this.cc =cc;
        this.ccp = ccp;
    }
    
    public String getCcp() {
        return ccp;
    }
    
    public String getCc() {
        return cc;
    }

}