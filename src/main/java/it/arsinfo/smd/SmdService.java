package it.arsinfo.smd;

import java.util.List;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

public interface SmdService {

    
    void deleteCampagnaAbbonamenti(Campagna campagna) throws Exception;
    
    void deleteAbbonamento(Abbonamento abbonamento) throws Exception;

    void annullaAbbonamento(Abbonamento abbonamento) throws Exception;
    void sospendiSpedizioniAbbonamento(Abbonamento abbonamento) throws Exception;
    void riattivaSpedizioniAbbonamento(Abbonamento abbonamento) throws Exception;
    void sospendiStoricoAbbonamento(Abbonamento abbonamento) throws Exception;
    void riattivaStoricoAbbonamento(Abbonamento abbonamento) throws Exception;

    void aggiornaAbbonamentoDaStorico(Storico storico) throws Exception;

    void rimuoviECDaStorico(Storico storico) throws Exception;

    void generaAbbonamento(Abbonamento abbonamento, EstrattoConto estrattoConto) throws Exception;
    void generaAbbonamento(Abbonamento abbonamento, List<EstrattoConto> estrattiConto) throws Exception;
    void aggiornaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto) throws Exception;
    void cancellaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto) throws Exception;
    
    void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi) throws Exception;
    void inviaCampagna(Campagna campagna) throws Exception;
    void inviaEstrattoConto(Campagna campagna) throws Exception;
    void chiudiCampagna(Campagna campagna) throws Exception;

    void generaStatisticheTipografia(Anno anno, Mese mese); 
    void generaStatisticheTipografia(Anno anno); 
    void inviaSpedizionere(Mese meseSpedizione, Anno annoSpedizione);
    

    List<SpedizioneItem> listItems(Mese meseSpedizione, Anno annoSpedizione, InvioSpedizione invioSpedizione);

    void incassa(Abbonamento abbonamento, Versamento versamento) throws Exception;
    void reverti(Abbonamento abbonamento, Versamento versamento) throws Exception;    
    List<Abbonamento> getAssociati(Versamento versamento);
    List<Abbonamento> getAssociabili(Versamento versamento);

    List<Spedizione> findSpedizioneByDestinatario(Anagrafica a);

    List<Spedizione> findSpedizioneAll();
    
    void delete(Storico storico);
    
    void save(Storico storico, Nota nota);

}
