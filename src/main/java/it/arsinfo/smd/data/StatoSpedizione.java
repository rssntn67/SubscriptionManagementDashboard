package it.arsinfo.smd.data;

public enum StatoSpedizione {
    PROGRAMMATA("Programmata"),
    INVIATA("Inviata"),
    SOSPESA("Sospesa"),
    ANNULLATA("Annullata");
    
    String descr;
    
    private StatoSpedizione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}
