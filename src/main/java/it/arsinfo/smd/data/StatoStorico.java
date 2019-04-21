package it.arsinfo.smd.data;

public enum StatoStorico {
    NUO("Nuovo"),
    ANN("Annullato"),
    REG("In Regola"),
    NON("Non in Regola"),
    OMA("Omaggio"),
    SOS("Sospeso");
    
    String descr;
    
    private StatoStorico(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}
