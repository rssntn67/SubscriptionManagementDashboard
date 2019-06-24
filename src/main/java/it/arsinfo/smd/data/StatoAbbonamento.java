package it.arsinfo.smd.data;

public enum StatoAbbonamento {
    PROPOSTA("Proposta"),
    INVIOEC("Inviato Estratto Conto"),
    VALIDATO("Validato"),
    ANNULLATO("Annullato");
    
    String descr;
    
    private StatoAbbonamento(String descr) {
        this.descr=descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    
    
}
