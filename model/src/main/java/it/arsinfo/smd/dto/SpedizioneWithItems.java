package it.arsinfo.smd.dto;

import it.arsinfo.smd.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SpedizioneWithItems {

    private static final Logger log = LoggerFactory.getLogger(SpedizioneWithItems.class);
    private final Spedizione spedizione;
    private List<SpedizioneItem> spedizioneItems = new ArrayList<>();

    public SpedizioneWithItems(Spedizione sped) {
        this.spedizione=sped;
    }
    
    public List<SpedizioneItem> getSpedizioniPosticipate() {
        return 
            spedizioneItems.stream()
            .filter(
                    SpedizioneItem::isPosticipata
        ).collect(Collectors.toList());
    }

    public List<SpedizioneItem> getSpedizioneItems() {
        return spedizioneItems;
    }

    public void setSpedizioneItems(List<SpedizioneItem> spedizioneItems) {
        this.spedizioneItems = spedizioneItems;
    }

    public void addSpedizioneItem(SpedizioneItem item) {
        spedizioneItems.remove(item);
        spedizioneItems.add(item);
    }
    
    public void deleteSpedizioneItem(SpedizioneItem item) {
        spedizioneItems.remove(item);
    }



    public Spedizione getSpedizione() {
        return spedizione;
    }



    @Override
    public int hashCode() {
        return spedizione.hashCode();
    }

    public static Map<Integer, SpedizioneWithItems> getSpedizioneMap(List<SpedizioneWithItems> spedizioni) {
        final Map<Integer,SpedizioneWithItems> spedMap = new HashMap<>();
        for (SpedizioneWithItems spedizione:spedizioni) {
            spedMap.put(getHashCode(spedizione.getSpedizione(), spedizione.getSpedizioneItems().iterator().next().getPubblicazione()), spedizione);
        }
        return spedMap;
    }

    public static int getHashCode(Spedizione sped, Pubblicazione p) {
        if (sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
            return sped.hashCode()+p.hashCode();
        }
        return sped.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpedizioneWithItems other = (SpedizioneWithItems) obj;
        if (spedizione == null) {
            return other.spedizione == null;
        } else return spedizione.equals(other.spedizione);
    }

}
