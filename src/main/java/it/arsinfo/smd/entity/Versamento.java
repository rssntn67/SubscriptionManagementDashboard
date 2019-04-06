package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Accettazione;
import it.arsinfo.smd.data.Bollettino;
import it.arsinfo.smd.data.Sostitutivo;

@Entity
public class Versamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToMany
    private List<Abbonamento> abbonamenti; 
    
    @ManyToOne
    private Incasso incasso;

    private String bobina;
    private String progressivoBobina;
    
    private String progressivo="9999999";
        
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento = SmdApplication.getStandardDate(new Date());
    
    @Enumerated(EnumType.STRING)
    private Bollettino bollettino;
    
    private BigDecimal importo = BigDecimal.ZERO;
    private BigDecimal incassato =BigDecimal.ZERO;
    
    private String operazione;

    private String provincia;
    private String ufficio;
    private String sportello;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile;
    
    private String campo;
    
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
        this.setDataContabile(incasso.getDataContabile());
    }
    
    public Versamento(Incasso incasso, BigDecimal importo) {
        this.incasso=incasso;
        this.setDataContabile(incasso.getDataContabile());
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
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
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
    public String getCampo() {
        return campo;
    }
    public void setCampo(String campo) {
        this.campo = campo;
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
    @Transient
    public boolean isCampovalido() {
        return SmdApplication.checkCampo(campo);
    }
    public String getProgressivo() {
        return progressivo;
    }
    public void setProgressivo(String progressivo) {
        this.progressivo = progressivo;
    }
    @Override
    public String toString() {
        return String.format("Versamento[id=%d,Incasso=%d,progressivo='%s',campo='%s',operazione='%s'valido='%b', importo='%.2f', incassato='%.2f', residuo='%.2f']",
                             id,incasso.getId(),progressivo,campo, operazione,isCampovalido(),importo,incassato,getResiduo());
    }
    public List<Abbonamento> getAbbonamenti() {
        return abbonamenti;
    }
    public void setAbbonamenti(List<Abbonamento> abbonamenti) {
        this.abbonamenti = abbonamenti;
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

    public String getOperazione() {
        return operazione;
    }
    public void setOperazione(String operazione) {
        this.operazione = operazione;
    }

    @Transient
    public void setDefaultDataPagamento(Date dataPagamento) {
        this.dataPagamento = SmdApplication.getStandardDate(dataPagamento);
    }
}
