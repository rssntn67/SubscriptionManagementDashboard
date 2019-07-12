package it.arsinfo.smd.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.SmdEntity;

@Entity
public class Nota implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Storico storico;

    private String description;

    private final Date data = new Date();

    public Nota() {
    }

    
    public Nota(Storico storico) {
        this.storico = storico;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Date getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Nota[id=%d, '%s', '%s', '%td %tb %tY %tR %tZ']",
                             id, storico.getCaption(), description,
                             data, data, data, data, data);
    }

    public Storico getStorico() {
        return storico;
    }

    public void setStorico(Storico storico) {
        this.storico = storico;
    }
}
