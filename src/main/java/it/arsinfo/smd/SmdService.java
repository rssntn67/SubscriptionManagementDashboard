package it.arsinfo.smd;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpedizioneWithItems;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

public interface SmdService {

    void delete(Storico storico);
    void save(Storico storico, Nota nota);
    
    void rimuovi(Campagna campagna) throws Exception;
    void genera(Campagna campagna, List<Pubblicazione> attivi) throws Exception;
    void invia(Campagna campagna) throws Exception;
    void estratto(Campagna campagna) throws Exception;
    void chiudi(Campagna campagna) throws Exception;

    void rimuovi(Abbonamento abbonamento) throws Exception;
    void genera(Abbonamento abbonamento, EstrattoConto... estrattiConto) throws Exception;
    void aggiorna(Campagna campagna,Storico storico) throws Exception;
    void aggiorna(EstrattoConto estrattoConto) throws Exception;
    void cancella(EstrattoConto estrattoConto) throws Exception;
    
    void generaStatisticheTipografia(Anno anno, Mese mese); 
    void generaStatisticheTipografia(Anno anno); 
    void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione);
    

    List<SpedizioneItem> listItems(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invioSpedizione);
    List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb);

    List<Abbonamento> getAssociati(Versamento versamento);
    List<Abbonamento> getAssociabili(Versamento versamento);

    List<Spedizione> findSpedizioneByDestinatario(Anagrafica a);
    List<Spedizione> findSpedizioneByPubblicazione(Pubblicazione p);
    List<Spedizione> findSpedizioneAll();    
    
    void save(Incasso incasso);
    void save(Versamento versamento);
    void delete(Versamento versamento);
    
    void incassa(Abbonamento abbonamento, Versamento versamento) throws Exception;
    void reverti(Abbonamento abbonamento, Versamento versamento) throws Exception;    
    void incassa(Abbonamento abbonamento) throws Exception;
    
    
    
}
