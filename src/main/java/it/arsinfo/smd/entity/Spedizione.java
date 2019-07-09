package it.arsinfo.smd.entity;

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
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.StatoSpedizione;

@Entity
public class Spedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private EstrattoConto estrattoConto;
    
    @Enumerated(EnumType.STRING)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    @Enumerated(EnumType.STRING)
    private StatoSpedizione statoSpedizione = StatoSpedizione.PROGRAMMATA;

    @Enumerated(EnumType.STRING)
    private Mese meseSpedizione;
    @Enumerated(EnumType.STRING)
    private Anno annoSpedizione;
    
    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione;
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione;

    private Integer numero;
    
    public Spedizione() {
    }

    public Long getId() {
        return id;
    }


    @Transient
    public String getHeader() {
        return String.format("Spedizione:Edit");
    }

    @Override
    public String toString() {
        return String.format("Spedizione[id=%d, %s %s, %s %s %s, num. %d, %s, %s ]", 
                             id,
                             meseSpedizione,
                             annoSpedizione,
                             estrattoConto.getPubblicazione().getNome(),
                             mesePubblicazione, 
                             annoPubblicazione, 
                             numero, 
                             estrattoConto.getIntestazione(), 
                             statoSpedizione);
    }

    public EstrattoConto getEstrattoConto() {
        return estrattoConto;
    }

    public void setEstrattoConto(EstrattoConto estrattoConto) {
        this.estrattoConto = estrattoConto;
    }

    public Mese getMeseSpedizione() {
        return meseSpedizione;
    }

    public void setMeseSpedizione(Mese meseSpedizione) {
        this.meseSpedizione = meseSpedizione;
    }

    public Anno getAnnoSpedizione() {
        return annoSpedizione;
    }

    public void setAnnoSpedizione(Anno annoSpedizione) {
        this.annoSpedizione = annoSpedizione;
    }

    public Mese getMesePubblicazione() {
        return mesePubblicazione;
    }

    public void setMesePubblicazione(Mese mesePubblicazione) {
        this.mesePubblicazione = mesePubblicazione;
    }

    public Anno getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Anno annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }

    public StatoSpedizione getStatoSpedizione() {
        return statoSpedizione;
    }

    public void setStatoSpedizione(StatoSpedizione statoSpedizione) {
        this.statoSpedizione = statoSpedizione;
    }

    @Transient
    public String getPubblicazione() {
        return estrattoConto.getPubblicazione().getNome();
    }
    
    public Integer getNumero() {
        return numero;
    }
    
    @Transient
    public String getIntestazione() {
        return estrattoConto.getIntestazione();
    }
    
    @Transient
    public String getSottoIntestazione() {
        return estrattoConto.getSottoIntestazione();
    }
    
    @Transient
    public String getIndirizzo() {
        return estrattoConto.getIndirizzo();
    }

    @Transient
    public String getCap() {
        return estrattoConto.getCap();
    }

    @Transient
    public String getCitta() {
        return estrattoConto.getCitta();
    }
    
    @Transient
    public Provincia getProvincia() {
        return estrattoConto.getProvincia();
    }
    
    @Transient
    public Paese getPaese() {
        return estrattoConto.getPaese();
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }


}
