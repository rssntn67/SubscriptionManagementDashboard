package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpedizioneWithItems;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
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
import it.arsinfo.smd.ui.security.CustomLogoutSuccessHandler;
import it.arsinfo.smd.ui.security.RedirectAuthenticationSuccessHandler;
import it.arsinfo.smd.ui.security.SecurityConfig;
import it.arsinfo.smd.ui.security.UserDetailsServiceImpl;

@RunWith(SpringRunner.class)
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
    private EstrattoContoDao estrattoContoDao;
    @Autowired
    private CampagnaDao campagnaDao;
    @Autowired
    private CampagnaItemDao campagnaItemDao;
    @Autowired
    private IncassoDao incassoDao;
    @Autowired
    private VersamentoDao versamentoDao;
    @Autowired
    private OperazioneDao operazioneDao;
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
    private SecurityConfig securityConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired 
    private LogoutSuccessHandler logoutSuccessHandler;
    
    private static final Logger log = LoggerFactory.getLogger(SmdApplicationTests.class);

    @Before
    public void setUp() {
        
        log.info("----------------->EnteringSetUp<----------------");
        try {
        operazioneDao.deleteAll();
        spedizioneItemDao.deleteAll();
        spedizioneDao.deleteAll();
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        campagnaItemDao.deleteAll();
        campagnaDao.deleteAll();
        notaDao.deleteAll();
        storicoDao.deleteAll();
        anagraficaDao.deleteAll();
        pubblicazioneDao.deleteAll();
        spesaSpedizioneDao.deleteAll();
        versamentoDao.deleteAll();
        incassoDao.deleteAll();  
        } catch (Exception e) {
            log.error(e.getMessage(),e);            
        }
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        assertEquals(0, campagnaDao.findAll().size());
        assertEquals(0, campagnaItemDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, operazioneDao.findAll().size());
        assertEquals(2, userInfoDao.findAll().size());

        
    }
    @Test
    public void testAutowire() {
        log.info("----------------->testAutowire<----------------");
        assertNotNull(abbonamentoDao);
        assertNotNull(anagraficaDao);
        assertNotNull(pubblicazioneDao);
        assertNotNull(estrattoContoDao);
        assertNotNull(storicoDao);
        assertNotNull(campagnaDao);
        assertNotNull(campagnaItemDao);
        assertNotNull(notaDao);
        assertNotNull(storicoDao);
        assertNotNull(versamentoDao);
        assertNotNull(incassoDao);
        assertNotNull(operazioneDao);
        assertNotNull(userInfoDao);
        assertNotNull(spedizioneDao);
        assertNotNull(spedizioneItemDao);
        assertNotNull(spesaSpedizioneDao);

        assertNotNull(smdService);
        assertTrue(smdService instanceof SmdServiceImpl);

        assertNotNull(securityConfig);
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof UserDetailsServiceImpl);
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        assertNotNull(authenticationSuccessHandler);
        assertTrue(authenticationSuccessHandler instanceof RedirectAuthenticationSuccessHandler);        
        assertNotNull(logoutSuccessHandler);
        assertTrue(logoutSuccessHandler instanceof CustomLogoutSuccessHandler);        
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
            assertTrue(false);
        }

        UserInfo admin = userInfoDao.findById(1L).get();
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
            assertTrue(false);
        }
                
        userInfoDao.delete(user);
        assertEquals(2, userInfoDao.findAll().size());
        
    }        

    @Test
    public void testAnagraficaCRUD() {
        log.info("----------------->testAnagraficaCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        Anagrafica antonioRusso =  SmdHelper.getAR();
        anagraficaDao.save(antonioRusso);
        assertEquals(1, anagraficaDao.findAll().size());
        
        assertNotNull(anagraficaDao.findById(antonioRusso.getId()));
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
            assertTrue(false);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        
        anagraficaDao.findAll().stream().forEach( a -> log.info(a.toString()));
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
            assertTrue(false);
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
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        for (SpesaSpedizione ss : SmdHelper.getSpeseSpedizione()) {
            spesaSpedizioneDao.save(ss);
            log.info(ss.toString());
        }
        assertEquals(5,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo).size());
        assertEquals(5,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.AmericaAfricaAsia).size());
        assertEquals(10,spesaSpedizioneDao.findByAreaSpedizione(AreaSpedizione.Italia).size());
        assertEquals(20,spesaSpedizioneDao.findAll().size());

        SpesaSpedizione spedizioneItaliaDa1a2Kg=spesaSpedizioneDao.findByAreaSpedizioneAndRangeSpeseSpedizione(AreaSpedizione.Italia, RangeSpeseSpedizione.Da1KgA2Kg);
        assertNotNull(spedizioneItaliaDa1a2Kg);
        
        SpesaSpedizione duplicato=new SpesaSpedizione();
        duplicato.setAreaSpedizione(AreaSpedizione.Italia);
        duplicato.setRangeSpeseSpedizione(RangeSpeseSpedizione.Da1KgA2Kg);
        duplicato.setSpese(new BigDecimal(10.88));
        
        try {
            spesaSpedizioneDao.save(duplicato);
            assertTrue(false);
        } catch (DataIntegrityViolationException e) {
            log.info(e.getMessage());
        }
        
        spesaSpedizioneDao.delete(spedizioneItaliaDa1a2Kg);
        spedizioneItaliaDa1a2Kg=spesaSpedizioneDao.findByAreaSpedizioneAndRangeSpeseSpedizione(AreaSpedizione.Italia, RangeSpeseSpedizione.Da1KgA2Kg);
        assertNull(spedizioneItaliaDa1a2Kg);
        assertEquals(19, spesaSpedizioneDao.findAll().size());
        spesaSpedizioneDao.save(duplicato);
        assertEquals(20, spesaSpedizioneDao.findAll().size());
        spesaSpedizioneDao.deleteAll();
        assertEquals(0, spesaSpedizioneDao.findAll().size());

    }
    @Test 
    public void testPubblicazioneDaoCRUD() {
        log.info("----------------->testPubblicazioneDaoCRUD<----------------");
        assertEquals(0, pubblicazioneDao.findAll().size());
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
        assertEquals(1, pubblicazioneDao.findAll().size());
        
        assertEquals(1, pubblicazioneDao.findAll().size());


        assertEquals(1, pubblicazioneDao.findAll().size());

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
        
        assertEquals(2, pubblicazioneDao.findAll().size());

        anagraficaDao.findAll().stream().forEach( msg -> log.info(msg.toString()));
        spesaSpedizioneDao.findAll().stream().forEach( msg -> log.info(msg.toString()));

        List<Pubblicazione> ff = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Pr");
        assertEquals(1, ff.size());
        Pubblicazione ffp = ff.iterator().next();
        assertEquals(p.getId(), ffp.getId());
        assertEquals("prova", ffp.getNome());
        log.info(ffp.toString());
        
        assertEquals(2, pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE).size());
        assertEquals(0, pubblicazioneDao.findByTipo(TipoPubblicazione.SEMESTRALE).size());
        
        pubblicazioneDao.delete(p1);
        assertEquals(1, pubblicazioneDao.findAll().size());
        pubblicazioneDao.delete(p);
        assertEquals(0, pubblicazioneDao.findAll().size());
               
    }

    @Test 
    public void testPubblicazioniAdp() {
        log.info("----------------->testPubblicazioniAdp<----------------");

        assertEquals(0, pubblicazioneDao.findAll().size());

        Pubblicazione m = SmdHelper.getMessaggio();
        pubblicazioneDao.save(m);
        
        Pubblicazione l = SmdHelper.getLodare();
        pubblicazioneDao.save(l);
        
        Pubblicazione b =SmdHelper.getBlocchetti();
        pubblicazioneDao.save(b);
        
        Pubblicazione e = SmdHelper.getEstratti();
        pubblicazioneDao.save(e);
        
        log.info("Pubblicazioni found with findAll():");
        log.info("-------------------------------");
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        assertEquals(4, pubblicazioni.size());
        Map<String, Long> nameToIdMap = new HashMap<>();
        for (Pubblicazione pubblicazione : pubblicazioni) {
            assertNotNull(pubblicazione.getId());
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
        Pubblicazione messaggio = pubblicazioneDao.findById(nameToIdMap.get("Messaggio")).get();
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

        Pubblicazione lodare = pubblicazioneDao.findById(nameToIdMap.get("Lodare")).get();
        log.info("lodare found with findOne: " + nameToIdMap.get("Lodare"));
        log.info("--------------------------------");
        assertEquals(nameToIdMap.get("Lodare").longValue(), lodare.getId().longValue());

        log.info(lodare.toString());
        log.info("");

        Pubblicazione blocchetti = pubblicazioneDao.findById(nameToIdMap.get("Blocchetti")).get();
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
        
        pubblicazioneDao.delete(messaggio);
        pubblicazioneDao.delete(blocchetti);
        pubblicazioneDao.delete(lodare);
        pubblicazioneDao.delete(estratti);
        
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());       
    }
    
    @Test 
    public void testStoricoCRUD() {
        //you should be not able to create a storico 
        // without intestatario,destinatario and pubblicazione
        assertEquals(0, storicoDao.findAll().size());
        Storico storico0 = new Storico();
        
        try {
            storicoDao.save(storico0);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica tizio = SmdHelper.getAR();
        anagraficaDao.save(tizio);
        
        storico0.setDestinatario(tizio);

        try {
            storicoDao.save(storico0);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica matteoParo = SmdHelper.getMP();
        anagraficaDao.save(matteoParo);
        
        storico0.setIntestatario(matteoParo);

        try {
            storicoDao.save(storico0);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        pubblicazioneDao.save(messaggio);
        
        storico0.setPubblicazione(messaggio);
        storicoDao.save(storico0);
        assertEquals(1, storicoDao.findAll().size());
        
        //seems not updating id from...
        storico0 = storicoDao.findAll().iterator().next();

        Pubblicazione lodare = SmdHelper.getLodare();
        pubblicazioneDao.save(lodare);

        Storico storico1 = new Storico();
        storico1.setIntestatario(matteoParo);
        storico1.setDestinatario(matteoParo);
        storico1.setPubblicazione(lodare);
        storico1.setTipoEstrattoConto(TipoEstrattoConto.OmaggioGesuiti);
        storico1.setInvioSpedizione(InvioSpedizione.AdpSede);
        storico1.setNumero(10);
        storico1.setInvio(Invio.Intestatario);
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

        assertEquals(1, storicoDao.findByInvio(Invio.Intestatario).size());
        assertEquals(1, storicoDao.findByInvio(Invio.Destinatario).size());
        
        assertEquals(2, storicoDao.findByCassa(Cassa.Ccp).size());
        assertEquals(0, storicoDao.findByCassa(Cassa.Contrassegno).size());
        
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.AdpSede).size());
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.Spedizioniere).size());

        assertEquals(1, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.Ordinario).size());
        assertEquals(1, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.OmaggioGesuiti).size());
        assertEquals(0, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.OmaggioDirettoreAdp).size());

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

        storicoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        notaDao.findAll().stream().forEach(msg -> log.info(msg.toString()));

        notaDao.delete(nota2);
        assertEquals(2, notaDao.findAll().size());
        assertEquals(2, storicoDao.findAll().size());
        
        smdService.delete(storico0);
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());
        
        storico1 = storicoDao.findAll().iterator().next();
        smdService.delete(storico1);
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size()); 
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();

        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());        
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());

    }
    
    @Test
    public void testSmdLoadStorico() {
        log.info("----------------->testSmdLoadStorico<----------------");
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(blocchetti);
        Anagrafica matteo = SmdHelper.getMS();
        anagraficaDao.save(matteo);
        
        Storico storico = SmdHelper.getStoricoBy(matteo, matteo, blocchetti, 100, Cassa.Carte, TipoEstrattoConto.Sostenitore, Invio.Destinatario, InvioSpedizione.AdpSede);
        storicoDao.save(storico);
        notaDao.save(SmdHelper.getNota(storico));
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());
        
        notaDao.deleteAll();
        storicoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();

        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());        
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
                
    }
    
    @Test
    public void testAbbonamentoCRUD() {
        log.info("----------------->testAbbonamentoCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        abb.setPregresso(new BigDecimal(10.0));
        abb.setImporto(new BigDecimal(15.0));
        
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        abbonamentoDao.delete(abb);
        anagraficaDao.delete(tizio);
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        
    }
    
    @Test
    public void testEstrattoContoCRUD() {
        log.info("----------------->testEstrattoContoCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        
        EstrattoConto ec = new EstrattoConto();

        try {
            estrattoContoDao.save(ec);
            assertTrue(false);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        abb.setPregresso(new BigDecimal(10.0));
        abb.setImporto(new BigDecimal(15.0));        
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        ec.setAbbonamento(abb);

        try {
            estrattoContoDao.save(ec);
            assertTrue(false);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Pubblicazione lodare =SmdHelper.getLodare();
        pubblicazioneDao.save(lodare);

        ec.setPubblicazione(lodare);
        ec.setDestinatario(tizio);
        estrattoContoDao.save(ec);
        
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findAll().size());

        log.info(ec.toString());

        //Need to retrieve a fresh copy from database
        ec = estrattoContoDao.findAll().iterator().next();
        estrattoContoDao.delete(ec);
        assertEquals(0, estrattoContoDao.findAll().size());
        abbonamentoDao.delete(abb);
        anagraficaDao.delete(tizio);
        pubblicazioneDao.delete(lodare);

        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());

    }

    @Test
    public void testSpedizioneCRUD() {
        log.info("----------------->testSpedizioneCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        
        Spedizione sped = new Spedizione();

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        abb.setPregresso(new BigDecimal(10.0));
        abb.setImporto(new BigDecimal(15.0));        
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        sped.setAbbonamento(abb);
        sped.setDestinatario(tizio);

        spedizioneDao.save(sped);
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, spedizioneDao.findAll().size());

        log.info(sped.toString());

        //Need to retrieve a fresh copy from database
        sped = spedizioneDao.findAll().iterator().next();
        spedizioneDao.delete(sped);
        assertEquals(0, estrattoContoDao.findAll().size());
        abbonamentoDao.delete(abb);
        anagraficaDao.delete(tizio);

        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());

    }

    @Test
    public void testSpedizioneItemCRUD() {
        log.info("----------------->testSpedizioneItemCRUD<----------------");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Pubblicazione lodare =SmdHelper.getLodare();
        pubblicazioneDao.save(lodare);

        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        abb.setPregresso(new BigDecimal(10.0));
        abb.setImporto(new BigDecimal(15.0));        
        assertEquals(25.0, abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        assertEquals(1, abbonamentoDao.findAll().size());
        
        Spedizione sped = new Spedizione();
        sped.setAbbonamento(abb);
        sped.setDestinatario(tizio);

        spedizioneDao.save(sped);
        assertEquals(1, spedizioneDao.findAll().size());

        EstrattoConto ec = new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(lodare);
        ec.setDestinatario(tizio);
        estrattoContoDao.save(ec);
        assertEquals(1, estrattoContoDao.findAll().size());

        SpedizioneItem item = new SpedizioneItem();
        item.setEstrattoConto(ec);
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
        ec = estrattoContoDao.findAll().iterator().next();
        log.info(ec.toString());        
        estrattoContoDao.delete(ec);
        //Need to retrieve a fresh copy from database
        sped = spedizioneDao.findAll().iterator().next();
        log.info(sped.toString());
        assertEquals(0, spedizioneItemDao.count());
        spedizioneDao.delete(sped);
        assertEquals(0, estrattoContoDao.findAll().size());
        abbonamentoDao.delete(abb);
        anagraficaDao.delete(tizio);
        pubblicazioneDao.delete(lodare);

        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());

    }

    
    @Test 
    public void testAbbonamentoLoad() {
        log.info("----------------->testAbbonamentoLoad<----------------");
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        for (SpesaSpedizione ss : SmdHelper.getSpeseSpedizione()) {
            spesaSpedizioneDao.save(ss);
        }

        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        pubblicazioneDao.save(messaggio);

        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);

        EstrattoConto ec = new EstrattoConto();
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
        
        assertTrue(ec.isAbbonamentoAnnuale());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getEstrattoConto() == ec).forEach(item -> items.add(item)));
        assertEquals(messaggio.getMesiPubblicazione().size(), items.size());
        EnumSet<Mese> mesi = EnumSet.noneOf(Mese.class);
        for (SpedizioneItem item: items) {
            mesi.add(item.getMesePubblicazione());
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec.getNumero(), item.getNumero());
            assertEquals(ec, item.getEstrattoConto());
        }
        assertEquals(mesi, messaggio.getMesiPubblicazione());
        assertEquals(messaggio.getAbbonamento().doubleValue()*ec.getNumero(), abb.getTotale().doubleValue(),0);
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findByAbbonamento(abb).size());
        assertEquals(items.size(), spedizioneDao.findAll().size());
        assertEquals(items.size(), spedizioneItemDao.findByEstrattoConto(ec).size());
                        
        abbonamentoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        estrattoContoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().stream().forEach(msg -> log.info(msg.toString()));

        spedizioneItemDao.findAll().stream().forEach(item -> spedizioneItemDao.delete(item));
        estrattoContoDao.delete(ec);
        spedizioneDao.findByAbbonamento(abb).stream().forEach(sped -> spedizioneDao.delete(sped));
        abbonamentoDao.delete(abb);
        anagraficaDao.deleteAll();
        pubblicazioneDao.deleteAll();
        spesaSpedizioneDao.deleteAll();

        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
    }
    
    @Test
    public void testAbbonamentoAggiungiEstrattoConto() {
        log.info("----------------->testAbbonamentoAggiungiEstrattoConto<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(messaggio);
        pubblicazioneDao.save(lodare);
        pubblicazioneDao.save(blocchetti);
        EstrattoConto ec1 = new EstrattoConto();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.GIUGNO);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        EstrattoConto ec2 = new EstrattoConto();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);        
        ec2.setDestinatario(tizio);
        List<SpedizioneWithItems> spedizioni = Smd.genera(abb, ec1, new ArrayList<>(),SmdHelper.getSpeseSpedizione());
        spedizioni = Smd.genera(abb, ec2, spedizioni,SmdHelper.getSpeseSpedizione());
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec1);
        estrattoContoDao.save(ec2);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
       assertEquals(2, estrattoContoDao.findAll().size());
       assertEquals(6, spedizioneDao.findAll().size());
       assertEquals(12, spedizioneItemDao.findAll().size());

        abbonamentoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        estrattoContoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().stream().forEach(msg -> log.info(msg.toString()));

        EstrattoConto ec3 = new EstrattoConto();
        ec3.setAbbonamento(abb);
        ec3.setPubblicazione(blocchetti);
        ec3.setMeseInizio(Mese.GENNAIO);
        ec3.setAnnoInizio(anno);
        ec3.setMeseFine(Mese.DICEMBRE);
        ec3.setAnnoFine(anno);     
        ec3.setDestinatario(tizio);
        spedizioni = smdService.findByAbbonamento(abb);
        spedizioni = Smd.genera(abb,
                                          ec3,
                                          spedizioni,
                                          SmdHelper.getSpeseSpedizione());
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec3);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> {
                spedizioneItemDao.save(item);
            });
        });
        assertEquals(3, estrattoContoDao.findAll().size());        
        assertEquals(7, spedizioneDao.findAll().size());
        assertEquals(14, spedizioneItemDao.findAll().size());

        abbonamentoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        estrattoContoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        spedizioneItemDao.findAll().stream().forEach(msg -> log.info(msg.toString()));

        spedizioneItemDao.deleteAll();
        spedizioneDao.deleteAll();
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();

        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
 
    }

    @Test
    public void testAbbonamentoRimuoviEstrattoConto() {
        log.info("----------------->testAbbonamentoRimuoviEstrattoConto<----------------");
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(messaggio);
        pubblicazioneDao.save(lodare);
        pubblicazioneDao.save(blocchetti);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        
        EstrattoConto ec1 = new EstrattoConto();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.GIUGNO);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        EstrattoConto ec2 = new EstrattoConto();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        EstrattoConto ec3 = new EstrattoConto();
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
                     new ArrayList<SpedizioneWithItems>(),
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
        estrattoContoDao.save(ec1);
        estrattoContoDao.save(ec2);
        estrattoContoDao.save(ec3);        

        spedizioni.stream().forEach( sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });

        assertEquals(3, pubblicazioneDao.findAll().size());
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(3, estrattoContoDao.findAll().size());
        assertEquals(7, spedizioneDao.findAll().size());
        assertEquals(14, spedizioneItemDao.findAll().size());
        
        List<SpedizioneItem> deleted = Smd.rimuoviEC(abb,
                      ec2, 
                      spedizioni,
                      SmdHelper.getSpeseSpedizione());

        assertEquals(6, deleted.size());
        
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> {
               log.info(item.toString());
                spedizioneItemDao.save(item);
               });
        });
        for (SpedizioneItem delitem: deleted ) {
            log.info("deleted: " + delitem);
            spedizioneItemDao.deleteById(delitem.getId());
        }
        assertEquals(0, ec2.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec2.getImporto().doubleValue(),0);
        for (SpedizioneWithItems sped: spedizioni) {
            assertFalse(sped.getSpedizioneItems().isEmpty());
        }
        estrattoContoDao.deleteById(ec2.getId());
        abbonamentoDao.save(abb);
        assertEquals(3, pubblicazioneDao.findAll().size());
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(2, estrattoContoDao.findAll().size());
        assertEquals(7, spedizioneDao.findAll().size());
        assertEquals(8, spedizioneItemDao.findAll().size());
        
        log.info("----------------->testAbbonamentoRimuoviEstrattoConto Rimosso:"+ec2);

        
        spedizioni=smdService.findByAbbonamento(abb);
        log.info(abb.toString());
        log.info(ec1.toString());
        spedizioni.forEach(sped ->{
            log.info(sped.toString());
            sped.getSpedizioneItems().forEach(item -> log.info(item.toString()));
        });
        deleted = Smd.rimuoviEC(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, deleted.size());
        
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> {
               log.info(item.toString());
                spedizioneItemDao.save(item);
               });
        });
        for (SpedizioneItem delitem: deleted ) {
            log.info("deleted: " + delitem);
            spedizioneItemDao.deleteById(delitem.getId());
        }

        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        assertEquals(0, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec1.getImporto().doubleValue(),0);
        estrattoContoDao.deleteById(ec1.getId());
        abbonamentoDao.save(abb);
        assertEquals(3, pubblicazioneDao.findAll().size());
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findAll().size());
        assertEquals(2, spedizioneDao.findAll().size());
        assertEquals(2, spedizioneItemDao.findAll().size());
        

        spedizioni=smdService.findByAbbonamento(abb);
        deleted = Smd.rimuoviEC(abb,ec3, spedizioni,SmdHelper.getSpeseSpedizione());
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        for (SpedizioneItem delitem: deleted ) {
            log.info("deleted: " + delitem);
            spedizioneItemDao.deleteById(delitem.getId());
        }
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }
        assertEquals(0, ec3.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec3.getImporto().doubleValue(),0);
        estrattoContoDao.delete(ec3);
        abbonamentoDao.save(abb);
        assertEquals(3, pubblicazioneDao.findAll().size());
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        assertEquals(0, ec3.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec3.getImporto().doubleValue(),0);
        
        assertEquals(abb.getTotale().doubleValue(), 0,0);
        
        abbonamentoDao.delete(abb);
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());

        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();
    }

    @Test
    public void testAbbonamentoRimuoviEstrattoContoConSpediti() {
        log.info("----------------->testAbbonamentoRimuoviEstrattoContoConSpediti<----------------");
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoCorrente(), Cassa.Ccp);
        
        Pubblicazione lodare = SmdHelper.getLodare();
        pubblicazioneDao.save(lodare);
        EstrattoConto ec1 = new EstrattoConto();
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
        assertTrue(ec1.isAbbonamentoAnnuale());
        
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec1);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        log.info(abb.toString());
        log.info(ec1.toString());
        
        spedizioni.stream()
        .filter(sped -> Mese.getMeseCorrente() == sped.getSpedizione().getMeseSpedizione())
        .forEach(sped -> {
          assertEquals(sped.getSpedizione().getAnnoSpedizione(), Anno.getAnnoCorrente());
          sped.getSpedizione().setStatoSpedizione(StatoSpedizione.INVIATA);
          spedizioneDao.save(sped.getSpedizione());
          log.info(sped.toString());
        });

        int spedanticipate = 12 - spedizioni.size()+1;
        spedizioni=smdService.findByAbbonamento(abb);
        List<SpedizioneItem> deletedItems = Smd.rimuoviEC(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        for (SpedizioneItem deletedItem:deletedItems) {
            assertEquals(StatoSpedizione.PROGRAMMATA, deletedItem.getSpedizione().getStatoSpedizione());
            assertEquals(ec1.getId(), deletedItem.getEstrattoConto().getId());
            log.info("deleted: " + deletedItem);
            spedizioneItemDao.deleteById(deletedItem.getId());            
        }
        for (SpedizioneWithItems sped:spedizioni) {
            if (sped.getSpedizioneItems().isEmpty()) {
                spedizioneDao.deleteById(sped.getSpedizione().getId());
            }
        }        
        spedizioni=smdService.findByAbbonamento(abb);
        assertEquals(1, spedizioni.size());
        SpedizioneWithItems inviata = spedizioni.iterator().next();
        assertEquals(inviata.getSpedizione().getAnnoSpedizione(), Anno.getAnnoCorrente());
        assertEquals(StatoSpedizione.INVIATA, inviata.getSpedizione().getStatoSpedizione());
        assertEquals(spedanticipate, inviata.getSpedizioneItems().size());
        inviata.getSpedizioneItems().forEach(item ->log.info(item.toString()));

        log.info(abb.toString());
        log.info(ec1.toString());
        log.info(inviata.toString());
        
        for (SpedizioneItem item: inviata.getSpedizioneItems()) {
            spedizioneItemDao.deleteById(item.getId());
        }
        spedizioneDao.deleteById(inviata.getSpedizione().getId());
        estrattoContoDao.deleteById(ec1.getId());
        abbonamentoDao.delete(abb);
        
    }

    @Test
    public void testAbbonamentoAggiornaEstrattoConto() {
        log.info("----------------->testAbbonamentoAggiornaEstrattoConto<----------------");
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());        
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        Anagrafica tizio = SmdHelper.getGP();
        anagraficaDao.save(tizio);
        
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, anno, Cassa.Ccp);
        
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        pubblicazioneDao.save(messaggio);
        EstrattoConto ec1 = new EstrattoConto();
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
        assertTrue(ec1.isAbbonamentoAnnuale());
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec1);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findAll().size());
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
        ec1.setNumero(10);
        List<SpedizioneItem> rimItems = Smd.aggiornaEC(abb, ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        
        assertEquals(11, rimItems.size());
        abbonamentoDao.save(abb);
        estrattoContoDao.save(ec1);
        spedizioni.stream().forEach(sped -> {
            spedizioneDao.save(sped.getSpedizione());
            sped.getSpedizioneItems().stream().forEach(item -> spedizioneItemDao.save(item));
        });
        rimItems.forEach(rimitem -> spedizioneItemDao.deleteById(rimitem.getId()));

        spedizioneDao.findAll().forEach(s -> {
            assertEquals(0, s.getSpesePostali().doubleValue(),0);
            assertEquals(InvioSpedizione.Spedizioniere, s.getInvioSpedizione());
            assertEquals(1, spedizioneItemDao.findBySpedizione(s).size());
            SpedizioneItem item = spedizioneItemDao.findBySpedizione(s).iterator().next();
            assertEquals(10, item.getNumero().intValue());
        });

        assertEquals(messaggio.getAbbonamento().multiply(new BigDecimal(10)).doubleValue(), abb.getTotale().doubleValue(),0);
        assertEquals(0, abb.getSpese().doubleValue(),0);
        
        spedizioneItemDao.deleteAll();
        spedizioneDao.deleteAll();
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();
        
        
    }

    @Test
    public void testCampagnaCRUD() {
        log.info("----------------->testCampagnaCRUD<----------------");
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(messaggio);
        pubblicazioneDao.save(blocchetti);
        assertEquals(2, pubblicazioneDao.findAll().size());
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
        
        pubblicazioneDao.deleteAll();
        assertEquals(0, pubblicazioneDao.findAll().size());
        
        
        
        
        
    }

    @Test 
    public void testVersamentoCRUD() {
        log.info("----------------->testVersamentoCRUD<----------------");
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        Incasso incasso = SmdHelper.getIncassoTelematici();
        incassoDao.save(incasso);
        incasso.getVersamenti().stream().forEach(v -> versamentoDao.save(v));
        
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());
                
        incassoDao.delete(incasso);
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
                
    }
    
    @Test
    public void testIncassa() {
        log.info("----------------->testIncassa<----------------");

        Anagrafica davidePalma = SmdHelper.getDP();
        anagraficaDao.save(davidePalma);

        Pubblicazione b = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(b);

        Abbonamento abb = SmdHelper.getAbbonamentoBy(
                            davidePalma, 
                            Anno.getAnnoCorrente(), 
                            Cassa.Ccp
                            );
        
        EstrattoConto ec = new EstrattoConto();
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
        estrattoContoDao.save(ec);
        
        Incasso incasso = SmdHelper.getIncassoByImportoAndCodeLine(abb.getTotale(), abb.getCodeLine());
        incassoDao.save(incasso);
        incasso.getVersamenti().stream().forEach(v-> {
            versamentoDao.save(v);
        });

        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());

        incasso.getVersamenti().stream().forEach(v-> {
        	assertEquals(abb.getCodeLine(), v.getCodeLine());
            Smd.incassa(incasso,v, abb);
            versamentoDao.save(v);
        });
        incassoDao.save(incasso);
        abbonamentoDao.save(abb);

        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, incassoDao.findAll().size());
        assertEquals(1, versamentoDao.findAll().size());

        assertEquals(0, abb.getResiduo().doubleValue(),0);
        assertEquals(0, incasso.getResiduo().doubleValue(),0);
        assertEquals(Incassato.Si, Smd.getStatoIncasso(abb));
        
        Versamento versamento = versamentoDao.findAll().iterator().next();        
        Abbonamento abbonamento = abbonamentoDao.findByCodeLine(versamento.getCodeLine());
        assertNotNull(abbonamento);
        assertEquals(versamento.getCodeLine(), abbonamento.getCodeLine());
        assertEquals(StatoAbbonamento.Nuovo, abbonamento.getStatoAbbonamento());
        assertEquals(Incassato.Si, Smd.getStatoIncasso(abbonamento));
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        incassoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();
        
    }

    @Test
    public void testIncassaAbbonamento() throws Exception {
        log.info("----------------->testIncassaAbbonamento<----------------");

        Anagrafica davidePalma = SmdHelper.getDP();
        anagraficaDao.save(davidePalma);
        Anagrafica antonioRusso = SmdHelper.getAR();
        anagraficaDao.save(antonioRusso);

        Pubblicazione b = SmdHelper.getBlocchetti();
        pubblicazioneDao.save(b);
        
        SmdHelper.getSpeseSpedizione().forEach(ss -> spesaSpedizioneDao.save(ss));
        
        Abbonamento abb1 = SmdHelper.getAbbonamentoBy(
                                                     davidePalma, 
                                                     Anno.getAnnoCorrente(), 
                                                     Cassa.Ccp
                                                     );
        EstrattoConto ec1 = new EstrattoConto();
        ec1.setPubblicazione(b);
        ec1.setNumero(2);
        ec1.setAbbonamento(abb1);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(Anno.getAnnoProssimo());
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(Anno.getAnnoProssimo());
        ec1.setDestinatario(davidePalma);

        smdService.genera(abb1, ec1);

        assertEquals(1, abbonamentoDao.findAll().size());
        assertEquals(1, estrattoContoDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());


        Abbonamento abb2 = SmdHelper.getAbbonamentoBy(
                                                      antonioRusso, 
                                                      Anno.getAnnoCorrente(), 
                                                      Cassa.Ccp
                                                      );
        EstrattoConto ec2 = new EstrattoConto();
        ec2.setPubblicazione(b);
        ec2.setNumero(2);
        ec2.setAbbonamento(abb1);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(Anno.getAnnoProssimo());
        ec2.setMeseFine(Mese.DICEMBRE);
        ec2.setAnnoFine(Anno.getAnnoProssimo());
        ec2.setDestinatario(antonioRusso);

        smdService.genera(abb2, ec2);

        assertEquals(2, abbonamentoDao.findAll().size());
        assertEquals(2, estrattoContoDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());

        Date date = new Date();
        abb1.setDataPagamento(date);
        abb1.setDataContabile(date);
        assertEquals(abb1.getTotale().doubleValue(), abb1.getResiduo().doubleValue(),0);
        smdService.incassa(abb1,abb1.getTotale(),userInfoDao.findByUsername("adp"));
        assertEquals(BigDecimal.ZERO.doubleValue(), abb1.getResiduo().doubleValue(),0);
        assertEquals(1, incassoDao.count());
        assertEquals(1, versamentoDao.count());
        
        Incasso inc1 = incassoDao.findAll().iterator().next();
        System.out.println(inc1.getDataContabile());
        System.out.println(Smd.getStandardDate(abb1.getDataContabile()));
    Incasso incasso = 
        incassoDao
        .findByDataContabileAndCassaAndCcpAndCuas(
              abb1.getDataContabile(),
              abb1.getCassa(), 
              abb1.getCcp(), 
              abb1.getCuas());

        assertNotNull(incasso);
        abb2.setDataPagamento(date);
        abb2.setDataContabile(date);
        assertEquals(abb2.getTotale().doubleValue(), abb2.getResiduo().doubleValue(),0);
        smdService.incassa(abb2,abb2.getTotale(),userInfoDao.findByUsername("adp"));
        assertEquals(BigDecimal.ZERO.doubleValue(), abb2.getResiduo().doubleValue(),0);
        assertEquals(1, incassoDao.count());
        assertEquals(2, versamentoDao.count());

        

        spedizioneItemDao.deleteAll();
        spedizioneDao.deleteAll();
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        incassoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        spesaSpedizioneDao.deleteAll();
        anagraficaDao.deleteAll();
        
    }

    @Test
    public void testSmdLoadSampleData() throws Exception {
        
        log.info("----------------->testSmdLoadSampleData<----------------");

        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        assertEquals(0, campagnaDao.findAll().size());
        assertEquals(0, campagnaItemDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, operazioneDao.findAll().size());
        assertEquals(2, userInfoDao.findAll().size());

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
                                         ).run();
        
        assertEquals(24, anagraficaDao.findAll().size());
        assertEquals(4, pubblicazioneDao.findAll().size());
        assertEquals(20, spesaSpedizioneDao.findAll().size());
        assertEquals(12, storicoDao.findAll().size());
        assertEquals(0, campagnaDao.findAll().size());
        assertEquals(20, abbonamentoDao.findAll().size());
        assertEquals(30, estrattoContoDao.findAll().size());
                      
        assertEquals(5, incassoDao.findAll().size());
        assertEquals(24, versamentoDao.findAll().size());
        
        for (Versamento versamento: versamentoDao.findAll()) {
            if (versamento.getCodeLine() == null) {
                continue;
            }
            System.out.println(versamento);
            Abbonamento abbonamento = abbonamentoDao.findByCodeLine(versamento.getCodeLine());
            if (abbonamento == null) {
                continue;
            }
            System.out.println(abbonamento);
            assertNull(abbonamento.getVersamento());
            assertEquals(0, versamento.getIncassato().doubleValue(),0);
            assertEquals(0, abbonamento.getIncassato().doubleValue(),0);
            smdService.incassa(abbonamento,versamento,userInfoDao.findByUsername("admin"));
            assertEquals(0, versamento.getResiduo().doubleValue(),0);
        }

        for (Abbonamento abbonamento: abbonamentoDao.findByAnno(Anno.ANNO2017)) {
            assertEquals(0, abbonamento.getResiduo().doubleValue(),0);
            assertNull(abbonamento.getVersamento());
            abbonamento.setPregresso(new BigDecimal("10.50"));
            abbonamentoDao.save(abbonamento);
            assertEquals(abbonamento.getPregresso().doubleValue(), abbonamento.getResiduo().doubleValue(),0);
        }
        
        smdService.incassaCodeLine(incassoDao.findAll(),userInfoDao.findByUsername("admin"));
        
        for (Abbonamento abbonamento: abbonamentoDao.findByAnno(Anno.ANNO2017)) {
            assertNull(abbonamento.getVersamento());
            assertEquals(abbonamento.getPregresso().doubleValue(), abbonamento.getResiduo().doubleValue(),0);
        }

        operazioneDao.deleteAll();
        spedizioneItemDao.deleteAll();
        spedizioneDao.deleteAll();
        estrattoContoDao.deleteAll();
        abbonamentoDao.deleteAll();
        
        campagnaItemDao.deleteAll();
        campagnaDao.deleteAll();
        notaDao.deleteAll();
        storicoDao.deleteAll();

        versamentoDao.deleteAll();
        incassoDao.deleteAll();
        
        pubblicazioneDao.deleteAll();  
        anagraficaDao.deleteAll();
        spesaSpedizioneDao.deleteAll();
        
        log.info("final check load data");
        assertEquals(0, anagraficaDao.findAll().size());
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        assertEquals(0, campagnaDao.findAll().size());
        assertEquals(0, campagnaItemDao.findAll().size());
        assertEquals(0, abbonamentoDao.findAll().size());
        assertEquals(0, estrattoContoDao.findAll().size());
        assertEquals(0, spedizioneDao.findAll().size());
        assertEquals(0, spedizioneItemDao.findAll().size());
        assertEquals(0, incassoDao.findAll().size());
        assertEquals(0, versamentoDao.findAll().size());
        assertEquals(0, operazioneDao.findAll().size());
        assertEquals(2, userInfoDao.findAll().size());

    }
}
