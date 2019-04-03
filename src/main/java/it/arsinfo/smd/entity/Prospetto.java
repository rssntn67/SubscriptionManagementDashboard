package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;

@Entity
public class Prospetto implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @Enumerated(EnumType.STRING)
    private Anno anno = SmdApplication.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = SmdApplication.getMeseCorrente();

    private Integer numero = 1;


    public Prospetto() {
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }


    @Override
    public String toString() {
        return String.format("Prospetto[id=%d, Pubblicazione=%d, Numero=%d]", 
                             id,pubblicazione.getId(),numero);
    }
}
