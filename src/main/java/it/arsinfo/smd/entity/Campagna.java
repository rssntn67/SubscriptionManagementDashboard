package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Campagna {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean estratti = false;
    private boolean blocchetti = false;
    private boolean lodare = false;
    private boolean messaggio = false;

    private boolean pagato = false;
    private boolean anagraficaFlagA = false;
    private boolean anagraficaFlagB = false;
    private boolean anagraficaFlagC = false;

    @Enumerated(EnumType.STRING)
    private Anno anno;
    @Enumerated(EnumType.STRING)
    private Mese inizio = Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine = Mese.DICEMBRE;

    @OneToMany(cascade = { CascadeType.PERSIST })
    List<Abbonamento> abbonamenti = new ArrayList<Abbonamento>();

    public Campagna() {

    }

    public boolean isAnagraficaFlagC() {
        return anagraficaFlagC;
    }

    public void setAnagraficaFlagC(boolean anagraficaFlagC) {
        this.anagraficaFlagC = anagraficaFlagC;
    }

    public boolean isEstratti() {
        return estratti;
    }

    public void setEstratti(boolean estratti) {
        this.estratti = estratti;
    }

    public boolean isBlocchetti() {
        return blocchetti;
    }

    public void setBlocchetti(boolean blocchetti) {
        this.blocchetti = blocchetti;
    }

    public boolean isLodare() {
        return lodare;
    }

    public void setLodare(boolean lodare) {
        this.lodare = lodare;
    }

    public boolean isMessaggio() {
        return messaggio;
    }

    public void setMessaggio(boolean messaggio) {
        this.messaggio = messaggio;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }

    public boolean isAnagraficaFlagA() {
        return anagraficaFlagA;
    }

    public void setAnagraficaFlagA(boolean anagraficaFlagA) {
        this.anagraficaFlagA = anagraficaFlagA;
    }

    public boolean isAnagraficaFlagB() {
        return anagraficaFlagB;
    }

    public void setAnagraficaFlagB(boolean anagraficaFlagB) {
        this.anagraficaFlagB = anagraficaFlagB;
    }

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
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

    public Long getId() {
        return id;
    }

    public List<Abbonamento> getAbbonamenti() {
        return abbonamenti;
    }

    public void setAbbonamenti(List<Abbonamento> abbonamenti) {
        this.abbonamenti = abbonamenti;
    }

}
