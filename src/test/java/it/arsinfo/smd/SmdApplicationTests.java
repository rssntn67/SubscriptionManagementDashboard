package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.EnumSet;
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
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Operazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
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
    private SecurityConfig securityConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private static final Logger log = LoggerFactory.getLogger(Smd.class);
    
    @Test
    public void testSmd() {
        assertNotNull(abbonamentoDao);
        assertNotNull(anagraficaDao);
        assertNotNull(pubblicazioneDao);
        assertNotNull(estrattoContoDao);
        assertNotNull(storicoDao);
        assertNotNull(notaDao);
        assertNotNull(campagnaDao);
        assertNotNull(campagnaItemDao);
        assertNotNull(notaDao);
        assertNotNull(storicoDao);
        assertNotNull(versamentoDao);
        assertNotNull(incassoDao);
        assertNotNull(operazioneDao);
        assertNotNull(userInfoDao);
        assertNotNull(securityConfig);
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService instanceof UserDetailsServiceImpl);
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        assertNotNull(authenticationSuccessHandler);
        assertTrue(authenticationSuccessHandler instanceof RedirectAuthenticationSuccessHandler);

        Authentication auth =
                new UsernamePasswordAuthenticationToken("admin", "admin");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(false);
        }

        auth =
                new UsernamePasswordAuthenticationToken("adp", "adp");
        try {
            securityConfig.authenticationManagerBean().authenticate(auth);
            assertTrue(false);
        } catch (Exception e) {
            log.info(e.getMessage());
            assertTrue(true);

        }
        
        UserInfo admin = userInfoDao.findById(1L).get();
        assertEquals("admin", admin.getUsername());

        Anagrafica dm = SmdLoadSampleData.getDiocesiMi();
        anagraficaDao.save(dm);
        assertEquals(1, anagraficaDao.findAll().size());
        assertEquals(Long.parseLong("2"), dm.getId().longValue());
        
        Anagrafica ar = SmdLoadSampleData.getAR();
        ar.setCo(dm);
        anagraficaDao.save(ar);
        assertEquals(2, anagraficaDao.findAll().size());
        assertEquals(Long.parseLong("3"), ar.getId().longValue());

        Anagrafica ps = SmdLoadSampleData.getPS();
        ps.setCo(dm);
        anagraficaDao.save(ps);
        assertEquals(3, anagraficaDao.findAll().size());
        assertEquals(Long.parseLong("4"), ps.getId().longValue());

        anagraficaDao.findAll().stream().filter(a -> a.getCo() != null).forEach(a -> {
            System.out.println(a);
            System.out.println(a.getCo());
                    });

        anagraficaDao.deleteAll();
        assertEquals(0, anagraficaDao.findAll().size());
        
        new SmdLoadSampleData(
                              anagraficaDao, 
                              storicoDao, 
                              pubblicazioneDao, 
                              abbonamentoDao, 
                              estrattoContoDao, 
                              campagnaDao, 
                              incassoDao, 
                              versamentoDao, 
                              operazioneDao,
                              userInfoDao,
                              passwordEncoder,false,false,false,true).run();

        auth =
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
        EnumSet<Mese> pubs = estratti.getMesiPubblicazione();
        assertEquals(1, pubs.size());
        assertEquals(Mese.LUGLIO, pubs.iterator().next());
        assertTrue(estratti.isLug());
        assertFalse(estratti.isGen());
        assertFalse(estratti.isFeb());
        assertFalse(estratti.isMar());
        assertFalse(estratti.isApr());
        assertFalse(estratti.isMag());
        assertFalse(estratti.isGiu());
        assertFalse(estratti.isAgo());
        assertFalse(estratti.isSet());
        assertFalse(estratti.isOtt());
        assertFalse(estratti.isNov());
        assertFalse(estratti.isDic());
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

        Pubblicazione messaggio = pubblicazioneDao.findById(5L).get();
        log.info("Messaggio found with findOne(5L):");
        log.info("--------------------------------");
        assertEquals(Long.parseLong("5"), messaggio.getId().longValue());
        assertEquals(TipoPubblicazione.MENSILE, messaggio.getTipo());
        assertEquals("Messaggio", messaggio.getNome());
        log.info(messaggio.toString());
        log.info("");

        Pubblicazione lodare = pubblicazioneDao.findById(6L).get();
        log.info("lodare found with findOne(6L):");
        log.info("--------------------------------");
        assertEquals(Long.parseLong("6"), lodare.getId().longValue());
        assertEquals(TipoPubblicazione.MENSILE, lodare.getTipo());
        assertEquals("Lodare", lodare.getNome());
        log.info(lodare.toString());
        log.info("");

        Pubblicazione blocchetti = pubblicazioneDao.findById(7L).get();
        log.info("lodare found with findOne(6L):");
        log.info("--------------------------------");
        assertEquals(Long.parseLong("7"), blocchetti.getId().longValue());
        assertEquals(TipoPubblicazione.SEMESTRALE, blocchetti.getTipo());
        assertEquals("Blocchetti", blocchetti.getNome());
        log.info(blocchetti.toString());
        log.info("");

        log.info("Anagrafica found with findAll():");
        log.info("-------------------------------");
        List<Anagrafica> anagrafiche = anagraficaDao.findAll();
        assertEquals(7, anagrafiche.size());
        for (Anagrafica customer : anagrafiche) {
            log.info(customer.toString());
        }
        log.info("");

        log.info("Anagrafica Russo found with findOne(9L):");
        log.info("--------------------------------------------");
        Anagrafica russo = anagraficaDao.findById(9L).get();
        assertEquals("Russo", russo.getCognome());
        log.info(russo.toString());
        log.info("");

        log.info("Anagrafica found with findByLastNameStartsWithIgnoreCase('Russo'):");
        log.info("--------------------------------------------");
        anagrafiche = anagraficaDao.findByCognomeContainingIgnoreCase("rUsSo");
        assertEquals(1, anagrafiche.size());
        for (Anagrafica ana : anagrafiche) {
            assertEquals("Russo", ana.getCognome());
            log.info(ana.toString());
        }
        log.info("");

        log.info("Anagrafica found with findByDiocesi('ROMA'):");
        log.info("--------------------------------------------");
        anagrafiche = anagraficaDao.findByDiocesi(Diocesi.DIOCESI168);
        assertEquals(2, anagrafiche.size());
        for (Anagrafica roma : anagrafiche) {
            assertEquals(Diocesi.DIOCESI168, roma.getDiocesi());
            log.info(roma.toString());
        }
        log.info("");

        log.info("Storico found with findByIntestatario('michele santoro id=17'):");
        log.info("--------------------------------------------");
        Anagrafica ms = anagraficaDao.findByCognomeContainingIgnoreCase("Santoro").iterator().next();
        assertEquals("Michele", ms.getNome());
        List<Storico> storici = storicoDao.findByIntestatario(ms);
        assertEquals(2, storici.size());
        for (Storico anp : storici) {
            assertEquals(StatoStorico.NUOVO, anp.getStatoStorico());
            assertEquals(blocchetti.getId(), anp.getPubblicazione().getId());
            assertEquals(TipoEstrattoConto.Ordinario, anp.getTipoEstrattoConto());
            log.info(anp.toString());
        }
        log.info("");

        log.info("Storico found with findByDestinatario('davide palma id=15'):");
        Anagrafica dp = anagraficaDao.findByCognomeContainingIgnoreCase("Palma").iterator().next();
        log.info("--------------------------------------------");
        storici = storicoDao.findByDestinatario(dp);
        assertEquals(1, storici.size());
        for (Storico anp : storici) {
            assertEquals(StatoStorico.NUOVO, anp.getStatoStorico());
            assertEquals(messaggio.getId(), anp.getPubblicazione().getId());
            assertEquals(TipoEstrattoConto.OmaggioCuriaGeneralizia, anp.getTipoEstrattoConto());
            assertEquals(10, anp.getNumero().intValue());
            log.info(anp.toString());
        }
        log.info("");

        log.info("Storico found with findByPubblicazione('blocchetti'):");
        log.info("--------------------------------------------");
        storici = storicoDao.findByPubblicazione(blocchetti);
        assertEquals(4, storici.size());
        for (Storico anp : storici) {
            assertEquals(StatoStorico.NUOVO, anp.getStatoStorico());
            assertEquals(blocchetti.getId(), anp.getPubblicazione().getId());
            log.info(anp.toString());
        }
        log.info("");

        log.info("Abbonamenti found with findAll():");
        log.info("-------------------------------");
        List<Abbonamento> abbonamenti = abbonamentoDao.findAll();
        assertEquals(30, abbonamenti.size());
        for (Abbonamento abbonamento : abbonamenti) {
            log.info(abbonamento.toString());
            for (EstrattoConto estrattoConto: abbonamento.getEstrattiConto()) {
                log.info(estrattoConto.toString());
                if (abbonamento.getCampagna() == null) {
                    assertNull(estrattoConto.getStorico());
                } else {
                    assertNotNull(estrattoConto.getStorico());
                }
                
            }
        }
        log.info("");

        log.info("Abbonamenti found with findByIntestatario(ms):");
        log.info("-------------------------------");
        abbonamenti = abbonamentoDao.findByIntestatario(ms);
        assertEquals(10, abbonamenti.size());
        for (Abbonamento abbonamentoms : abbonamenti) {
            assertEquals(ms.getId().longValue(), abbonamentoms.getIntestatario().getId().longValue());
            log.info(abbonamentoms.toString());
        }
        log.info("");

        log.info("Campagna found with findAll():");
        log.info("-------------------------------");
        List<Campagna> campagne = campagnaDao.findAll();
        assertEquals(2, campagne.size());
        for (Campagna campagna : campagne) {
            log.info(campagna.toString());
            abbonamenti = abbonamentoDao.findByCampagna(campagna);
            assertEquals(5, abbonamenti.size());
            for (Abbonamento abbonamento: abbonamenti) {
                assertEquals(campagna.getId().longValue(), abbonamento.getCampagna().getId().longValue());
            }
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
        
        log.info("stato Storico found by findAll");
        log.info("-------------------------------");
        abbonamenti = abbonamentoDao.findAll();
        for (Storico storico : storicoDao.findAll()) {
            log.info(storico.toString());
            StatoStorico ss = Smd.getStatoStorico(storico, abbonamenti);
            log.info("StatoStoricoCalcolato: " + ss.getDescr());
            assertEquals(StatoStorico.NUOVO, storico.getStatoStorico());
            if (storico.getTipoEstrattoConto() == TipoEstrattoConto.Ordinario || storico.getTipoEstrattoConto() == TipoEstrattoConto.Scontato) {
                assertEquals(StatoStorico.SOSPESO, ss);
            } else {
                assertEquals(StatoStorico.VALIDO, ss);                
            }
        }
        log.info("");

        log.info("EstrattoConto find by Destinatario");
        log.info("-------------------------------");
        List<EstrattoConto> estrattiConto = estrattoContoDao.findByDestinatario(russo);
        assertEquals(10, estrattiConto.size());
        for (EstrattoConto ec : estrattiConto) {
            log.info(ec.toString());
            assertEquals(russo.getId(), ec.getDestinatario().getId());
            assertEquals(russo.getCognome(),ec.getDestinatario().getCognome());
            Abbonamento abb = ec.getAbbonamento();
            assertNotNull(abb);
            assertTrue(abb.getEstrattiConto().size() > 0);
        }
    }
        
}
