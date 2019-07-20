package it.arsinfo.smd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpedizioneItemDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;

@Service
public class SmdServiceImpl implements SmdService {
    @Autowired
    private CampagnaDao campagnaDao;

    @Autowired
    private CampagnaItemDao campagnaItemDao;

    @Autowired
    private SpesaSpedizioneDao spesaSpedizioneDao;
    
    @Autowired
    AnagraficaDao anagraficaDao;

    @Autowired
    StoricoDao storicoDao;

    @Autowired
    AbbonamentoDao abbonamentoDao;
    
    @Autowired
    EstrattoContoDao estrattoContoDao;
    
    @Autowired
    SpedizioneDao spedizioneDao;

    @Autowired
    SpedizioneItemDao spedizioneItemDao;

    @Override
    public void generaCampagnaAbbonamenti(Campagna campagna, List<Pubblicazione> attivi) {
        List<SpesaSpedizione> spese = spesaSpedizioneDao.findAll();
        List<Abbonamento> abbonamentiCampagna = Smd.generaAbbonamentiCampagna(campagna, anagraficaDao.findAll(),
                                                                              storicoDao.findAll(),
                                                                              attivi);
                                                           
        campagnaDao.save(campagna);
        campagna.getCampagnaItems().stream().forEach(ci -> campagnaItemDao.save(ci));

        for (Abbonamento abb:abbonamentiCampagna) {
            List<EstrattoConto> ecs = new ArrayList<>();
            List<Spedizione> spedizioni = new ArrayList<>();
            for (Storico storico: storicoDao.findByIntestatario(abb.getIntestatario()).stream().filter(s -> s.getCassa() == abb.getCassa()).collect(Collectors.toList())) {
               EstrattoConto ec = Smd.generaECDaStorico(abb, storico);
               spedizioni = Smd.generaSpedizioni(abb, ec, spedizioni, spese);
               ecs.add(ec);
            }
            if (ecs.isEmpty()) {
                continue;
            }

            abbonamentoDao.save(abb);
            ecs.stream().forEach( ec -> {
                estrattoContoDao.save(ec);
            });
            spedizioni.stream().forEach(sped -> {
                spedizioneDao.save(sped);
                sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
            });
        }
        
    }

    @Override
    public void deleteCampagnaAbbonamenti(Campagna campagna) {
        abbonamentoDao.findByCampagna(campagna).stream().forEach(abb -> deleteAbbonamento(abb));
        campagna.getCampagnaItems().stream().forEach(item -> campagnaItemDao.delete(item));
        campagnaDao.delete(campagna);
        
    }

    @Override
    public void deleteAbbonamento(Abbonamento abbonamento) {
        spedizioneDao
        .findByAbbonamento(abbonamento)
        .forEach(sped -> 
            {
                sped.getSpedizioneItems().forEach(item -> {
                    sped.deleteSpedizioneItem(item);
                    spedizioneItemDao.deleteById(item.getId());
                });
                spedizioneDao.deleteById(sped.getId());
            }
        );
        estrattoContoDao.findByAbbonamento(abbonamento).forEach(ec -> estrattoContoDao.deleteById(ec.getId()));
        abbonamentoDao.delete(abbonamento);
    }    
}
