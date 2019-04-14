package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

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

import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Prospetto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.security.RedirectAuthenticationSuccessHandler;
import it.arsinfo.smd.security.SecurityConfig;
import it.arsinfo.smd.security.UserDetailsServiceImpl;

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
    private SpedizioneDao spedizioneDao;
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
    private ProspettoDao prospettoDao;
    @Autowired
    private NotaDao notaDao;
    @Autowired
    private UserInfoDao userInfoDao;

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
    public void testContextLoads() {
        assertNotNull(abbonamentoDao);
        assertNotNull(anagraficaDao);
        assertNotNull(pubblicazioneDao);
        assertNotNull(spedizioneDao);
        assertNotNull(storicoDao);
        assertNotNull(notaDao);
        assertNotNull(campagnaDao);
        assertNotNull(campagnaItemDao);
        assertNotNull(notaDao);
        assertNotNull(storicoDao);
        assertNotNull(versamentoDao);
        assertNotNull(incassoDao);
        assertNotNull(operazioneDao);
        assertNotNull(prospettoDao);
        assertNotNull(userInfoDao);
        assertNotNull(securityConfig);
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof UserDetailsServiceImpl);
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        assertNotNull(authenticationSuccessHandler);
        assertTrue(authenticationSuccessHandler instanceof RedirectAuthenticationSuccessHandler);
   }

    @Test 
    public void testAuthenticateAdmin() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin", "admin");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }
    }
    
    @Test 
    public void testAuthenticateAdp() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("adp", "adp");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void testSmd() {
        new SmdLoadSampleData(
                              anagraficaDao, 
                              storicoDao, 
                              pubblicazioneDao, 
                              abbonamentoDao, 
                              spedizioneDao, 
                              campagnaDao, 
                              incassoDao, 
                              versamentoDao, 
                              operazioneDao,
                              prospettoDao,
                              userInfoDao,
                              passwordEncoder).run();

        Authentication auth =
                new UsernamePasswordAuthenticationToken("adp", "adp");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }

        log.info("Pubblicazioni found with findAll():");
        log.info("-------------------------------");
        List<Pubblicazione> pubblicazioni = pubblicazioneDao.findAll();
        assertEquals(4, pubblicazioni.size());
        for (Pubblicazione pubblicazione : pubblicazioni) {
            assertEquals("AAVV", pubblicazione.getAutore());
            assertEquals("ADP", pubblicazione.getEditore());
            assertEquals(Smd.getAnnoCorrente(), pubblicazione.getAnno());
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
        assertEquals(Mese.LUGLIO, estratti.getMese());
        assertEquals(TipoPubblicazione.ANNUALE, estratti.getTipo());
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

        Pubblicazione first = pubblicazioneDao.findById(2L).get();
        log.info("Messaggio found with findOne(2L):");
        log.info("--------------------------------");
        assertEquals(Long.parseLong("2"), first.getId().longValue());
        assertEquals(TipoPubblicazione.MENSILE, first.getTipo());
        assertEquals("Messaggio", first.getNome());
        log.info(first.toString());
        log.info("");

        Pubblicazione second = pubblicazioneDao.findById(3L).get();
        log.info("lodare found with findOne(3L):");
        log.info("--------------------------------");
        assertEquals(Long.parseLong("3"), second.getId().longValue());
        assertEquals(TipoPubblicazione.MENSILE, second.getTipo());
        assertEquals("Lodare e Servire", second.getNome());
        log.info(second.toString());
        log.info("");

        log.info("Anagrafica found with findAll():");
        log.info("-------------------------------");
        List<Anagrafica> anagrafiche = anagraficaDao.findAll();
        assertEquals(6, anagrafiche.size());
        for (Anagrafica customer : anagrafiche) {
            log.info(customer.toString());
        }
        log.info("");

        log.info("Anagrafica Russo found with findOne(6L):");
        log.info("--------------------------------------------");
        Anagrafica russo = anagraficaDao.findById(6L).get();
        assertEquals("Russo", russo.getCognome());
        log.info(russo.toString());
        log.info("");

        log.info("Anagrafica found with findByLastNameStartsWithIgnoreCase('Russo'):");
        log.info("--------------------------------------------");
        for (Anagrafica ana : anagraficaDao.findByCognomeContainingIgnoreCase("rUsSo")) {
            assertEquals("Russo", ana.getCognome());
            log.info(ana.toString());
        }
        log.info("");

        log.info("Anagrafica found with findByDiocesi('ROMA'):");
        log.info("--------------------------------------------");
        for (Anagrafica roma : anagraficaDao.findByDiocesi(Diocesi.DIOCESI168)) {
            assertEquals(Diocesi.DIOCESI168, roma.getDiocesi());
            log.info(roma.toString());
        }
        log.info("");

        log.info("Storico found with findByIntestatario('michele santoro id=17'):");
        log.info("--------------------------------------------");
        Anagrafica ms = anagraficaDao.findByCognomeContainingIgnoreCase("Santoro").iterator().next();
        for (Storico anp : storicoDao.findByIntestatario(ms)) {
            log.info(anp.toString());
        }
        log.info("");

        log.info("Storico found with findByDestinatario('davide palma id=15'):");
        Anagrafica dp = anagraficaDao.findByCognomeContainingIgnoreCase("Palma").iterator().next();
        log.info("--------------------------------------------");
        for (Storico anp : storicoDao.findByDestinatario(dp)) {
            log.info(anp.toString());
        }
        log.info("");

        log.info("Storico found with findByPubblicazione('blocchetti'):");
        log.info("--------------------------------------------");
        Pubblicazione blocchetti = pubblicazioneDao.findByNomeStartsWithIgnoreCase("blocchetti").iterator().next();
        for (Storico anp : storicoDao.findByPubblicazione(blocchetti)) {
            log.info(anp.toString());
        }
        log.info("");

        log.info("Abbonamenti found with findAll():");
        log.info("-------------------------------");
        for (Abbonamento abbonamento : abbonamentoDao.findAll()) {
            log.info(abbonamento.toString());
            for (Spedizione spedizione: abbonamento.getSpedizioni()) {
                log.info(spedizione.toString());
                
            }
        }
        log.info("");

        log.info("Abbonamenti found with findByIntestatario(ms):");
        log.info("-------------------------------");
        for (Abbonamento abbonamentomd : abbonamentoDao.findByIntestatario(ms)) {
            log.info(abbonamentomd.toString());
        }
        log.info("");

        log.info("Campagna found with findAll():");
        log.info("-------------------------------");
        for (Campagna campagna : campagnaDao.findAll()) {
            log.info(campagna.toString());
        }
        log.info("");

        log.info("Versamenti found with findAll():");
        log.info("-------------------------------");
        for (Versamento versamento : versamentoDao.findAll()) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("Incassi found with findAll():");
        log.info("-------------------------------");
        for (Incasso incasso : incassoDao.findAll()) {
            log.info(incasso.toString());
        }
        log.info("");

        log.info("Versamenti found by findByIncasso(incasso1):");
        log.info("-------------------------------");
        Incasso incasso1 = incassoDao.findByCuas(Cuas.TELEMATICI).iterator().next();
        for (Versamento versamento : versamentoDao.findByIncasso(incasso1)) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("Versamenti found by findByImporto(new BigDecimal(\"40.00\"):");
        log.info("-------------------------------");
        for (Versamento versamento : versamentoDao.findByImporto(new BigDecimal("40.00"))) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("Versamenti found by data contabile 2017-ott-06:");
        log.info("-------------------------------");
        for (Versamento versamento : versamentoDao.findByDataContabile(Smd.getStandardDate("171006"))) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("Versamenti found by data pagamento 2017-ott-03:");
        log.info("-------------------------------");
        for (Versamento versamento : versamentoDao.findByDataPagamento(Smd.getStandardDate("171003"))) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("Incassi found by CUAS.VENEZIA:");
        log.info("-------------------------------");
        for (Incasso incasso : incassoDao.findByCuas(Cuas.VENEZIA)) {
            log.info(incasso.toString());
        }
        log.info("");

        log.info("Abbonamenti found per Costo > 0 e Versamenti Not Null");
        log.info("-------------------------------");
        for (Abbonamento abb : abbonamentoDao.findByCostoGreaterThanAndVersamentoNotNull(BigDecimal.ZERO)) {
            log.info(abb.toString());
        }
        log.info("");

        log.info("Abbonamenti found per Costo > 0 e Versamenti Null");
        log.info("-------------------------------");
        for (Abbonamento abb : abbonamentoDao.findByCostoGreaterThanAndVersamentoNull(BigDecimal.ZERO)) {
            log.info(abb.toString());
        }
        log.info("");

        log.info("Abbonamenti found per Costo > 0 ");
        log.info("-------------------------------");
        for (Abbonamento abb : abbonamentoDao.findByCostoGreaterThan(BigDecimal.ZERO)) {
            log.info(abb.toString());
            if (abb.getVersamento() == null)
                log.info("versamento:null");
            else
                log.info(abb.getVersamento().getId().toString());

        }
        log.info("");

        log.info("versamenti found by incasso1");
        log.info("-------------------------------");
        for (Versamento versamento : versamentoDao.findByIncasso(incasso1)) {
            log.info(versamento.toString());
        }
        log.info("");

        log.info("operazioni found by findAll");
        log.info("-------------------------------");
        for (Operazione operazione : operazioneDao.findAll()) {
            log.info(operazione.toString());
        }
        log.info("");

        log.info("prospetti found by findAll");
        log.info("-------------------------------");
        for (Prospetto prospetto : prospettoDao.findAll()) {
            log.info(prospetto.toString());
        }
        log.info("");

    }
    
}
