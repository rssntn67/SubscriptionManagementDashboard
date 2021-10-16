package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Storico implements SmdEntityItems<Nota> {

    public static List<Abbonamento> genera(final Campagna campagna, List<Anagrafica> anagrafiche, List<Storico> storici) {
        final List<Abbonamento> abbonamenti = new ArrayList<>();
        anagrafiche.forEach(a -> abbonamenti.addAll(genera(campagna, a, storici)));
        return abbonamenti;

    }

    public static List<Abbonamento> genera(Campagna campagna,Anagrafica a, List<Storico> storici) {
        final List<Abbonamento> abbonamenti = new ArrayList<>();
        for (Storico storico: storici) {
            if (
                    campagna.hasPubblicazione(storico.getPubblicazione())
                            && storico.getIntestatario().equals(a)
                            && storico.isContrassegno()
            ) {
                try {
                    abbonamenti.add(Abbonamento.genera(campagna, a,true));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Storico storico: storici) {
            if (
                    campagna.hasPubblicazione(storico.getPubblicazione())
                            && storico.getIntestatario().equals(a)
                            && !storico.isContrassegno()
            ) {
                try {
                    abbonamenti.add(Abbonamento.genera(campagna, a,false));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return abbonamenti;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Anagrafica intestatario;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;

    private Integer numero = 1;

    @Column(nullable=false)
    private boolean contrassegno = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TipoAbbonamentoRivista tipoAbbonamentoRivista = TipoAbbonamentoRivista.Ordinario;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Anagrafica destinatario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatoStorico statoStorico = StatoStorico.Nuovo;

    @Transient
    private List<Nota> items = new ArrayList<>();

    public Storico() {
        super();
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica intestatario) {
        this.intestatario = intestatario;
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }
    
    @Transient
    public String getBeneficiario() {
        return Anagrafica.generaIntestazione(destinatario);
    }

    @Transient
    public String getIntestazione() {
        return Anagrafica.generaIntestazione(intestatario);
    }

    @Transient
    public String getCaption() {
    	if (intestatario.equals(destinatario)) {
            return String.format("'%s', %d %s", getIntestazione(),numero,  pubblicazione.getNome());
    	}
        return String.format("'%s', %d %s ->'%s'", getIntestazione(),numero,  pubblicazione.getNome(),getBeneficiario());
    }

    @Transient
    public String getCaptionPubblicazione() {
        if (pubblicazione != null)
            return pubblicazione.getNome();
        return "";
    }

    @Transient
    public String getHeader() {
        return String.format("'%s' %d %s %s",
                intestatario.getHeader(),
                numero, 
                pubblicazione.getHeader(),
                tipoAbbonamentoRivista);
    }
    
    @Override
    public String toString() {
        return String.format("Storico[id=%d, %d %s '%s' -> '%s', %s %s contrassegno %b]",
                             id, 
                             numero, 
                             pubblicazione.getNome(), 
                             intestatario.getIntestazione(), 
                             destinatario.getIntestazione(), 
                             tipoAbbonamentoRivista,
                             statoStorico, contrassegno);
    }

    public TipoAbbonamentoRivista getTipoAbbonamentoRivista() {
        return tipoAbbonamentoRivista;
    }

    public void setTipoAbbonamentoRivista(TipoAbbonamentoRivista omaggio) {
        this.tipoAbbonamentoRivista = omaggio;
    }

    public StatoStorico getStatoStorico() {
        return statoStorico;
    }

    public void setStatoStorico(StatoStorico statoStorico) {
        this.statoStorico = statoStorico;
    }
    
    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }

	@Override
	public boolean addItem(Nota item) {
		return items.add(item);
	}

	@Override
	public boolean removeItem(Nota item) {
		return items.remove(item);
	}

	@Override
	public List<Nota> getItems() {
		return items;
	}

	@Override
	public void setItems(List<Nota> items) {
		this.items=items;
		
	}

	public boolean isContrassegno() {
		return contrassegno;
	}

	public void setContrassegno(boolean contrassegno) {
		this.contrassegno = contrassegno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (contrassegno ? 1231 : 1237);
		result = prime * result + ((destinatario == null) ? 0 : destinatario.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((intestatario == null) ? 0 : intestatario.hashCode());
		result = prime * result + ((invioSpedizione == null) ? 0 : invioSpedizione.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((pubblicazione == null) ? 0 : pubblicazione.hashCode());
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
		Storico other = (Storico) obj;
		if (contrassegno != other.contrassegno)
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
		if (intestatario == null) {
			if (other.intestatario != null)
				return false;
		} else if (!intestatario.equals(other.intestatario))
			return false;
		if (invioSpedizione != other.invioSpedizione)
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (pubblicazione == null)
		    return false;
        if (other.pubblicazione == null)
				return false;
		return pubblicazione.equals(other.pubblicazione);
	}
    
}
