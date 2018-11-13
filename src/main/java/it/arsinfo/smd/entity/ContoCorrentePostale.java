package it.arsinfo.smd.entity;

public enum ContoCorrentePostale {
    UNO("000063470009");
    private String ccp; 
    
    private ContoCorrentePostale(String ccp) {
        this.ccp = ccp;
    }
    
    public String getCcp() {
        return ccp;
    }
}