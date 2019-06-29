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

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.repository.VersamentoDao;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

        
    @Value("${load.sample.data}")
    private String loadSampleData;

    @Value("${load.pubblicazioni.adp}")
    private String loadPubblicazioniAdp;

    @Value("${load.sample.anagrafica}")
    private String loadSampleAnagraficaAdp;

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner loadData(
            AnagraficaDao anagraficaDao, 
            StoricoDao storicoDao,
            PubblicazioneDao pubblicazioneDao, 
            AbbonamentoDao abbonamentoDao,
            EstrattoContoDao spedizioneDao,
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

            boolean loadSD = loadSampleData != null && loadSampleData.equals("true");
            log.info("loadSampleData="+loadSD);
            boolean loadPAdp = loadPubblicazioniAdp != null && loadPubblicazioniAdp.equals("true");
            log.info("loadPubblicazioniAdp="+loadPAdp);
            boolean loadSA = loadSampleAnagraficaAdp != null && loadSampleAnagraficaAdp.equals("true");
            log.info("loadSampleAnagraficaAdp="+loadSA);
            if (loadSD || loadPAdp || loadSA) {
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
                      userInfoDao,
                      passwordEncoder,
                      loadPAdp,
                      loadSA,
                      loadSD
                      )).start();
            }
        };
    }

}
