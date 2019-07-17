package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
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

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoEstrattoConto;

@Entity
public class EstrattoConto implements SmdEntity {

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
    private TipoEstrattoConto tipoEstrattoConto = TipoEstrattoConto.Ordinario;

    private Mese meseInizio=Mese.GENNAIO;
    private Anno annoInizio=Anno.getAnnoProssimo();
    private Mese meseFine = Mese.DICEMBRE;
    private Anno annoFine = Anno.getAnnoProssimo();
    private Integer numero = 1;
    private Integer numeroTotaleRiviste = 0;
    
    private BigDecimal importo = BigDecimal.ZERO;

    public EstrattoConto() {
    }

    public boolean isAbbonamentoAnnuale() {
        if (annoInizio != annoFine) {
            return false;
        }
        if (meseInizio != Mese.GENNAIO) {
            return false;
        }
        if (meseFine != Mese.DICEMBRE) {
            return false;
        }
        if (numeroTotaleRiviste != numero*pubblicazione.getMesiPubblicazione().size()) {
            return false;
        }
        return true;
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

    public TipoEstrattoConto getTipoEstrattoConto() {
        return tipoEstrattoConto;
    }

    public void setTipoEstrattoConto(TipoEstrattoConto omaggio) {
        this.tipoEstrattoConto = omaggio;
    }

    @Transient
    public String getHeader() {
        return String.format("EstrattoConto:Edit");
    }

    @Override
    public String toString() {
        return String.format("EstrattoConto[id=%d, Abb.%d, '%d %s' %s]", 
                             id,abbonamento.getId(),numero,pubblicazione.getNome(), tipoEstrattoConto);
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

    public static Map<Anno, EnumSet<Mese>> getAnnoMeseMap(EstrattoConto ec) throws UnsupportedOperationException {
        if (ec.getAnnoInizio().getAnno() > ec.getAnnoFine().getAnno()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        if (ec.getAnnoInizio() == ec.getAnnoFine() && ec.getMeseInizio().getPosizione() > ec.getMeseFine().getPosizione()) {
            throw new UnsupportedOperationException("data inizio maggiore di data fine");
        }
        Map<Anno,EnumSet<Mese>> map = new HashMap<>();
        Anno anno = ec.getAnnoInizio();
        while (anno.getAnno() <= ec.getAnnoFine().getAnno()) {
            EnumSet<Mese> mesiin = EnumSet.noneOf(Mese.class);
            for (Mese mese: ec.getPubblicazione().getMesiPubblicazione()) {
                if (anno == ec.getAnnoInizio() && anno == ec.getAnnoFine()) {
                    if (mese.getPosizione() >= ec.getMeseInizio().getPosizione() && mese.getPosizione() <= ec.getMeseFine().getPosizione()) {
                        mesiin.add(mese);
                    }
                } else if (anno == ec.getAnnoInizio() ) {
                    if (mese.getPosizione() >= ec.getMeseFine().getPosizione()) {
                        mesiin.add(mese);
                    }
                } else if (anno == ec.getAnnoFine()) {
                    if (mese.getPosizione() <= ec.getMeseFine().getPosizione()) {
                        mesiin.add(mese);
                    }
                } else {
                    mesiin.add(mese);
                }
            }
            map.put(anno, mesiin);
            anno=Anno.getAnnoSuccessivo(anno);
        }
        
        return map;
    }
}
