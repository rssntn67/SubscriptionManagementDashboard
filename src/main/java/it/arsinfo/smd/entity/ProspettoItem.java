package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;

@Entity
public class ProspettoItem implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Prospetto prospetto;
    
    @Enumerated(EnumType.STRING)
    private Omaggio omaggio = Omaggio.No;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

    private Integer stimato = 0;

    public ProspettoItem() {
    }

    public ProspettoItem(Prospetto prospetto) {
        super();
        this.prospetto = prospetto;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ProspettoItem[id=%d, Prospetto=%d, Omaggio=%s, Invio=%s, Stimati=%d]", 
                             id,prospetto.getId(),omaggio, invio,stimato);
    }

    public Prospetto getProspetto() {
        return prospetto;
    }

    public void setProspetto(Prospetto prospetto) {
        this.prospetto = prospetto;
    }

    public Omaggio getOmaggio() {
        return omaggio;
    }

    public void setOmaggio(Omaggio omaggio) {
        this.omaggio = omaggio;
    }

    public Invio getInvio() {
        return invio;
    }

    public void setInvio(Invio invio) {
        this.invio = invio;
    }

    public Integer getStimato() {
        return stimato;
    }

    public void setStimato(Integer stimato) {
        this.stimato = stimato;
    }
}
