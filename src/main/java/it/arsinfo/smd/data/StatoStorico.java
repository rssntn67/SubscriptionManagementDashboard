package it.arsinfo.smd.data;

public enum StatoStorico {
    NUOVO("Nuovo"),
    VALIDO("Valido"),
    SOSPESO("Sospeso");
    
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
