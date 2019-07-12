package it.arsinfo.smd.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"rangeSpeseSpedizione" , "areaSpedizione"})})
public class SpesaSpedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal spese=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RangeSpeseSpedizione rangeSpeseSpedizione = RangeSpeseSpedizione.Base;
    @Enumerated(EnumType.STRING)
    private AreaSpedizione areaSpedizione=AreaSpedizione.Italia;

    public Long getId() {
        return id;
    }

    public SpesaSpedizione() {
    }

    @Override
    public String toString() {
        return String.format("SpesaSpedizione[id=%d,%s '%.2f' Eur - %s]",
                             id, rangeSpeseSpedizione,spese,areaSpedizione);
    }

    public BigDecimal getSpese() {
        return spese;
    }

    public void setSpese(BigDecimal speseSpedizione) {
        this.spese = speseSpedizione;
    }

    public AreaSpedizione getArea() {
        return areaSpedizione;
    }

    public void setArea(AreaSpedizione areaSpedizione) {
        this.areaSpedizione = areaSpedizione;
    }

    public RangeSpeseSpedizione getRange() {
        return rangeSpeseSpedizione;
    }

    public void setRange(RangeSpeseSpedizione range) {
        this.rangeSpeseSpedizione = range;
    }

}
