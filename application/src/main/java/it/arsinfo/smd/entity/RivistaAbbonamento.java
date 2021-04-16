package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoRivista;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.service.Smd;

@Entity
public class RivistaAbbonamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    private Abbonamento abbonamento;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;

    @ManyToOne
    private Storico storico;

    @Enumerated(EnumType.STRING)
    private TipoAbbonamentoRivista tipoAbbonamentoRivista = TipoAbbonamentoRivista.Ordinario;

    @Enumerated(EnumType.STRING)
    private StatoRivista statoRivista = StatoRivista.Attiva;

    private Mese meseInizio=Mese.GENNAIO;
    private Anno annoInizio=Anno.getAnnoCorrente();
    private Mese meseFine = Mese.DICEMBRE;
    private Anno annoFine = Anno.getAnnoCorrente();
    private Integer numero = 1;
    private Integer numeroTotaleRiviste = 0;
    
    private BigDecimal importo = BigDecimal.ZERO;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Anagrafica destinatario;
    
    @Enumerated(EnumType.STRING)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    public RivistaAbbonamento() {
    }
    
    public Long getId() {
        return id;
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

    public Storico getStorico() {
        return storico;
    }

    public void setStorico(Storico storico) {
        this.storico = storico;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoAbbonamentoRivista getTipoAbbonamentoRivista() {
        return tipoAbbonamentoRivista;
    }

    public void setTipoAbbonamentoRivista(TipoAbbonamentoRivista omaggio) {
        this.tipoAbbonamentoRivista = omaggio;
    }

    @Transient
    public String getHeader() {
        return String.format("' %d %s' %s %s]", 
                numero,pubblicazione.getNome(), tipoAbbonamentoRivista, statoRivista);
    }

    @Override
    public String toString() {
        return String.format("RivistaAbbonamento[id=%d, Abb.%d, '%d %s' %s imp. %.2f %s, %s %s -> %s %s]", 
                             id,abbonamento.getId(),numero,pubblicazione.getNome(), tipoAbbonamentoRivista, importo, statoRivista, 
                             meseInizio,annoInizio,meseFine,annoFine);
    }
        
    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    
    public Mese getMeseInizio() {
        return meseInizio;
    }

    public void setMeseInizio(Mese meseInizio) {
        this.meseInizio = meseInizio;
    }

    public Anno getAnnoInizio() {
        return annoInizio;
    }

    public void setAnnoInizio(Anno annoInizio) {
        this.annoInizio = annoInizio;
    }

    public Mese getMeseFine() {
        return meseFine;
    }

    public void setMeseFine(Mese meseFine) {
        this.meseFine = meseFine;
    }

    public Anno getAnnoFine() {
        return annoFine;
    }

    public void setAnnoFine(Anno annoFine) {
        this.annoFine = annoFine;
    }

    public Integer getNumeroTotaleRiviste() {
        return numeroTotaleRiviste;
    }

    public void setNumeroTotaleRiviste(Integer numeroTotaleRiviste) {
        this.numeroTotaleRiviste = numeroTotaleRiviste;
    }

    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(RivistaAbbonamento ec) throws UnsupportedOperationException {
        
        if (ec.getPubblicazione() == null) {
            throw new UnsupportedOperationException("pubblicazione null");
        }        
        return Smd.getAnnoMeseMap(ec.getMeseInizio(), ec.getAnnoInizio(), ec.getMeseFine(), ec.getAnnoFine(), ec.getPubblicazione());
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }
    
    @Transient
    public String getBeneficiario() {
        return Anagrafica.generaIntestazione(destinatario);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abbonamento == null) ? 0 : 
			(abbonamento.getId() != null) ? abbonamento.getId().hashCode() :abbonamento.hashCode());
		result = prime * result + ((annoFine == null) ? 0 : annoFine.hashCode());
		result = prime * result + ((annoInizio == null) ? 0 : annoInizio.hashCode());
		result = prime * result + ((destinatario == null) ? 0 : destinatario.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((invioSpedizione == null) ? 0 : invioSpedizione.hashCode());
		result = prime * result + ((meseFine == null) ? 0 : meseFine.hashCode());
		result = prime * result + ((meseInizio == null) ? 0 : meseInizio.hashCode());
		result = prime * result + ((pubblicazione == null) ? 0 : pubblicazione.hashCode());
		result = prime * result + ((storico == null) ? 0 : storico.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RivistaAbbonamento other = (RivistaAbbonamento) obj;
		if (abbonamento == null) {
			if (other.abbonamento != null)
				return false;
		}
		if (other.abbonamento == null) {
			if ( abbonamento != null)
				return false;
		}
		if (abbonamento.getId() != null && other.abbonamento.getId() == null) {
			return false;
		}
		if (abbonamento.getId() == null && other.abbonamento.getId() != null) {
			return false;
		}
		if (abbonamento.getId() != other.abbonamento.getId()) {
			return false;
		}
		if (abbonamento.getId() == null && !abbonamento.equals(other.abbonamento))
			return false;
		if (annoFine != other.annoFine)
			return false;
		if (annoInizio != other.annoInizio)
			return false;
		if (destinatario == null) {
			if (other.destinatario != null)
				return false;
		} else if (!destinatario.equals(other.destinatario))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (invioSpedizione != other.invioSpedizione)
			return false;
		if (meseFine != other.meseFine)
			return false;
		if (meseInizio != other.meseInizio)
			return false;
		if (pubblicazione == null) {
			if (other.pubblicazione != null)
				return false;
		} else if (!pubblicazione.equals(other.pubblicazione))
			return false;
		if (storico == null) {
			if (other.storico != null)
				return false;
		} else if (!storico.equals(other.storico))
			return false;
		return true;
	}
	
	public RivistaAbbonamento clone() {
		RivistaAbbonamento r = new RivistaAbbonamento();
		r.setAbbonamento(abbonamento);
		r.setAnnoFine(annoFine);
		r.setAnnoInizio(annoInizio);
		r.setDestinatario(destinatario);
		r.setInvioSpedizione(invioSpedizione);
		r.setMeseFine(meseFine);
		r.setMeseInizio(meseInizio);
		r.setPubblicazione(pubblicazione);
		r.setStorico(storico);
		return r;
	}

	public StatoRivista getStatoRivista() {
		return statoRivista;
	}

	@Transient
	public String getInizio() {
		return meseInizio.getNomeBreve()+annoInizio.getAnnoAsString();
	}
	@Transient
	public String getFine() {
		return meseFine.getNomeBreve()+annoFine.getAnnoAsString();
		
	}
	public void setStatoRivista(StatoRivista statoRivista) {
		this.statoRivista = statoRivista;
	}

}
