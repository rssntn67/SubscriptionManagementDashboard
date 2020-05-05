package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"anno"})
        })
//create unique index campagna_idx_anno on campagna (anno);
public class Campagna implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoProssimo();

    @Enumerated(EnumType.STRING)
    private StatoCampagna statoCampagna=StatoCampagna.Generata;

    @OneToMany(mappedBy="campagna", orphanRemoval=true, fetch=FetchType.EAGER)
    List<CampagnaItem> campagnaItems = new ArrayList<CampagnaItem>();

    public Campagna() {}

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Long getId() {
        return id;
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
        return String.format("Camp.%s", anno.getAnnoAsString());
    }
    
    @Override
    public String toString() {
        return String.format("Campagna[id=%d, '%d' %s]", id,anno.getAnno(),statoCampagna);
    }

    public StatoCampagna getStatoCampagna() {
        return statoCampagna;
    }

    public void setStatoCampagna(StatoCampagna statoCampagna) {
        this.statoCampagna = statoCampagna;
    }

   @Transient
   public boolean hasPubblicazione(Pubblicazione p) {
       return 
           campagnaItems
           .stream()
           .filter(ci ->  ci.getPubblicazione().equals(p))
           .collect(Collectors.toList()).size() == 1;
   }
}
