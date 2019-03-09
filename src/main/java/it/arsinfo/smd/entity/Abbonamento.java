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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Abbonamento implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Anagrafica intestatario;

    @ManyToOne
    private Campagna campagna;
    
    @OneToMany(cascade = { CascadeType.PERSIST })
    private List<AbbonamentoPubblicazione> listaAbbonamentoPubblicazione = new ArrayList<>();
    
    @OneToOne
    private Versamento versamento;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    private BigDecimal cost;
    private BigDecimal spese = BigDecimal.ZERO;

    private boolean pagato = false;
    @Temporal(TemporalType.TIMESTAMP)
    private Date incasso;

    private String campo;

    @Enumerated(EnumType.STRING)
    private Anno anno;
    @Enumerated(EnumType.STRING)
    private Mese inizio = Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine = Mese.DICEMBRE;

    public Abbonamento() {
        this.intestatario = new Anagrafica();
        this.data = new Date();
        this.cost = BigDecimal.ZERO;
    }

    public Abbonamento(Anagrafica intestatario) {
        super();
        this.intestatario = intestatario;
        this.data = new Date();
        this.cost = BigDecimal.ZERO;
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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Abbonamento[id=%d, Anagrafica='%s', Costo='%f', Campo='%s', Data='%td %tb %tY %tR %tZ']",
                                   id, intestatario, cost,
                                   data, data, data, data, data, data);
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
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

    public Date getIncasso() {
        return incasso;
    }

    public void setIncasso(Date incasso) {
        this.incasso = incasso;
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

    public List<AbbonamentoPubblicazione> getListaAbbonamentoPubblicazione() {
        return new ArrayList<>(listaAbbonamentoPubblicazione);
    }

    public void setListaAbbonamentoPubblicazione(
            List<AbbonamentoPubblicazione> listaAbbonamentoPubblicazione) {
        this.listaAbbonamentoPubblicazione = listaAbbonamentoPubblicazione;
    }
    
    public void addPubblicazione(Pubblicazione pubblicazione, int numero) {
        listaAbbonamentoPubblicazione.add(new AbbonamentoPubblicazione(this, pubblicazione, numero));
    }

}
