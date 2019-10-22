package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.StatoAbbonamento;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"codeLine"}),
        @UniqueConstraint(columnNames = {"intestatario_id","campagna_id", "cassa"})
        })
//create unique index abb_idx_codeline on abbonamento (codeline);
//create unique index abb_idx_select on abbonamento (intestatario_id, campagna_id, cassa);
public class Abbonamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Anagrafica intestatario;

    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoCorrente();

    @ManyToOne
    private Campagna campagna;
    
    @OneToOne
    private Versamento versamento;

    private BigDecimal pregresso=BigDecimal.ZERO;
    private BigDecimal importo=BigDecimal.ZERO;
    private BigDecimal spese=BigDecimal.ZERO;
    private BigDecimal incassato=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;
    private String codeLine;
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;

    @Enumerated(EnumType.STRING)
    private StatoAbbonamento statoAbbonamento = StatoAbbonamento.Nuovo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

    @Transient
    private Cuas cuas = Cuas.NOCCP;

    @Transient
    private String operazione;

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
        return String.format("Abbonamento[id=%d, %s , Intestatario='%d', Imp. '%.2f', Spese '%.2f',Preg '%.2f', %s,'%s', Anno=%s",
                                   id, 
                                   getStatoIncasso(), 
                                   intestatario.getId(), 
                                   importo,
                                   spese,
                                   pregresso,
                                   cassa,
                                   codeLine,
                                   anno);
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

    public Versamento getVersamento() {
        return versamento;
    }

    public void setVersamento(Versamento versamento) {
        this.versamento = versamento;
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
        return String.format("Abbonamento:Edit:'%s %s'", intestatario.getCaption(), anno.getAnnoAsString());
    }
    
    @Transient
    public String getCampagnaAsString() {
        if (campagna == null) {
            return "Non Associato a Campagna";
        }
        return campagna.getCaption();
    }
    
    @Transient
    public Incassato getStatoIncasso() {
        if (getTotale().signum() == 0) {
            return Incassato.Omaggio;
        }
        if (versamento == null || incassato == BigDecimal.ZERO) {
            return Incassato.No;
        }        
        if (getResiduo().signum() == 0) {
            return Incassato.Si;
        } 
        if (getResiduo().compareTo(new BigDecimal(3)) <= 0) {
            return Incassato.SiConDebito;
        }
        return Incassato.Parzialmente;
    }
    
    @Transient
    public BigDecimal getResiduo() {
        return getTotale().subtract(incassato);
    }

    public BigDecimal getIncassato() {
        return incassato;
    }

    public StatoAbbonamento getStatoAbbonamento() {
        return statoAbbonamento;
    }

    public void setStatoAbbonamento(StatoAbbonamento statoAbbonamento) {
        this.statoAbbonamento = statoAbbonamento;
    }
    
    @Transient
    public String getIntestazione() {
        return intestatario.getCaption();
    }

    @Transient
    public String getSottoIntestazione() {
        if (intestatario.getCo() == null) {
            return "";
        } 
        return "c/o" + intestatario.getCo().getCaption();
    }
    
    @Transient
    public String getIndirizzo() {
        if (intestatario.getCo() == null) {
            return intestatario.getIndirizzo();
        }
        return intestatario.getCo().getIndirizzo();            
    }

    @Transient
    public String getCap() {
        if (intestatario.getCo() == null) {
            return intestatario.getCap();
        }
        return intestatario.getCo().getCap();        
    }

    @Transient
    public String getCitta() {
        if (intestatario.getCo() == null) {
            return intestatario.getCitta();
        }
        return intestatario.getCo().getCitta();        
    }

    @Transient
    public Provincia getProvincia() {
        if (intestatario.getCo() == null) {
            return intestatario.getProvincia();
        }
        return intestatario.getCo().getProvincia();        
    }
    @Transient
    public Paese getPaese() {
        if (intestatario.getCo() == null) {
            return intestatario.getPaese();
        }
        return intestatario.getCo().getPaese();        
    }

    public BigDecimal getTotale() {
        return importo.add(spese).add(pregresso);
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
        this.dataPagamento = dataPagamento;
    }

    @Transient
    public Date getDataContabile() {
        return dataContabile;
    }

    @Transient
    public void setDataContabile(Date dataContabile) {
        this.dataContabile = dataContabile;
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
    public String getOperazione() {
        return operazione;
    }

    @Transient
    public void setOperazione(String operazione) {
        this.operazione = operazione;
    }


}