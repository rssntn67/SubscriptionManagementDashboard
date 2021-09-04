package it.arsinfo.smd.entity;

public enum Bollettino {
     TIPO247(247,"premarcati Mav"),
     TIPO896(896,"premarcati fatturatori"),
     TIPO674(674,"premarcati non fatturatori"),
     TIPO451(451,"bianchi personalizzati"),
     TIPO123(123,"bianchi");
    
    private final String bollettino;
    private final int tipo;
    
    public static Bollettino getTipoBollettino(int tipo) {
        for (Bollettino l : Bollettino.values()) {
            if (l.tipo == tipo) return l;
        }
        throw new IllegalArgumentException("Tipo Documento not found.");
    }
    
    Bollettino(int tipo,String descr) {
        this.bollettino=descr;
        this.tipo=tipo;        
    }

    public String getBollettino() {
        return bollettino;
    }

    public int getTipo() {
        return tipo;
    }

}
