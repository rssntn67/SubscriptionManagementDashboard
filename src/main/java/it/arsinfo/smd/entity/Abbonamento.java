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
    private Anno anno = Anno.getAnnoCorrente();

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
        return String.format("Abbonamento[id=%d, %s , Int='%d', Imp. '%.2f', Spese '%.2f', , %s,'%s', Anno=%s, Data='%td %tb %tY %tR %tZ']",
                                   id, getStatoIncasso(), intestatario.getId(), 
                                   getImporto(),
                                   getSpese(),
                                   cassa,
                                   campo,
                                   anno,
                                   data, data, data, data, data);
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

    public static boolean checkCampo(String campo) {
        if (campo == null || campo.length() != 18) {
            return false;
            
        }
        
        String codice = campo.substring(0, 16);
        
        Long valorecodice = (Long.parseLong(codice) % 93);
        Integer codicecontrollo = Integer.parseInt(campo.substring(16,18));
        return codicecontrollo.intValue() == valorecodice.intValue();
    }

    /*
     * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici
     * riservati al correntista che intende utilizzare tale campo 2 caratteri
     * numerici di controcodice pari al resto della divisione dei primi 16
     * caratteri per 93 (Modulo 93. Valori possibili dei caratteri di
     * controcodice: 00 - 92)
     */
    public static String generaCodeLine(Anno anno, Anagrafica anagrafica) {
        // primi 2 caratteri anno
        String campo = anno.getAnnoAsString().substring(2, 4);
        // 3-16
        campo += anagrafica.getCodeLineBase();
        campo += String.format("%02d", Long.parseLong(campo) % 93);
        return campo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((campo == null) ? 0 : campo.hashCode());
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
        Abbonamento other = (Abbonamento) obj;
        if (campo == null) {
            if (other.campo != null)
                return false;
        } else if (!campo.equals(other.campo))
            return false;
        return true;
    }


}