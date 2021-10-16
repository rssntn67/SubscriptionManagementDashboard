package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"dataContabile","cassa", "cuas","ccp"})
        })
//create unique index ukh0do4klnqwq54yhlvtj5hjcbe on incasso (data_contabile, cassa, cuas, ccp);
public class DistintaVersamento implements SmdEntityItems<Versamento> {

    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, DocumentiTrasportoCumulati ddtAnno, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (ddtAnno == null ) {
            log.error("incassa: Ddt Anno null");
            throw new UnsupportedOperationException("incassa: Ddt Anno null");
        }

        BigDecimal incassato = importo;
        if (importo.compareTo(versamento.getResiduo()) > 0) {
            incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
        }
        versamento.setIncassato(versamento.getIncassato().add(incassato));
        ddtAnno.setImporto(ddtAnno.getImporto().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
    }

    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, OfferteCumulate offerte, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (offerte == null ) {
            log.error("incassa: Offerte null");
            throw new UnsupportedOperationException("incassa: Abbonamento null");
        }

        BigDecimal incassato = importo;
        if (importo.compareTo(versamento.getResiduo()) > 0) {
            incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
        }
        versamento.setIncassato(versamento.getIncassato().add(incassato));
        offerte.setImporto(offerte.getImporto().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
    }

