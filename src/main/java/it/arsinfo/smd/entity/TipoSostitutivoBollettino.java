package it.arsinfo.smd.entity;

public enum TipoSostitutivoBollettino {
     S("S","Bollettino sostitutivo"),
     N("N","Bollettino originale");
    
    String descr;
    String tipo;
    
    public static TipoSostitutivoBollettino getTipoAccettazione(String tipo) {
        for (TipoSostitutivoBollettino l : TipoSostitutivoBollettino.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Sostitutivo not found.");
    }
    
    private TipoSostitutivoBollettino(String tipo,String descr) {
        this.descr=descr;
        this.tipo=tipo;        
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
}
