package it.arsinfo.smd.data;

public enum TipoAccettazioneBollettino {
     CC("CC","premarcati Mav"),
     AV("AV","AVDS"),
     DP("DP","Dematerializzato Premarcato"),
     DI("DI","Dematerializzato con Immagine");
    
    String descr;
    String tipo;
    
    public static TipoAccettazioneBollettino getTipoAccettazione(String tipo) {
        for (TipoAccettazioneBollettino l : TipoAccettazioneBollettino.values()) {
            if (l.tipo.equals(tipo)) return l;
        }
        throw new IllegalArgumentException("Tipo Accettazione not found.");
    }
    
    private TipoAccettazioneBollettino(String tipo,String descr) {
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
