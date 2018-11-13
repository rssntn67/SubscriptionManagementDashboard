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
public class Versamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private Incasso incasso;

    String bobina;
    String progressivoBobina;
    
    @Enumerated(EnumType.STRING)
    ContoCorrentePostale ccp;
    
    @Temporal(TemporalType.TIMESTAMP)
    Date dataPagamento;
    
    @Enumerated(EnumType.STRING)
    TipoDocumentoBollettino tipoDocumento;
    
    BigDecimal importo;
    String provincia;
    String ufficio;
    String sportello;
    
    @Temporal(TemporalType.TIMESTAMP)
    Date dataContabile;
    
    String campo;
    
    @Enumerated(EnumType.STRING)
    TipoAccettazioneBollettino tipoAccettazione;
    @Enumerated(EnumType.STRING)
    TipoSostitutivoBollettino tipoSostitutivo;
    
    
    public Versamento() {
        super();
        // TODO Auto-generated constructor stub
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
    public ContoCorrentePostale getCcp() {
        return ccp;
    }
    public void setCcp(ContoCorrentePostale ccp) {
        this.ccp = ccp;
    }
    public Date getDataPagamento() {
        return dataPagamento;
    }
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    public TipoDocumentoBollettino getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(TipoDocumentoBollettino tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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
    public TipoAccettazioneBollettino getTipoAccettazione() {
        return tipoAccettazione;
    }
    public void setTipoAccettazione(TipoAccettazioneBollettino tipoAccettazione) {
        this.tipoAccettazione = tipoAccettazione;
    }
    public TipoSostitutivoBollettino getTipoSostitutivo() {
        return tipoSostitutivo;
    }
    public void setTipoSostitutivo(TipoSostitutivoBollettino tipoSostitutivo) {
        this.tipoSostitutivo = tipoSostitutivo;
    }
    
}
