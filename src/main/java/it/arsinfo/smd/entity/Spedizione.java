package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Spedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private EstrattoConto estrattoConto;
    
    private Mese meseSpedizione;
    private Anno annoSpedizione;
    
    private Mese mesePubblicazione;
    private Anno annoPubblicazione;
    private boolean sospesa=false;

    public Spedizione() {
    }

    public Long getId() {
        return id;
    }


    @Transient
    public String getHeader() {
        return String.format("%s:Spedizione:Edit:'%s'", estrattoConto.getHeader());
    }

    @Override
    public String toString() {
        return String.format("Spedizione[id=%d, EstrattoConto=%d, Numero=%d, Destinatario=%d, Sospeso=%b]", 
                             id,estrattoConto.getId(),estrattoConto.getNumero(), estrattoConto.getDestinatario().getId(), sospesa);
    }

    public boolean isSospesa() {
        return sospesa;
    }

    public void setSospesa(boolean sospesa) {
        this.sospesa = sospesa;
    }
    
    @Transient
    public String getDecodeSospesa() {
        return Smd.decodeForGrid(sospesa);
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

}
