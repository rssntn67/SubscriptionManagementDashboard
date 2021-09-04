package it.arsinfo.smd.entity;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"campagna_id","pubblicazione_id"})
        })
public class OperazioneSospendi implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;
        
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Campagna campagna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mese meseSpedizione = Mese.getMeseCorrente();

    @Column(nullable = false)
    private String operatore="admin";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private final Date data = new Date();


    public OperazioneSospendi() {
    }

    public OperazioneSospendi(Pubblicazione pubblicazione, Campagna campagna) {
        super();
        this.pubblicazione = pubblicazione;
        this.campagna = campagna;
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

    public Campagna getCampagna() {
        return campagna;
    }

    public void setCampagna(Campagna c) {
        this.campagna = c;
    }

    public Mese getMeseSpedizione() {
        return meseSpedizione;
    }

    public void setMeseSpedizione(Mese mese) {
        this.meseSpedizione = mese;
    }

	@Override
	public String toString() {
		return "OperazioneSospendiPubblicazione [id=" + id + ", pubblicazione=" + pubblicazione.getNome() + ", anno=" + campagna.getAnno()
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
		return pubblicazione.getNome() + " " + campagna.getAnno()
				+ " " + meseSpedizione;
	}

}
