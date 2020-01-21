package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Sostitutivo;

@Entity
public class Versamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
        
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Incasso incasso;

    private String bobina;
    private String progressivoBobina;
    
    private String progressivo="9999999";
        
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento = Smd.getStandardDate(new Date());
    
    @Enumerated(EnumType.STRING)
    private Bollettino bollettino;
    
    private BigDecimal importo = BigDecimal.ZERO;
    private BigDecimal incassato =BigDecimal.ZERO;
    
    private String provincia;
    private String ufficio;
    private String sportello;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile;
    
    private String codeLine;
    
    @Enumerated(EnumType.STRING)
    private Accettazione accettazione;
    @Enumerated(EnumType.STRING)
    private Sostitutivo sostitutivo;

    public Versamento() {
        super();
    }

    
    public Versamento(Incasso incasso) {
        super();
        this.incasso=incasso;
        this.dataContabile = incasso.getDataContabile();
    }
    
    public Versamento(Incasso incasso, BigDecimal importo) {
        this.incasso=incasso;
        this.dataContabile = incasso.getDataContabile();
        this.importo=importo;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Incasso getIncasso() {
        return incasso;
    }
    public void setIncasso(Incasso incasso) {
        this.incasso = incasso;
    }
    public String getBobina() {
        return bobina;
    }
    public void setBobina(String bobina) {
        this.bobina = bobina;
    }
    public String getProgressivoBobina() {
        return progressivoBobina;
    }
    public void setProgressivoBobina(String progressivoBobina) {
        this.progressivoBobina = progressivoBobina;
    }
    public Date getDataPagamento() {
        return dataPagamento;
    }
    public Bollettino getBollettino() {
        return bollettino;
    }
    public void setBollettino(Bollettino bollettino) {
        this.bollettino = bollettino;
    }
    public BigDecimal getImporto() {
        return importo;
    }
    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    public String getProvincia() {
        return provincia;
    }
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public String getUfficio() {
        return ufficio;
    }
    public void setUfficio(String ufficio) {
        this.ufficio = ufficio;
    }
    public String getSportello() {
        return sportello;
    }
    public void setSportello(String sportello) {
        this.sportello = sportello;
    }
    public Date getDataContabile() {
        return dataContabile;
    }
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = dataContabile;
    }
    public String getCodeLine() {
        return codeLine;
    }
    public void setCodeLine(String codeLine) {
        this.codeLine = codeLine;
    }
    public Accettazione getAccettazione() {
        return accettazione;
    }
    public void setAccettazione(Accettazione tipoAccettazione) {
        this.accettazione = tipoAccettazione;
    }
    public Sostitutivo getSostitutivo() {
        return sostitutivo;
    }
    public void setSostitutivo(Sostitutivo tipoSostitutivo) {
        this.sostitutivo = tipoSostitutivo;
    }    
    
    public String getProgressivo() {
        return progressivo;
    }
    public void setProgressivo(String progressivo) {
        this.progressivo = progressivo;
    }
    @Override
    public String toString() {
        return String.format("Versamento[id=%d,progressivo='%s',codeLine='%s', importo='%.2f', incassato='%.2f']",
                             id,progressivo,codeLine, importo,incassato);
    }

    @Transient
    public BigDecimal getResiduo() {
        return importo.subtract(incassato);
    }

    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }
    
    public BigDecimal getIncassato() {
        return incassato;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = Smd.getStandardDate(dataPagamento);
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Versamento other = (Versamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
