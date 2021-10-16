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

    public static void calcolaPesoESpesePostali(Abbonamento abb, Collection<SpedizioneWithItems> spedizioni, List<SpesaSpedizione> spese) {
        abb.setSpese(BigDecimal.ZERO);
        abb.setSpeseEstero(BigDecimal.ZERO);
        for (SpedizioneWithItems sped: spedizioni) {
            int pesoStimato=0;
            for (SpedizioneItem item: sped.getSpedizioneItems()) {
                pesoStimato+=item.getNumero()*item.getPubblicazione().getGrammi();
            }
            sped.getSpedizione().setPesoStimato(pesoStimato);

            sped.getSpedizione().setSpesePostali(getSpesaSpedizione(
                    spese,
                    sped.getSpedizione().getDestinatario().getAreaSpedizione(),
                    RangeSpeseSpedizione.getByPeso(pesoStimato)
            ).calcolaSpesePostali(sped.getSpedizione().getInvioSpedizione()));
            switch (sped.getSpedizione().getDestinatario().getAreaSpedizione()) {
                case Italia:
                    abb.setSpese(abb.getSpese().add(sped.getSpedizione().getSpesePostali()));
                    break;
                case EuropaBacinoMediterraneo:
                case AmericaAfricaAsia:
                    abb.setSpeseEstero(abb.getSpeseEstero().add(sped.getSpedizione().getSpesePostali()));
                    break;
                default:
                    break;
            }
        }
    }

    public static SpesaSpedizione getSpesaSpedizione(List<SpesaSpedizione> ss, AreaSpedizione area, RangeSpeseSpedizione range) throws UnsupportedOperationException {
        for (SpesaSpedizione s: ss) {
            if (s.getAreaSpedizione() == area && s.getRangeSpeseSpedizione() == range) {
                return s;
            }
        }
        throw new UnsupportedOperationException("cannot get spese di spedizione per Area: " + area.name() + ", range: " + range.name());
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
