package it.arsinfo.smd.data;

public enum Invio {
    AdpSede("Invio in sede"),
    Destinatario("Invia a destinatario"),
    Intestatario("Invia a destinatario c/o intestatario");
    
    String descr;
    
    private Invio(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}