package it.arsinfo.smd;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringBootApplication
public class SmdApplication {

        
    @Value("${load.sample.data}")
    private String loadSampleData;

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner loadData(AnagraficaDao anagraficaDao, StoricoDao storicoDao,
            PubblicazioneDao pubblicazioneDao, AbbonamentoDao abbonamentoDao,
            SpedizioneDao spedizioneDao,
            CampagnaDao campagnaDao, IncassoDao incassoDao, VersamentoDao versamentoDao,
            OperazioneDao operazioneDao, ProspettoDao prospettoDao) {
        return (args) -> {
            if (loadSampleData != null && loadSampleData.equals("true")) {
                
                     new Thread(new SmdLoadSampleData(
                      anagraficaDao, 
                      storicoDao, 
                      pubblicazioneDao, 
                      abbonamentoDao, 
                      spedizioneDao, 
                      campagnaDao, 
                      incassoDao, 
                      versamentoDao, 
                      operazioneDao,
                      prospettoDao)).start();
            }
            
        };
    }

}
