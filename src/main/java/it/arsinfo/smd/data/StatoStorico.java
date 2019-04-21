package it.arsinfo.smd.data;

public enum StatoStorico {
    N("Nuovo"),
    A("Annullato"),
    P("In Regola"),
    R("Non in Regola"),
    O("Omaggio"),
    S("Sospeso");
    
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
