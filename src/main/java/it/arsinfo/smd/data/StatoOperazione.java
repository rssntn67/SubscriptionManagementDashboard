package it.arsinfo.smd.data;

public enum StatoOperazione {
    PROGRAMMATA("Programmata"),
    INVIATA("Inviata");
    
    String descr;
    
    private StatoOperazione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}
