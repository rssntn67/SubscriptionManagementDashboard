package it.arsinfo.smd.data;

public enum InvioSpedizione {
    AdpSede("Invio in sede"),
    Spedizioniere("Invia a spedizioniere");
    
    String descr;
    
    private InvioSpedizione(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}