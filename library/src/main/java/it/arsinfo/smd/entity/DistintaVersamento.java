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

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"dataContabile","cassa", "cuas","ccp"})
        })
//create unique index ukh0do4klnqwq54yhlvtj5hjcbe on incasso (data_contabile, cassa, cuas, ccp);
public class DistintaVersamento implements SmdEntityItems<Versamento> {

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
    private List<Versamento> versamenti = new ArrayList<Versamento>();

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
        StringBuffer sb = new StringBuffer("");
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
        if (versamenti.contains(versamento)) {
            versamenti.remove(versamento);
        }
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
