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
import javax.persistence.Transient;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;

@Entity
public class Incasso implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;
    @Enumerated(EnumType.STRING)
    private Cuas cuas;
    @Enumerated(EnumType.STRING)
    private Ccp ccp;
    
    private String operazione;
        
    @OneToMany(cascade = { CascadeType.ALL })
    private List<Versamento> versamenti = new ArrayList<Versamento>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataContabile;
    
    private int documenti=0;
    private BigDecimal importo=BigDecimal.ZERO;
    
    private int esatti=0;
    private BigDecimal importoEsatti=BigDecimal.ZERO;
    
    private int errati=0;
    private BigDecimal importoErrati=BigDecimal.ZERO;
    
    public Incasso() {
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
    public List<Versamento> getVersamenti() {
        return versamenti;
    }
    public void setVersamenti(List<Versamento> abbonamenti) {
        this.versamenti = abbonamenti;
    }
    public Date getDataContabile() {
        return dataContabile;
    }
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = dataContabile;
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
        switch (cassa) {
        case Ccp:
            sb.append(cuas.getDenominazione());
            sb.append(",ccp:");
            sb.append(ccp.getCcp());
            break;
        case Contante:
            sb.append(operazione);
            break;
        case Contrassegno:
            sb.append(operazione);
            break;
        case Bonifico:
            sb.append(operazione);
            break;
        case Paypal:
            sb.append(operazione);
            break;
        case Carte:
            sb.append(operazione);
            break;
        default:
            break;
        }
        return sb.toString();
    }
    public void addVersamento(Versamento versamento) {
        if (versamenti.contains(versamento)) {
            versamenti.remove(versamento);
        }
        versamenti.add(versamento);
    }
    @Override
    public String toString() {
        return String.format("Incasso[id=%d,cassa='%s', dettagli='%s', documenti='%d', importo='%.2f', esatti='%d', imp.esatti='%.2f', errati='%d', imp.errati='%.2f']",
                             id,cassa, getDettagli(), documenti, importo,esatti,importoEsatti,errati,importoErrati);
    }

    public Cassa getCassa() {
        return cassa;
    }
    public void setCassa(Cassa cassa) {
        this.cassa = cassa;
    }
    public String getOperazione() {
        return operazione;
    }
    public void setOperazione(String operazione) {
        this.operazione = operazione;
    }

    
}
