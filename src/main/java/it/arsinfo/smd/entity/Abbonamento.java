package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Abbonamento {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@ManyToOne
	private Anagrafica anagrafica;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	private Float cost;
	private String campo;

	@OneToMany
	private List<AbbonamentoPubblicazione> abbonamentoPubblicazione = new ArrayList<AbbonamentoPubblicazione>();
	
	public Abbonamento() {
		this.anagrafica = new Anagrafica();
		this.data = new Date();
		this.campo="";
		this.cost=0.00f;
	}

	public Abbonamento(Anagrafica anagrafica) {
		super();
		this.anagrafica = anagrafica;
		this.data = new Date();
		this.campo="";
		this.cost=0.00f;
	}
	
	public Anagrafica getAnagrafica() {
		return anagrafica;
	}
	
	public void setAnagrafica(Anagrafica anagrafica) {
		this.anagrafica = anagrafica;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public Float getCost() {
		return cost;
	}
	
	public void setCost(Float cost) {
		this.cost = cost;
	}
	
	public String getCampo() {
		return campo;
	}
	
	public void setCampo(String campo) {
		this.campo = campo;
	}
	
	public Long getId() {
		return id;
	}
	
    @Override
	public String toString() {
		String aaa= String.format("Abbonamento[id=%d, Anagrafica='%s', Campo='%s', Costo='%f', Data='%td %tb %tY %tR %tZ']", 
				id, anagrafica.getCognome(), campo,cost,data,data,data,data,data,data);
		/*
		for (AbbonamentoPubblicazione ab: abbonamentoPubblicazione) {
			aaa+="\n"+ ab.toString();
		}
		*/
		return aaa;
	}

	public List<AbbonamentoPubblicazione> getAbbonamentoPubblicazione() {
		return abbonamentoPubblicazione;
	}

	public void setAbbonamentoPubblicazione(
			List<AbbonamentoPubblicazione> abbonamentoPubblicazione) {
		this.abbonamentoPubblicazione = abbonamentoPubblicazione;
	}
		
}
