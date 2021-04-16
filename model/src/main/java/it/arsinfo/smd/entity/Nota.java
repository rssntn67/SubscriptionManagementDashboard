package it.arsinfo.smd.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Nota implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Storico storico;

    private String description;
    private String operatore="admin";

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
        return String.format("Nota[id=%d, '%s', %s '%s', '%td %tb %tY %tR %tZ']",
                             id, storico.getCaption(), operatore, description,
                             data, data, data, data, data);
    }

    public Storico getStorico() {
        return storico;
    }

    public void setStorico(Storico storico) {
        this.storico = storico;
    }


    public String getOperatore() {
        return operatore;
    }


    public void setOperatore(String operatore) {
        this.operatore = operatore;
    }


	@Override
	public String getHeader() {
		return String.format("'%s','%td %tb %tY %tR %tZ']",
                 operatore, 
                data, data, data, data, data);
				}
}
