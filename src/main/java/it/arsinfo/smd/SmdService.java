package it.arsinfo.smd;

import java.math.BigDecimal;
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
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;

public interface SmdService {

    void delete(Storico storico);
    void save(Storico storico, Nota... note);

    void invia(Campagna campagna) throws Exception;
    void estratto(Campagna campagna) throws Exception;
    void chiudi(Campagna campagna) throws Exception;

    void genera(Campagna campagna, List<Pubblicazione> attivi) throws Exception;
    void genera(Abbonamento abbonamento, EstrattoConto... estrattiConto) throws Exception;
    void genera(Campagna campagna,Storico storico, Nota...note) throws Exception;
    
    void delete(Campagna campagna) throws Exception;
    void delete(Abbonamento abbonamento) throws Exception;
    
    void rimuovi(EstrattoConto estrattoConto) throws Exception;
    void rimuovi(Abbonamento abbonamento) throws Exception;
    void rimuovi(Campagna campagna,Storico storico,Nota...note) throws Exception;
    
    void aggiorna(EstrattoConto estrattoConto) throws Exception;    
    void aggiorna(Campagna campagna,Storico storico, Nota...note) throws Exception;
    
    void generaStatisticheTipografia(Anno anno, Mese mese); 
    void generaStatisticheTipografia(Anno anno); 
    void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione);
    

    List<SpedizioneItem> listItems(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invioSpedizione);
    List<SpedizioneWithItems> findByAbbonamento(Abbonamento abb);

    List<Abbonamento> getAssociati(Versamento versamento);
    List<Abbonamento> getAssociabili(Versamento versamento);
    List<Versamento> getAssociati(Abbonamento abbonamento);

    List<Spedizione> findSpedizioneByDestinatario(Anagrafica a);
    List<Spedizione> findSpedizioneByPubblicazione(Pubblicazione p);
    List<Spedizione> findSpedizioneAll();    
    
    void save(Incasso incasso) throws Exception;
    void save(Versamento versamento) throws Exception;
    void delete(Versamento versamento) throws Exception;
    
    void incassa(Abbonamento abbonamento, Versamento versamento, UserInfo user, String description) throws Exception;
    void dissocia(Abbonamento abbonamento, Versamento versamento,UserInfo user, String description) throws Exception;    
    
    void incassa(Abbonamento abbonamento, BigDecimal incassato,UserInfo user) throws Exception;
    void incassaCodeLine(List<Incasso> incassi,UserInfo user) throws Exception;
    
    void sospendi(Abbonamento abbonamento)throws Exception;
    void attiva(Abbonamento abbonamento)  throws Exception;
    void annulla(Abbonamento abbonamento) throws Exception;
    
    
}
