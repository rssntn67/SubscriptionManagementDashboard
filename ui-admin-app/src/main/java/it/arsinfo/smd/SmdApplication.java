package it.arsinfo.smd;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.service.api.SmdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

@SpringBootApplication
public class SmdApplication {

    private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SmdApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

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
            RivistaDao rivistaDao,
            SpedizioneDao spedizioneDao,
            SpedizioneItemDao spedizioneItemDao,
            CampagnaDao campagnaDao, 
            DistintaVersamentoDao incassoDao,
            VersamentoDao versamentoDao,
            OperazioneDao operazioneDao, 
            UserInfoDao userInfoDao, 
            PasswordEncoder passwordEncoder) {
        return (args) -> {
            UserInfo administrator = userInfoDao.findByUsernameAndProvider("admin", UserInfo.Provider.LOCAL);
            if (administrator == null) {
                administrator = new UserInfo("admin", passwordEncoder.encode("admin000!!!"), Role.ADMIN);
                userInfoDao.save(administrator);
                log.info("creato user admin/admin");
            }

            UserInfo adp = userInfoDao.findByUsernameAndProvider("adp", UserInfo.Provider.LOCAL);
            if (adp == null) {
               adp = new UserInfo("adp", passwordEncoder.encode("adp111!!"), Role.LOCKED);
               userInfoDao.save(adp);
               log.info("creato user adp/adp");
            }
        };
    }

}
