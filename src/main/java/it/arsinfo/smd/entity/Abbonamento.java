package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.service.Smd;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"codeLine"}),
        @UniqueConstraint(columnNames = {"intestatario_id","campagna_id","contrassegno"})
        })
//create unique index abb_idx_codeline on abbonamento (codeline);
//create unique index abb_idx_select on abbonamento (intestatario_id, campagna_id, contrassegno);
public class Abbonamento implements SmdEntityItems<RivistaAbbonamento> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Anagrafica intestatario;

    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoCorrente();

    @ManyToOne
    private Campagna campagna;
    
    private BigDecimal pregresso=BigDecimal.ZERO;
    private BigDecimal importo=BigDecimal.ZERO;
    private BigDecimal spese=BigDecimal.ZERO;
    private BigDecimal speseEstero=BigDecimal.ZERO;
    private BigDecimal speseEstrattoConto=BigDecimal.ZERO;
    private BigDecimal incassato=BigDecimal.ZERO;

    private String codeLine;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();
    @Column(nullable=false)
    private boolean contrassegno = false;

    @Transient
    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;

    @Transient
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;
    @Transient
    private Cuas cuas = Cuas.NOCCP;

    @Transient
    private String progressivo;

    @Transient
    private List<RivistaAbbonamento> estrattiConto = new ArrayList<RivistaAbbonamento>();
    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;
    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile;

    public Abbonamento() {
    }

    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica anagrafica) {
        this.intestatario = anagrafica;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Abbonamento[id=%d, Imp:'%.2f', Spese:'%.2f', Estero:'%.2f', 'Preg:'%.2f','Inc.to:'%.2f',CL:'%s', Anno=%s",
                                   id, 
                                   importo,
                                   spese,
                                   speseEstero,
                                   pregresso,
                                   incassato,
                                   codeLine,
                                   anno.getAnnoAsString());
    }
    
    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Campagna getCampagna() {
        return campagna;
    }

    public void setCampagna(Campagna campagna) {
        this.campagna = campagna;
    }

    public String getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(String codeLine) {
        this.codeLine = codeLine;
    }

    public Cassa getCassa() {
        return cassa;
    }

    public void setCassa(Cassa cassa) {
        this.cassa = cassa;
    }

    public Ccp getCcp() {
        return ccp;
    }

    public void setCcp(
            Ccp ccp) {
        this.ccp = ccp;
    }
        
    @Transient
    public String getHeader() {
        return String.format("%s:%s", anno.getAnnoAsString(),getIntestazione());
    }
    
    @Transient
    public String getCampagnaAsString() {
        if (campagna == null) {
            return "Non Associato a Campagna";
        }
        return campagna.getCaption();
    }
    
    @Transient
    public BigDecimal getResiduo() {
        return getTotale().subtract(incassato);
    }

    public BigDecimal getIncassato() {
        return incassato;
    }
    
    @Transient
    public String getIntestazione() {
        return Anagrafica.generaIntestazione(intestatario);
    }

    public BigDecimal getTotale() {
        return importo.add(pregresso).add(spese).add(speseEstero).add(speseEstrattoConto);
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public BigDecimal getSpese() {
        return spese;
    }

    public void setSpese(BigDecimal spese) {
        this.spese = spese;
    }

    public static boolean checkCodeLine(String codeline) {
        if (codeline == null || codeline.length() != 18) {
            return false;
            
        }
        
        String codice = codeline.substring(0, 16);
        
        Long valorecodice = (Long.parseLong(codice) % 93);
        Integer codicecontrollo = Integer.parseInt(codeline.substring(16,18));
        return codicecontrollo.intValue() == valorecodice.intValue();
    }

    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale codeLine 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generaCodeLine(Anno anno, Anagrafica anagrafica) {
        // primi 2 caratteri anno
        String codeline = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        codeline += anagrafica.getCodeLineBase();
        codeline += String.format("%02d", Long.parseLong(codeline) % 93);
        return codeline;
    }
    
    public static String generaCodeLine(Anno anno) {
        // primi 2 caratteri anno
        String codeLine = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        codeLine += String.format("%014d", ThreadLocalRandom.current().nextLong(99999999999999l));
        codeLine += String.format("%02d", Long.parseLong(codeLine) % 93);
        return codeLine;
    }

    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }

    public BigDecimal getPregresso() {
        return pregresso;
    }

    public void setPregresso(BigDecimal pregresso) {
        this.pregresso = pregresso;
    }

    @Transient
    public Date getDataPagamento() {
        return dataPagamento;
    }

    @Transient
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = Smd.getStandardDate(dataPagamento);
    }

    @Transient
    public Date getDataContabile() {
        return dataContabile;
    }

    @Transient
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = Smd.getStandardDate(dataContabile);
    }

    @Transient
    public Cuas getCuas() {
        return cuas;
    }

    @Transient
    public void setCuas(Cuas cuas) {
        this.cuas = cuas;
    }

    @Transient
    public String getProgressivo() {
        return progressivo;
    }

    @Transient
    public void setProgressivo(String progressivo) {
        this.progressivo= progressivo;
    }

    @Transient
    public Incassato getStatoIncasso() {
        return Smd.getStatoIncasso(this);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (id != null) {
			return prime * result + id.hashCode();
		}
		result = prime * result + ((codeLine == null) ? 0 : codeLine.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Abbonamento other = (Abbonamento) obj;
		if (id != null) {
			return id.equals(other.getId());
		}
		if (codeLine == null) {
			if (other.codeLine != null)
				return false;
		} else if (!codeLine.equals(other.codeLine))
			return false;
		return true;
	}

	public BigDecimal getSpeseEstero() {
		return speseEstero;
	}

	public void setSpeseEstero(BigDecimal speseEstero) {
		this.speseEstero = speseEstero;
	}

	public BigDecimal getSpeseEstrattoConto() {
		return speseEstrattoConto;
	}

	public void setSpeseEstrattoConto(BigDecimal speseEstrattoConto) {
		this.speseEstrattoConto = speseEstrattoConto;
	}

	public List<RivistaAbbonamento> getItems() {
		return estrattiConto;
	}

	public void setItems(List<RivistaAbbonamento> estrattiConto) {
		this.estrattiConto = estrattiConto;
	}
	
	public boolean addItem(RivistaAbbonamento ec) {
		return estrattiConto.add(ec);
	}
    
	public boolean removeItem(RivistaAbbonamento ec) {
		return estrattiConto.remove(ec);
	}

	public boolean isContrassegno() {
		return contrassegno;
	}

	public void setContrassegno(boolean contrassegno) {
		this.contrassegno = contrassegno;
	}


}