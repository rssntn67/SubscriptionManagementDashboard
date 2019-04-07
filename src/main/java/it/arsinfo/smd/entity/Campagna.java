package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Campagna implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoProssimo();
    @Enumerated(EnumType.STRING)
    private Mese inizio = Mese.GENNAIO;
    @Enumerated(EnumType.STRING)
    private Mese fine = Mese.DICEMBRE;
    
    private boolean rinnovaSoloAbbonatiInRegola;

    @OneToMany(cascade = { CascadeType.ALL })
    List<Abbonamento> abbonamenti = new ArrayList<Abbonamento>();

    @OneToMany(cascade = { CascadeType.ALL })
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

    public boolean deleteCampagnaItemByPubblicazione(Pubblicazione pubblicazione) {
        if (campagnaItems.size() == 0) {
            return false;
        }
        int size = campagnaItems.size();
        campagnaItems =
        campagnaItems.stream().filter(item -> item.getPubblicazione().getNome().equals(pubblicazione.getNome())).collect(Collectors.toList());
        
        return size != campagnaItems.size();
    }

    @Transient
    public String getCaption() {
        return String.format("Campagna%s", anno.getAnnoAsString());
    }
    
    @Override
    public String toString() {
        return String.format("Campagna[id=%d, anno='%s', inizio='%s', fine='%s']", id,anno, inizio,fine);
    }

}
