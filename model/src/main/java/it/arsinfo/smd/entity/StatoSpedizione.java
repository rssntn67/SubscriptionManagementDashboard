package it.arsinfo.smd.entity;

public enum StatoSpedizione {
    PROGRAMMATA("Programmata"),
    INVIATA("Inviata"),
    SOSPESA("Sospesa"),
    ANNULLATA("Annullata");
    
    private final String descr;
    
    StatoSpedizione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

}
