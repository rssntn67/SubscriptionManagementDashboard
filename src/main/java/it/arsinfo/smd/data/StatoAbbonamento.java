package it.arsinfo.smd.data;

public enum StatoAbbonamento {
    PROPOSTA("Proposta"),
    VALIDATO("Validato"),
    ANNULLATO("Annullato"),    
    SOSPESO("Sospeso");
    
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
