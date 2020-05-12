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

import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.IncassoDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.helper.SmdLoadSampleData;
import it.arsinfo.smd.service.SmdService;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);

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
            RivistaAbbonamentoDao estrattoContoDao,
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
            }

        };
    }

}
