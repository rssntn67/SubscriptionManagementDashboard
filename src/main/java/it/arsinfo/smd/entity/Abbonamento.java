package it.arsinfo.smd.entity;

import java.math.BigDecimal;
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
		
		public String getAnnoAsString() {
			return Integer.toString(anno);
		}

		public int getAnno() {
			return anno;
		}

		public void setAnno(int anno) {
			this.anno = anno;
		}
	}
	
	public enum Mese {
		GENNAIO("Gen",1, "01"),
		FEBBRAIO("Feb",2, "02"),
		MARZO("Mar",3, "03"),
		APRILE("Apr",4, "04"),
		MAGGIO("Mag",5, "05"),
		GIUGNO("Giu",6, "06"),
		LUGLIO("Lug",7, "07"),
		AGOSTO("Ago",8, "08"),
		SETTEMBRE("Set",9, "09"),
		OTTOBRE("Ott",10, "10"),
		NOVEMBRE("Nov",11, "11"),
		DICEMBRE("Dic",12, "12");
		
		private String nomeBreve;
		private int posizione;
		private String code;
		
		
		private Mese(String nome, int posizione, String cod) {
			this.nomeBreve=nome;
			this.posizione=posizione;
			this.code=cod;
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


		public String getCode() {
			return code;
		}


		public void setCode(String code) {
			this.code = code;
		}
	}
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@ManyToOne
	private Anagrafica anagrafica;

	@ManyToOne
	private Anagrafica destinatario;

	@ManyToOne
	private Campagna campagna;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	private BigDecimal cost;
	private String campo;
    
	private boolean omaggio=false;
	private boolean pagato=false;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataincasso;
	
    private boolean estratti=false;
    private boolean blocchetti=false;
    private boolean lodare=false;
    private boolean messaggio=false;
    private boolean spese=false;
    
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
		this.cost=BigDecimal.ZERO;
	}

	public Abbonamento(Anagrafica anagrafica) {
		super();
		this.anagrafica = anagrafica;
		this.destinatario = anagrafica;
		this.data = new Date();
		this.campo="";
		this.cost=BigDecimal.ZERO;
	}

	public Abbonamento(Anagrafica anagrafica, Anagrafica destinatario) {
		super();
		this.anagrafica = anagrafica;
		this.destinatario = destinatario;
		this.data = new Date();
		this.campo="";
		this.cost=BigDecimal.ZERO;
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
	
	public BigDecimal getCost() {
		return cost;
	}
	
	public void setCost(BigDecimal cost) {
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

	public boolean isSpese() {
		return spese;
	}

	public void setSpese(boolean spese) {
		this.spese = spese;
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

	public Anagrafica getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(Anagrafica destinatario) {
		this.destinatario = destinatario;
	}

	public Anno getAnno() {
		return anno;
	}

	public void setAnno(Anno anno) {
		this.anno = anno;
	}

	public Date getDataincasso() {
		return dataincasso;
	}

	public void setDataincasso(Date dataincasso) {
		this.dataincasso = dataincasso;
	}

	public Campagna getCampagna() {
		return campagna;
	}

	public void setCampagna(Campagna campagna) {
		this.campagna = campagna;
	}

	public boolean isOmaggio() {
		return omaggio;
	}

	public void setOmaggio(boolean omaggio) {
		this.omaggio = omaggio;
	}
		
}
