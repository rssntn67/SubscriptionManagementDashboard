package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Anagrafica {
	public enum Diocesi {
		ROMA,
		NAPOLI,
		POZZUOLI,
		CAPUA,
		BENEVENTO,
		CASERTA,
		MILANO;
	}

	public enum Paese {
		ITALIA,
		VATICANO,
		SANMARINO,
		ESTERO;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String cognome;
    @Enumerated(EnumType.STRING)
    private Diocesi diocesi;
    
	private String indirizzo;
	private String cap;
	private String citta;
	private Paese paese;
	private String email;
	private String telefono;
	private String cellulare;
	private String note;
	private boolean omaggio;
	private boolean privilegiato;

	public Anagrafica(String nome, String cognome) {
    	this.nome=nome;
    	this.cognome=cognome;
    	this.paese=Paese.ITALIA;
	}

	public Anagrafica() {
		this.cognome="";
    	this.paese=Paese.ITALIA;
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

	public boolean isOmaggio() {
		return omaggio;
	}

	public void setOmaggio(boolean omaggio) {
		this.omaggio = omaggio;
	}

	public boolean isPrivilegiato() {
		return privilegiato;
	}

	public void setPrivilegiato(boolean privilegiato) {
		this.privilegiato = privilegiato;
	}

	public Paese getPaese() {
		return paese;
	}

	public void setPaese(Paese paese) {
		this.paese = paese;
	}
}
