package it.arsinfo.smd;

import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;

public interface SmdService {

    void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi);
    
    void deleteCampagnaAbbonamenti(Campagna campagna);
    
    void deleteAbbonamento(Abbonamento abbonamento);
}
