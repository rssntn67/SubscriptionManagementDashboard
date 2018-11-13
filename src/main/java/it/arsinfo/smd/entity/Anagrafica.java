package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Anagrafica {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Diocesi diocesi;
    @Enumerated(EnumType.STRING)
    private Regione regioneVescovi;
    @Enumerated(EnumType.STRING)
    private CentroDiocesano centroDiocesano;

    private TitoloAnagrafica titolo;   
    private String nome;
    private String cognome;
    private String intestazione;
    
    private String indirizzo;
    private String cap;
    private String citta;
    @Enumerated(EnumType.STRING)
    private Paese paese;

    private String email;
    private String telefono;
    private String cellulare;
    private String note;
    
    private String codfis;
    private String piva;

    @Enumerated(EnumType.STRING)
    private Omaggio omaggio; 
    @Enumerated(EnumType.STRING)
    private BmCassa bmCassa;

	
	// Aggiornamento manuale
    private boolean consiglioNazionaleADP; //10 | CONSIGLIO NAZIONALE A.D.P.
    private boolean presidenzaADP; //49 | CONSIGLIO PRESIDENZA ADP
    private boolean direzioneADP; //15 | MEMBRI DIREZIONE ADP
    private boolean caricheSocialiADP; //141 | CARICHE SOCIALI E RAPPRESENTANTI
    private boolean delegatiRegionaliADP; //140 | DELEGATI REGIONALI

	// se e' vescovo	non invio ma utilizzo gli invii per le Diocesi
	//66 | CARDINALI // serve per le lettere, nella corrispondenza deve comparire eminenza vescovi che sono anche cardinali
	//In Anagrafica la Curia Diocesana
	// Viene usata soprattutto per l'invio omaggio del messaggio ai vescovi
	// Bisogna mettere il nome del Vescovo
	// dal sito chiesacattolicaitaliana.it
	// I controlli vanno fatti prima di ogni invio delle riviste	
	// Logica implementativa Associare All'indirizzo della diocesi
	// l'Id della Diocesi.
	// Pertanto invio al Vescovo della diocesi avendo aggiornato il Vescovo.
			
    private boolean presidenteDiocesano;//52 | Presidenti e Referenti DIOCESANI    
    private boolean direttoreDiocesiano;//1 | DIRETTORE DIOCESANO	
    private boolean direttoreZonaMilano;//00013 | DIRETTORI ZONE MILANO	
    private boolean elencoMarisaBisi; //144 | MARISA BISI ELENCO
    private boolean promotoreRegionale; //12 | PROMOTORI REGIONALI

    @Enumerated(EnumType.STRING)
    private Regione regionePresidenteDiocesano;
    
    @Enumerated(EnumType.STRING)
    private Regione regioneDirettoreDiocesano;

    	
    public Anagrafica(String nome, String cognome) {
        this.nome=nome;
    	this.cognome=cognome;
    	this.paese=Paese.ITALIA;
    }

    public Anagrafica() {
        this.cognome = "";
        this.paese = Paese.ITALIA;
    }

    public Long getId() {
        return id;
    }

    public Diocesi getDiocesi() {
        return diocesi;
    }

    public void setDiocesi(Diocesi diocesi) {
        this.diocesi = diocesi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Override
    public String toString() {
        return String.format("Anagrafica[id=%d, Nome='%s', Cognome='%s', Diocesi='%s']",
                             id, nome, cognome, diocesi);
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Omaggio getOmaggio() {
        return omaggio;
    }

    public void setOmaggio(Omaggio omaggio) {
        this.omaggio = omaggio;
    }

    public Paese getPaese() {
        return paese;
    }

    public void setPaese(Paese paese) {
        this.paese = paese;
    }

    public String getCodfis() {
        return codfis;
    }

    public void setCodfis(String codfis) {
        this.codfis = codfis;
    }

    public String getPiva() {
        return piva;
    }

    public void setPiva(String piva) {
        this.piva = piva;
    }

    public CentroDiocesano getCentroDiocesano() {
        return centroDiocesano;
    }

    public void setCentroDiocesano(CentroDiocesano centroDiocesano) {
        this.centroDiocesano = centroDiocesano;
    }

    public BmCassa getBmCassa() {
        return bmCassa;
    }

    public void setBmCassa(BmCassa bmCassa) {
        this.bmCassa = bmCassa;
    }

    public boolean isConsiglioNazionaleADP() {
        return consiglioNazionaleADP;
    }

    public void setConsiglioNazionaleADP(boolean consiglioNazionaleADP) {
        this.consiglioNazionaleADP = consiglioNazionaleADP;
    }

    public boolean isPresidenzaADP() {
        return presidenzaADP;
    }

    public void setPresidenzaADP(boolean presidenzaADP) {
        this.presidenzaADP = presidenzaADP;
    }

    public boolean isDirezioneADP() {
        return direzioneADP;
    }

    public void setDirezioneADP(boolean direzioneADP) {
        this.direzioneADP = direzioneADP;
    }

    public boolean isCaricheSocialiADP() {
        return caricheSocialiADP;
    }

    public void setCaricheSocialiADP(boolean caricheSocialiADP) {
        this.caricheSocialiADP = caricheSocialiADP;
    }

    public boolean isDelegatiRegionaliADP() {
        return delegatiRegionaliADP;
    }

    public void setDelegatiRegionaliADP(boolean delegatiRegionaliADP) {
        this.delegatiRegionaliADP = delegatiRegionaliADP;
    }

    public Regione getRegioneVescovi() {
        return regioneVescovi;
    }

    public void setRegioneVescovi(Regione regioneVescovi) {
        this.regioneVescovi = regioneVescovi;
    }

    public boolean isDirettoreZonaMilano() {
        return direttoreZonaMilano;
    }

    public void setDirettoreZonaMilano(boolean direttoreZonaMilano) {
        this.direttoreZonaMilano = direttoreZonaMilano;
    }

    public boolean isPresidenteDiocesano() {
        return presidenteDiocesano;
    }

    public void setPresidenteDiocesano(boolean presidenteDiocesano) {
        this.presidenteDiocesano = presidenteDiocesano;
    }

    public Regione getRegionePresidenteDiocesano() {
        return regionePresidenteDiocesano;
    }

    public void setRegionePresidenteDiocesano(
            Regione regionePresidenteDiocesano) {
        this.regionePresidenteDiocesano = regionePresidenteDiocesano;
    }

    public boolean isDirettoreDiocesiano() {
        return direttoreDiocesiano;
    }

    public void setDirettoreDiocesiano(boolean direttoreDiocesiano) {
        this.direttoreDiocesiano = direttoreDiocesiano;
    }

    public Regione getRegioneDirettoreDiocesano() {
        return regioneDirettoreDiocesano;
    }

    public void setRegioneDirettoreDiocesano(
            Regione regioneDirettoreDiocesano) {
        this.regioneDirettoreDiocesano = regioneDirettoreDiocesano;
    }

    public boolean isElencoMarisaBisi() {
        return elencoMarisaBisi;
    }

    public void setElencoMarisaBisi(boolean elencoMarisaBisi) {
        this.elencoMarisaBisi = elencoMarisaBisi;
    }

    public boolean isPromotoreRegionale() {
        return promotoreRegionale;
    }

    public void setPromotoreRegionale(boolean promotoreRegionale) {
        this.promotoreRegionale = promotoreRegionale;
    }

    public TitoloAnagrafica getTitolo() {
        return titolo;
    }

    public void setTitolo(TitoloAnagrafica titolo) {
        this.titolo = titolo;
    }

    public String getIntestazione() {
        return intestazione;
    }

    public void setIntestazione(String intestazione) {
        this.intestazione = intestazione;
    }

    @Transient
    public String getCaption() {
        return this.nome + " " + this.cognome;
    }
}