    public static void storna(DistintaVersamento incasso, Versamento versamento, OfferteCumulate offerte, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (offerte == null ) {
            log.error("storna: Offerte null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (offerte.getImporto().compareTo(importo) < 0) {
            log.error("storna: incassato Offerte minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale Offerte minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        offerte.setImporto(offerte.getImporto().subtract(importo));
        incasso.setIncassato(incasso.getIncassato().subtract(importo));
    }

    public static void storna(DistintaVersamento incasso, Versamento versamento,DocumentiTrasportoCumulati ddts, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (ddts == null ) {
            log.error("storna: DDT null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (ddts.getImporto().compareTo(importo) < 0) {
            log.error("storna: incassato DDT minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale DDT minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        ddts.setImporto(ddts.getImporto().subtract(importo));
        incasso.setIncassato(incasso.getIncassato().subtract(importo));
    }


    public static BigDecimal incassa(DistintaVersamento incasso, Versamento versamento, Abbonamento abbonamento) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("incassa: Incasso null");
            throw new UnsupportedOperationException("incassa: Incasso null");
        }
        if (versamento == null ) {
            log.error("incassa: Versamento null");
            throw new UnsupportedOperationException("incassa: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("incassa: Abbonamento null");
            throw new UnsupportedOperationException("incassa: Abbonamento null");
        }

        BigDecimal incassato;
        if ((versamento.getResiduo()).compareTo(abbonamento.getResiduo()) < 0) {
            incassato = BigDecimal.valueOf(versamento.getResiduo().doubleValue());
        } else {
            incassato = BigDecimal.valueOf(abbonamento.getResiduo().doubleValue());
        }

        versamento.setIncassato(versamento.getIncassato().add(incassato));
        abbonamento.setIncassato(abbonamento.getIncassato().add(incassato));
        incasso.setIncassato(incasso.getIncassato().add(incassato));
        return incassato;
    }

    public static void storna(DistintaVersamento incasso, Versamento versamento, Abbonamento abbonamento, BigDecimal importo) throws UnsupportedOperationException {
        if (incasso == null ) {
            log.error("storna: Incasso null");
            throw new UnsupportedOperationException("storna: Incasso null");
        }
        if (versamento == null ) {
            log.error("storna: Versamento null");
            throw new UnsupportedOperationException("storna: Versamento null");
        }
        if (abbonamento == null ) {
            log.error("storna: Abbonamento null");
            throw new UnsupportedOperationException("storna: Abbonamento null");
        }
        if (versamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Versamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: importo Versamento minore importo da stornare");
        }
        if (abbonamento.getIncassato().compareTo(importo) < 0) {
            log.error("storna: incassato Abbonamento minore importo da stornare");
            throw new UnsupportedOperationException("storna: totale Abbonamento minore importo da stornare");
        }
        versamento.setIncassato(versamento.getIncassato().subtract(importo));
        abbonamento.setIncassato(abbonamento.getIncassato().subtract(importo));
        incasso.setIncassato(incasso.getIncassato().subtract(importo));
    }

    public static void calcoloImportoIncasso(DistintaVersamento incasso) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Versamento versamento: incasso.getItems()) {
            importo=importo.add(versamento.getImporto());
        }
        incasso.setImporto(importo);
        incasso.setDocumenti(incasso.getItems().size());
        incasso.setErrati(0);
        incasso.setEsatti(incasso.getDocumenti());
        incasso.setImportoErrati(BigDecimal.ZERO);
        incasso.setImportoEsatti(incasso.getImporto());
    }

    public static void calcoloImportoIncasso(DistintaVersamento incasso, List<Versamento> versamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Versamento versamento: versamenti) {
            importo=importo.add(versamento.getImporto());
        }
        incasso.setImporto(importo);
        incasso.setImportoEsatti(incasso.getImporto().subtract(incasso.getImportoErrati()));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Contante;
    @Enumerated(EnumType.STRING)
    private Cuas cuas = Cuas.NOCCP;
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;
            
    @OneToMany(mappedBy="distintaVersamento", orphanRemoval=true, fetch=FetchType.LAZY)
    private List<Versamento> versamenti = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile = SmdEntity.getStandardDate(new Date());
    
    private int documenti=0;
    private BigDecimal importo=BigDecimal.ZERO;
    private BigDecimal incassato=BigDecimal.ZERO;
    
    private int esatti=0;
    private BigDecimal importoEsatti=BigDecimal.ZERO;
    
    private int errati=0;
    private BigDecimal importoErrati=BigDecimal.ZERO;
    
    public DistintaVersamento() {
        super();
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
    public Ccp getCcp() {
        return ccp;
    }
    public void setCcp(Ccp ccp) {
        this.ccp = ccp;
    }
    public Date getDataContabile() {
        return dataContabile;
    }
    public int getDocumenti() {
        return documenti;
    }
    public void setDocumenti(int documenti) {
        this.documenti = documenti;
    }
    public BigDecimal getImporto() {
        return importo;
    }
    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    public int getEsatti() {
        return esatti;
    }
    public void setEsatti(int esatti) {
        this.esatti = esatti;
    }
    public BigDecimal getImportoEsatti() {
        return importoEsatti;
    }
    public void setImportoEsatti(BigDecimal importoEsatti) {
        this.importoEsatti = importoEsatti;
    }
    public int getErrati() {
        return errati;
    }
    public void setErrati(int errati) {
        this.errati = errati;
    }
    public BigDecimal getImportoErrati() {
        return importoErrati;
    }
    public void setImportoErrati(BigDecimal importoErrati) {
        this.importoErrati = importoErrati;
    }
    
    @Transient
    public String getDettagli() {
        StringBuffer sb = new StringBuffer();
        sb.append(cassa.name());
        sb.append(", ");
        sb.append(ccp.getCcp());
        sb.append(", ");
        sb.append(cuas.getDenominazione());
        return sb.toString();
    }

    @Override
    public List<Versamento> getItems() {
        return versamenti;
    }
    @Override
    public void setItems(List<Versamento> abbonamenti) {
        this.versamenti = abbonamenti;
    }
    @Override
    public boolean addItem(Versamento versamento) {
        versamenti.remove(versamento);
        return versamenti.add(versamento);
    }
    @Override
    public boolean removeItem(Versamento versamento) {
       return versamenti.remove(versamento);
    }
    
    @Override
    public String toString() {
        return String.format(
         "Distinta[id=%d,cassa='%s', dettagli='%s', documenti='%d', importo='%.2f',incassato='%.2f', residuo='%.2f',esatti='%d', imp.esatti='%.2f', errati='%d', imp.errati='%.2f']",
                             id,cassa, getDettagli(), documenti, importo,incassato,getResiduo(),esatti,importoEsatti,errati,importoErrati);
    }

    public Cassa getCassa() {
        return cassa;
    }
    public void setCassa(Cassa cassa) {
        this.cassa = cassa;
    }
    public BigDecimal getIncassato() {
        return incassato;
    }
    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }
    @Transient
    public BigDecimal getResiduo() {
        return importo.subtract(incassato);
    }

    public void setDataContabile(Date datacontabile) {
        this.dataContabile = SmdEntity.getStandardDate(datacontabile);
    }
    @Transient
    public String getHeader() {
		return String.format("%s:%s:'%td %tb %tY'",                 
				ccp.getCcp(),
				cassa,
               dataContabile, dataContabile, dataContabile
				);
    }    
}
