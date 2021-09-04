package it.arsinfo.smd.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"campagna_id","stato"})
        })
public class OperazioneCampagna implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Campagna campagna;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoCampagna stato;
    
    @Column(nullable = false)
    private String operatore="admin";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date data = new Date();


    public OperazioneCampagna() {
    }


	@Override
	public String getHeader() {
		return campagna.getHeader() + ":" + stato;
	}


	@Override
	public Long getId() {
		return id;
	}


	public Campagna getCampagna() {
		return campagna;
	}


	public void setCampagna(Campagna campagna) {
		this.campagna = campagna;
	}


	public StatoCampagna getStato() {
		return stato;
	}


	public void setStato(StatoCampagna stato) {
		this.stato = stato;
	}


	public String getOperatore() {
		return operatore;
	}


	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}


	public Date getData() {
		return data;
	}


	public void setId(Long id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "OperazioneCampagna [id=" + id + ", campagna=" + campagna + ", stato=" + stato + ", operatore="
				+ operatore + ", data=" + data + "]";
	}


	public void setData(Date data) {
		this.data = data;
	}

}
