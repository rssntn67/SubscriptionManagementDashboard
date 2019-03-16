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

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Campagna implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Anno anno = SmdApplication.getAnnoCorrente();
    @Enumerated(EnumType.STRING)
    private Mese inizio = Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine = Mese.DICEMBRE;
    
    private boolean rinnovaSoloAbbonatiInRegola;

    @OneToMany(cascade = { CascadeType.PERSIST })
    List<Abbonamento> abbonamenti = new ArrayList<Abbonamento>();

    @OneToMany(cascade = { CascadeType.PERSIST })
    List<CampagnaItem> campagnaItems = new ArrayList<CampagnaItem>();

    public Campagna() {}

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

    public boolean isRinnovaSoloAbbonatiInRegola() {
        return rinnovaSoloAbbonatiInRegola;
    }

    public void setRinnovaSoloAbbonatiInRegola(
            boolean rinnovaSoloAbbonatiInRegola) {
        this.rinnovaSoloAbbonatiInRegola = rinnovaSoloAbbonatiInRegola;
    }

    public List<CampagnaItem> getCampagnaItems() {
        return campagnaItems;
    }

    public void setCampagnaItems(List<CampagnaItem> campagnaItems) {
        this.campagnaItems = campagnaItems;
    }
    
    public void addCampagnaItem(CampagnaItem campagnaItem) {
        campagnaItems.add(campagnaItem);
    }


}
