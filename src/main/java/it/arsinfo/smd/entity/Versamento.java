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

import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Sostitutivo;
import it.arsinfo.smd.service.Smd;

@Entity
public class Versamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
        
    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private DistintaVersamento distintaVersamento;

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

    @ManyToOne(optional=true,fetch=FetchType.LAZY)
    private Anagrafica committente;

    public Versamento() {
        super();
    }

    
    public Versamento(DistintaVersamento incasso) {
        super();
        this.distintaVersamento=incasso;
        this.dataContabile = incasso.getDataContabile();
    }
    
    public Versamento(DistintaVersamento incasso, BigDecimal importo) {
        this.distintaVersamento=incasso;
        this.dataContabile = incasso.getDataContabile();
        this.importo=importo;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DistintaVersamento getDistintaVersamento() {
        return distintaVersamento;
    }
    public void setDistintaVersamento(DistintaVersamento distintaVersamento) {
        this.distintaVersamento = distintaVersamento;
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
        this.dataContabile = Smd.getStandardDate(dataContabile);
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

    @Transient
    public String getAssociatoCommittente() {
    	if (committente != null) {
    		return "si";
    	}
    	return "no";
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


	public Anagrafica getCommittente() {
		return committente;
	}


	public void setCommittente(Anagrafica committente) {
		this.committente = committente;
	}


	@Override
	public String getHeader() {
		return "Versamento";
	}
}
