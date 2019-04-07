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

@Entity
public class Operazione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = Smd.getMeseCorrente();

    private Integer definitivo = 0;

    private Integer stimato = 0;

    public Operazione() {
    }


    public Operazione(Pubblicazione pubblicazione, Anno anno, Mese mese) {
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
