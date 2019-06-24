package it.arsinfo.smd.data;

public enum Stato {
    NUOVO("Nuovo"),
    VALIDO("Valido"),
    SOSPESO("Sospeso");
    
    String descr;
    
    private Stato(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}
