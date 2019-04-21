package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.Mese;

@Entity
public class Abbonamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Anagrafica intestatario;
    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoCorrente();
    @Enumerated(EnumType.STRING)
    private Mese inizio = Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine = Mese.DICEMBRE;

    @ManyToOne
    private Campagna campagna;
    @ManyToOne
    private Versamento versamento;
    @OneToMany(cascade = { CascadeType.ALL }, fetch=FetchType.EAGER)
    private List<Spedizione> spedizioni = new ArrayList<>();

    private BigDecimal costo = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;
    private String campo;
    @Enumerated(EnumType.STRING)
    private Ccp ccp = Ccp.UNO;
    private BigDecimal spese = BigDecimal.ZERO;
        
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

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal cost) {
        this.costo = cost;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Abbonamento[id=%d, Incassato=%s , Intestatario='%d', Totale= '%.2f', Costo='%.2f', Spese='%.2f', Campo='%s', Anno=%s, Data='%td %tb %tY %tR %tZ', %s]",
                                   id, getIncassato(), intestatario.getId(), getTotale(),costo,spese,campo,anno,
                                   data, data, data, data, data, cassa);
    }
    
    public BigDecimal getSpese() {
        return spese;
    }

    public void setSpese(BigDecimal spese) {
        this.spese = spese;
    }

    public Mese getInizio() {
        return inizio;
    }

    public void setInizio(Mese inizio) {
        this.inizio = inizio;
    }

    public Mese getFine() {
        return fine;
    }

    public void setFine(Mese fine) {
        this.fine = fine;
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

    public List<Spedizione> getSpedizioni() {
        return new ArrayList<>(spedizioni);
    }

    public void setSpedizioni(
            List<Spedizione> listaAbbonamentoPubblicazione) {
        this.spedizioni = listaAbbonamentoPubblicazione;
    }
    
    public void addSpedizione(Spedizione spedizione) {
        if (spedizioni.contains(spedizione)) {
            spedizioni.remove(spedizione);
        }
        spedizioni.add(spedizione);
    }

    public boolean deleteSpedizione(Spedizione spedizione) {
        return spedizioni.remove(spedizione);
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
    public Incassato getIncassato() {
        if (costo.doubleValue() == BigDecimal.ZERO.doubleValue() && spese.doubleValue() == BigDecimal.ZERO.doubleValue()) {
            return Incassato.valueOf("Omaggio");
        }
        if (versamento != null) {
            return Incassato.valueOf("Si");
        }
        return Incassato.valueOf("No");
    }
    
    @Transient
    public BigDecimal getTotale() {
        return costo.add(spese);
    }
}