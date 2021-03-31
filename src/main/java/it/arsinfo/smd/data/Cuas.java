package it.arsinfo.smd.data;

public enum Cuas {
    NOCCP(999999,"No CCP","Vedere Versamenti"),
    VENEZIA(3,"Venezia","C.U.A.S. reale"),
    TELEMATICI(4,"Telematici","C.U.A.S. virtuale"),
    FIRENZE(5,"Firenze","C.U.A.S. reale"),
    ANCONA(6,"Ancona","C.U.A.S. reale"),
    BARI(7,"Bari","C.U.A.S. reale"),
    AVDS(8,"AVDS e dematerializzati","C.U.A.S. virtuale");
    
    private final int codice;
    private final String denominazione;
    private final String note;

    public static Cuas getCuas(int codice) 
    {
        for (Cuas l : Cuas.values()) {
            if (l.codice == codice) return l;
        }
        throw new IllegalArgumentException("CUAS not found.");

    }

    Cuas(int codice, String denominazione, String note) {
        this.codice=codice;
        this.denominazione = denominazione;
        this.note=note;
    }

    public int getCodice() {
        return codice;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public String getNote() {
        return note;
    }    
    
}
