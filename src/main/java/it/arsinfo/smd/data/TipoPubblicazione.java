package it.arsinfo.smd.data;

public enum TipoPubblicazione {
	UNICO("Pubblicazione"),
	MENSILE("Periodico Mensile"),
	SEMESTRALE("Periodico Semestrale"),
	ANNUALE("Periodico Annuale");
    
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    private TipoPubblicazione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    
}