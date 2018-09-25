package it.arsinfo.smd.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class AbbonamentoPubblicazione {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	public AbbonamentoPubblicazione(Abbonamento abbonamento,
			Pubblicazione pubblicazione) {
		super();
		this.abbonamento = abbonamento;
		this.pubblicazione = pubblicazione;
	}

	@ManyToOne
	@JoinColumn
	private Abbonamento abbonamento;

	@OneToOne
	Pubblicazione pubblicazione;
	
	public AbbonamentoPubblicazione() {
		
	}

	public AbbonamentoPubblicazione(Abbonamento abbonamento) {
		super();
		this.abbonamento = abbonamento;
	}
		
    @Override
	public String toString() {
		String print = String.format("Pubblicazione[id=%d, Abbonamento='%d', Pubblicazione='%s']", 
				id, abbonamento.getId(), pubblicazione.getNome());
		return print;
	}

	public Abbonamento getAbbonamento() {
		return abbonamento;
	}

	public void setAbbonamento(Abbonamento abbonamento) {
		this.abbonamento = abbonamento;
	}

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}

	public void setPubblicazione(Pubblicazione pubblicazione) {
		this.pubblicazione = pubblicazione;
	}

		
}
