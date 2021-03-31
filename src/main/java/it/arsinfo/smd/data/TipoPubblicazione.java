package it.arsinfo.smd.data;

public enum TipoPubblicazione {
	UNICO("Pubblicazione"),
	MENSILE("Periodico Mensile"),
	SEMESTRALE("Periodico Semestrale"),
	ANNUALE("Periodico Annuale");
    
    private final String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    TipoPubblicazione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    
}