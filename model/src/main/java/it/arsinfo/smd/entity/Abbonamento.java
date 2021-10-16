package it.arsinfo.smd.entity;

import it.arsinfo.smd.dto.SpedizioneWithItems;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"codeLine"}),
        @UniqueConstraint(columnNames = {"intestatario_id","campagna_id","contrassegno"})
        })
//create unique index abb_idx_codeline on abbonamento (codeline);
//create unique index abb_idx_select on abbonamento (intestatario_id, campagna_id, contrassegno);
public class Abbonamento implements SmdEntityItems<RivistaAbbonamento> {

    public static void aggiungiItemSpedizione(Abbonamento abb, RivistaAbbonamento ec,Map<Integer,SpedizioneWithItems> spedMap, SpedizioneItem item, Mese mesePost, Anno annoPost) {
        Anagrafica destinatario = ec.getDestinatario();
        InvioSpedizione isped = ec.getInvioSpedizione();
        Mese mesePubblicazione = item.getMesePubblicazione();
        Anno annoPubblicazione = item.getAnnoPubblicazione();
        boolean posticipata=false;
        int anticipoSpedizione = ec.getPubblicazione().getAnticipoSpedizione();
        Mese spedMese;
        Anno spedAnno;

        if (mesePubblicazione.getPosizione() - anticipoSpedizione <= 0) {
            spedMese = Mese.getByPosizione(12
                    + mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = Anno.getAnnoPrecedente(annoPubblicazione);
        } else {
            spedMese = Mese.getByPosizione(mesePubblicazione.getPosizione()
                    - anticipoSpedizione);
            spedAnno = annoPubblicazione;
        }
        assert spedMese != null;
        log.info("aggiungiItemSpedizione: teorico: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);

        if (spedAnno.getAnno() < annoPost.getAnno()
                || (spedAnno == annoPost
                && spedMese.getPosizione() < mesePost.getPosizione())) {
            spedMese = Mese.getMeseCorrente();
            spedAnno = Anno.getAnnoCorrente();
            isped = InvioSpedizione.AdpSede;
            posticipata=true;
            log.info("aggiungiItemSpedizione: posticipata: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);

        }
        if (destinatario.getAreaSpedizione() != AreaSpedizione.Italia) {
            isped = InvioSpedizione.AdpSede;
            log.info("aggiungiItemSpedizione: estero: {}, {}, {}",spedMese.getNomeBreve(),spedAnno.getAnnoAsString(),isped);
        }
        Spedizione spedizione = new Spedizione();
        spedizione.setMeseSpedizione(spedMese);
        spedizione.setAnnoSpedizione(spedAnno);
        spedizione.setInvioSpedizione(isped);
        spedizione.setAbbonamento(abb);
        spedizione.setDestinatario(destinatario);
        int hash = SpedizioneWithItems.getHashCode(spedizione, item.getPubblicazione());
        if (!spedMap.containsKey(hash)) {
            spedMap.put(hash, new SpedizioneWithItems(spedizione));
        }
        SpedizioneWithItems sped = spedMap.get(hash);
        item.setPosticipata(posticipata);
        item.setSpedizione(sped.getSpedizione());
        sped.addSpedizioneItem(item);
        log.info("aggiungiItemSpedizione: aggiunto {}, a spedizione {}, size {}",item,spedizione,spedMap.size());
    }

    public static List<SpedizioneWithItems> genera(Abbonamento abb,
                                                   RivistaAbbonamento ec,
                                                   List<SpedizioneWithItems> spedizioni,
                                                   List<SpesaSpedizione> spese) throws UnsupportedOperationException {
        return genera(abb,
                ec,
                spedizioni,
                spese, Mese.getMeseCorrente(), Anno.getAnnoCorrente());
    }

    public static List<SpedizioneWithItems> genera(Abbonamento abb,
                                                   RivistaAbbonamento ec,
                                                   List<SpedizioneWithItems> spedizioni,
                                                   List<SpesaSpedizione> spese, Mese mesePost, Anno annoPost) throws UnsupportedOperationException {


        ec.setAbbonamento(abb);
        List<SpedizioneItem> items = SpedizioneItem.generaSpedizioneItems(ec);
        ec.setNumeroTotaleRiviste(ec.getNumero()*items.size());
        ec.calcolaImporto();
        abb.setImporto(abb.getImporto().add(ec.getImporto()));
        Map<Integer, SpedizioneWithItems> spedMap = SpedizioneWithItems.getSpedizioneMap(spedizioni);

        if (ec.getTipoAbbonamentoRivista() != TipoAbbonamentoRivista.Web) {
            for (SpedizioneItem item : items) {
                aggiungiItemSpedizione(abb, ec, spedMap, item,mesePost,annoPost);
            }
        }
        Abbonamento.calcolaPesoESpesePostali(abb, spedMap.values(), spese);
        return new ArrayList<>(spedMap.values());
    }

    public static void calcolaPesoESpesePostali(Abbonamento abb, Collection<SpedizioneWithItems> spedizioni, List<SpesaSpedizione> spese) {
        abb.setSpese(BigDecimal.ZERO);
        abb.setSpeseEstero(BigDecimal.ZERO);
        for (SpedizioneWithItems sped: spedizioni) {
            int pesoStimato=0;
            for (SpedizioneItem item: sped.getSpedizioneItems()) {
                pesoStimato+=item.getNumero()*item.getPubblicazione().getGrammi();
            }
            sped.getSpedizione().setPesoStimato(pesoStimato);

            sped.getSpedizione().setSpesePostali(SpesaSpedizione.getSpesaSpedizione(
                    spese,
                    sped.getSpedizione().getDestinatario().getAreaSpedizione(),
                    RangeSpeseSpedizione.getByPeso(pesoStimato)
            ).calcolaSpesePostali(sped.getSpedizione().getInvioSpedizione()));
            switch (sped.getSpedizione().getDestinatario().getAreaSpedizione()) {
                case Italia:
                    abb.setSpese(abb.getSpese().add(sped.getSpedizione().getSpesePostali()));
                    break;
                case EuropaBacinoMediterraneo:
                case AmericaAfricaAsia:
                    abb.setSpeseEstero(abb.getSpeseEstero().add(sped.getSpedizione().getSpesePostali()));
                    break;
                default:
                    break;
            }
        }
    }

    public static Abbonamento genera(Campagna campagna, Anagrafica a,boolean contrassegno) throws Exception {
        if (campagna == null) {
            throw new Exception("genera: Null Campagna");
        }
        if (a == null) {
            throw new Exception("genera: Null Intestatario");
        }
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setIntestatario(a);
        abbonamento.setCampagna(campagna);
        abbonamento.setAnno(campagna.getAnno());
        abbonamento.setContrassegno(contrassegno);
        if (contrassegno) {
            abbonamento.setSpeseContrassegno(campagna.getContrassegno());
        }
        abbonamento.setCodeLine(Abbonamento.generaCodeLine(abbonamento.getAnno(),a));
        return abbonamento;
    }

    public static boolean checkCodeLine(String codeline) {
        if (codeline == null || codeline.length() != 18) {
            return false;

        }

        String codice = codeline.substring(0, 16);

        long valorecodice = (Long.parseLong(codice) % 93);
        int codicecontrollo = Integer.parseInt(codeline.substring(16,18));
        return codicecontrollo == valorecodice;
    }

    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale codeLine 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generaCodeLine(Anno anno, Anagrafica anagrafica) {
        // primi 2 caratteri anno
        String codeline = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        codeline += anagrafica.getCodeLineBase();
        codeline += String.format("%02d", Long.parseLong(codeline) % 93);
        return codeline;
    }

    public static String generaCodeLine(Anno anno) {
        // primi 2 caratteri anno
        String codeLine = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        codeLine += String.format("%014d", ThreadLocalRandom.current().nextLong(99999999999999L));
        codeLine += String.format("%02d", Long.parseLong(codeLine) % 93);
        return codeLine;
    }

    public static Incassato getStatoIncasso(Abbonamento abbonamento, BigDecimal sogliaImportoTotale, BigDecimal minPercIncassato, BigDecimal maxDebito ) {
        if (abbonamento.getResiduo().signum() == 0) {
            log.info("getStatoIncasso: {} {}", Incassato.Si,abbonamento);
            return Incassato.Si;
        }
        if (abbonamento.getIncassato().signum() == 0) {
            log.info("getStatoIncasso: {} {}", Incassato.No,abbonamento);
            return Incassato.No;
        }
        if (abbonamento.getTotale().compareTo(sogliaImportoTotale) >= 0 && abbonamento.getTotale().multiply(minPercIncassato).compareTo(abbonamento.getIncassato()) < 0 ){
            log.info("getStatoIncasso: {} {} maggiore {} debito inferiore al {}", abbonamento, Incassato.SiConDebito,sogliaImportoTotale, minPercIncassato);
            return Incassato.SiConDebito;
        }
        if (abbonamento.getTotale().compareTo(sogliaImportoTotale) < 0 && abbonamento.getTotale().subtract(maxDebito).compareTo(abbonamento.getIncassato()) < 0) {
            log.info("getStatoIncasso: {} {} minore {} ma debito inferiore al {}", abbonamento, Incassato.SiConDebito, sogliaImportoTotale, maxDebito);
            return Incassato.SiConDebito;
        }
        log.info("getStatoIncasso: {} {}", Incassato.Parzialmente,abbonamento);
        return Incassato.Parzialmente;
    }

    public static boolean isAbbonamentoValid(Abbonamento abbonamento, Campagna campagna) {
        if (campagna == null)
            return true;
        boolean valid=false;
        switch (abbonamento.getStatoIncasso(campagna)) {
            case Si:
            case SiConDebito:
                valid=true;
                break;
            default:
                break;
        }
        return valid;
    }

    public static BigDecimal getPregresso(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregresso());
        }
        return importo;
    }

    public static BigDecimal getImporto(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImporto());
        }
        return importo;
    }

    public static BigDecimal getSpese(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpese());
        }
        return importo;
    }

    public static BigDecimal getSpeseEstero(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstero());
        }
        return importo;
    }

    public static BigDecimal getSpeseEstrattoConto(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoConto());
        }
        return importo;
    }

    public static BigDecimal getTotale(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotale());
        }
        return importo;
    }

    public static BigDecimal getIncassato(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abb:abbonamenti) {
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassato());
        }
        return importo;
    }

    public static BigDecimal getResiduo(List<Abbonamento> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Abbonamento abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getResiduo());
        }
        return importo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Anagrafica intestatario;

    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoCorrente();

    @ManyToOne
    private Campagna campagna;
    
    private BigDecimal pregresso=BigDecimal.ZERO;
    private BigDecimal importo=BigDecimal.ZERO;
    private BigDecimal spese=BigDecimal.ZERO;
    private BigDecimal speseEstero=BigDecimal.ZERO;
    private BigDecimal speseEstrattoConto=BigDecimal.ZERO;
    private BigDecimal incassato=BigDecimal.ZERO;

    private String codeLine;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

    @Column(nullable=false)
    private boolean contrassegno=false;

    @Column(nullable=false)
    private BigDecimal speseContrassegno=BigDecimal.ZERO;

    @Column(nullable=false)
    private boolean sollecitato = false;

    @Column(nullable=false)
    private boolean inviatoEC = false;

    @Transient
    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;

    @Transient
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;
    @Transient
    private Cuas cuas = Cuas.NOCCP;

    @Transient
    private String progressivo;

    @Transient
    private List<RivistaAbbonamento> items = new ArrayList<>();
    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;
    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile;

    public Abbonamento() {
    }


    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica anagrafica) {
        this.intestatario = anagrafica;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Campagna getCampagna() {
        return campagna;
    }

    public void setCampagna(Campagna campagna) {
        this.campagna = campagna;
    }

    public String getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(String codeLine) {
        this.codeLine = codeLine;
    }

    public Cassa getCassa() {
        return cassa;
    }

    public void setCassa(Cassa cassa) {
        this.cassa = cassa;
    }

    public Ccp getCcp() {
        return ccp;
    }

    public void setCcp(
            Ccp ccp) {
        this.ccp = ccp;
    }


    public BigDecimal getIncassato() {
        return incassato;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public BigDecimal getSpese() {
        return spese;
    }

    public void setSpese(BigDecimal spese) {
        this.spese = spese;
    }

    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }

    public BigDecimal getPregresso() {
        return pregresso;
    }

    public void setPregresso(BigDecimal pregresso) {
        this.pregresso = pregresso;
    }

    public BigDecimal getSpeseEstero() {
        return speseEstero;
    }

    public void setSpeseEstero(BigDecimal speseEstero) {
        this.speseEstero = speseEstero;
    }

    public BigDecimal getSpeseEstrattoConto() {
        return speseEstrattoConto;
    }

    public void setSpeseEstrattoConto(BigDecimal speseEstrattoConto) {
        this.speseEstrattoConto = speseEstrattoConto;
    }

    public List<RivistaAbbonamento> getItems() {
        return items;
    }

    public void setItems(List<RivistaAbbonamento> estrattiConto) {
        this.items = estrattiConto;
    }

    public boolean addItem(RivistaAbbonamento ec) {
        return items.add(ec);
    }

    public boolean removeItem(RivistaAbbonamento ec) {
        return items.remove(ec);
    }

    public boolean isSollecitato() {
        return sollecitato;
    }

    public void setSollecitato(boolean sollecitato) {
        this.sollecitato = sollecitato;
    }

    public boolean isInviatoEC() {
        return inviatoEC;
    }

    public void setInviatoEC(boolean inviatoEC) {
        this.inviatoEC = inviatoEC;
    }

    public boolean isContrassegno() {
        return contrassegno;
    }

    public void setContrassegno(boolean contrassegno) {
        this.contrassegno = contrassegno;
    }

    public BigDecimal getSpeseContrassegno() {
        return speseContrassegno;
    }

    public void setSpeseContrassegno(BigDecimal speseContrassegno) {
        this.speseContrassegno = speseContrassegno;
    }

    @Transient
    public Date getDataPagamento() {
        return dataPagamento;
    }

    @Transient
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = SmdEntity.getStandardDate(dataPagamento);
    }

    @Transient
    public Date getDataContabile() {
        return dataContabile;
    }

    @Transient
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = SmdEntity.getStandardDate(dataContabile);
    }

    @Transient
    public Cuas getCuas() {
        return cuas;
    }

    @Transient
    public void setCuas(Cuas cuas) {
        this.cuas = cuas;
    }

    @Transient
    public String getProgressivo() {
        return progressivo;
    }

    @Transient
    public void setProgressivo(String progressivo) {
        this.progressivo= progressivo;
    }

    @Transient
    public boolean isAbbonamentoValid(Campagna campagna) {
        return isAbbonamentoValid(this,campagna);
    }
    @Transient
    public Incassato getStatoIncasso(Campagna campagna) {
        if (getResiduo().signum() == 0) {
            return Incassato.Si;
        } else if (campagna == null) {
            return Incassato.No;
        }
        return getStatoIncasso(this, campagna.getSogliaImportoTotale(),campagna.getMinPercIncassato(),campagna.getMaxDebito());
    }

    @Transient
    public String getHeader() {
        return String.format("%s:%s", anno.getAnnoAsString(),getIntestazione());
    }

    @Transient
    public BigDecimal getResiduo() {
        return getTotale().subtract(incassato);
    }

    @Transient
    public BigDecimal getTotale() {
        return importo.add(pregresso).add(spese).add(speseEstero).add(speseEstrattoConto).add(speseContrassegno);
    }

    @Transient
    public String getIntestazione() {
        return Anagrafica.generaIntestazione(intestatario);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (id != null) {
			return prime * result + id.hashCode();
		}
		result = prime * result + ((codeLine == null) ? 0 : codeLine.hashCode());
		return result;
	}

    @Override
    public String toString() {
        return String.format("Abbonamento[id=%d, Imp:'%.2f', Spese:'%.2f', Estero:'%.2f', 'Preg:'%.2f','Inc.to:'%.2f',CL:'%s', Anno=%s",
                id,
                importo,
                spese,
                speseEstero,
                pregresso,
                incassato,
                codeLine,
                anno.getAnnoAsString());
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Abbonamento other = (Abbonamento) obj;
		if (id != null) {
			return id.equals(other.getId());
		}
		if (codeLine == null) return other.codeLine == null;
        else return codeLine.equals(other.codeLine);
    }

}