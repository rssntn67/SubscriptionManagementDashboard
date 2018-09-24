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
		MILANO;
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String cognome;
    @Enumerated(EnumType.STRING)
    private Diocesi diocesi;
    
	private String indirizzo;

    public Anagrafica(String nome, String cognome) {
    	this.nome=nome;
    	this.cognome=cognome;
	}

	public Anagrafica() {
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
}
