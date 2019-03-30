package it.arsinfo.smd.data;

public enum Sostitutivo {
     S("S","Bollettino sostitutivo"),
     N("N","Bollettino originale");
    
    String descr;
    String tipo;
    
    public static Sostitutivo getTipoAccettazione(String tipo) {
        for (Sostitutivo l : Sostitutivo.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Sostitutivo not found.");
    }
    
    private Sostitutivo(String tipo,String descr) {
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
