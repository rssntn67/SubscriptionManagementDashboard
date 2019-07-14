package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class SpedizioneItem implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Spedizione spedizione;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private EstrattoConto estrattoConto;
    
    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione=Mese.getMeseCorrente();
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione=Anno.getAnnoCorrente();
    
    private boolean posticipata = false;
    
    Integer numero=1;

    public SpedizioneItem() {
    }


    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Spedizione getSpedizione() {
        return spedizione;
    }

    public void setSpedizione(Spedizione spedizione) {
        this.spedizione = spedizione;
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


    public EstrattoConto getEstrattoConto() {
        return estrattoConto;
    }


    public void setEstrattoConto(EstrattoConto estrattoConto) {
        this.estrattoConto = estrattoConto;
    }
        
    @Override
    public String toString() {
        return String.format("SpedizioneItem[id=%d, %s %s %s, num. %d, post %b ]", 
                             id,
                             estrattoConto.getPubblicazione().getNome(),
                             mesePubblicazione,
                             annoPubblicazione,
                             numero, 
                             posticipata
                             );
    }


    public boolean isPosticipata() {
        return posticipata;
    }


    public void setPosticipata(boolean posticipata) {
        this.posticipata = posticipata;
    }

 }
