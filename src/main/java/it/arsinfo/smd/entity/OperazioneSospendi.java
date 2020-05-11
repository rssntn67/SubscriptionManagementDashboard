package it.arsinfo.smd.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"anno","pubblicazione_id"})
        })
//create unique index operazione_sospendi_idx_key on operazioneSospenmdiPubblicazione(anno,pubblicazione_id);
public class OperazioneSospendi implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;
        
    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese meseSpedizione = Mese.getMeseCorrente();

    private String operatore="admin";
    
    @Temporal(TemporalType.TIMESTAMP)
    private final Date data = new Date();


    public OperazioneSospendi() {
    }

    public OperazioneSospendi(Pubblicazione pubblicazione, Anno anno, Mese mese) {
        super();
        this.pubblicazione = pubblicazione;
        this.anno = anno;
        this.meseSpedizione = mese;
    }

    public Long getId() {
        return id;
    }

    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }

    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Mese getMeseSpedizione() {
        return meseSpedizione;
    }

    public void setMeseSpedizione(Mese mese) {
        this.meseSpedizione = mese;
    }

	@Override
	public String toString() {
		return "OperazioneSospendiPubblicazione [id=" + id + ", pubblicazione=" + pubblicazione.getNome() + ", anno=" + anno
				+ ", meseSpedizione=" + meseSpedizione + ", operatore=" + operatore + ", data=" + data + "]";
	}

	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	public Date getData() {
		return data;
	}

	@Override
	public String getHeader() {
		return pubblicazione.getNome() + " " + anno
				+ " " + meseSpedizione + " " + operatore + " " + data;
	}

}
