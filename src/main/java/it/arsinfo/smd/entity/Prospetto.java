package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Prospetto implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @OneToMany
    private List<ProspettoItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Anno anno = SmdApplication.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = SmdApplication.getMeseCorrente();

    private Integer definitivo = 0;

    private Integer stimato = 0;

    public Prospetto() {
    }


    public Prospetto(Pubblicazione pubblicazione, Anno anno, Mese mese) {
        super();
        this.pubblicazione = pubblicazione;
        this.anno = anno;
        this.mese = mese;
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

    @Override
    public String toString() {
        return String.format("Prospetto[id=%d, Pubblicazione=%d, Stimato=%d, Definitivo=%d, Anno=%s, Mese=%s]", 
                             id,pubblicazione.getId(),stimato,definitivo,anno,mese);
    }


    public List<ProspettoItem> getItems() {
        return items;
    }


    public void setItems(List<ProspettoItem> items) {
        this.items = items;
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


    public Integer getDefinitivo() {
        return definitivo;
    }


    public void setDefinitivo(Integer definitivo) {
        this.definitivo = definitivo;
    }


    public Integer getStimato() {
        return stimato;
    }


    public void setStimato(Integer stimato) {
        this.stimato = stimato;
    }
}
