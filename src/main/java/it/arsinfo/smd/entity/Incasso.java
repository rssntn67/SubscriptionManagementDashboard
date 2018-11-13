package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Incasso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    Cuas cuas;
    @Enumerated(EnumType.STRING)
    ContoCorrentePostale ccp;
        
    @OneToMany(cascade = { CascadeType.PERSIST })
    List<Versamento> abbonamenti = new ArrayList<Versamento>();

    @Temporal(TemporalType.TIMESTAMP)
    Date dataContabile;
    
    int totaleDocumenti;
    BigDecimal totaleImporto;
    
    int documentiEsatti;
    BigDecimal importoDocumentiEsatti;
    
    int documentiErrati;
    BigDecimal importoDocumentiErrati;
    
    
    
    public Incasso() {
        super();
        // TODO Auto-generated constructor stub
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Cuas getCuas() {
        return cuas;
    }
    public void setCuas(Cuas cuas) {
        this.cuas = cuas;
    }
    public ContoCorrentePostale getCcp() {
        return ccp;
    }
    public void setCcp(ContoCorrentePostale ccp) {
        this.ccp = ccp;
    }
    public List<Versamento> getAbbonamenti() {
        return abbonamenti;
    }
    public void setAbbonamenti(List<Versamento> abbonamenti) {
        this.abbonamenti = abbonamenti;
    }
    public Date getDataContabile() {
        return dataContabile;
    }
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = dataContabile;
    }
    public int getTotaleDocumenti() {
        return totaleDocumenti;
    }
    public void setTotaleDocumenti(int totaleDocumenti) {
        this.totaleDocumenti = totaleDocumenti;
    }
    public BigDecimal getTotaleImporto() {
        return totaleImporto;
    }
    public void setTotaleImporto(BigDecimal totaleImporto) {
        this.totaleImporto = totaleImporto;
    }
    public int getDocumentiEsatti() {
        return documentiEsatti;
    }
    public void setDocumentiEsatti(int documentiEsatti) {
        this.documentiEsatti = documentiEsatti;
    }
    public BigDecimal getImportoDocumentiEsatti() {
        return importoDocumentiEsatti;
    }
    public void setImportoDocumentiEsatti(BigDecimal importoDocumentiEsatti) {
        this.importoDocumentiEsatti = importoDocumentiEsatti;
    }
    public int getDocumentiErrati() {
        return documentiErrati;
    }
    public void setDocumentiErrati(int documentiErrati) {
        this.documentiErrati = documentiErrati;
    }
    public BigDecimal getImportoDocumentiErrati() {
        return importoDocumentiErrati;
    }
    public void setImportoDocumentiErrati(BigDecimal importoDocumentiErrati) {
        this.importoDocumentiErrati = importoDocumentiErrati;
    }
    
    
    
}
