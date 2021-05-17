package it.arsinfo.smd;

import it.arsinfo.smd.bancoposta.api.BancoPostaService;
import it.arsinfo.smd.bancoposta.impl.BancoPostaServiceImpl;
import it.arsinfo.smd.bollettino.api.BollettinoService;
import it.arsinfo.smd.config.CampagnaConfigImpl;
import it.arsinfo.smd.config.CcpConfigImpl;
import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.data.*;
import it.arsinfo.smd.service.dto.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.service.dto.SpedizioneWithItems;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.helper.SmdHelper;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.ui.security.CustomLogoutSuccessHandler;
import it.arsinfo.smd.ui.security.RedirectAuthenticationSuccessHandler;
import it.arsinfo.smd.ui.security.SecurityConfig;
import it.arsinfo.smd.ui.security.UserDetailsServiceImpl;
import it.arsinfo.smd.service.api.AbbonamentoService;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.service.dao.SmdServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SmdApplicationTests {

    @Autowired
    private SmdService smdService;
    @Autowired
    private AnagraficaDao anagraficaDao;
    @Autowired
    private StoricoDao storicoDao;
    @Autowired
    private PubblicazioneDao pubblicazioneDao;
    @Autowired
    private AbbonamentoDao abbonamentoDao;
    @Autowired
    private AbbonamentoService abbonamentoServiceDao;
    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;
    @Autowired
    private CampagnaDao campagnaDao;
    @Autowired
    private CampagnaItemDao campagnaItemDao;
    @Autowired
    private DistintaVersamentoDao incassoDao;
    @Autowired
    private VersamentoDao versamentoDao;
    @Autowired
    private OperazioneDao operazioneDao;
    @Autowired
    private OperazioneIncassoDao operazioneIncassoDao;
    @Autowired
    private OperazioneSospendiDao operazioneSospendiDao;
    @Autowired
    private NotaDao notaDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private SpedizioneDao spedizioneDao;
    @Autowired
    private SpedizioneItemDao spedizioneItemDao;
    @Autowired
    private SpesaSpedizioneDao spesaSpedizioneDao;
    @Autowired
    private OfferteCumulateDao offerteCumulateDao;
    @Autowired
    private OffertaDao offertaDao;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private CcpConfigImpl ccpConfig;
    @Autowired
    private CampagnaConfigImpl campagnaConfig;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired 
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private BollettinoService bollettinoService;

    @Autowired
    private BancoPostaService bancoPostaService;

    private static final Logger log = LoggerFactory.getLogger(SmdApplicationTests.class);

    @BeforeEach
    public void onSetUp() {
        assertThat(abbonamentoDao).isNotNull();
        assertThat(anagraficaDao).isNotNull();
        assertThat(pubblicazioneDao).isNotNull();
        assertThat(rivistaAbbonamentoDao).isNotNull();
        assertThat(storicoDao).isNotNull();
        assertThat(campagnaDao).isNotNull();
        assertThat(campagnaItemDao).isNotNull();
        assertThat(notaDao).isNotNull();
        assertThat(storicoDao).isNotNull();
        assertThat(versamentoDao).isNotNull();
        assertThat(incassoDao).isNotNull();
        assertThat(operazioneDao).isNotNull();
        assertThat(operazioneIncassoDao).isNotNull();
        assertThat(operazioneSospendiDao).isNotNull();
        assertThat(userInfoDao).isNotNull();
        assertThat(spedizioneDao).isNotNull();
        assertThat(spedizioneItemDao).isNotNull();
        assertThat(spesaSpedizioneDao).isNotNull();
        assertThat(offerteCumulateDao).isNotNull();
        assertThat(offertaDao).isNotNull();

        assertThat(smdService).isNotNull();
        assertThat(smdService instanceof SmdServiceImpl).isTrue();

        assertThat(securityConfig).isNotNull();
        assertThat(userDetailsService).isNotNull();
        assertThat(userDetailsService instanceof UserDetailsServiceImpl).isTrue();
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder instanceof BCryptPasswordEncoder).isTrue();
        assertThat(authenticationSuccessHandler).isNotNull();
        assertThat(authenticationSuccessHandler instanceof RedirectAuthenticationSuccessHandler).isTrue();        
        assertThat(logoutSuccessHandler).isNotNull();
        assertThat(logoutSuccessHandler instanceof CustomLogoutSuccessHandler).isTrue();

        assertNotNull(bancoPostaService);
        assertNotNull(bollettinoService);

        log.info("----------------->EnteringSetUp<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        assertEquals(0, campagnaDao.findAll().size());
        assertEquals(0, campagnaItemDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, rivistaAbbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, operazioneDao.findAll().size());
        assertEquals(0, operazioneIncassoDao.findAll().size());
        assertEquals(0, operazioneSospendiDao.findAll().size());
        assertEquals(2, userInfoDao.findAll().size());
        for (SpesaSpedizione ss : SmdHelper.getSpeseSpedizione()) {
            spesaSpedizioneDao.save(ss);
        }
        assertEquals(5,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo).size());
        assertEquals(5,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.AmericaAfricaAsia).size());
        assertEquals(10,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.Italia).size());
        assertEquals(20,spesaSpedizioneDao.findAll().size());
        
        assertEquals(0, pubblicazioneDao.findAll().size());

        Pubblicazione m = SmdHelper.getMessaggio();
        pubblicazioneDao.save(m);
        
        Pubblicazione l = SmdHelper.getLodare();
        pubblicazioneDao.save(l);
        
        Pubblicazione b =SmdHelper.getBlocchetti();
        pubblicazioneDao.save(b);
        
        Pubblicazione e = SmdHelper.getEstratti();
        pubblicazioneDao.save(e);
        assertEquals(4, pubblicazioneDao.findAll().size());
        
    }
    
    @AfterEach
    public void onTearDown() {
        try {
	        operazioneDao.deleteAll();
	        spedizioneItemDao.deleteAll();
	        spedizioneDao.deleteAll();
	        rivistaAbbonamentoDao.deleteAll();
	        operazioneIncassoDao.deleteAll();
	        operazioneSospendiDao.deleteAll();
	        abbonamentoDao.deleteAll();
	        campagnaItemDao.deleteAll();
	        campagnaDao.deleteAll();
	        notaDao.deleteAll();
	        storicoDao.deleteAll();
	        versamentoDao.deleteAll();
	        anagraficaDao.deleteAll();
	        incassoDao.deleteAll();  
	        pubblicazioneDao.deleteAll();
	        spesaSpedizioneDao.deleteAll();
        } catch (Exception e) {
            log.error(e.getMessage(),e);            
        }
        log.info("----------------->ExitingClearDown<----------------");
    }

    @Test
    public void testConfiguration() {
        assertNotNull(ccpConfig);
        assertNotNull(campagnaConfig);
        assertEquals(7.00,campagnaConfig.getLimiteInvioEstratto().doubleValue());
        assertEquals(7.00,campagnaConfig.getLimiteInvioSollecito().doubleValue());
        assertEquals("https://api.stampabollettini.com/api/td674",ccpConfig.getCcpApiUrl());
        assertEquals("druslcruwaw2up5swexospl6awruphut",ccpConfig.getCcpApiKey());
        assertEquals("adp-289020",ccpConfig.getCcpApiUser());
        assertEquals("/Users/antonio/Downloads",ccpConfig.getCcpFilePath());
    }

    @Test
    public void testLoginAdmin() {
        log.info("----------------->testLoginAdmin<----------------");
        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin", "admin");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            assertThat(false).isTrue();
        }

        UserInfo admin = userInfoDao.findById(1L).orElse(null);
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());

    }

    @Test
    public void testUserInfo() {
        log.info("----------------->testUserInfo<----------------");
                
        UserInfo user = new UserInfo("user", passwordEncoder.encode("pass"), Role.USER);
        userInfoDao.save(user);
        
        assertEquals(3, userInfoDao.findAll().size());
        Authentication auth =
                new UsernamePasswordAuthenticationToken("user", "pass");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertThat(false).isTrue();
        }
                
        userInfoDao.delete(user);
        assertEquals(2, userInfoDao.findAll().size());        
    }        
    
    @Test
    public void testOperazioneSospendiCRUD() {
        log.info("----------------->testOperazioneSospendiCRUD<----------------");
        Pubblicazione estratti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Estratti").iterator().next();
        assertNotNull(estratti);
        Campagna campagna = new Campagna();
        campagnaDao.save(campagna);
        OperazioneSospendi sospendiEstratti = new OperazioneSospendi(estratti, campagna);
        operazioneSospendiDao.save(sospendiEstratti);
        assertEquals(1, operazioneSospendiDao.findAll().size());
                
        operazioneSospendiDao.findAll().forEach( a -> log.info(a.toString()));
        
        OperazioneSospendi sospeso = operazioneSospendiDao.findUniqueByCampagnaAndPubblicazione(campagna, estratti);
        assertNotNull(sospeso);
        
        OperazioneSospendi sospendiEstratti2 = new OperazioneSospendi(estratti, campagna);
        sospendiEstratti2.setMeseSpedizione(Mese.APRILE);
        
        try {
        	operazioneSospendiDao.save(sospendiEstratti2);
            assertThat(false).isTrue();
        } catch (Exception e) {
            log.info("Fail saving duplicate key");        	
		}                
    }


    @Test
    public void testAnagraficaCRUD() {
        log.info("----------------->testAnagraficaCRUD<----------------");
        Anagrafica antonioRusso =  SmdHelper.getAR();
        anagraficaDao.save(antonioRusso);
        assertEquals(1, anagraficaDao.findAll().size());
        
        assertThat(anagraficaDao.findById(antonioRusso.getId())).isNotNull();
        assertEquals(1,anagraficaDao.findByDenominazioneContainingIgnoreCase("us").size());
        assertEquals(0,anagraficaDao.findByDenominazioneContainingIgnoreCase("Rosso").size());
        assertEquals(1,anagraficaDao.findByDiocesi(Diocesi.DIOCESI116).size());
        assertEquals(0,anagraficaDao.findByDiocesi(Diocesi.DIOCESI115).size());
        
        Anagrafica diocesiMilano = SmdHelper.getDiocesiMi();
        anagraficaDao.save(diocesiMilano);
        assertEquals(2, anagraficaDao.findAll().size());
        
        assertEquals(1,anagraficaDao.findByDenominazioneContainingIgnoreCase("ar").size());
        assertEquals(1,anagraficaDao.findByDenominazioneContainingIgnoreCase("mi").size());
        assertEquals(2,anagraficaDao.findByDiocesi(Diocesi.DIOCESI116).size());
        assertEquals(0,anagraficaDao.findByDiocesi(Diocesi.DIOCESI115).size());
        
        antonioRusso.setCo(diocesiMilano);
        anagraficaDao.save(antonioRusso);
        
        assertEquals(0,anagraficaDao.findByCo(antonioRusso).size());
        assertEquals(1,anagraficaDao.findByCo(diocesiMilano).size());
        
        try {
            anagraficaDao.delete(diocesiMilano);
            assertThat(false).isTrue();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        
        anagraficaDao.findAll().forEach( a -> log.info(a.toString()));
        anagraficaDao.delete(antonioRusso);
        anagraficaDao.delete(diocesiMilano);
        
        assertEquals(0, anagraficaDao.findAll().size());        
    }
    
    @Test
    public void testAnagraficaCo() {
        log.info("----------------->testAnagraficaCo<----------------");
        Anagrafica diocesiMilano = SmdHelper.getDiocesiMi();
        anagraficaDao.save(diocesiMilano);
        assertEquals(1, anagraficaDao.findAll().size());
        
        Anagrafica ar = SmdHelper.getAR();
        ar.setCo(diocesiMilano);
        anagraficaDao.save(ar);
        assertEquals(2, anagraficaDao.findAll().size());
        
        List<Anagrafica> withco = anagraficaDao.findAll()
                .stream()
                .filter(a -> a.getCo() != null)
                .collect(Collectors.toList());

        assertEquals(1, withco.size());
        Anagrafica ff = withco.iterator().next();
        assertEquals(ar.getId().longValue(), ff.getId().longValue());
        
        try {
            anagraficaDao.delete(diocesiMilano);
            assertThat(false).isTrue();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        assertEquals(2, anagraficaDao.findAll().size());       
        anagraficaDao.delete(ar);
        assertEquals(1, anagraficaDao.findAll().size());       
        anagraficaDao.delete(diocesiMilano);
        assertEquals(0, anagraficaDao.findAll().size());       
    }

    @Test
    public void testSpesaSpedizioneCRUD() {
        log.info("----------------->testSpesaSpedizioneCRUD<----------------");
        SpesaSpedizione spedizioneItaliaDa1a2Kg=spesaSpedizioneDao.findByAreaSpedizioneAndRangeSpeseSpedizione(AreaSpedizione.Italia, RangeSpeseSpedizione.Da1KgA2Kg);
        assertThat(spedizioneItaliaDa1a2Kg).isNotNull();
        
        SpesaSpedizione duplicato=new SpesaSpedizione();
        duplicato.setAreaSpedizione(AreaSpedizione.Italia);
        duplicato.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);
        duplicato.setSpese(new BigDecimal("10.88"));
        
        try {
            spesaSpedizioneDao.save(duplicato);
            assertThat(false).isTrue();
        } catch (DataIntegrityViolationException e) {
            log.info(e.getMessage());
        }
        
        spesaSpedizioneDao.delete(spedizioneItaliaDa1a2Kg);
        spedizioneItaliaDa1a2Kg=spesaSpedizioneDao.findByAreaSpedizioneAndRangeSpeseSpedizione(AreaSpedizione.Italia, RangeSpeseSpedizione.Da1KgA2Kg);
        assertThat(spedizioneItaliaDa1a2Kg).isNull();
        assertEquals(19, spesaSpedizioneDao.findAll().size());
        spesaSpedizioneDao.save(duplicato);
        assertEquals(20, spesaSpedizioneDao.findAll().size());
    }

    @Test 
    public void testPubblicazioneDaoCRUD() {
        log.info("----------------->testPubblicazioneDaoCRUD<----------------");
        Pubblicazione p = new Pubblicazione("prova", TipoPubblicazione.MENSILE);
        p.setAbbonamento(new BigDecimal("30.00"));
        p.setAbbonamentoConSconto(new BigDecimal("20.00"));
        p.setAbbonamentoSostenitore(new BigDecimal("100.00"));
        p.setAbbonamentoWeb(new BigDecimal("10.00"));
        p.setCostoUnitario(new BigDecimal("2.50"));
        p.setFeb(true);
        p.setApr(true);
        p.setSet(true);
        p.setNov(true);
        pubblicazioneDao.save(p);

        assertEquals(5, pubblicazioneDao.count());
        assertEquals(5, pubblicazioneDao.findAll().size());

        Pubblicazione p1 = new Pubblicazione("zz", TipoPubblicazione.MENSILE);
        p1.setAbbonamento(new BigDecimal("30.00"));
        p1.setAbbonamentoConSconto(new BigDecimal("20.00"));
        p1.setAbbonamentoSostenitore(new BigDecimal("120.00"));
        p1.setAbbonamentoWeb(new BigDecimal("10.00"));
        p1.setCostoUnitario(new BigDecimal("3.00"));
        p1.setFeb(true);
        p1.setApr(true);
        p1.setSet(true);
        p1.setNov(true);
        pubblicazioneDao.save(p1);
        
        assertEquals(6, pubblicazioneDao.findAll().size());
        anagraficaDao.findAll().forEach( msg -> log.info(msg.toString()));

        List<Pubblicazione> ff = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Pr");
        assertEquals(1, ff.size());
        Pubblicazione ffp = ff.iterator().next();
        assertEquals(p.getId(), ffp.getId());
        assertEquals("prova", ffp.getNome());
        log.info(ffp.toString());
        
        assertEquals(4, pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE).size());
        assertEquals(1, pubblicazioneDao.findByTipo(TipoPubblicazione.SEMESTRALE).size());
        assertEquals(1, pubblicazioneDao.findByTipo(TipoPubblicazione.ANNUALE).size());
        assertEquals(0, pubblicazioneDao.findByTipo(TipoPubblicazione.UNICO).size());

        assertEquals(2, pubblicazioneDao.findByTipoNot(TipoPubblicazione.MENSILE).size());
        assertEquals(5, pubblicazioneDao.findByTipoNot(TipoPubblicazione.SEMESTRALE).size());
        assertEquals(5, pubblicazioneDao.findByTipoNot(TipoPubblicazione.ANNUALE).size());
        assertEquals(6, pubblicazioneDao.findByTipoNot(TipoPubblicazione.UNICO).size());

        assertEquals(4, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.MENSILE,true).size());
        assertEquals(1, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.SEMESTRALE,true).size());
        assertEquals(1, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.ANNUALE,true).size());
        assertEquals(0, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.UNICO,true).size());
        
        assertEquals(0, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.MENSILE,false).size());
        assertEquals(0, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.SEMESTRALE,false).size());
        assertEquals(0, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.ANNUALE,false).size());
        assertEquals(0, pubblicazioneDao.findByTipoAndActive(TipoPubblicazione.UNICO,false).size());

        pubblicazioneDao.delete(p1);
        assertEquals(5, pubblicazioneDao.findAll().size());
        pubblicazioneDao.delete(p);        
        assertEquals(4, pubblicazioneDao.findAll().size());
        assertEquals(4, pubblicazioneDao.findByTipoNotAndActive(TipoPubblicazione.UNICO,true).size());
    }

    @Test 
    public void testPubblicazioniAdp() {
        log.info("----------------->testPubblicazioniAdp<----------------");
        log.info("Pubblicazioni found with findAll():");
        log.info("-------------------------------");
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        assertEquals(4, pubblicazioni.size());
        Map<String, Long> nameToIdMap = new HashMap<>();
        for (Pubblicazione pubblicazione : pubblicazioni) {
            assertThat(pubblicazione.getId()).isNotNull();
            nameToIdMap.put(pubblicazione.getNome(), pubblicazione.getId());
            assertEquals("AAVV", pubblicazione.getAutore());
            assertEquals("ADP", pubblicazione.getEditore());
            assertEquals(Anno.getAnnoCorrente(), pubblicazione.getAnno());
            assertTrue(pubblicazione.isActive());
            log.info(pubblicazione.toString());
        }
        log.info("");

        log.info("Pubblicazione found with findByNameStartsWithIgnoreCase('Estratti'):");
        pubblicazioni = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Estratti");
        assertEquals(1, pubblicazioni.size());
        Pubblicazione estratti = pubblicazioni.iterator().next();
        log.info("--------------------------------------------");
        log.info(estratti.toString());
        assertEquals(nameToIdMap.get(estratti.getNome()).longValue(), estratti.getId().longValue());
        assertEquals(TipoPubblicazione.ANNUALE, estratti.getTipo());
        EnumSet<Mese> pubs = estratti.getMesiPubblicazione();
        assertEquals(1, pubs.size());
        assertEquals(Mese.GENNAIO, pubs.iterator().next());
        assertTrue(estratti.isGen());
        assertFalse(estratti.isFeb());
        assertFalse(estratti.isMar());
        assertFalse(estratti.isApr());
        assertFalse(estratti.isMag());
        assertFalse(estratti.isGiu());
        assertFalse(estratti.isLug());
        assertFalse(estratti.isAgo());
        assertFalse(estratti.isSet());
        assertFalse(estratti.isOtt());
        assertFalse(estratti.isNov());
        assertFalse(estratti.isDic());
        
        log.info("Pubblicazione found with findByTipo('MENSILE'):");
        log.info("--------------------------------------------");
        pubblicazioni = pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE);
        assertEquals(2, pubblicazioni.size());
        for (Pubblicazione mensile : pubblicazioni) {
            assertEquals(TipoPubblicazione.MENSILE, mensile.getTipo());
            log.info(mensile.toString());
        }
        log.info("");

        assertTrue(nameToIdMap.containsKey("Messaggio"));
        assertNotNull(nameToIdMap.get("Messaggio"));
        Pubblicazione messaggio = pubblicazioneDao.findById(nameToIdMap.get("Messaggio")).orElse(null);
        assertNotNull(messaggio);
        log.info("Messaggio found with findOne: "+nameToIdMap.get("Messaggio"));
        log.info("--------------------------------");
        assertEquals(nameToIdMap.get("Messaggio").longValue(), messaggio.getId().longValue());
        assertEquals(TipoPubblicazione.MENSILE, messaggio.getTipo());
        assertEquals("Messaggio", messaggio.getNome());
        assertTrue(messaggio.isGen());
        assertTrue(messaggio.isFeb());
        assertTrue(messaggio.isMar());
        assertTrue(messaggio.isApr());
        assertTrue(messaggio.isMag());
        assertTrue(messaggio.isGiu());
        assertTrue(messaggio.isLug());
        assertFalse(messaggio.isAgo());
        assertTrue(messaggio.isSet());
        assertTrue(messaggio.isOtt());
        assertTrue(messaggio.isNov());
        assertTrue(messaggio.isDic());
        log.info(messaggio.toString());

        Pubblicazione lodare = pubblicazioneDao.findById(nameToIdMap.get("Lodare")).orElse(null);
        assertNotNull(lodare);
        log.info("lodare found with findOne: " + nameToIdMap.get("Lodare"));
        log.info("--------------------------------");
        assertEquals(nameToIdMap.get("Lodare").longValue(), lodare.getId().longValue());

        log.info(lodare.toString());
        log.info("");

        Pubblicazione blocchetti = pubblicazioneDao.findById(nameToIdMap.get("Blocchetti")).orElse(null);
        assertNotNull(blocchetti);
        log.info("blocchetti found with findOne:" + nameToIdMap.get("Blocchetti"));
        log.info("--------------------------------");
        assertEquals(nameToIdMap.get("Blocchetti").longValue(), blocchetti.getId().longValue());
        assertEquals(TipoPubblicazione.SEMESTRALE, blocchetti.getTipo());
        assertEquals("Blocchetti", blocchetti.getNome());
        assertTrue(blocchetti.isGen());
        assertFalse(blocchetti.isFeb());
        assertFalse(blocchetti.isMar());
        assertFalse(blocchetti.isApr());
        assertFalse(blocchetti.isMag());
        assertFalse(blocchetti.isGiu());
        assertTrue(blocchetti.isLug());
        assertFalse(blocchetti.isAgo());
        assertFalse(blocchetti.isSet());
        assertFalse(blocchetti.isOtt());
        assertFalse(blocchetti.isNov());
        assertFalse(blocchetti.isDic());
        log.info(blocchetti.toString());
        log.info("");        
    }
    
    @Test 
    public void testStoricoCRUD() {
        Storico storico0 = new Storico();
        
        try {
            storicoDao.save(storico0);
            fail();
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica tizio = SmdHelper.getAR();
        anagraficaDao.save(tizio);
        
        storico0.setDestinatario(tizio);

        try {
            storicoDao.save(storico0);
            fail();
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica matteoParo = SmdHelper.getMP();
        anagraficaDao.save(matteoParo);
        
        storico0.setIntestatario(matteoParo);

        try {
            storicoDao.save(storico0);
            fail();
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Messaggio").iterator().next();
        
        storico0.setPubblicazione(messaggio);
        storicoDao.save(storico0);
        assertEquals(1, storicoDao.findAll().size());
        
        //seems not updating id from...
        storico0 = storicoDao.findAll().iterator().next();

        Pubblicazione lodare = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Lodare").iterator().next();

        Storico storico1 = new Storico();
        storico1.setIntestatario(matteoParo);
        storico1.setDestinatario(matteoParo);
        storico1.setPubblicazione(lodare);
        storico1.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioGesuiti);
        storico1.setInvioSpedizione(InvioSpedizione.AdpSede);
        storico1.setNumero(10);
        storicoDao.save(storico1);
        assertEquals(2, storicoDao.findAll().size());
        assertEquals(0, notaDao.findAll().size());
        

        Nota nota = new Nota();
        nota.setStorico(storico0);
        nota.setDescription("Test Nota");
        notaDao.save(nota);
        assertEquals(1, notaDao.findAll().size());
        
        Nota nota1 = new Nota();
        nota1.setStorico(storico1);
        nota1.setDescription("Storico");
        notaDao.save(nota1);
        assertEquals(2, notaDao.findAll().size());
        
        storico0.setStatoStorico(StatoStorico.Sospeso);
        storicoDao.save(storico0);
        Nota nota2 = new Nota();
        nota2.setStorico(storico0);
        nota2.setDescription("Aggiornato Stato a SOSPESO data......");
        notaDao.save(nota2);
        assertEquals(3, notaDao.findAll().size());
        assertEquals(2, storicoDao.findAll().size());

        assertEquals(0, storicoDao.findByStatoStorico(StatoStorico.Valido).size());
        assertEquals(1, storicoDao.findByStatoStorico(StatoStorico.Nuovo).size());
        assertEquals(1, storicoDao.findByStatoStorico(StatoStorico.Sospeso).size());
        
        assertEquals(2, storicoDao.findByContrassegno(false).size());
        assertEquals(0, storicoDao.findByContrassegno(true).size());
        
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.AdpSede).size());
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.Spedizioniere).size());

        assertEquals(1, storicoDao.findByTipoAbbonamentoRivista(TipoAbbonamentoRivista.Ordinario).size());
        assertEquals(1, storicoDao.findByTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioGesuiti).size());
        assertEquals(0, storicoDao.findByTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioDirettoreAdp).size());

        assertEquals(1, storicoDao.findByDestinatario(tizio).size());
        assertEquals(1, storicoDao.findByDestinatario(matteoParo).size());

        assertEquals(2, storicoDao.findByIntestatario(matteoParo).size());
        assertEquals(0, storicoDao.findByIntestatario(tizio).size());

        assertEquals(1, storicoDao.findByPubblicazione(lodare).size());
        assertEquals(1, storicoDao.findByPubblicazione(messaggio).size());
        
        assertEquals(1, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, tizio, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, tizio, lodare).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, matteoParo, messaggio).size());
        assertEquals(1, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, matteoParo, lodare).size());

        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(tizio, matteoParo, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(tizio, matteoParo, lodare).size());

        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(tizio, tizio, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(tizio, tizio, lodare).size());
        
        assertEquals(1, notaDao.findByStorico(storico1).size());
        assertEquals(2, notaDao.findByStorico(storico0).size());
        
        assertEquals(1, notaDao.findByDescriptionContainingIgnoreCase("sospeso").size());

        storicoDao.findAll().forEach(msg -> log.info(msg.toString()));
        notaDao.findAll().forEach(msg -> log.info(msg.toString()));

        notaDao.delete(nota2);
        assertEquals(2, notaDao.findAll().size());
        assertEquals(2, storicoDao.findAll().size());
        
        notaDao.findByStorico(storico0).forEach(anota -> notaDao.deleteById(anota.getId()));
        storicoDao.delete(storico0);
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());
        
        storico1 = storicoDao.findAll().iterator().next();
        notaDao.findByStorico(storico1).forEach(bnota -> notaDao.deleteById(bnota.getId()));
        storicoDao.delete(storico1);
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size()); 
    }
    
    @Test
    public void testSmdLoadStorico() {
        log.info("----------------->testSmdLoadStorico<----------------");
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anagrafica matteo = SmdHelper.getMS();
        anagraficaDao.save(matteo);
        
        Storico storico = SmdHelper.getStoricoBy(matteo, matteo, blocchetti, 100, false, TipoAbbonamentoRivista.Sostenitore, InvioSpedizione.AdpSede);
        storicoDao.save(storico);
        notaDao.save(SmdHelper.getNota(storico));
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());                        
    }
    
    @Test
    public void testAbbonamentoCRUD() {
        log.info("----------------->testAbbonamentoCRUD<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        abb.setPregresso(new BigDecimal("10.0"));
        abb.setImporto(new BigDecimal("15.0"));
        
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        abbonamentoDao.delete(abb);
        anagraficaDao.delete(tizio);
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
    }
    
    @Test
    public void testRivistaAbbonamentoCRUD() {
        log.info("----------------->testRivistaAbbonamentoCRUD<----------------");
        RivistaAbbonamento ec = new RivistaAbbonamento();

        try {
            rivistaAbbonamentoDao.save(ec);
            fail();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
                
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        abb.setPregresso(new BigDecimal("10.0"));
        abb.setImporto(new BigDecimal("15.0"));
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        ec.setAbbonamento(abb);

        try {
            rivistaAbbonamentoDao.save(ec);
            fail();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Pubblicazione lodare =pubblicazioneDao.findByNomeStartsWithIgnoreCase("lodare").iterator().next();
        ec.setPubblicazione(lodare);
        ec.setDestinatario(tizio);
        rivistaAbbonamentoDao.save(ec);
        
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());

        log.info(ec.toString());

        //Need to retrieve a fresh copy from database
        ec = rivistaAbbonamentoDao.findAll().iterator().next();
        rivistaAbbonamentoDao.delete(ec);
        assertEquals(0, rivistaAbbonamentoDao.findAll().size());
    }

    @Test
    public void testSpedizioneCRUD() {
        log.info("----------------->testSpedizioneCRUD<----------------");
        Spedizione sped = new Spedizione();
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        abb.setPregresso(new BigDecimal("10.0"));
        abb.setImporto(new BigDecimal("15.0"));
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        
        sped.setAbbonamento(abb);
        sped.setDestinatario(tizio);

        spedizioneDao.save(sped);
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, spedizioneDao.findAll().size());

        log.info(sped.toString());

        //Need to retrieve a fresh copy from database
        sped = spedizioneDao.findAll().iterator().next();
        spedizioneDao.delete(sped);
        assertEquals(0, spedizioneDao.findAll().size());
    }

    @Test
    public void testSpedizioneItemCRUD() {
        log.info("----------------->testSpedizioneItemCRUD<----------------");

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Pubblicazione lodare =pubblicazioneDao.findByNomeStartsWithIgnoreCase("lodare").iterator().next();

        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        abb.setPregresso(new BigDecimal("10.0"));
        abb.setImporto(new BigDecimal("15.0"));
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        Spedizione sped = new Spedizione();
        sped.setAbbonamento(abb);
        sped.setDestinatario(tizio);

        spedizioneDao.save(sped);
        assertEquals(1, spedizioneDao.findAll().size());

        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(lodare);
        ec.setDestinatario(tizio);
        rivistaAbbonamentoDao.save(ec);
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());

        SpedizioneItem item = new SpedizioneItem();
        item.setRivistaAbbonamento(ec);
        item.setPubblicazione(ec.getPubblicazione());
        item.setSpedizione(sped);
        SpedizioneWithItems spwi = new SpedizioneWithItems(sped);
        spwi.addSpedizioneItem(item);

        spedizioneItemDao.save(item);
        assertEquals(1, spedizioneItemDao.findAll().size());
 
        sped = spedizioneDao.findAll().iterator().next();
        log.info(sped.toString());
        assertEquals(1, spwi.getSpedizioneItems().size());
        for (SpedizioneItem it: spwi.getSpedizioneItems()) {
            log.info(it.toString());
        }
 
        item = spedizioneItemDao.findAll().iterator().next();
        spedizioneItemDao.delete(item);
        assertEquals(0, spedizioneItemDao.count());
    }

    
    @Test 
    public void testAbbonamentoLoad() {
        log.info("----------------->testAbbonamentoLoad<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();

        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);

        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(messaggio);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setAnnoInizio(anno);
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(anno);
        ec.setDestinatario(tizio);
        List<SpedizioneWithItems> spedizioni 
            = Smd.genera(abb,
                               ec,
                               new ArrayList<>(),
                               spesaSpedizioneDao.findByAreaSpedizione(tizio.getAreaSpedizione()));
        
        assertTrue(Smd.isAbbonamentoAnnuale(ec));
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec).forEach(items::add));
        assertEquals(messaggio.getMesiPubblicazione().size(), items.size());
        EnumSet<Mese> mesi = EnumSet.noneOf(Mese.class);
        for (SpedizioneItem item: items) {
            mesi.add(item.getMesePubblicazione());
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec.getNumero(), item.getNumero());
            assertEquals(ec, item.getRivistaAbbonamento());
        }
        assertEquals(mesi, messaggio.getMesiPubblicazione());
        assertEquals(messaggio.getAbbonamento().doubleValue()*ec.getNumero(), abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec);
        spedizioni.forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findByAbbonamento(abb).size());
        assertEquals(items.size(), spedizioneDao.findAll().size());
        assertEquals(items.size(), spedizioneItemDao.findByRivistaAbbonamento(ec).size());

        abbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        rivistaAbbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().forEach(msg -> log.info(msg.toString()));
    }
    
    @Test 
    public void testAbbonamentoFindByResiduo() {
        log.info("----------------->testAbbonamentoFindByResiduo<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();

        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);

        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(messaggio);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setAnnoInizio(anno);
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(anno);
        ec.setDestinatario(tizio);
        List<SpedizioneWithItems> spedizioni 
            = Smd.genera(abb, 
                               ec,
                               new ArrayList<>(),
                               spesaSpedizioneDao.findByAreaSpedizione(tizio.getAreaSpedizione()));
        
        assertTrue(Smd.isAbbonamentoAnnuale(ec));
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec).forEach(items::add));
        assertEquals(messaggio.getMesiPubblicazione().size(), items.size());
        EnumSet<Mese> mesi = EnumSet.noneOf(Mese.class);
        for (SpedizioneItem item: items) {
            mesi.add(item.getMesePubblicazione());
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec.getNumero(), item.getNumero());
            assertEquals(ec, item.getRivistaAbbonamento());
        }
        assertEquals(mesi, messaggio.getMesiPubblicazione());
        assertEquals(messaggio.getAbbonamento().doubleValue()*ec.getNumero(), abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertFalse(abb.isContrassegno());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, abbonamentoDao.findWithResiduoAndAnno(abb.getAnno()).size());                
        abb.setContrassegno(true);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, abbonamentoDao.findWithResiduoAndAnno(abb.getAnno()).size());                
        
        abb.setIncassato(abb.getImporto());
        assertEquals(0, abb.getResiduo().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(0, abbonamentoDao.findWithResiduoAndAnno(abb.getAnno()).size());                
        
    }

    private RivistaAbbonamento checkAbbonamento(Anagrafica tizio,String codeline,Pubblicazione blocchetti, int numero, TipoAbbonamentoRivista tipoEC,InvioSpedizione invioSpedizioneEc, InvioSpedizione invioSpedizioneSped) {

    	assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());
        assertEquals(2, spedizioneDao.findAll().size());
        assertEquals(2, spedizioneItemDao.findAll().size());
        Abbonamento abbonamento = abbonamentoDao.findByCodeLine(codeline);
        log.info(abbonamento.toString());
        RivistaAbbonamento rivista = rivistaAbbonamentoDao.findAll().iterator().next();
        log.info(rivista.toString());
        assertEquals(0, abbonamento.getPregresso().doubleValue(),0);

        switch (tipoEC) {
        	case OmaggioDirettoreAdp:
            case OmaggioCuriaDiocesiana:
            case OmaggioCuriaGeneralizia:
            case OmaggioGesuiti:
                assertEquals(0, abbonamento.getImporto().doubleValue(),0);
		       assertEquals(0, rivista.getImporto().doubleValue(),0);
		       break;
            default:
		       assertEquals(numero*blocchetti.getAbbonamento().doubleValue(), abbonamento.getImporto().doubleValue(),0);
		       assertEquals(numero*blocchetti.getAbbonamento().doubleValue(), rivista.getImporto().doubleValue(),0);
			break;
		}
        assertEquals(0, abbonamento.getPregresso().doubleValue(),0);
        assertEquals(invioSpedizioneEc, rivista.getInvioSpedizione());	
        assertEquals(blocchetti.getId(), rivista.getPubblicazione().getId());
        assertEquals(tizio.getId(), rivista.getDestinatario().getId());
        assertEquals(numero, rivista.getNumero().intValue());
        assertEquals(numero*blocchetti.getMesiPubblicazione().size(), rivista.getNumeroTotaleRiviste().intValue());
        
        for (Spedizione sped : spedizioneDao.findAll()) {
        	log.info(sped.toString());
        	assertEquals(tizio.getId(), sped.getDestinatario().getId());
        	assertEquals(invioSpedizioneSped, sped.getInvioSpedizione());
        	assertEquals(numero*blocchetti.getGrammi(), sped.getPesoStimato().intValue());
        	switch (invioSpedizioneSped) {
        		case Spedizioniere:
                case AdpSedeNoSpese:
                    assertEquals(0, sped.getSpesePostali().doubleValue(),0);
        			break;
                default:
        	        SpesaSpedizione ss = Smd.getSpesaSpedizione(spesaSpedizioneDao.findAll(), tizio.getAreaSpedizione(), RangeSpeseSpedizione.getByPeso(numero*blocchetti.getGrammi()));
        	        log.info(ss.toString());
        			double spesaSped =  ss.getSpese().doubleValue();
        			int nrSped = blocchetti.getMesiPubblicazione().size();
        			assertEquals(spesaSped, sped.getSpesePostali().doubleValue(),0);
        			if (tizio.getAreaSpedizione() == AreaSpedizione.Italia) {
                        assertEquals(0, abbonamento.getSpeseEstero().doubleValue(), 0);
                        assertEquals(nrSped * spesaSped, abbonamento.getSpese().doubleValue(), 0);
                    } else{
        	            assertEquals(0, abbonamento.getSpese().doubleValue(),0);
        	            assertEquals( nrSped*spesaSped, abbonamento.getSpeseEstero().doubleValue(),0);
        	        }
        	}
        	List<SpedizioneItem> items = spedizioneItemDao.findBySpedizione(sped);
        	assertEquals(1, items.size());
        	SpedizioneItem item = items.iterator().next();
        	log.info(item.toString());
        	assertEquals(blocchetti.getId(), item.getPubblicazione().getId());
        	assertEquals(numero, item.getNumero().intValue());
        	assertEquals(rivista.getId(), item.getRivistaAbbonamento().getId());        	
        }
        
        return rivista;
    	
    }


    @Test
    public void testAbbonamentoEsteroAdpSede() throws Exception {
        log.info("----------------->testAbbonamentoEsteroAdpSede<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        ec1.setInvioSpedizione(InvioSpedizione.AdpSede);
        abb.addItem(ec1);
        smdService.genera(abb);
        checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5, TipoAbbonamentoRivista.Ordinario,InvioSpedizione.AdpSede, InvioSpedizione.AdpSede);
    }

    @Test
    public void testAbbonamentoEsteroSpedizioniere() throws Exception {
        log.info("----------------->testAbbonamentoEsteroSpedizioniere<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        ec1.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        abb.addItem(ec1);
        smdService.genera(abb);
        RivistaAbbonamento rivista = checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5,TipoAbbonamentoRivista.Ordinario, InvioSpedizione.Spedizioniere, InvioSpedizione.AdpSede);
        smdService.aggiornaRivistaAbbonamento(rivista,4,TipoAbbonamentoRivista.Ordinario);
        rivista = checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 4, TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere, InvioSpedizione.AdpSede);
        smdService.rimuovi(abb,rivista);
        assertEquals(0, rivistaAbbonamentoDao.count());
        assertEquals(1, abbonamentoDao.count());
        assertEquals(0, spedizioneDao.count());
        assertEquals(0, spedizioneItemDao.count());
        abbonamentoDao.delete(abb);
        assertEquals(0, abbonamentoDao.count());
    }

    @Test
    public void testAbbonamentoItaliaSpedizioniere() throws Exception {
        log.info("----------------->testAbbonamentoItaliaSpedizioniere<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.Italia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        abb.addItem(ec1);
        smdService.genera(abb);        
        checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5, TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere, InvioSpedizione.Spedizioniere);
    }

    @Test
    public void testAbbonamentoItaliaAdpSede() throws Exception {
        log.info("----------------->testAbbonamentoItaliaAdpSede<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.Italia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        ec1.setInvioSpedizione(InvioSpedizione.AdpSede);
        abb.addItem(ec1);
        smdService.genera(abb);
        checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5, TipoAbbonamentoRivista.Ordinario,InvioSpedizione.AdpSede, InvioSpedizione.AdpSede);
                
    }

    @Test
    public void testAbbonamentoItaliaOmaggioAdpSede() throws Exception {
        log.info("----------------->testAbbonamentoItaliaOmaggioAdpSedeNoSpese<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.Italia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        ec1.setInvioSpedizione(InvioSpedizione.AdpSedeNoSpese);
        ec1.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioDirettoreAdp);
        abb.addItem(ec1);
        smdService.genera(abb);
        checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5, TipoAbbonamentoRivista.OmaggioDirettoreAdp,InvioSpedizione.AdpSedeNoSpese, InvioSpedizione.AdpSedeNoSpese);
     }

    @Test
    public void testAbbonamentoItaliaOmaggioSpedizioniere() throws Exception {
        log.info("----------------->testAbbonamentoItaliaOmaggioSpedizioniere<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        tizio.setAreaSpedizione(AreaSpedizione.Italia);
        anagraficaDao.save(tizio);
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(5);
        ec1.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioDirettoreAdp);
        abb.addItem(ec1);
        smdService.genera(abb);
        checkAbbonamento(tizio, abb.getCodeLine(), blocchetti, 5, TipoAbbonamentoRivista.OmaggioDirettoreAdp,InvioSpedizione.Spedizioniere, InvioSpedizione.Spedizioniere);
    }

    @Test
    public void testAbbonamentoAggiungiRivistaAbbonamento() throws Exception {
        log.info("----------------->testAbbonamentoAggiungiRivistaAbbonamento<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();
        Pubblicazione lodare = pubblicazioneDao.findByNomeStartsWithIgnoreCase("lodare").iterator().next();
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.GIUGNO);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        abb.addItem(ec1);
        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);        
        ec2.setDestinatario(tizio);
        abb.addItem(ec2);
        smdService.genera(abb);
        assertEquals(2, rivistaAbbonamentoDao.findAll().size());
        assertEquals(12, spedizioneDao.findAll().size());
        assertEquals(12, spedizioneItemDao.findAll().size());

        abbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        rivistaAbbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().forEach(msg -> log.info(msg.toString()));

        RivistaAbbonamento ec3 = new RivistaAbbonamento();
        ec3.setAbbonamento(abb);
        ec3.setPubblicazione(blocchetti);
        ec3.setMeseInizio(Mese.GENNAIO);
        ec3.setAnnoInizio(anno);
        ec3.setMeseFine(Mese.DICEMBRE);
        ec3.setAnnoFine(anno);     
        ec3.setDestinatario(tizio);
        List<SpedizioneWithItems> spedizioni = smdService.findByAbbonamento(abb);
        spedizioni = Smd.genera(abb,
                                          ec3,
                                          spedizioni,
                                          SmdHelper.getSpeseSpedizione());
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec3);
        spedizioni.forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(spedizioneItemDao::save);
        });
        assertEquals(3, rivistaAbbonamentoDao.findAll().size());        
        assertEquals(14, spedizioneDao.findAll().size());
        assertEquals(14, spedizioneItemDao.findAll().size());

        abbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        rivistaAbbonamentoDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().forEach(msg -> log.info(msg.toString()));
    }
    
    @Test
    public void testAbbonamentoRimuoviRivistaAbbonamento() {
        log.info("----------------->testAbbonamentoRimuoviRivistaAbbonamento<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();
        Pubblicazione lodare = pubblicazioneDao.findByNomeStartsWithIgnoreCase("lodare").iterator().next();
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.GIUGNO);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        RivistaAbbonamento ec3 = new RivistaAbbonamento();
        ec3.setAbbonamento(abb);
        ec3.setPubblicazione(blocchetti);
        ec3.setMeseInizio(Mese.GENNAIO);
        ec3.setAnnoInizio(anno);
        ec3.setMeseFine(Mese.DICEMBRE);
        ec3.setAnnoFine(anno);
        ec3.setDestinatario(tizio);

        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                     abb,
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());
        spedizioni = 
                Smd.genera(
                     abb,
                     ec2,
                    spedizioni,
                     SmdHelper.getSpeseSpedizione());        

        spedizioni = 
                Smd.genera(
                     abb,
                     ec3,
                    spedizioni,
                     SmdHelper.getSpeseSpedizione());        

        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec1);
        rivistaAbbonamentoDao.save(ec2);
        rivistaAbbonamentoDao.save(ec3);        

        spedizioni.forEach( sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });

        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(3, rivistaAbbonamentoDao.findAll().size());
        assertEquals(14, spedizioneDao.findAll().size());
        assertEquals(14, spedizioneItemDao.findAll().size());
        
        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abb,
                      ec2, 
                      spedizioni,
                      SmdHelper.getSpeseSpedizione());

        assertEquals(6, aggiorna.getItemsToDelete().size());
        
        aggiorna.getSpedizioniToSave().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item ->
                spedizioneItemDao.save(item)
               );
        });
        for (SpedizioneItem delitem: aggiorna.getItemsToDelete() ) {
            spedizioneItemDao.deleteById(delitem.getId());
        }
        aggiorna.getSpedizioniToSave()
        	.stream().filter(s -> s.getSpedizioneItems().isEmpty()).forEach(s ->spedizioneDao.deleteById(s.getSpedizione().getId()));
        assertEquals(1, aggiorna.getRivisteToDelete().size());
        RivistaAbbonamento rivista = aggiorna.getRivisteToDelete().iterator().next();
        assertEquals(0, aggiorna.getRivisteToSave().size());
        assertEquals(0, rivista.getNumeroTotaleRiviste().intValue());
        assertEquals(0, rivista.getImporto().doubleValue(),0);
        rivistaAbbonamentoDao.deleteById(rivista.getId());
        abbonamentoDao.save(abb);
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(2, rivistaAbbonamentoDao.findAll().size());
        assertEquals(8, spedizioneDao.findAll().size());
        assertEquals(8, spedizioneItemDao.findAll().size());
        
        log.info("----------------->testAbbonamentoRimuoviRivistaAbbonamento Rimosso: {}",ec2);

        
        spedizioni=smdService.findByAbbonamento(abb);
        aggiorna = Smd.rimuovi(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, aggiorna.getItemsToDelete().size());
        assertEquals(1, aggiorna.getRivisteToDelete().size());
        rivista = aggiorna.getRivisteToDelete().iterator().next();
        assertEquals(0, aggiorna.getRivisteToSave().size());
        assertEquals(0, rivista.getNumeroTotaleRiviste().intValue());
        assertEquals(0, rivista.getImporto().doubleValue(),0);
        
        aggiorna.getSpedizioniToSave().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(spedizioneItemDao::save);
        });
        for (SpedizioneItem delitem: aggiorna.getItemsToDelete()) {
            spedizioneItemDao.deleteById(delitem.getId());
        }

        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        
        rivistaAbbonamentoDao.deleteById(rivista.getId());
        abbonamentoDao.save(abb);
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());
        assertEquals(2, spedizioneDao.findAll().size());
        assertEquals(2, spedizioneItemDao.findAll().size());
        

        spedizioni=smdService.findByAbbonamento(abb);
        aggiorna = Smd.rimuovi(abb,ec3, spedizioni,SmdHelper.getSpeseSpedizione());
        for (SpedizioneItem delitem: aggiorna.getItemsToDelete() ) {
            spedizioneItemDao.deleteById(delitem.getId());
        }
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        assertEquals(1, aggiorna.getRivisteToDelete().size());
        rivista = aggiorna.getRivisteToDelete().iterator().next();
        assertEquals(0, aggiorna.getRivisteToSave().size());
        assertEquals(0, rivista.getNumeroTotaleRiviste().intValue());
        assertEquals(0, rivista.getImporto().doubleValue(),0);
        assertEquals(0, rivista.getNumeroTotaleRiviste().intValue());
        assertEquals(0, rivista.getImporto().doubleValue(),0);
        rivistaAbbonamentoDao.delete(rivista);
        abbonamentoDao.save(abb);
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(0, rivistaAbbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        
        assertEquals(abb.getTotale().doubleValue(), 0,0);
        
        abbonamentoDao.delete(abb);
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, rivistaAbbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());

    }

    @Test
    public void testAbbonamentoRimuoviRivistaAbbonamentoConSpediti() {
        log.info("----------------->testAbbonamentoRimuoviRivistaAbbonamentoConSpediti<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoCorrente(), false);
        
        Pubblicazione lodare = pubblicazioneDao.findByNomeStartsWithIgnoreCase("lodare").iterator().next();


        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(lodare);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(Anno.getAnnoCorrente());
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(Anno.getAnnoCorrente());
        ec1.setDestinatario(tizio);
        

        List<SpedizioneWithItems> spedizioni = Smd.genera(
                                           abb,
                                           ec1,
                                           new ArrayList<>(), 
                                           SmdHelper.getSpeseSpedizione()
                                           );
        spedizioni.forEach(s -> log.info("{}",s.getSpedizione()));
        assertTrue(Smd.isAbbonamentoAnnuale(ec1));
        // gen -> 1 sped post gen feb - 10 sped normale    -> 11 tot  -> tot riviste inv 3
        // feb -> 1 sped post ....... -  9 sped normale    -> 10 tot  -> tot riviste inv 4
        // mar -> 1 sped post ....... -  8 normale         ->  9 tot  -> tot riviste inv 5
        // apr -> 1 sped post ...     -  7 normale          -> 8 tot  -> tot riviste inv 6
        // mag -> 1 sped post ...     -  6 normale          -> 7 tot  -> tot riviste inv 7
        // giu -> 1 sped post ...     -  5 normale          -> 6 tot  -> tot riviste inv 8
        // lug -> 1 sped post ...     -  4 normale          -> 5 tot  -> tot riviste inv 9
        // ago -> 1 sped post ...     -  3 normale          -> 4 tot  -> tot riviste inv 10
        // set -> 1 sped post ...     -  2 normale          -> 3 tot  -> tot riviste inv 11
        // ott -> 1 sped post ...     -  1 normale          -> 2 tot  -> tot riviste inv 12
        // nov -> 1 sped post ...     -  0 normale          -> 1 tot  -> tot riviste inv 12
        // dic -> 1 sped post ...     -  0 normale          -> 1 tot  -> tot riviste inv 12
        assertEquals(lodare.getMesiPubblicazione().size(), 12);
        assertEquals(2, lodare.getAnticipoSpedizione());
    	log.info("mese corrente: {} pos: {}", Mese.getMeseCorrente(), Mese.getMeseCorrente().getPosizione());
        if (12-Mese.getMeseCorrente().getPosizione()-lodare.getAnticipoSpedizione() < 0) {
        	//1 Spedizione posticipata
        	assertEquals(1, spedizioni.size());
        } else {
        	assertEquals(lodare.getMesiPubblicazione().size()-lodare.getAnticipoSpedizione()-Mese.getMeseCorrente().getPosizione()+2, spedizioni.size());
        }
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec1);
        spedizioni.forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        
        spedizioni.stream()
        .filter(sped -> Mese.getMeseCorrente() == sped.getSpedizione().getMeseSpedizione())
        .forEach(sped -> {
          assertEquals(sped.getSpedizione().getAnnoSpedizione(), Anno.getAnnoCorrente());
          spedizioneItemDao.findBySpedizione(sped.getSpedizione()).forEach( item -> {
              item.setStatoSpedizione(StatoSpedizione.INVIATA);
              spedizioneItemDao.save(item);
        	  
          });
          log.info("Setting Inviata {}", sped.getSpedizione());
        });

        spedizioni=smdService.findByAbbonamento(abb);
        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        aggiorna.getSpedizioniToSave().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        for (SpedizioneItem deletedItem:aggiorna.getItemsToDelete()) {
            assertEquals(StatoSpedizione.PROGRAMMATA, deletedItem.getStatoSpedizione());
            assertEquals(ec1.getId(), deletedItem.getRivistaAbbonamento().getId());
            log.info("deleted: " + deletedItem);
            spedizioneItemDao.deleteById(deletedItem.getId());            
        }
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }     
        assertEquals(0, aggiorna.getRivisteToDelete().size());
        assertEquals(1, aggiorna.getRivisteToSave().size());
        RivistaAbbonamento rivista=aggiorna.getRivisteToSave().iterator().next();
        assertEquals(1, rivista.getNumero().intValue());
        assertEquals(Mese.GENNAIO, rivista.getMeseInizio());
        if (Mese.getByPosizione(Mese.getMeseCorrente().getPosizione()+lodare.getAnticipoSpedizione()) == null) {
        	assertEquals(Mese.DICEMBRE, rivista.getMeseFine());
        } else {
        	assertEquals(Mese.getByPosizione(Mese.getMeseCorrente().getPosizione()+lodare.getAnticipoSpedizione()), rivista.getMeseFine());
        }
        assertEquals(Anno.getAnnoCorrente(), rivista.getAnnoInizio());
        assertEquals(Anno.getAnnoCorrente(), rivista.getAnnoFine());
        rivistaAbbonamentoDao.save(aggiorna.getRivisteToSave().iterator().next());
        
        spedizioni=smdService.findByAbbonamento(abb);
        if (12-Mese.getMeseCorrente().getPosizione()-lodare.getAnticipoSpedizione() < 0) {
        	assertEquals(1, spedizioni.size());
        	assertEquals(12, rivista.getNumeroTotaleRiviste().intValue());
        } else {
            assertEquals(Mese.getMeseCorrente().getPosizione()+lodare.getAnticipoSpedizione(), rivista.getNumeroTotaleRiviste().intValue());
            assertEquals(2, spedizioni.size());
        }
        for (SpedizioneWithItems inviata : spedizioni) {
        	assertEquals(inviata.getSpedizione().getAnnoSpedizione(), Anno.getAnnoCorrente());
        	assertEquals(inviata.getSpedizione().getMeseSpedizione(), Mese.getMeseCorrente());
        	spedizioneItemDao.findBySpedizione(inviata.getSpedizione()).forEach(item->
        	assertEquals(StatoSpedizione.INVIATA, item.getStatoSpedizione()));
        	log.info("Spedizione: {}", inviata.getSpedizione().getInvioSpedizione());
        	log.info("Spedizione: {} {} ", inviata.getSpedizione().getMeseSpedizione().getNomeBreve(), inviata.getSpedizione().getAnnoSpedizione().getAnnoAsString());
        	log.info("Spedizione: {} Item ", inviata.getSpedizioneItems().size());
        	if (inviata.getSpedizione().getInvioSpedizione() == InvioSpedizione.AdpSede) {
                if (12-Mese.getMeseCorrente().getPosizione()-lodare.getAnticipoSpedizione() < 0) {
                	assertEquals(12, inviata.getSpedizioneItems().size());
                } else {
            		assertEquals(Mese.getMeseCorrente().getPosizione()+lodare.getAnticipoSpedizione()-1,inviata.getSpedizioneItems().size());
                }
        	} else {
        		assertEquals(1,inviata.getSpedizioneItems().size());
        	}
            for (SpedizioneItem item: inviata.getSpedizioneItems()) {
                spedizioneItemDao.deleteById(item.getId());
            }
            spedizioneDao.deleteById(inviata.getSpedizione().getId());
        }
        
        rivistaAbbonamentoDao.deleteById(ec1.getId());
        abbonamentoDao.delete(abb);
    }
    
    @Test
    public void testAbbonamentoAggiornaRivistaAbbonamento() {
        log.info("----------------->testAbbonamentoAggiornaRivistaAbbonamento<----------------");
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, anno, false);
        
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());        
        assertTrue(Smd.isAbbonamentoAnnuale(ec1));
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec1);
        spedizioni.forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());
        assertEquals(messaggio.getMesiPubblicazione().size(), spedizioneDao.findAll().size());
        assertEquals(messaggio.getMesiPubblicazione().size(), spedizioneItemDao.findAll().size());
  
        spedizioneDao.findAll().forEach(s -> {
            assertEquals(0, s.getSpesePostali().doubleValue(),0);
            assertEquals(InvioSpedizione.Spedizioniere, s.getInvioSpedizione());
            assertEquals(1, spedizioneItemDao.findBySpedizione(s).size());
            SpedizioneItem item = spedizioneItemDao.findBySpedizione(s).iterator().next();
            assertEquals(1, item.getNumero().intValue());
        });
        
        log.info("Costo abbonamento: " + abb.getTotale());
        assertEquals(messaggio.getAbbonamento().doubleValue(), abb.getTotale().doubleValue(),0);
        RivistaAbbonamentoAggiorna aggiorna = 
        		Smd.aggiorna(
        				abb,
        		        spedizioni,
        		        SmdHelper.getSpeseSpedizione(),
        		        ec1,
        		        10,
        		        ec1.getTipoAbbonamentoRivista()
				);       
        assertEquals(0, aggiorna.getItemsToDelete().size());
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec1);
        aggiorna.getSpedizioniToSave().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));
        });

        spedizioneDao.findAll().forEach(s -> {
            assertEquals(0, s.getSpesePostali().doubleValue(),0);
            assertEquals(InvioSpedizione.Spedizioniere, s.getInvioSpedizione());
            assertEquals(1, spedizioneItemDao.findBySpedizione(s).size());
            SpedizioneItem item = spedizioneItemDao.findBySpedizione(s).iterator().next();
            assertEquals(10, item.getNumero().intValue());
        });

        assertEquals(messaggio.getAbbonamento().multiply(new BigDecimal(10)).doubleValue(), abb.getTotale().doubleValue(),0);
        assertEquals(0, abb.getSpese().doubleValue(),0);                
    }

    @Test
    public void testCampagnaCRUD() {
        log.info("----------------->testCampagnaCRUD<----------------");
        Pubblicazione messaggio = pubblicazioneDao.findByNomeStartsWithIgnoreCase("messaggio").iterator().next();
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoProssimo());
        CampagnaItem itemMessaggio = new CampagnaItem();
        itemMessaggio.setCampagna(campagna);
        itemMessaggio.setPubblicazione(messaggio);
        campagna.addCampagnaItem(itemMessaggio);
        CampagnaItem itemBlocchetti = new CampagnaItem();
        itemBlocchetti.setCampagna(campagna);
        itemBlocchetti.setPubblicazione(blocchetti);
        campagna.addCampagnaItem(itemBlocchetti);
        campagnaDao.save(campagna);
        campagna.getCampagnaItems().forEach( item -> campagnaItemDao.save(item));
        
        campagna = campagnaDao.findByAnno(campagna.getAnno());
        assertNotNull(campagna);
        assertEquals(2, campagna.getCampagnaItems().size());
        assertEquals(2, campagnaItemDao.findByCampagna(campagna).size());
        assertEquals(1, campagnaItemDao.findByPubblicazione(blocchetti).size());
        assertEquals(1, campagnaItemDao.findByPubblicazione(messaggio).size());
        
        campagnaItemDao.findAll().forEach(item -> campagnaItemDao.delete(item));
        campagnaDao.deleteById(campagna.getId());
        assertEquals(0, campagnaItemDao.findAll().size());
        assertEquals(0, campagnaItemDao.findByCampagna(campagna).size());
        assertEquals(0, campagnaItemDao.findByPubblicazione(blocchetti).size());
        assertEquals(0, campagnaItemDao.findByPubblicazione(messaggio).size());
        assertEquals(0, campagnaDao.findAll().size());
        
    }

    @Test 
    public void testVersamentoCRUD() {
        log.info("----------------->testVersamentoCRUD<----------------");
        assertEquals(0, incassoDao.findAll().size());
        DistintaVersamento incasso = getIncassoTelematici();
        incassoDao.save(incasso);
        incasso.getItems().forEach(versamentoDao::save);
        
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
                
        incassoDao.delete(incasso);
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
                
    }

    @Test 
    public void testVersamentoWithRediduo() {
        log.info("----------------->testVersamentoWithRediduo<----------------");
        assertEquals(0, incassoDao.findAll().size());
        DistintaVersamento incasso = getIncassoTelematici();
        incassoDao.save(incasso);
        incasso.getItems().forEach(v -> versamentoDao.save(v));
        
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
        assertEquals(1, versamentoDao.findWithResiduo().size());
        
        Versamento versamento = versamentoDao.findWithResiduo().iterator().next();
        versamento.setIncassato(versamento.getImporto());
        assertEquals(0, versamento.getResiduo().doubleValue(),0);
        versamentoDao.save(versamento);
        assertEquals(0, versamentoDao.findWithResiduo().size());
        
        incassoDao.delete(incasso);
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
                
    }

    public static DistintaVersamento getIncassoTelematici() {
        String riepilogo1 = "4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1 = new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        return BancoPostaServiceImpl.generaIncasso(versamenti1, riepilogo1);
    }

    @Test
    public void testOfferteCumulateCRUD() {
        log.info("----------------->testOfferteCumulateCRUD<----------------");
        assertEquals(0, offerteCumulateDao.findAll().size());
        OfferteCumulate offerte = new OfferteCumulate();
    	offerteCumulateDao.save(offerte);
        assertEquals(1, offerteCumulateDao.findAll().size());
        OfferteCumulate offerte2 = offerteCumulateDao.findByAnno(Anno.getAnnoCorrente());
        assertEquals(offerte.getId().longValue(), offerte2.getId().longValue());
        offerteCumulateDao.delete(offerte2);
        assertEquals(0, offerteCumulateDao.findAll().size());       
    }
    
    @Test
    public void testOffertaCRUD() {
        log.info("----------------->testOffertaCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, offerteCumulateDao.findAll().size());
        assertEquals(0, offertaDao.findAll().size());
        
        Anagrafica ar = SmdHelper.getAR();
        anagraficaDao.save(ar);
        assertEquals(1, anagraficaDao.findAll().size());
        
        DistintaVersamento incasso = getIncassoTelematici();
        incassoDao.save(incasso);
        incasso.getItems().forEach(v -> versamentoDao.save(v));
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());

        OfferteCumulate offerte = new OfferteCumulate();
    	offerteCumulateDao.save(offerte);
        assertEquals(1, offerteCumulateDao.findAll().size());
        assertEquals(0, offerte.getImporto().signum());
        
        Versamento versamento = versamentoDao.findAll().iterator().next();
        log.info(versamento.toString());
        assertEquals(15.00, incasso.getImporto().doubleValue(),0);
        assertEquals(0.00, incasso.getIncassato().doubleValue(),0);
        assertEquals(15.00, versamento.getImporto().doubleValue(),0);
        assertEquals(0.00, versamento.getIncassato().doubleValue(),0);
        
        assertEquals(0.00, offerte.getImporto().doubleValue(),0);
        
        BigDecimal incassato = Smd.incassa(incasso, versamento, offerte,new BigDecimal(10));
        assertEquals(10.00, offerte.getImporto().doubleValue(),0);
        assertEquals(10.00, incassato.doubleValue(),0);
        assertEquals(10.00, versamento.getIncassato().doubleValue(),0);
        assertEquals(10.00, incasso.getIncassato().doubleValue(),0);

        offerteCumulateDao.save(offerte);
        versamentoDao.save(versamento);
        incassoDao.save(incasso);
        
        Offerta offerta = new Offerta();
        offerta.setCommittente(ar);
        offerta.setVersamento(versamento);
        offerta.setOfferteCumulate(offerte);
        offerta.setImporto(incassato);
        
        offertaDao.save(offerta);
        assertEquals(1, offertaDao.findAll().size());
                
        offertaDao.deleteAll();
        versamentoDao.deleteAll();
        incassoDao.deleteAll();
        anagraficaDao.deleteAll();
        offerteCumulateDao.deleteAll();
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, offertaDao.findAll().size());       
        assertEquals(0, offerteCumulateDao.findAll().size());       
    }

    @Test 
    public void testVersamentoCommittente() {
        log.info("----------------->testVersamentoCommittente<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        
        Anagrafica ar = SmdHelper.getAR();
        anagraficaDao.save(ar);
        assertEquals(1, anagraficaDao.findAll().size());

        DistintaVersamento incasso = getIncassoTelematici();
        incassoDao.save(incasso);
        incasso.getItems().forEach(v -> versamentoDao.save(v));
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
        
        Versamento versamento = versamentoDao.findAll().iterator().next();
        assertThat(versamento).isNotNull();
        assertThat(versamento.getCommittente()).isNull();
        Anagrafica committente = anagraficaDao.findAll().iterator().next();
        assertNotNull(committente);
        
        versamento.setCommittente(committente);
        versamentoDao.save(versamento);
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
        
        Versamento persisted1 = versamentoDao.findAll().iterator().next();
        assertEquals(versamento.getId(), persisted1.getId());
        assertEquals(1, anagraficaDao.findAll().size());
        assertNotNull(persisted1.getCommittente());
        
        log.info("committente: {}", persisted1.getCommittente().getId());

        persisted1.setCommittente(null);
        versamentoDao.save(persisted1);

        Versamento persisted2 = versamentoDao.findAll().iterator().next();
        assertEquals(versamento.getId(), persisted2.getId());
        assertEquals(1, anagraficaDao.findAll().size());
        assertThat(persisted2).isNotNull();
        assertThat(persisted2.getCommittente()).isNull();

        incassoDao.delete(incasso);
        anagraficaDao.delete(committente);
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
                
    }

     public static DistintaVersamento getIncassoByImportoAndCodeLine(BigDecimal importo,String codeLine) {
         DistintaVersamento incasso5 = new DistintaVersamento();
         incasso5.setCassa(Cassa.Contrassegno);
         incasso5.setCcp(Ccp.DUE);

         Versamento versamentoIncasso5 = new Versamento(incasso5,importo);
         versamentoIncasso5.setCodeLine(codeLine);
         versamentoIncasso5.setDataPagamento(incasso5.getDataContabile());
         incasso5.addItem(versamentoIncasso5);
         Smd.calcoloImportoIncasso(incasso5);
         return incasso5;
     }
    
    @Test
    public void testIncassa() {
        log.info("----------------->testIncassa<----------------");

        Anagrafica davidePalma = SmdHelper.getDP();
        anagraficaDao.save(davidePalma);

        Pubblicazione b = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();

        Abbonamento abb = SmdHelper.getAbbonamentoBy(
                            davidePalma, 
                            Anno.getAnnoCorrente(), 
                            false
                            );
        
        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setPubblicazione(b);
        ec.setNumero(2);
        ec.setAbbonamento(abb);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setAnnoInizio(Anno.getAnnoProssimo());
        ec.setMeseFine(Mese.SETTEMBRE);
        ec.setAnnoFine(Anno.getAnnoProssimo());
        ec.setDestinatario(davidePalma);

        Smd.genera(abb, ec, new ArrayList<>(), SmdHelper.getSpeseSpedizione());
        abbonamentoDao.save(abb);
        rivistaAbbonamentoDao.save(ec);
        
        DistintaVersamento incasso = getIncassoByImportoAndCodeLine(abb.getTotale(), abb.getCodeLine());
        incassoDao.save(incasso);
        incasso.getItems().forEach(versamentoDao::save);

        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
        assertEquals(0, operazioneIncassoDao.findAll().size());

        BigDecimal incassato = BigDecimal.ZERO;
        for (Versamento v: incasso.getItems()) {
        	assertEquals(abb.getCodeLine(), v.getCodeLine());
            incassato = incassato.add(Smd.incassa(incasso,v, abb));
            versamentoDao.save(v);
            incassoDao.save(incasso);
            abbonamentoDao.save(abb);
            OperazioneIncasso operazione = new OperazioneIncasso();
            operazione.setAbbonamento(abb);
            operazione.setVersamento(v);
            operazione.setStatoOperazioneIncasso(StatoOperazioneIncasso.Incasso);
            operazione.setOperatore("Test");
            operazione.setDescription("Test Incasso code");
            operazione.setImporto(incassato);
            operazioneIncassoDao.save(operazione);            
        }

        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
        assertEquals(1, operazioneIncassoDao.findAll().size());

        assertEquals(0, abb.getResiduo().doubleValue(),0);
        assertEquals(0, incasso.getResiduo().doubleValue(),0);
        assertEquals(incassato.doubleValue(), abb.getIncassato().doubleValue(),0);
        assertEquals(Incassato.Si, Abbonamento.getStatoIncasso(abb));

        Versamento versamento = versamentoDao.findAll().iterator().next();        
        Abbonamento abbonamento = abbonamentoDao.findByCodeLine(versamento.getCodeLine());
        assertNotNull(abbonamento);
        assertEquals(versamento.getCodeLine(), abbonamento.getCodeLine());
        assertEquals(Incassato.Si, Abbonamento.getStatoIncasso(abbonamento));
        assertEquals(incassato.doubleValue(), versamento.getIncassato().doubleValue(),0);
        assertEquals(0, versamento.getResiduo().doubleValue(),0);
   
        OperazioneIncasso operazione = 
        		operazioneIncassoDao
        		.findByAbbonamentoAndVersamentoAndStatoOperazioneIncasso(abbonamento, versamento, StatoOperazioneIncasso.Incasso).iterator().next();
        assertNotNull(operazione);
        assertEquals(abbonamento.getId(), operazione.getAbbonamento().getId());
        assertEquals(versamento.getId(), operazione.getVersamento().getId());
        assertEquals(incassato.doubleValue(), operazione.getImporto().doubleValue(),0);
        assertEquals(StatoOperazioneIncasso.Incasso, operazione.getStatoOperazioneIncasso());
        
    }

    @Test
    public void testIncassaAbbonamento() throws Exception {
        log.info("----------------->testIncassaAbbonamento<----------------");

        Anagrafica davidePalma = SmdHelper.getDP();
        anagraficaDao.save(davidePalma);
        Anagrafica antonioRusso = SmdHelper.getAR();
        anagraficaDao.save(antonioRusso);

        Pubblicazione b = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
                
        Abbonamento abb1 = SmdHelper.getAbbonamentoBy(
                                                     davidePalma, 
                                                     Anno.getAnnoCorrente(), 
                                                     false
                                                     );
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setPubblicazione(b);
        ec1.setNumero(2);
        ec1.setAbbonamento(abb1);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(Anno.getAnnoProssimo());
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(Anno.getAnnoProssimo());
        ec1.setDestinatario(davidePalma);
        abb1.addItem(ec1);
        smdService.genera(abb1);

        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, rivistaAbbonamentoDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());


        Abbonamento abb2 = SmdHelper.getAbbonamentoBy(
                                                      antonioRusso, 
                                                      Anno.getAnnoCorrente(), 
                                                      false
                                                      );
        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setPubblicazione(b);
        ec2.setNumero(2);
        ec2.setAbbonamento(abb1);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(Anno.getAnnoProssimo());
        ec2.setMeseFine(Mese.DICEMBRE);
        ec2.setAnnoFine(Anno.getAnnoProssimo());
        ec2.setDestinatario(antonioRusso);
        abb2.addItem(ec2);
        smdService.genera(abb2);

        assertEquals(2, abbonamentoDao.findAll().size());
        assertEquals(2, rivistaAbbonamentoDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());

        Date date = new Date();
        abb1.setDataPagamento(date);
        abb1.setDataContabile(date);
        abb1.setProgressivo("00001");
        assertEquals(abb1.getTotale().doubleValue(), abb1.getResiduo().doubleValue(),0);
        abbonamentoServiceDao.incassa(abb1,abb1.getTotale().toString(),userInfoDao.findByUsername("adp"));
        assertEquals(BigDecimal.ZERO.doubleValue(), abb1.getResiduo().doubleValue(),0);
        assertEquals(1, incassoDao.count());
        assertEquals(1, versamentoDao.count());
        assertEquals(1, operazioneIncassoDao.count());
        
        DistintaVersamento inc1 = incassoDao.findAll().iterator().next();
        assertEquals(SmdEntity.getStandardDate(inc1.getDataContabile()), SmdEntity.getStandardDate(abb1.getDataContabile()));
        DistintaVersamento incasso = 
        incassoDao
        .findByDataContabileAndCassaAndCcpAndCuas(
              abb1.getDataContabile(),
              abb1.getCassa(), 
              abb1.getCcp(), 
              abb1.getCuas());

        assertNotNull(incasso);
        abb2.setDataPagamento(date);
        abb2.setDataContabile(date);
        abb2.setProgressivo("00002");
       assertEquals(abb2.getTotale().doubleValue(), abb2.getResiduo().doubleValue(),0);
        abbonamentoServiceDao.incassa(abb2,abb2.getTotale().toString(),userInfoDao.findByUsername("adp"));
        assertEquals(BigDecimal.ZERO.doubleValue(), abb2.getResiduo().doubleValue(),0);
        assertEquals(1, incassoDao.count());
        assertEquals(2, versamentoDao.count());  
        assertEquals(2, operazioneIncassoDao.count());
        for (OperazioneIncasso op: operazioneIncassoDao.findAll()) {
        	assertEquals(StatoOperazioneIncasso.Incasso, op.getStatoOperazioneIncasso());
        	assertEquals("adp", op.getOperatore());
        }
    }

}
