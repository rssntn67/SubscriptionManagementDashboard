package it.arsinfo.smd.entity;

public enum Accettazione {
     CC("CC","premarcati Mav"),
     AV("AV","AVDS"),
     DP("DP","Dematerializzato Premarcato"),
     DI("DI","Dematerializzato con Immagine");
    
    private final String descr;
    private final String tipo;
    
    public static Accettazione getTipoAccettazione(String tipo) {
        for (Accettazione l : Accettazione.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Accettazione not found.");
    }
    
    Accettazione(String tipo, String descr) {
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
