package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.arsinfo.smd.data.StatoOperazioneIncasso;

@Entity
public class OperazioneIncasso implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Abbonamento abbonamento;
    
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Versamento versamento;
    
    @Enumerated(EnumType.STRING)
    private StatoOperazioneIncasso statoOperazioneIncasso=StatoOperazioneIncasso.Incasso;
    
    private String description;
    private String operatore="admin";
    private BigDecimal importo=BigDecimal.ZERO;

    @Temporal(TemporalType.TIMESTAMP)
    private final Date data = new Date();

	public Abbonamento getAbbonamento() {
		return abbonamento;
	}

	public void setAbbonamento(Abbonamento abbonamento) {
		this.abbonamento = abbonamento;
	}

	public Versamento getVersamento() {
		return versamento;
	}

	public void setVersamento(Versamento versamento) {
		this.versamento = versamento;
	}

	public StatoOperazioneIncasso getStatoOperazioneIncasso() {
		return statoOperazioneIncasso;
	}

	public void setStatoOperazioneIncasso(StatoOperazioneIncasso statoOperazioneIncasso) {
		this.statoOperazioneIncasso = statoOperazioneIncasso;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public Date getData() {
		return data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "OperazioneIncasso [id=" + id + ", abbonamento_id=" + abbonamento.getId() + ", versamento_id=" + versamento.getId()
				+ ", statoOperazioneIncasso=" + statoOperazioneIncasso + ", description=" + description + ", operatore="
				+ operatore + ", importo=" + importo + ", data=" + data + "]";
	}
    
    
}
