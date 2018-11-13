package it.arsinfo.smd.entity;

public enum Cuas {
    CUAS3(3,"Venezia","C.U.A.S. reale"),
    CUAS4(4,"Telematici","C.U.A.S. virtuale"),
    CUAS5(5,"Firenze","C.U.A.S. reale"),
    CUAS6(6,"Ancona","C.U.A.S. reale"),
    CUAS7(7,"Bari","C.U.A.S. reale"),
    CUAS8(8,"AVDS e dematerializzati","C.U.A.S. virtuale");
    
    private int codice;
    private String denominazione;
    private String note;

    public static Cuas getCuas(int codice) 
    {
        for (Cuas l : Cuas.values()) {
            if (l.codice == codice) return l;
        }
        throw new IllegalArgumentException("CUAS not found.");

    }
    private Cuas(int codice, String denominazione, String note) {
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
