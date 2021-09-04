package it.arsinfo.smd.entity;

public enum Sostitutivo {
     S("S","Bollettino sostitutivo"),
     N("N","Bollettino originale");
    
    private final String descr;
    private final String tipo;
    
    public static Sostitutivo getTipoAccettazione(String tipo) {
        for (Sostitutivo l : Sostitutivo.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Sostitutivo not found.");
    }
    
    Sostitutivo(String tipo,String descr) {
        this.descr=descr;
        this.tipo=tipo;        
    }

    public String getDescr() {
        return descr;
    }
    public String getTipo() {
        return tipo;
    }


}
