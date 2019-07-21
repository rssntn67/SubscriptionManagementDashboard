package it.arsinfo.smd;

import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;

public interface SmdService {

    void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi);
    
    void deleteCampagnaAbbonamenti(Campagna campagna);
    
    void deleteAbbonamento(Abbonamento abbonamento);
    
    void aggiornaAbbonamentoDaStorico(Storico storico);
    
    void generaAbbonamento(Abbonamento abbonamento, EstrattoConto estrattoConto);

    void aggiornaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto);
    void cancellaECAbbonamento(Abbonamento abbonamento,EstrattoConto estrattoConto);

    void generaAbbonamento(Abbonamento abbonamento, List<EstrattoConto> estrattiConto);

}
