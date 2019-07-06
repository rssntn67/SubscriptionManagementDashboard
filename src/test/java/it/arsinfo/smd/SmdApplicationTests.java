package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
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
import it.arsinfo.smd.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.ui.security.RedirectAuthenticationSuccessHandler;
import it.arsinfo.smd.ui.security.SecurityConfig;
import it.arsinfo.smd.ui.security.UserDetailsServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmdApplicationTests {

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
    private SpesaSpedizioneDao spesaSpedizioneDao;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    @Test
    public void testAutowire() {
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
        assertNotNull(spesaSpedizioneDao);

        assertNotNull(securityConfig);
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof UserDetailsServiceImpl);
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        assertNotNull(authenticationSuccessHandler);
        assertTrue(authenticationSuccessHandler instanceof RedirectAuthenticationSuccessHandler);        
    }
    
    @Test
    public void testLoginAdmin() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin", "admin");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }

        UserInfo admin = userInfoDao.findById(1L).get();
        assertEquals("admin", admin.getUsername());

    }

    @Test
    public void testUserInfo() {
        UserInfo adp = new UserInfo("adp", passwordEncoder.encode("adp"), Role.LOCKED);
        userInfoDao.save(adp);
        
        UserInfo user = new UserInfo("user", passwordEncoder.encode("pass"), Role.USER);
        userInfoDao.save(user);
        
        assertEquals(3, userInfoDao.findAll().size());
        Authentication auth =
                new UsernamePasswordAuthenticationToken("adp", "adp");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }
        
        auth =
                new UsernamePasswordAuthenticationToken("user", "pass");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }
        
        userInfoDao.delete(user);
        userInfoDao.deleteById(adp.getId());

        assertEquals(1, userInfoDao.findAll().size());
        
    }        

    @Test
    public void testAnagraficaCRUD() {
        assertEquals(0, anagraficaDao.findAll().size());
        Anagrafica antonioRusso =  SmdLoadSampleData.getAR();
        anagraficaDao.save(antonioRusso);
        assertEquals(1, anagraficaDao.findAll().size());
        
        assertNotNull(anagraficaDao.findById(antonioRusso.getId()));
        assertEquals(1,anagraficaDao.findByCognomeContainingIgnoreCase("us").size());
        assertEquals(0,anagraficaDao.findByCognomeContainingIgnoreCase("Rosso").size());
        assertEquals(1,anagraficaDao.findByDiocesi(Diocesi.DIOCESI116).size());
        assertEquals(0,anagraficaDao.findByDiocesi(Diocesi.DIOCESI115).size());
        
        Anagrafica diocesiMilano = SmdLoadSampleData.getDiocesiMi();
        anagraficaDao.save(diocesiMilano);
        assertEquals(2, anagraficaDao.findAll().size());
        
        assertEquals(1,anagraficaDao.findByCognomeContainingIgnoreCase("ar").size());
        assertEquals(1,anagraficaDao.findByCognomeContainingIgnoreCase("mi").size());
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
        Anagrafica diocesiMilano = SmdLoadSampleData.getDiocesiMi();
        anagraficaDao.save(diocesiMilano);
        assertEquals(1, anagraficaDao.findAll().size());
        
        Anagrafica ar = SmdLoadSampleData.getAR();
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
    public void testPubblicazioneDaoCRUD() {
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
        assertEquals(0, spesaSpedizioneDao.findAll().size());

        SpesaSpedizione s1p = new SpesaSpedizione();
        s1p.setPubblicazione(p);
        s1p.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
        s1p.setNumero(1);
        s1p.setSpeseSpedizione(new BigDecimal("4.45"));
        p.addSpesaSpedizione(s1p);
        spesaSpedizioneDao.save(s1p);
        
        assertEquals(1, p.getSpeseSpedizione().size());
        assertEquals(1, pubblicazioneDao.findAll().size());
        assertEquals(1, spesaSpedizioneDao.findAll().size());

        SpesaSpedizione s2p = new SpesaSpedizione();
        s2p.setPubblicazione(p);
        s2p.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        s2p.setNumero(1);
        s2p.setSpeseSpedizione(new BigDecimal("7.45"));
        p.addSpesaSpedizione(s2p);
        spesaSpedizioneDao.save(s2p);

        assertEquals(1, pubblicazioneDao.findAll().size());
        assertEquals(2, spesaSpedizioneDao.findAll().size());

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
        
        SpesaSpedizione sp1 = new SpesaSpedizione();
        sp1.setPubblicazione(p1);
        sp1.setAreaSpedizione(AreaSpedizione.Italia);
        sp1.setNumero(1);
        sp1.setSpeseSpedizione(new BigDecimal("1.50"));
        p1.addSpesaSpedizione(sp1);
        pubblicazioneDao.save(p1);
        spesaSpedizioneDao.save(sp1);
        
        assertEquals(2, pubblicazioneDao.findAll().size());
        assertEquals(3, spesaSpedizioneDao.findAll().size());

        anagraficaDao.findAll().stream().forEach( msg -> log.info(msg.toString()));
        spesaSpedizioneDao.findAll().stream().forEach( msg -> log.info(msg.toString()));

        List<Pubblicazione> ff = pubblicazioneDao.findByNomeStartsWithIgnoreCase("Pr");
        assertEquals(1, ff.size());
        Pubblicazione ffp = ff.iterator().next();
        assertEquals(p.getId(), ffp.getId());
        assertEquals("prova", ffp.getNome());
        log.info(ffp.toString());
        ffp.getSpeseSpedizione().stream().forEach(ss -> log.info(ss.toString()));
        assertEquals(2, ffp.getSpeseSpedizione().size());
        
        assertEquals(2, pubblicazioneDao.findByTipo(TipoPubblicazione.MENSILE).size());
        assertEquals(0, pubblicazioneDao.findByTipo(TipoPubblicazione.SEMESTRALE).size());
        
        List<SpesaSpedizione> sps = spesaSpedizioneDao.findByPubblicazione(p);
        assertEquals(2, sps.size());
        sps.stream().forEach(spsp -> assertEquals(p.getId().longValue(), spsp.getPubblicazione().getId().longValue()));

        assertEquals(1, 
             spesaSpedizioneDao.findByAreaSpedizioneAndNumero(AreaSpedizione.Italia, 1).size());
        assertEquals(1, 
                     spesaSpedizioneDao.findByAreaSpedizioneAndNumero(AreaSpedizione.AmericaAfricaAsia, 1).size());
        assertEquals(1, 
                     spesaSpedizioneDao.findByAreaSpedizioneAndNumero(AreaSpedizione.EuropaBacinoMediterraneo, 1).size());
        assertEquals(0, 
                     spesaSpedizioneDao.findByAreaSpedizioneAndNumero(AreaSpedizione.Italia, 2).size());

        pubblicazioneDao.delete(p1);
        assertEquals(1, pubblicazioneDao.findAll().size());
        assertEquals(2, spesaSpedizioneDao.findAll().size());
        pubblicazioneDao.delete(p);
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, spesaSpedizioneDao.findAll().size());
               
    }

    @Test 
    public void testPubblicazioniAdp() {
        
        assertEquals(0, pubblicazioneDao.findAll().size());

        Pubblicazione m = SmdLoadSampleData.getMessaggio();
        pubblicazioneDao.save(m);
        m.getSpeseSpedizione().forEach(sps -> spesaSpedizioneDao.save(sps));
        
        Pubblicazione l = SmdLoadSampleData.getLodare();
        pubblicazioneDao.save(l);
        l.getSpeseSpedizione().forEach(sps -> spesaSpedizioneDao.save(sps));
        
        Pubblicazione b =SmdLoadSampleData.getBlocchetti();
        pubblicazioneDao.save(b);
        b.getSpeseSpedizione().forEach(sps -> spesaSpedizioneDao.save(sps));
        
        Pubblicazione e = SmdLoadSampleData.getEstratti();
        pubblicazioneDao.save(e);
        e.getSpeseSpedizione().forEach(sps -> spesaSpedizioneDao.save(sps));
        
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
            assertEquals(Smd.getAnnoCorrente(), pubblicazione.getAnno());
            assertTrue(pubblicazione.isActive());
            log.info(pubblicazione.toString());
            for (SpesaSpedizione spesaSped: pubblicazione.getSpeseSpedizione()) {
                log.info(spesaSped.toString());
            }
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
        assertEquals(Mese.LUGLIO, pubs.iterator().next());
        assertFalse(estratti.isGen());
        assertFalse(estratti.isFeb());
        assertFalse(estratti.isMar());
        assertFalse(estratti.isApr());
        assertFalse(estratti.isMag());
        assertFalse(estratti.isGiu());
        assertTrue(estratti.isLug());
        assertFalse(estratti.isAgo());
        assertFalse(estratti.isSet());
        assertFalse(estratti.isOtt());
        assertFalse(estratti.isNov());
        assertFalse(estratti.isDic());
        
        SpesaSpedizione spesaSpedizioneEstrattiItalia = estratti.getSpesaSpedizioneBy(AreaSpedizione.Italia, 1);
        assertNotNull(spesaSpedizioneEstrattiItalia);
        log.info(spesaSpedizioneEstrattiItalia.toString());
        log.info("");

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
        SpesaSpedizione ssMessaggioAmerica = messaggio.getSpesaSpedizioneBy(AreaSpedizione.AmericaAfricaAsia, 2);
        assertNotNull(ssMessaggioAmerica);
        log.info(ssMessaggioAmerica.toString());
        log.info("");

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
        assertFalse(blocchetti.isGen());
        assertFalse(blocchetti.isFeb());
        assertTrue(blocchetti.isMar());
        assertFalse(blocchetti.isApr());
        assertFalse(blocchetti.isMag());
        assertFalse(blocchetti.isGiu());
        assertFalse(blocchetti.isLug());
        assertFalse(blocchetti.isAgo());
        assertTrue(blocchetti.isSet());
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
        Storico storico = new Storico();
        
        try {
            storicoDao.save(storico);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica antonioRusso = SmdLoadSampleData.getAR();
        anagraficaDao.save(antonioRusso);
        
        storico.setDestinatario(antonioRusso);

        try {
            storicoDao.save(storico);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Anagrafica matteoParo = SmdLoadSampleData.getMP();
        anagraficaDao.save(matteoParo);
        
        storico.setIntestatario(matteoParo);

        try {
            storicoDao.save(storico);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(0, storicoDao.findAll().size());
            log.info(e.getMessage());
        }
        
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        pubblicazioneDao.save(messaggio);
        
        storico.setPubblicazione(messaggio);
        storicoDao.save(storico);
        assertEquals(1, storicoDao.findAll().size());
        
        //seems not updating id from...
        storico = storicoDao.findAll().iterator().next();

        Pubblicazione lodare = SmdLoadSampleData.getLodare();
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
        nota.setStorico(storico);
        nota.setDescription("Test Nota");
        notaDao.save(nota);
        assertEquals(1, notaDao.findAll().size());
        
        Nota nota1 = new Nota();
        nota1.setStorico(storico1);
        nota1.setDescription("Storico");
        notaDao.save(nota1);
        assertEquals(2, notaDao.findAll().size());
        
        storico.setStatoStorico(StatoStorico.SOSPESO);
        storicoDao.save(storico);
        Nota nota2 = new Nota();
        nota2.setStorico(storico);
        nota2.setDescription("Aggiornato Stato a SOSPESO data......");
        notaDao.save(nota2);
        assertEquals(3, notaDao.findAll().size());
        assertEquals(2, storicoDao.findAll().size());

        assertEquals(0, storicoDao.findByStatoStorico(StatoStorico.VALIDO).size());
        assertEquals(1, storicoDao.findByStatoStorico(StatoStorico.NUOVO).size());
        assertEquals(1, storicoDao.findByStatoStorico(StatoStorico.SOSPESO).size());

        assertEquals(1, storicoDao.findByInvio(Invio.Intestatario).size());
        assertEquals(1, storicoDao.findByInvio(Invio.Destinatario).size());
        
        assertEquals(2, storicoDao.findByCassa(Cassa.Ccp).size());
        assertEquals(0, storicoDao.findByCassa(Cassa.Contrassegno).size());
        
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.AdpSede).size());
        assertEquals(1, storicoDao.findByInvioSpedizione(InvioSpedizione.Spedizioniere).size());

        assertEquals(1, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.Ordinario).size());
        assertEquals(1, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.OmaggioGesuiti).size());
        assertEquals(0, storicoDao.findByTipoEstrattoConto(TipoEstrattoConto.OmaggioDirettoreAdp).size());

        assertEquals(1, storicoDao.findByDestinatario(antonioRusso).size());
        assertEquals(1, storicoDao.findByDestinatario(matteoParo).size());

        assertEquals(2, storicoDao.findByIntestatario(matteoParo).size());
        assertEquals(0, storicoDao.findByIntestatario(antonioRusso).size());

        assertEquals(1, storicoDao.findByPubblicazione(lodare).size());
        assertEquals(1, storicoDao.findByPubblicazione(messaggio).size());
        
        assertEquals(1, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, antonioRusso, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, antonioRusso, lodare).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, matteoParo, messaggio).size());
        assertEquals(1, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(matteoParo, matteoParo, lodare).size());

        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(antonioRusso, matteoParo, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(antonioRusso, matteoParo, lodare).size());

        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(antonioRusso, antonioRusso, messaggio).size());
        assertEquals(0, storicoDao.findByIntestatarioAndDestinatarioAndPubblicazione(antonioRusso, antonioRusso, lodare).size());
        
        assertEquals(1, notaDao.findByStorico(storico1).size());
        assertEquals(2, notaDao.findByStorico(storico).size());
        
        assertEquals(1, notaDao.findByDescriptionContainingIgnoreCase("sospeso").size());

        storicoDao.findAll().stream().forEach(msg -> log.info(msg.toString()));
        notaDao.findAll().stream().forEach(msg -> log.info(msg.toString()));

        notaDao.delete(nota2);
        assertEquals(2, notaDao.findAll().size());
        assertEquals(2, storicoDao.findAll().size());
        
        storicoDao.delete(storico);
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());
        
        storico1 = storicoDao.findAll().iterator().next();
        storicoDao.delete(storico1);
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
        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());
        
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        pubblicazioneDao.save(blocchetti);
        Anagrafica matteo = SmdLoadSampleData.getMS();
        anagraficaDao.save(matteo);
        
        Storico storico = SmdLoadSampleData.getStoricoBy(matteo, matteo, blocchetti, 100, Cassa.Carte, TipoEstrattoConto.Sostenitore, Invio.Destinatario, InvioSpedizione.AdpSede);
        storicoDao.save(storico);
        storico.getNote().stream().forEach(nota -> notaDao.save(nota));
        assertEquals(1, notaDao.findAll().size());
        assertEquals(1, storicoDao.findAll().size());
        
        storicoDao.deleteAll();
        pubblicazioneDao.deleteAll();
        anagraficaDao.deleteAll();

        assertEquals(0, notaDao.findAll().size());
        assertEquals(0, storicoDao.findAll().size());        
        assertEquals(0, pubblicazioneDao.findAll().size());
        assertEquals(0, anagraficaDao.findAll().size());
                
    }
    
    @Test 
    public void testCampagnaCRUD() {
    }
}
