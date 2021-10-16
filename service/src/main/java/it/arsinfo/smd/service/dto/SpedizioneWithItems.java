package it.arsinfo.smd.service.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.arsinfo.smd.entity.InvioSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneWithItems {

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
