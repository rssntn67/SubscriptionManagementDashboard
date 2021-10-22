package it.arsinfo.smd.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Rivista implements SmdEntity {

    public static Rivista genera(Storico storico, Abbonamento abbonamento) {
        final Rivista ec = new Rivista();
        ec.setStorico(storico);
        ec.setAbbonamento(abbonamento);
        ec.setPubblicazione(storico.getPubblicazione());
        ec.setNumero(storico.getNumero());
        ec.setTipoAbbonamentoRivista(storico.getTipoAbbonamentoRivista());
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setAnnoInizio(abbonamento.getAnno());
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(abbonamento.getAnno());
        ec.setInvioSpedizione(storico.getInvioSpedizione());
        ec.setDestinatario(storico.getDestinatario());
        return ec;
    }

    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(Mese meseInizio, Anno annoInizio, Mese meseFine, Anno annoFine, Pubblicazione p) throws UnsupportedOperationException {
        if (annoInizio.getAnno() > annoFine.getAnno()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        if (annoInizio == annoFine
                && meseInizio.getPosizione() > meseFine.getPosizione()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        Map<Anno,EnumSet<Mese>> map = new HashMap<>();
        Anno anno = annoInizio;
        Mese mese = meseInizio;
        while (anno.getAnno() < annoFine.getAnno()) {
            if (p.getMesiPubblicazione().contains(mese)) {
                if (!map.containsKey(anno)) {
                    map.put(anno, EnumSet.noneOf(Mese.class));
                }
                map.get(anno).add(mese);
            }
            mese = Mese.getMeseSuccessivo(mese);
            if (mese == Mese.GENNAIO) {
                anno=Anno.getAnnoSuccessivo(anno);
            }
        }

        while (mese.getPosizione() <= meseFine.getPosizione()) {
            if (p.getMesiPubblicazione().contains(mese)) {
                if (!map.containsKey(anno)) {
                    map.put(anno, EnumSet.noneOf(Mese.class));
                }
                map.get(anno).add(mese);
            }
            mese = Mese.getMeseSuccessivo(mese);
            if (mese == Mese.GENNAIO) {
                break;
            }
        }
        return map;
    }

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
    @Column(nullable=false)
    private TipoAbbonamentoRivista tipoAbbonamentoRivista = TipoAbbonamentoRivista.Ordinario;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatoRivista statoRivista = StatoRivista.Attiva;

    @Column(nullable=false)
    private Mese meseInizio=Mese.GENNAIO;
    @Column(nullable=false)
    private Anno annoInizio=Anno.getAnnoCorrente();
    @Column(nullable=false)
    private Mese meseFine = Mese.DICEMBRE;
    @Column(nullable=false)
    private Anno annoFine = Anno.getAnnoCorrente();
    @Column(nullable=false)
    private Integer numero = 1;
    @Column(nullable=false)
    private Integer numeroTotaleRiviste = 0;

    @Column(nullable=false)
    private BigDecimal importo = BigDecimal.ZERO;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Anagrafica destinatario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    public Rivista() {
    }

    public Rivista(Long id) {
        this.id=id;
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
        return String.format("Rivista[id=%d, Abb.%d, numero %d '%d %s' %s imp. %.2f %s, %s %s -> %s %s]",
                             id,abbonamento.getId(),numero,numeroTotaleRiviste,pubblicazione.getNome(), tipoAbbonamentoRivista, importo, statoRivista,
                             meseInizio,annoInizio,meseFine,annoFine);
    }

    @Transient
    public boolean isAbbonamentoAnnuale() {
        if (annoInizio != annoFine) {
            return false;
        }
        if (meseInizio != Mese.GENNAIO) {
            return false;
        }
        return meseFine == Mese.DICEMBRE;

    }

    @Transient
    public void calcolaImporto() {
        importo=BigDecimal.ZERO;
        switch (tipoAbbonamentoRivista) {
            case Ordinario:
                if (isAbbonamentoAnnuale() ) {
                    importo=pubblicazione.getAbbonamento().multiply(new BigDecimal(numero));
                    int numrivres=numeroTotaleRiviste-pubblicazione.getMesiPubblicazione().size()*numero;
                    BigDecimal residuo= pubblicazione.getCostoUnitario().multiply(new BigDecimal(numrivres));
                    importo=importo.add(residuo);
                } else {
                    importo = pubblicazione.getCostoUnitario().multiply(new BigDecimal(numeroTotaleRiviste));
                }

                break;

            case Web:
                if (!isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
                }
                importo = pubblicazione.getAbbonamentoWeb().multiply(new BigDecimal(numero));
                break;

            case Scontato:
                if (!isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
                }
                importo = pubblicazione.getAbbonamentoConSconto().multiply(new BigDecimal(numero));
                break;

            case Sostenitore:
                if (!isAbbonamentoAnnuale()) {
                    throw new UnsupportedOperationException("Valori mesi inizio e fine non ammissibili per " + TipoAbbonamentoRivista.Web);
                }
                importo = pubblicazione.getAbbonamentoSostenitore().multiply(new BigDecimal(numero));
                break;

            default:
                break;

        }
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

    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(Rivista ec) throws UnsupportedOperationException {
        
        if (ec.getPubblicazione() == null) {
            throw new UnsupportedOperationException("pubblicazione null");
        }        
        return getAnnoMeseMap(ec.getMeseInizio(), ec.getAnnoInizio(), ec.getMeseFine(), ec.getAnnoFine(), ec.getPubblicazione());
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
