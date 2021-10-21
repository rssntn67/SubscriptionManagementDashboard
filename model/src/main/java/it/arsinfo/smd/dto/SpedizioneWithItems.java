package it.arsinfo.smd.dto;

import it.arsinfo.smd.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpedizioneWithItems {

    public static class SpedizioneWithItemsData {

        private List<SpedizioneItem> usabili = new ArrayList<>();
        private List<SpedizioneItem> inviate = new ArrayList<>();
        private Mese meseUltimaSped=null;
        private Anno annoUltimaSped=null;

        public SpedizioneWithItemsData() {}

        public List<SpedizioneItem> getUsabili() {
            return usabili;
        }

        public List<SpedizioneItem> getInviate() {
            return inviate;
        }

        public Mese getMeseUltimaSpedizione() {
            return meseUltimaSped;
        }

        public Anno getAnnoUltimaSpedizione() {
            return annoUltimaSped;
        }

        @Override
        public String toString() {
            return "SpedizioneWithItemsData{" +
                    ", usabili=" + usabili.size() +
                    ", inviate=" + inviate.size() +
                    ", meseUltimaSpedizione=" + meseUltimaSped +
                    ", annoUltimaSpedizione=" + annoUltimaSped +
                    '}';
        }
    }

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

    public static SpedizioneWithItemsData getData(List<SpedizioneWithItems> spedizioni, RivistaAbbonamento rivistaAbbonamento) {
        List<SpedizioneItem> usabili = new ArrayList<>();
        List<SpedizioneItem> inviate = new ArrayList<>();
        Mese meseUltimaSped=null;
        Anno annoUltimaSped=null;
        for (SpedizioneWithItems spedwith: spedizioni) {
            for (SpedizioneItem item : spedwith.getSpedizioneItems()) {
                if ( rivistaAbbonamento.getId().equals(item.getRivistaAbbonamento().getId())) {
                    switch (item.getStatoSpedizione()) {
                        case INVIATA:
                            inviate.add(item);
                            if (meseUltimaSped==null) {
                                meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
                                annoUltimaSped=spedwith.getSpedizione().getAnnoSpedizione();
                            } else if (annoUltimaSped.getAnno() < spedwith.getSpedizione().getAnnoSpedizione().getAnno()) {
                                meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
                                annoUltimaSped=spedwith.getSpedizione().getAnnoSpedizione();
                            } else if (annoUltimaSped.getAnno() == spedwith.getSpedizione().getAnnoSpedizione().getAnno() &&
                                    meseUltimaSped.getPosizione() < spedwith.getSpedizione().getMeseSpedizione().getPosizione()) {
                                meseUltimaSped=spedwith.getSpedizione().getMeseSpedizione();
                            }
                            break;
                       case PROGRAMMATA:
                        case SOSPESA:
                            usabili.add(item);
                            break;

                        case ANNULLATA:
                        default:
                            break;
                    }

                }
            }
        }
        SpedizioneWithItemsData data = new SpedizioneWithItemsData();
        data.meseUltimaSped=meseUltimaSped;
        data.annoUltimaSped=annoUltimaSped;
        data.inviate=inviate;
        data.usabili=usabili;
        return data;
    }

    public static boolean noSpedizioniInviateOrAnnullate(List<SpedizioneWithItems> spedizioni, RivistaAbbonamento rivistaAbbonamento) {
        for (SpedizioneWithItems spedwith: spedizioni) {
            for (SpedizioneItem item : spedwith.getSpedizioneItems()) {
                if ( rivistaAbbonamento.getId().equals(item.getRivistaAbbonamento().getId())) {
                    if (item.getStatoSpedizione() == StatoSpedizione.INVIATA || item.getStatoSpedizione() == StatoSpedizione.ANNULLATA) {
                            return false;
                    }

                }
            }
        }
        return true;
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
