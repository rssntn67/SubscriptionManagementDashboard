package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpedizioneWithItems {

    private final Spedizione spedizione;
    private List<SpedizioneItem> spedizioneItems = new ArrayList<SpedizioneItem>();


    public SpedizioneWithItems(Spedizione sped) {
        this.spedizione=sped;
    }
    
    public List<SpedizioneItem> getSpedizioniPosticipate() {
        return 
            spedizioneItems.stream()
            .filter(
            item -> item.isPosticipata()
        ).collect(Collectors.toList());
    }

    public List<SpedizioneItem> getSpedizioneItems() {
        return spedizioneItems;
    }

    public void setSpedizioneItems(List<SpedizioneItem> spedizioneItems) {
        this.spedizioneItems = spedizioneItems;
    }

    public void addSpedizioneItem(SpedizioneItem item) {
        if (spedizioneItems.contains(item)) {
            spedizioneItems.remove(item);
        }
        spedizioneItems.add(item);
    }
    
    public boolean deleteSpedizioneItem(SpedizioneItem item) {
        return spedizioneItems.remove(item);
    }



    public Spedizione getSpedizione() {
        return spedizione;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((spedizione == null) ? 0 : spedizione.hashCode());
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
        SpedizioneWithItems other = (SpedizioneWithItems) obj;
        if (spedizione == null) {
            if (other.spedizione != null)
                return false;
        } else if (!spedizione.equals(other.spedizione))
            return false;
        return true;
    }

    public static Map<Integer,SpedizioneWithItems> getSpedizioneMap(List<SpedizioneWithItems> spedizioni) {
        final Map<Integer,SpedizioneWithItems> spedMap = new HashMap<>();
        for (SpedizioneWithItems spedizione:spedizioni) {
            spedMap.put(spedizione.hashCode(), spedizione);
        }
        return spedMap;        
    }


}
