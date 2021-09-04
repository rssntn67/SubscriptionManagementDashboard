package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
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

@Entity
public class DocumentoTrasporto implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private DocumentiTrasportoCumulati documentiTrasportoCumulati;
    
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Versamento versamento;
    
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Anagrafica committente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoOperazioneIncasso statoOperazioneIncasso=StatoOperazioneIncasso.Incasso;

    @Column(nullable = false)
    private String ddt="ddt";

    @Column(nullable = false)
    private String operatore="admin";
    @Column(nullable = false)
    private BigDecimal importo=BigDecimal.ZERO;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private final Date data = new Date();

	public Versamento getVersamento() {
		return versamento;
	}

	public void setVersamento(Versamento versamento) {
		this.versamento = versamento;
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
    
	public String getHeader() {
		return String.format("%s:%s %.2f",ddt, committente.getHeader(),importo);
	}

	@Override
	public String toString() {
		return "Offerta [id=" + id + "ddt="+ddt+", offerteCumulate=" + documentiTrasportoCumulati + ", versamento=" + versamento
				+ ", committente=" + committente + ", statoOperazioneIncasso=" + statoOperazioneIncasso + ", operatore="
				+ operatore + ", importo=" + importo + ", data=" + data + "]";
	}

	public Anagrafica getCommittente() {
		return committente;
	}

	public void setCommittente(Anagrafica committente) {
		this.committente = committente;
	}

	public StatoOperazioneIncasso getStatoOperazioneIncasso() {
		return statoOperazioneIncasso;
	}

	public void setStatoOperazioneIncasso(StatoOperazioneIncasso statoOperazioneIncasso) {
		this.statoOperazioneIncasso = statoOperazioneIncasso;
	}

	public DocumentiTrasportoCumulati getDocumentiTrasportoCumulati() {
		return documentiTrasportoCumulati;
	}

	public void setDocumentiTrasportoCumulati(DocumentiTrasportoCumulati documentiTrasportoCumulati) {
		this.documentiTrasportoCumulati = documentiTrasportoCumulati;
	}

	public String getDdt() {
		return ddt;
	}

	public void setDdt(String ddt) {
		this.ddt = ddt;
	}
    
}
