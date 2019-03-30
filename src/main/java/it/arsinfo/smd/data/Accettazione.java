package it.arsinfo.smd.data;

public enum Accettazione {
     CC("CC","premarcati Mav"),
     AV("AV","AVDS"),
     DP("DP","Dematerializzato Premarcato"),
     DI("DI","Dematerializzato con Immagine");
    
    String descr;
    String tipo;
    
    public static Accettazione getTipoAccettazione(String tipo) {
        for (Accettazione l : Accettazione.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Accettazione not found.");
    }
    
    private Accettazione(String tipo,String descr) {
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
