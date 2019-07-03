package it.arsinfo.smd.data;

public enum StatoAbbonamento {
    Nuovo("Nuova Proposta"),
    Proposto("Inviata Proposta"),
    Validato("Validato"),
    InviatoEC("Inviato Estratto Conto"),
    Annullato("Annullato");
    
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
