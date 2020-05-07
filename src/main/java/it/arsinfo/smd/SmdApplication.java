package it.arsinfo.smd;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.CampagnaDao;
import it.arsinfo.smd.dao.EstrattoContoDao;
import it.arsinfo.smd.dao.IncassoDao;
import it.arsinfo.smd.dao.NotaDao;
import it.arsinfo.smd.dao.OperazioneDao;
import it.arsinfo.smd.dao.PubblicazioneDao;
import it.arsinfo.smd.dao.SpedizioneDao;
import it.arsinfo.smd.dao.SpedizioneItemDao;
import it.arsinfo.smd.dao.SpesaSpedizioneDao;
import it.arsinfo.smd.dao.StoricoDao;
import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.dao.VersamentoDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.helper.SmdImportAdp;
import it.arsinfo.smd.helper.SmdLoadSampleData;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.SmdService;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    @Value("${load.anagrafica.adp}")
    private String loadAnagraficaAdp;

    @Value("${load.sample.data}")
    private String loadSampleData;

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
            }

        };
    }

}
