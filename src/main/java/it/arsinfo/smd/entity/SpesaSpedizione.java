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

import it.arsinfo.smd.data.AreaSpedizione;

@Entity
public class SpesaSpedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal speseSpedizione=BigDecimal.ZERO;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;
    private Integer numero=1;

    @Enumerated(EnumType.STRING)
    private AreaSpedizione areaSpedizione=AreaSpedizione.Italia;

    public Long getId() {
        return id;
    }

    public SpesaSpedizione() {
    }

    @Override
    public String toString() {
        return String.format("SpesaSpedizione[id=%d,%d '%s' '%.2f' Eur - %s]",
                             id, numero, pubblicazione.getNome(),speseSpedizione,areaSpedizione);
    }

    public BigDecimal getSpeseSpedizione() {
        return speseSpedizione;
    }

    public void setSpeseSpedizione(BigDecimal speseSpedizione) {
        this.speseSpedizione = speseSpedizione;
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

    public AreaSpedizione getAreaSpedizione() {
        return areaSpedizione;
    }

    public void setAreaSpedizione(AreaSpedizione areaSpedizione) {
        this.areaSpedizione = areaSpedizione;
    }

}
