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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.StatoAbbonamento;

@Entity
public class Abbonamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER)
    private Anagrafica intestatario;
    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoCorrente();

    @ManyToOne
    private Campagna campagna;
    @OneToOne
    private Versamento versamento;

    private BigDecimal importo=BigDecimal.ZERO;

    private BigDecimal spese=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;
    private String campo;
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;

    @Enumerated(EnumType.STRING)
    private StatoAbbonamento statoAbbonamento = StatoAbbonamento.Nuovo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

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
        return String.format("Abbonamento[id=%d, Incassato=%s , Intestatario='%d', Totale= '%.2f',  V Campo='%s', Anno=%s, Data='%td %tb %tY %tR %tZ', %s]",
                                   id, getIncassato(), intestatario.getId(), getTotale(),campo,anno,
                                   data, data, data, data, data, cassa);
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

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
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
        if (versamento == null) {
            return Incassato.No;
        }
        if (getResiduo().signum() == 0) {
            return Incassato.Si;
        } 
        if (getResiduo().signum() < 0) {
            return Incassato.SiConOfferta;
        }
        return Incassato.Parzialmente;
    }
    
    @Transient
    public BigDecimal getResiduo() {
        if (versamento == null) {
            return getTotale();
        }
        return getTotale().subtract(versamento.getImporto());
    }

    @Transient
    public BigDecimal getIncassato() {
        if (versamento == null) {
            return BigDecimal.ZERO;
        }
        return versamento.getImporto();
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
        return importo.add(spese);
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


}