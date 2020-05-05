package it.arsinfo.smd;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.SpedizioneItemDao;
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.SmdService;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    @Value("${load.anagrafica.adp}")
    private String loadAnagraficaAdp;

    @Value("${load.sample.data}")
    private String loadSampleData;

    @Value("${update.spesa.spedizione}")
    private String updateSpesaSpedizione;

    @Value("${split.spedizione}")
    private String splitSpedizione;

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner loadData(
            SmdService smdService,
            AnagraficaDao anagraficaDao, 
            StoricoDao storicoDao,
            NotaDao notaDao,
            PubblicazioneDao pubblicazioneDao,
            SpesaSpedizioneDao spesaSpedizioneDao,
            AbbonamentoDao abbonamentoDao,
            EstrattoContoDao estrattoContoDao,
            SpedizioneDao spedizioneDao,
            SpedizioneItemDao spedizioneItemDao,
            CampagnaDao campagnaDao, 
            IncassoDao incassoDao, 
            VersamentoDao versamentoDao,
            OperazioneDao operazioneDao, 
            UserInfoDao userInfoDao, 
            PasswordEncoder passwordEncoder) {
        return (args) -> {
            UserInfo administrator = userInfoDao.findByUsername("admin");
            if (administrator == null) {
                administrator = new UserInfo("admin", passwordEncoder.encode("admin"), Role.ADMIN);
                userInfoDao.save(administrator);
                log.info("creato user admin/admin");
            }

            UserInfo adp = userInfoDao.findByUsername("adp");
            if (adp == null) {
               adp = new UserInfo("adp", passwordEncoder.encode("adp"), Role.LOCKED);
               userInfoDao.save(adp);
               log.info("creato user adp/adp");
            }
            
            boolean loadSD = loadSampleData != null && loadSampleData.equals("true");
            log.info("loadSampleData {}",loadSampleData);
            boolean loadADP = loadAnagraficaAdp != null && loadAnagraficaAdp.equals("true");
            log.info("loadAnagraficaAdp {}",loadAnagraficaAdp);
            boolean updateSSADP = updateSpesaSpedizione != null && updateSpesaSpedizione.equals("true");
            log.info("updateSpesaSpedizione {}",updateSpesaSpedizione);
            boolean splitSped = splitSpedizione != null && splitSpedizione.equals("true");
            log.info("splitSpedizione {}",splitSped);
            
            if (loadSD ) {
                new Thread(
                   new SmdLoadSampleData(
                      smdService,                                
                      anagraficaDao, 
                      storicoDao, 
                      notaDao,
                      pubblicazioneDao, 
                      spesaSpedizioneDao,
                      abbonamentoDao, 
                      estrattoContoDao,
                      spedizioneDao,
                      spedizioneItemDao,
                      campagnaDao, 
                      incassoDao, 
                      versamentoDao, 
                      operazioneDao
                   )
                ).start();
            } else if (loadADP) {
                new Thread(
                   new SmdImportAdp(
                     smdService,                                
                     anagraficaDao, 
                     storicoDao, 
                     notaDao,
                     pubblicazioneDao, 
                     spesaSpedizioneDao,
                     abbonamentoDao, 
                     estrattoContoDao,
                     spedizioneDao,
                     spedizioneItemDao,
                     campagnaDao, 
                     incassoDao, 
                     versamentoDao, 
                     operazioneDao
                   )
                ).start();
            } else if (updateSSADP) {
            	SmdHelper.getSpeseSpedizione().forEach(ss -> {
            		SpesaSpedizione spesaSpedizione = 
            				spesaSpedizioneDao.findByAreaSpedizioneAndRangeSpeseSpedizione(ss.getAreaSpedizione(), ss.getRangeSpeseSpedizione());
            		
            		spesaSpedizione.setCor24h(ss.getCor24h());
            		spesaSpedizione.setCor3gg(ss.getCor3gg());
            		spesaSpedizioneDao.save(spesaSpedizione);
            	});
            } else if (splitSped) {
            	Set<Long> spedidswithpostitems = new HashSet<>();
            	for (SpedizioneItem spedItem: spedizioneItemDao.findAll()) {
            		if (spedItem.isPosticipata()) {
            			spedidswithpostitems.add(spedItem.getSpedizione().getId());
            			log.info("posticipata: {}",spedItem.getSpedizione());
            			log.info("posticipata: {}",spedItem);
            		}
            	}
            	Set<Long> parsed = new HashSet<>();
            	for (SpedizioneItem spedItem: spedizioneItemDao.findAll()) {
            		if (spedItem.isPosticipata()) {
                		continue;
            		}
            		if (spedidswithpostitems.contains(spedItem.getSpedizione().getId())) {
            			log.info("sped contains posticipata: {}",spedItem.getSpedizione());
            			log.info("must be splitted because sped contains posticipata: {}", spedItem);
            			continue;
            		}
            		if (parsed.contains(spedItem.getSpedizione().getId())) {
            			log.info("must be splitted because sped contains two: {}", spedItem.getSpedizione());
            			log.info("must be splitted because sped contains two: {}", spedItem);
            			continue;
            		}
            		parsed.add(spedItem.getSpedizione().getId());
            	} 
            }

        };
    }

}
