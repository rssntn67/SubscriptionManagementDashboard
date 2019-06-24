package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Stato;
import it.arsinfo.smd.data.TipoEstrattoConto;

@Entity
public class EstrattoConto implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Abbonamento abbonamento;

    @ManyToOne
    private Pubblicazione pubblicazione;

    @ManyToOne
    private Anagrafica destinatario;

    @ManyToOne
    private Storico storico;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

    @Enumerated(EnumType.STRING)
    private TipoEstrattoConto tipoEstrattoConto = TipoEstrattoConto.Ordinario;

    @OneToMany(cascade = { CascadeType.ALL }, fetch=FetchType.EAGER)
    private List<Spedizione> spedizioni = new ArrayList<>();

    private Integer numero = 1;
    
    private BigDecimal importo = BigDecimal.ZERO;
    private BigDecimal spesePostali = BigDecimal.ZERO;

    private Stato statoEstrattoConto=Stato.NUOVO;
    public EstrattoConto() {
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

    public Invio getInvio() {
        return invio;
    }

    public void setInvio(Invio invio) {
        this.invio = invio;
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
        return String.format("%s:EstrattoConto:Edit:'%s'", abbonamento.getHeader(),pubblicazione.getNome());
    }

    @Override
    public String toString() {
        return String.format("s:EstrattoConto[id=%d, Abbonamento=%d, Pubblicazione=%d, Numero=%d]", 
                             id,abbonamento.getId(),pubblicazione.getId(),numero, tipoEstrattoConto);
    }
        
    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public BigDecimal getSpesePostali() {
        return spesePostali;
    }

    public void setSpesePostali(BigDecimal spesePostali) {
        this.spesePostali = spesePostali;
    }
    
    @Transient
    public BigDecimal getTotale() {
        return importo.add(spesePostali);
    }

    public List<Spedizione> getSpedizioni() {
        return spedizioni;
    }

    public void setSpedizioni(List<Spedizione> spedizioni) {
        this.spedizioni = spedizioni;
    }
    
    public void addSpedizione(Spedizione spedizione) {
        if (spedizioni.contains(spedizione)) {
            spedizioni.remove(spedizione);
        }
        spedizioni.add(spedizione);
    }

    public boolean deleteSpedizione(Spedizione spedizione) {
        return spedizioni.remove(spedizione);
    }

    public boolean hasAllMesiPubblicazione() {
        EnumSet<Mese> mesiPubblicazione = EnumSet.noneOf(Mese.class);
        for (Spedizione spedizione:spedizioni) {
            mesiPubblicazione.add(spedizione.getMesePubblicazione());
        }
        if (pubblicazione.getMesiPubblicazione() == mesiPubblicazione) {
            return true;
        }
        return false;
    }
    
    public int getNumeroSpedizioniConSpesePostali() {
        int i = 0;
        for (Spedizione spedizione :spedizioni) {
            if (Smd.spedizionePosticipata(spedizione, pubblicazione.getAnticipoSpedizione())) {
                i++;
            }
        }            
        return i;
    }

    @Transient
    public String getIntestazione() {
        return destinatario.getCaption();
    }

    @Transient
    public String getSottoIntestazione() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return "";
            } 
            return "c/o" + destinatario.getCo().getCaption();
        }
        return "c/o " + 
        getAbbonamento().getIntestatario().getCaption();
    }
    
    @Transient
    public String getIndirizzo() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return destinatario.getIndirizzo();
            }
            return destinatario.getCo().getIndirizzo();            
        }
        return getAbbonamento().getIndirizzo();
    }

    @Transient
    public String getCap() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return destinatario.getCap();
            }
            return destinatario.getCo().getCap();        
        }
        return getAbbonamento().getCap();
    }

    @Transient
    public String getCitta() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return destinatario.getCitta();
            }
            return destinatario.getCo().getCitta();        
        }
        return getAbbonamento().getCitta();
    }

    @Transient
    public Provincia getProvincia() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return destinatario.getProvincia();
            }
            return destinatario.getCo().getProvincia();        
        }
        return getAbbonamento().getProvincia();

    }
    @Transient
    public Paese getPaese() {
        if (invio == Invio.Destinatario) {
            if (destinatario.getCo() == null) {
                return destinatario.getPaese();
            }
            return destinatario.getCo().getPaese();        
        }
        return getAbbonamento().getPaese();        
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }

    public Stato getStatoEstrattoConto() {
        return statoEstrattoConto;
    }

    public void setStatoEstrattoConto(Stato statoEstrattoConto) {
        this.statoEstrattoConto = statoEstrattoConto;
    }

}