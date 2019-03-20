package it.arsinfo.smd.data;

public enum ContoCorrentePostale {
    UNO("000063470009");
    private String ccp; 
    
    public static ContoCorrentePostale getByCcp(String ccp) {
        for (ContoCorrentePostale l : ContoCorrentePostale.values()) {
            if (l.ccp.equals(ccp)) return l;
        }
        throw new IllegalArgumentException("ContoCorrentePostale not found.");
    }

    private ContoCorrentePostale(String ccp) {
        this.ccp = ccp;
    }
    
    public String getCcp() {
        return ccp;
    }
}