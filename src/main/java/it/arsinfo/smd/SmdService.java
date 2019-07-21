package it.arsinfo.smd;

import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;

public interface SmdService {

    void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi) throws Exception;
    
    void deleteCampagnaAbbonamenti(Campagna campagna) throws Exception;
    
    void deleteAbbonamento(Abbonamento abbonamento) throws Exception;

    void annullaAbbonamento(Abbonamento abbonamento) throws Exception;

    void aggiornaAbbonamentoDaStorico(Storico storico) throws Exception;

    void rimuoviECDaStorico(Storico storico) throws Exception;

    void generaAbbonamento(Abbonamento abbonamento, EstrattoConto estrattoConto) throws Exception;

    void aggiornaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto) throws Exception;
    void cancellaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto) throws Exception;

    void generaAbbonamento(Abbonamento abbonamento, List<EstrattoConto> estrattiConto) throws Exception;

}
