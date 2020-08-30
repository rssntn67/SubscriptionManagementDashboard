package it.arsinfo.smd.entity;

import java.math.BigDecimal;

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

@Entity
public class Spedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private Abbonamento abbonamento;

    @ManyToOne(fetch=FetchType.EAGER)
    private Anagrafica destinatario;

    @Enumerated(EnumType.STRING)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    @Enumerated(EnumType.STRING)
    private Mese meseSpedizione=Mese.getMeseCorrente();

    @Enumerated(EnumType.STRING)
    private Anno annoSpedizione=Anno.getAnnoCorrente();
    
    private Integer pesoStimato=0;
    
    private BigDecimal spesePostali = BigDecimal.ZERO;

    public Spedizione() {
    }

    public Long getId() {
        return id;
    }


    @Transient
    public String getHeader() {
        return String.format("'%s' %s %s", 
                destinatario.getHeader(),
                meseSpedizione,
                annoSpedizione
                );
    }

    @Override
    public String toString() {
        return String.format("Spedizione[id=%d, abb.%d %s %s, peso gr. %d Eur %.2f, dest. %s, %s]", 
                             id,
                             abbonamento.getId(),
                             meseSpedizione,
                             annoSpedizione,
                             pesoStimato,
                             spesePostali,
                             destinatario,
                             invioSpedizione
                             );
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

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }

    public Abbonamento getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(Abbonamento abbonamento) {
        this.abbonamento = abbonamento;
    }

    public Integer getPesoStimato() {
        return pesoStimato;
    }

    public void setPesoStimato(Integer pesoStimato) {
        this.pesoStimato = pesoStimato;
    }
        
    public BigDecimal getSpesePostali() {
        return spesePostali;
    }

    public void setSpesePostali(BigDecimal spesePostali) {
        this.spesePostali = spesePostali;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((abbonamento == null || abbonamento.getId() == null ) ? 0 : abbonamento.getId().hashCode());
        result = prime * result
                + ((annoSpedizione == null) ? 0 : annoSpedizione.hashCode());
        result = prime * result
                + ((destinatario == null) ? 0 : destinatario.hashCode());
        result = prime * result
                + ((invioSpedizione == null) ? 0
                                             : invioSpedizione.hashCode());
        result = prime * result
                + ((meseSpedizione == null) ? 0 : meseSpedizione.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Spedizione other = (Spedizione) obj;
        if (abbonamento == null) {
            if (other.abbonamento != null)
                return false;
        } else if (!abbonamento.equals(other.abbonamento))
            return false;
        if (annoSpedizione != other.annoSpedizione)
            return false;
        if (destinatario == null) {
            if (other.destinatario != null)
                return false;
        } else if (!destinatario.equals(other.destinatario))
            return false;
        if (invioSpedizione != other.invioSpedizione)
            return false;
        if (meseSpedizione != other.meseSpedizione)
            return false;
        return true;
    }

    @Transient
    public String getDestinazione() {
        return Anagrafica.generaIntestazione(destinatario);
    }
}
