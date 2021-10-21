package it.arsinfo.smd.dto;

import it.arsinfo.smd.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpedizioneWithItems {

    public static class SpedizioneWithItemsData {
        private List<SpedizioneWithItems> spedizioneWithItemsInviate = new ArrayList<>();

        public List<SpedizioneWithItems> getSpedizioneWithItemsInviate() {
            return spedizioneWithItemsInviate;
        }

        private List<SpedizioneItem> annullate = new ArrayList<>();
        private List<SpedizioneItem> usabili = new ArrayList<>();
        private List<SpedizioneItem> inviate = new ArrayList<>();
        private Mese meseInizioInv=null;
        private Anno annoInizioInv=null;
        private Mese meseFineInv=null;
        private Anno annoFineInv=null;
        private Mese meseUltimaSped=null;
        private Anno annoUltimaSped=null;

        public SpedizioneWithItemsData() {}

        public List<SpedizioneItem> getAnnullate() {
            return annullate;
        }

        public List<SpedizioneItem> getUsabili() {
            return usabili;
        }

        public List<SpedizioneItem> getInviate() {
            return inviate;
        }

        public Mese getMesePrimaPubblicazioneInviata() {
            return meseInizioInv;
        }

        public Anno getAnnoPrimaPubblicazioneInviata() {
            return annoInizioInv;
        }

        public Mese getMeseUltimaPubblicazioneInviata() {
            return meseFineInv;
        }

        public Anno getAnnoUltimaPubblicazioneInviata() {
            return annoFineInv;
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
                    "annullate=" + annullate.size() +
                    ", usabili=" + usabili.size() +
                    ", inviate=" + inviate.size() +
                    ", mesePrimaPubblicazioneInviata=" + meseInizioInv +
                    ", annoPrimaPubblicazioneInviata=" + annoInizioInv +
                    ", meseUltimaPubbicazioneInviata=" + meseFineInv +
                    ", annoUltimaIPubblicazioneInviata=" + annoFineInv +
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
        List<SpedizioneWithItems> spedizioniinviate = new ArrayList<>();
        List<SpedizioneItem> annullate = new ArrayList<>();
        List<SpedizioneItem> usabili = new ArrayList<>();
        List<SpedizioneItem> inviate = new ArrayList<>();
        Mese meseInizioInv=null;
        Anno annoInizioInv=null;
        Mese meseFineInv=null;
        Anno annoFineInv=null;
        Mese meseUltimaSped=null;
        Anno annoUltimaSped=null;
        for (SpedizioneWithItems spedwith: spedizioni) {
            for (SpedizioneItem item : spedwith.getSpedizioneItems()) {
                if ( rivistaAbbonamento.getId().equals(item.getRivistaAbbonamento().getId())) {
                    switch (item.getStatoSpedizione()) {
                        case INVIATA:
                            inviate.add(item);
                            spedizioniinviate.add(spedwith);

                            if (meseInizioInv==null) {
                                meseInizioInv=item.getMesePubblicazione();
                                annoInizioInv=item.getAnnoPubblicazione();
                            } else if (annoInizioInv.getAnno() > item.getAnnoPubblicazione().getAnno()) {
                                meseInizioInv=item.getMesePubblicazione();
                                annoInizioInv=item.getAnnoPubblicazione();
                            } else if (annoInizioInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
                                    meseInizioInv.getPosizione() > item.getMesePubblicazione().getPosizione()) {
                                meseInizioInv=item.getMesePubblicazione();
                            }
                            if (meseFineInv==null) {
                                meseFineInv=item.getMesePubblicazione();
                                annoFineInv=item.getAnnoPubblicazione();
                            } else if (annoFineInv.getAnno() < item.getAnnoPubblicazione().getAnno()) {
                                meseFineInv=item.getMesePubblicazione();
                                annoFineInv=item.getAnnoPubblicazione();
                            } else if (annoFineInv.getAnno() == item.getAnnoPubblicazione().getAnno() &&
                                    meseFineInv.getPosizione() < item.getMesePubblicazione().getPosizione()) {
                                meseFineInv=item.getMesePubblicazione();
                            }
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
                            annullate.add(item);
                            break;

                        default:
                            break;
                    }

                }
            }
        }
        SpedizioneWithItemsData data = new SpedizioneWithItemsData();
        data.annoFineInv=annoFineInv;
        data.annoInizioInv=annoInizioInv;
        data.meseFineInv=meseFineInv;
        data.meseInizioInv=meseInizioInv;
        data.meseUltimaSped=meseUltimaSped;
        data.annoUltimaSped=annoUltimaSped;
        data.annullate=annullate;
        data.inviate=inviate;
        data.usabili=usabili;
        data.spedizioneWithItemsInviate=spedizioniinviate;
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
