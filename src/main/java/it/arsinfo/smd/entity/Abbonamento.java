package it.arsinfo.smd.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Abbonamento {

	public enum Anno {
		ANNO2018(2018),
		ANNO2019(2019),
		ANNO2020(2020),
		ANNO2021(2021),
		ANNO2022(2022),
		ANNO2023(2023);
		
		private int anno;
		
		private Anno(int anno) {
			this.anno=anno;
		}

		public int getAnno() {
			return anno;
		}

		public void setAnno(int anno) {
			this.anno = anno;
		}
	}
	
	public enum Mese {
		GENNAIO("Gen",1),
		FEBBRAIO("Gen",2),
		MARZO("Gen",3),
		APRILE("Gen",4),
		MAGGIO("Gen",5),
		GIUGNO("Gen",6),
		LUGLIO("Gen",7),
		AGOSTO("Gen",8),
		SETTEMBRE("Gen",9),
		OTTOBRE("Gen",10),
		NOVEMBRE("Gen",11),
		DICEMBRE("Gen",12);
		
		private String nomeBreve;
		private int posizione;
		
		
		private Mese(String nome, int posizione) {
			this.nomeBreve=nome;
			this.posizione=posizione;
		}


		public String getNomeBreve() {
			return nomeBreve;
		}


		public void setNomeBreve(String nomeBreve) {
			this.nomeBreve = nomeBreve;
		}


		public int getPosizione() {
			return posizione;
		}


		public void setPosizione(int posizione) {
			this.posizione = posizione;
		}
	}
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@ManyToOne
	private Anagrafica anagrafica;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	private Float cost;
	private String campo;
    
	private boolean pagato=false;

    private boolean estratti=false;
    private boolean blocchetti=false;
    private boolean lodare=false;
    private boolean messaggio=false;
    private boolean costi=false;
    
    @Enumerated(EnumType.STRING)
    private Anno anno;
    @Enumerated(EnumType.STRING)
    private Mese inizio=Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine=Mese.DICEMBRE;
	
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

	public boolean isPagato() {
		return pagato;
	}

	public void setPagato(boolean pagato) {
		this.pagato = pagato;
	}

	public boolean isEstratti() {
		return estratti;
	}

	public void setEstratti(boolean estratti) {
		this.estratti = estratti;
	}

	public boolean isBlocchetti() {
		return blocchetti;
	}

	public void setBlocchetti(boolean blocchetti) {
		this.blocchetti = blocchetti;
	}

	public boolean isLodare() {
		return lodare;
	}

	public void setLodare(boolean lodare) {
		this.lodare = lodare;
	}

	public boolean isMessaggio() {
		return messaggio;
	}

	public void setMessaggio(boolean messaggio) {
		this.messaggio = messaggio;
	}

	public boolean isCosti() {
		return costi;
	}

	public void setCosti(boolean costi) {
		this.costi = costi;
	}

	public Mese getInizio() {
		return inizio;
	}

	public void setInizio(Mese inizio) {
		this.inizio = inizio;
	}

	public Mese getFine() {
		return fine;
	}

	public void setFine(Mese fine) {
		this.fine = fine;
	}
		
}
