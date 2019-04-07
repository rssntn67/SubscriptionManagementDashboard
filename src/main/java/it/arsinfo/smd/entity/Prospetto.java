package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;

@Entity
public class Prospetto implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Omaggio omaggio = Omaggio.No;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = Smd.getMeseCorrente();

    private Integer stimato = 0;

    public Prospetto() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ProspettoItem[id=%d, Prospetto=%d, Omaggio=%s, Anno=%s, Mese=%s, Stimati=%d]", 
                             id,omaggio, anno,mese,stimato);
    }

    public Omaggio getOmaggio() {
        return omaggio;
    }

    public void setOmaggio(Omaggio omaggio) {
        this.omaggio = omaggio;
    }

    public Integer getStimato() {
        return stimato;
    }

    public void setStimato(Integer stimato) {
        this.stimato = stimato;
    }

    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }

    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Mese getMese() {
        return mese;
    }

    public void setMese(Mese mese) {
        this.mese = mese;
    }
}
