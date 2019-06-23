package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.ProspettoDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.repository.VersamentoDao;


public class SmdLoadSampleData implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    private final AnagraficaDao anagraficaDao; 
    private final StoricoDao storicoDao;
    private final PubblicazioneDao pubblicazioneDao;
    private final AbbonamentoDao abbonamentoDao;
    private final EstrattoContoDao estrattoContoDao;
    private final CampagnaDao campagnaDao;
    private final IncassoDao incassoDao; 
    private final VersamentoDao versamentoDao;
    private final OperazioneDao operazioneDao;
    private final ProspettoDao prospettoDao;
    private final UserInfoDao userInfoDao;
    private final PasswordEncoder passwordEncoder;
    
    private final boolean loadOnlyPubblicazioniAdp;
    private Pubblicazione messaggio;
    private Pubblicazione lodare;
    private Pubblicazione estratti;
    private Pubblicazione blocchetti;
    
    private Anagrafica antonioRusso;
    private Anagrafica diocesiMilano;
    private Anagrafica gabrielePizzo;
    private Anagrafica matteoParo;
    private Anagrafica davidePalma;
    private Anagrafica micheleSantoro;
    private Anagrafica pasqualinaSantoro;


    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            InvioSpedizione invio,
            TipoEstrattoConto omaggio
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        storico.setInvioSpedizione(invio);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            Cassa cassa,
            TipoEstrattoConto omaggio
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        storico.setCassa(cassa);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            Cassa cassa
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setCassa(cassa);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            TipoEstrattoConto omaggio
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Anagrafica destinatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            TipoEstrattoConto omaggio
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(destinatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        storico.setInvio(Invio.Intestatario);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Pubblicazione pubblicazione, 
            int numero) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(intestatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Anagrafica destinatario,
            Pubblicazione pubblicazione, 
            int numero) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(destinatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        Nota nota= new Nota(storico);
        nota.setDescription("Creato storico");
        storico.getNote().add(nota);
        return storico;
    }

    public static Anagrafica getAnagraficaBy(String nome, String cognome) {
        Anagrafica anagrafica = new Anagrafica();
        anagrafica.setNome(nome);
        anagrafica.setCognome(cognome);
        return anagrafica;
    }

    public static Abbonamento getAbbonamentoBy(Anagrafica intestatario) {
        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setIntestatario(intestatario);
        return abbonamento;
    }

    public static Abbonamento getAbbonamentoBy(
            Anagrafica intestatario, 
            Anno anno, 
            Mese inizio,
            Mese fine,
            Cassa cassa,
            BigDecimal spese,
            Table<Pubblicazione,Anagrafica,Integer> spedizioni) {
                final Abbonamento abb = new Abbonamento();
                abb.setAnno(anno);
                abb.setCassa(cassa);
                abb.setIntestatario(intestatario);
                spedizioni.cellSet()
                .stream().forEach( c -> addSpedizione(abb, c.getRowKey(),c.getColumnKey(),c.getValue()));
                Smd.calcoloAbbonamento(abb);
                return abb;   
    }

    public static EstrattoConto addSpedizione(TipoEstrattoConto abb, Pubblicazione rowKey,
            Anagrafica columnKey, Integer value) {
        EstrattoConto spedizione = new EstrattoConto();
        spedizione.setAbbonamento(abb);
        spedizione.setDestinatario(columnKey);
        spedizione.setPubblicazione(rowKey);
        spedizione.setNumero(value);
        abb.addEstrattoConto(spedizione);
        return spedizione;
    }

    public static Campagna getCampagnaBy(Anno anno) {
        Campagna campagna=new Campagna();
        campagna.setAnno(anno);
        return campagna;
    }

    public SmdLoadSampleData(
            AnagraficaDao anagraficaDao, 
            StoricoDao storicoDao,
            PubblicazioneDao pubblicazioneDao, 
            AbbonamentoDao abbonamentoDao,
            EstrattoContoDao spedizioneDao,
            CampagnaDao campagnaDao, 
            IncassoDao incassoDao, 
            VersamentoDao versamentoDao,
            OperazioneDao operazioneDao,
            ProspettoDao prospettoDao,
            UserInfoDao userInfoDao,
            PasswordEncoder passwordEncoder,
            boolean loadOnlyPubblicazioniAdp
    ) {
        this.anagraficaDao=anagraficaDao;
        this.storicoDao=storicoDao;
        this.pubblicazioneDao=pubblicazioneDao;
        this.abbonamentoDao=abbonamentoDao;
        this.estrattoContoDao=spedizioneDao;
        this.campagnaDao=campagnaDao;
        this.incassoDao=incassoDao;
        this.versamentoDao=versamentoDao;
        this.operazioneDao=operazioneDao;
        this.prospettoDao=prospettoDao;
        this.userInfoDao=userInfoDao;
        this.passwordEncoder=passwordEncoder;
        this.loadOnlyPubblicazioniAdp=loadOnlyPubblicazioniAdp;
    }
    
    public static Pubblicazione getMessaggio() {
        Pubblicazione p = new Pubblicazione("Messaggio",
                                                    TipoPubblicazione.MENSILE);
        p.setDescrizione("Il Messaggio del Cuore di Gesù");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");

        p.setCostoUnitario(new BigDecimal(1.50));
        p.setSpeseSpedizione(new BigDecimal(0.50));
        p.setAbbonamentoItalia(new BigDecimal(15.00));
        p.setAbbonamentoWeb(new BigDecimal(12.00));
        p.setAbbonamentoConSconto(new BigDecimal(15.00));
        p.setAbbonamentoEuropa(new BigDecimal(58.00));
        p.setAbbonamentoAmericaAsiaAfrica(new BigDecimal(68.00));
        p.setAbbonamentoSostenitore(new BigDecimal(50.00));
        
        p.setGen(true);
        p.setFeb(true);
        p.setMar(true);
        p.setApr(true);
        p.setMag(true);
        p.setGiu(true);
        p.setLug(true);
        p.setAgo(false);
        p.setSet(true);
        p.setOtt(true);
        p.setNov(true);
        p.setDic(true);
        return p;
    }
    
    public static Pubblicazione getLodare() {
        Pubblicazione p = new Pubblicazione("Lodare",
                                                 TipoPubblicazione.MENSILE);
        p.setDescrizione("Lodare e Servire");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        
        p.setCostoUnitario(new BigDecimal(2.00));
        p.setSpeseSpedizione(new BigDecimal(0.50));
        p.setAbbonamentoItalia(new BigDecimal(20.00));
        p.setAbbonamentoWeb(new BigDecimal(18.00));
        p.setAbbonamentoConSconto(new BigDecimal(18.00));
        p.setAbbonamentoEuropa(new BigDecimal(65.00));
        p.setAbbonamentoAmericaAsiaAfrica(new BigDecimal(75.00));
        p.setAbbonamentoSostenitore(new BigDecimal(50.00));

        p.setGen(true);
        p.setFeb(true);
        p.setMar(true);
        p.setApr(true);
        p.setMag(true);
        p.setGiu(true);
        p.setLug(true);
        p.setAgo(true);
        p.setSet(true);
        p.setOtt(true);
        p.setNov(true);
        p.setDic(true);

        return p;
    }

    public static Pubblicazione getBlocchetti() {
        Pubblicazione p = new Pubblicazione("Blocchetti",
                                                     TipoPubblicazione.SEMESTRALE);
        p.setDescrizione("Biglietti Mensili");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");

        p.setCostoUnitario(new BigDecimal(3.00));
        p.setSpeseSpedizione(new BigDecimal(0.80));
        p.setAbbonamentoItalia(new BigDecimal(6.00));
        p.setAbbonamentoWeb(new BigDecimal(4.00));
        p.setAbbonamentoConSconto(new BigDecimal(6.00));
        p.setAbbonamentoEuropa(new BigDecimal(14.00));
        p.setAbbonamentoAmericaAsiaAfrica(new BigDecimal(16.00));
        p.setAbbonamentoSostenitore(new BigDecimal(12.00));

        p.setMar(true);
        p.setSet(true);
        p.setAnticipoSpedizione(4);
        return p;
    }
    
    public static Pubblicazione getEstratti() {
        Pubblicazione p = new Pubblicazione("Estratti",
                                                   TipoPubblicazione.ANNUALE);
        p.setDescrizione("Serie di dodici estratti del Messaggio");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        
        p.setCostoUnitario(new BigDecimal(10.00));
        p.setSpeseSpedizione(new BigDecimal(1.50));
        p.setAbbonamentoItalia(new BigDecimal(10.00));
        p.setAbbonamentoWeb(new BigDecimal(8.00));
        p.setAbbonamentoConSconto(new BigDecimal(10.00));
        p.setAbbonamentoEuropa(new BigDecimal(24.00));
        p.setAbbonamentoAmericaAsiaAfrica(new BigDecimal(36.00));
        p.setAbbonamentoSostenitore(new BigDecimal(10.00));

        p.setLug(true);
        
        p.setAnticipoSpedizione(4);
        return p;
    }
    
    public static Anagrafica getAR() {
        Anagrafica ar = SmdLoadSampleData.getAnagraficaBy("Antonio", "Russo");
        ar.setDiocesi(Diocesi.DIOCESI116);
        ar.setIndirizzo("");
        ar.setCitta("");
        ar.setEmail("ar@arsinfo.it");
        ar.setPaese(Paese.ITALIA);
        ar.setTelefono("+3902000009");
        ar.setTitolo(TitoloAnagrafica.Vescovo);
        ar.setRegioneVescovi(Regione.LOMBARDIA);
        return ar;
    }

    public static Anagrafica getDiocesiMi() {
        Anagrafica ar = SmdLoadSampleData.getAnagraficaBy("", "Arci Diocesi Milano");
        ar.setDiocesi(Diocesi.DIOCESI116);
        ar.setIndirizzo("Piazza Duomo 1");
        ar.setCitta("Milano");
        ar.setProvincia(Provincia.MI);
        ar.setCap("20100");
        ar.setEmail("milano@arsinfo.it");
        ar.setPaese(Paese.ITALIA);
        ar.setTelefono("+3902000001");
        ar.setTitolo(TitoloAnagrafica.Diocesi);
        return ar;
    }

    public static Anagrafica getGP() {
        Anagrafica gp = SmdLoadSampleData.getAnagraficaBy("Gabriele", "Pizzo");
        gp.setDiocesi(Diocesi.DIOCESI116);
        gp.setIndirizzo("Piazza Sant'Ambrogio 1");
        gp.setCitta("Milano");
        gp.setProvincia(Provincia.MI);
        gp.setCap("20110");
        gp.setPaese(Paese.ITALIA);
        gp.setEmail("gp@arsinfo.it");
        gp.setTelefono("+3902000010");
        return gp;
    }
    
    public static Anagrafica getMP() {
        Anagrafica mp = SmdLoadSampleData.getAnagraficaBy("Matteo", "Paro");
        mp.setDiocesi(Diocesi.DIOCESI168);
        mp.setTitolo(TitoloAnagrafica.Religioso);
        mp.setIndirizzo("Piazza del Gesu' 1");
        mp.setCitta("Roma");
        mp.setProvincia(Provincia.RM);
        mp.setCap("00192");
        mp.setPaese(Paese.ITALIA);
        mp.setEmail("mp@arsinfo.it");
        mp.setTelefono("+3906000020");
        return mp;
    }
    
    public static Anagrafica getDP() {
        Anagrafica dp = SmdLoadSampleData.getAnagraficaBy("Davide", "Palma");
        dp.setDiocesi(Diocesi.DIOCESI168);
        dp.setTitolo(TitoloAnagrafica.Sacerdote);
        dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
        dp.setCitta("Roma");
        dp.setProvincia(Provincia.RM);
        dp.setCap("00195");
        dp.setPaese(Paese.ITALIA);
        dp.setEmail("dp@arsinfo.it");
        dp.setTelefono("+3906000020");
        dp.setDirettoreDiocesiano(true);
        dp.setRegioneDirettoreDiocesano(Regione.LAZIO);
        return dp;
    }
    
    public static Anagrafica getMS() {
        Anagrafica ms = SmdLoadSampleData.getAnagraficaBy("Michele", "Santoro");
        ms.setDiocesi(Diocesi.DIOCESI126);
        ms.setIndirizzo("Via Duomo 10");
        ms.setCitta("Napoli");
        ms.setProvincia(Provincia.NA);
        ms.setCap("80135");
        ms.setPaese(Paese.ITALIA);
        ms.setEmail("ms@arsinfo.it");
        ms.setTelefono("+39081400022");
        return ms;
    }
    
    public static Anagrafica getPS() {
        Anagrafica ps = SmdLoadSampleData.getAnagraficaBy("Pasqualina", "Santoro");
        ps.setDiocesi(Diocesi.DIOCESI126);
        ps.setIndirizzo("Piazza Dante 10");
        ps.setCitta("Napoli");
        ps.setProvincia(Provincia.NA);
        ps.setCap("80135");
        ps.setPaese(Paese.ITALIA);
        ps.setEmail("arsinfo@adp.it");
        ps.setTelefono("+39081400023");
        return ps;
    }
    
    public static TipoEstrattoConto getAbbonamentoMs(Anagrafica ms, Pubblicazione ...pubblicazioni) {
        Table<Pubblicazione, Anagrafica, Integer> spedizioni = HashBasedTable.create();
        for (Pubblicazione pubblicazione: pubblicazioni) {
            spedizioni.put(pubblicazione, ms, 1);
        }
        
        return getAbbonamentoBy(
                ms, 
                Smd.getAnnoCorrente(), 
                Mese.GENNAIO, 
                Mese.DICEMBRE, 
                Cassa.Ccp, 
                BigDecimal.ZERO, 
                spedizioni
                );
    }
        
    public static TipoEstrattoConto getAbbonamentoGp(Anagrafica gp, Pubblicazione pubb, Anagrafica ar, Anagrafica mp, Pubblicazione ...pubblicazioni) {
        Table<Pubblicazione, Anagrafica, Integer> spedizioni = HashBasedTable.create();
        for (Pubblicazione pubblicazione: pubblicazioni) {
            spedizioni.put(pubblicazione, gp, 1);
        }
        spedizioni.put(pubb, ar, 2);
        spedizioni.put(pubb, mp, 2);
        return getAbbonamentoBy(
                            gp, 
                            Smd.getAnnoCorrente(), 
                            Mese.GENNAIO, 
                            Mese.DICEMBRE, 
                            Cassa.Ccp, 
                            BigDecimal.ZERO, 
                            spedizioni);
    }
    
    public static TipoEstrattoConto getAbbonamentoDp(Anagrafica dp, Mese inizio, Pubblicazione blocchetti) {
        Table<Pubblicazione, Anagrafica, Integer> spedizioni = HashBasedTable.create();
        spedizioni.put(blocchetti, dp, 1);
        return getAbbonamentoBy(
                            dp, 
                            Smd.getAnnoCorrente(), 
                            inizio, 
                            Mese.DICEMBRE, 
                            Cassa.Ccp, 
                            new BigDecimal("3.75"), spedizioni
                            );
    }
    
    public static List<Abbonamento> getAbbonamentiIncassi(
            Anagrafica ar, 
            Anagrafica ms, 
            Anagrafica dp, 
            Anagrafica mp, 
            Pubblicazione blocchetti,Pubblicazione estratti,Pubblicazione lodare) {
        List<Abbonamento> abbonamenti = new ArrayList<>();
        Abbonamento telematici001 = SmdLoadSampleData.getAbbonamentoBy(ar);
        addSpedizione(telematici001, blocchetti,ar,1);
        telematici001.setCosto(new BigDecimal(15));
        telematici001.setCampo("000000018000792609");
        telematici001.setAnno(Anno.ANNO2017);
        abbonamenti.add(telematici001);
        
        TipoEstrattoConto venezia002 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia002, blocchetti,ms,1);
        venezia002.setCosto(new BigDecimal(15));
        venezia002.setCampo("000000018000854368");
        venezia002.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia002);

        TipoEstrattoConto venezia003 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia003, blocchetti,ms,1);
        venezia003.setCosto(new BigDecimal(18));
        venezia003.setCampo("000000018000263519");
        venezia003.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia003);

        TipoEstrattoConto venezia004 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia004, blocchetti,ms,2);
        venezia004.setCosto(new BigDecimal(30));
        venezia004.setCampo("000000018000254017");
        venezia004.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia004);

        TipoEstrattoConto venezia005 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia005, blocchetti,ms,2);
        venezia005.setCosto(new BigDecimal(37));
        venezia005.setCampo("000000018000761469");
        venezia005.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia005);

        TipoEstrattoConto venezia006 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia006, blocchetti,ms,3);
        venezia006.setCosto(new BigDecimal(48));
        venezia006.setCampo("000000018000253916");
        venezia006.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia006);

        TipoEstrattoConto venezia007 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia007, blocchetti,ms,10);
        venezia007.setCosto(new BigDecimal(70));
        venezia007.setCampo("000000018000800386");
        venezia007.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia007);
        
        TipoEstrattoConto venezia008 = SmdLoadSampleData.getAbbonamentoBy(ms);
        addSpedizione(venezia008, blocchetti,ms,15);
        venezia008.setCosto(new BigDecimal(84));
        venezia008.setCampo("000000018000508854");
        venezia008.setAnno(Anno.ANNO2017);
        abbonamenti.add(venezia008);

        TipoEstrattoConto firenze009 = SmdLoadSampleData.getAbbonamentoBy(dp);
        addSpedizione(firenze009, estratti,dp,1);
        firenze009.setCosto(new BigDecimal(10));
        firenze009.setCampo("000000018000686968");
        firenze009.setAnno(Anno.ANNO2017);
        abbonamenti.add(firenze009);
        
        TipoEstrattoConto firenze010 = SmdLoadSampleData.getAbbonamentoBy(dp);
        addSpedizione(firenze010, lodare,dp,1);
        firenze010.setCosto(new BigDecimal(15));
        firenze010.setCampo("000000018000198318");
        firenze010.setAnno(Anno.ANNO2017);
        abbonamenti.add(firenze010);

        TipoEstrattoConto firenze011 = SmdLoadSampleData.getAbbonamentoBy(dp);
        addSpedizione(firenze011, lodare,dp,1);
        firenze011.setCosto(new BigDecimal(15));
        firenze011.setCampo("000000018000201449");
        firenze011.setAnno(Anno.ANNO2017);
        abbonamenti.add(firenze011);

        TipoEstrattoConto firenze012 = SmdLoadSampleData.getAbbonamentoBy(dp);
        addSpedizione(firenze012, lodare,dp,3);
        firenze012.setCosto(new BigDecimal(33));
        firenze012.setAnno(Anno.ANNO2017);
        firenze012.setCampo("000000018000633491");
        abbonamenti.add(firenze012);
        
        TipoEstrattoConto firenze013 = SmdLoadSampleData.getAbbonamentoBy(dp);
        addSpedizione(firenze013, lodare,dp,10);
        addSpedizione(firenze013, estratti,dp,10);
        addSpedizione(firenze013, blocchetti,dp,10);
        firenze013.setCosto(new BigDecimal(108));
        firenze013.setAnno(Anno.ANNO2017);
        firenze013.setCampo("000000018000196500");
        abbonamenti.add(firenze013);
        
        TipoEstrattoConto bari014 = SmdLoadSampleData.getAbbonamentoBy(mp);
        addSpedizione(bari014, lodare,mp,1);
        bari014.setCosto(new BigDecimal(12));
        bari014.setAnno(Anno.ANNO2017);
        bari014.setCampo("000000018000106227");
        abbonamenti.add(bari014);

        TipoEstrattoConto bari015 = SmdLoadSampleData.getAbbonamentoBy(mp);
        addSpedizione(bari015, lodare,mp,3);
        bari015.setCosto(new BigDecimal(36));
        bari015.setAnno(Anno.ANNO2017);
        bari015.setCampo("000000018000077317");
        abbonamenti.add(bari015);

        TipoEstrattoConto bari016 = SmdLoadSampleData.getAbbonamentoBy(mp);
        addSpedizione(bari016, lodare,mp,5);
        bari016.setCosto(new BigDecimal(60));
        bari016.setAnno(Anno.ANNO2017);
        bari016.setCampo("000000018000125029");
        abbonamenti.add(bari016);

        TipoEstrattoConto bari017 = SmdLoadSampleData.getAbbonamentoBy(mp);
        addSpedizione(bari017, estratti,mp,10);
        bari017.setCosto(new BigDecimal(67));
        bari017.setAnno(Anno.ANNO2017);
        bari017.setCampo("000000018000065383");
        abbonamenti.add(bari017);
        return abbonamenti;
    }
    
    public static List<Incasso> getIncassi() {
        List<Incasso> incassi = new ArrayList<>();
        String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1= new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        Incasso incasso1 = Smd.generaIncasso(versamenti1, riepilogo1); 
        incassi.add(incasso1);
        
        String riepilogo2="3000063470009171006              999000000090000000367000000000700000003020000000002000000006500                                                                                                        \n";
        Set<String> versamenti2= new HashSet<>();
        versamenti2.add("0865737400000020000634700091710056740000001500074046022171006000000018000854368DIN                                                                                                                      \n");
        versamenti2.add("0865298400000030000634700091710056740000001800076241052171006000000018000263519DIN                                                                                                                      \n");
        versamenti2.add("0863439100000040000634700091710056740000003000023013042171006000000018000254017DIN                                                                                                                      \n");
        versamenti2.add("0854922500000050000634700091710046740000003700023367052171006000000018000761469DIN                                                                                                                      \n");
        versamenti2.add("0863439000000060000634700091710056740000004800023013042171006000000018000253916DIN                                                                                                                      \n");
        versamenti2.add("0865570900000070000634700091710056740000007000023247042171006000000018000800386DIN                                                                                                                      \n");
        versamenti2.add("0863569900000080000634700091710056740000008400074264032171006000000018000508854DIN                                                                                                                      \n");
        versamenti2.add("0856588699999990000634700091710041230000001500038124062171006727703812406007375DIN                                                                                                                      \n");
        versamenti2.add("0858313299999990000634700091710041230000005000098101062171006727709810106010156DIN                                                                                                                      \n");

        Incasso incasso2 = Smd.generaIncasso(versamenti2, riepilogo2);
        incassi.add(incasso2);
        
        String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
        Set<String> versamenti3= new HashSet<>();
        versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
        versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
        versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
        versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
        versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
        versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");

        Incasso incasso3 = Smd.generaIncasso(versamenti3, riepilogo3);
        incassi.add(incasso3);
        
        String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
        Set<String> versamenti4= new HashSet<>();
        versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
        versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
        versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
        versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
        versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
        versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
        versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");
        
        Incasso incasso4=Smd.generaIncasso(versamenti4, riepilogo4);
        incassi.add(incasso4);

        return incassi;
    }
    
    public static Incasso getIncasso5(BigDecimal importo,String campo) {
        Incasso incasso5 = new Incasso();
        incasso5.setCassa(Cassa.Contrassegno);
        incasso5.setCcp(Ccp.DUE);
        
        Versamento versamentoIncasso5 = new Versamento(incasso5,importo);
        versamentoIncasso5.setCampo(campo);
        versamentoIncasso5.setDataPagamento(incasso5.getDataContabile());
        versamentoIncasso5.setOperazione("Assegno n.0002889893819813 Banca Popolare di Chiavari");
        incasso5.addVersamento(versamentoIncasso5);
        Smd.calcoloImportoIncasso(incasso5);
        return incasso5;
    }

    private void loadPubblicazioniAdp() {
        messaggio = getMessaggio();
        lodare = getLodare();
        blocchetti = getBlocchetti();
        estratti = getEstratti();
        
        pubblicazioneDao.save(messaggio);
        pubblicazioneDao.save(lodare);
        pubblicazioneDao.save(blocchetti);
        pubblicazioneDao.save(estratti);
        
    }
    @Override
    public void run() {
        log.info("Start Loading Sample Data");
        
        loadPubblicazioniAdp();
        if (loadOnlyPubblicazioniAdp) {
            return;
        }
        
        loadAnagrafica();
        loadStorico();
        loadSampleData();
        
        log.info("End Loading Sample Data");

    }

    private void loadStorico() {
        storicoDao.save(getStoricoBy(diocesiMilano,antonioRusso, messaggio, 10,TipoEstrattoConto.OmaggioCuriaDiocesiana));
        storicoDao.save(getStoricoBy(diocesiMilano,antonioRusso, lodare, 10,TipoEstrattoConto.OmaggioCuriaDiocesiana));
        storicoDao.save(getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 10,TipoEstrattoConto.OmaggioCuriaDiocesiana));
        storicoDao.save(getStoricoBy(diocesiMilano,antonioRusso, estratti, 10,TipoEstrattoConto.OmaggioCuriaDiocesiana));

        storicoDao.save(getStoricoBy(gabrielePizzo, messaggio, 10,Cassa.Contrassegno));
        storicoDao.save(getStoricoBy(gabrielePizzo, lodare, 10,Cassa.Contrassegno));
        storicoDao.save(getStoricoBy(gabrielePizzo, blocchetti, 10,Cassa.Contrassegno,TipoEstrattoConto.Scontato));

        storicoDao.save(getStoricoBy(matteoParo, messaggio, 10,InvioSpedizione.AdpSede,TipoEstrattoConto.OmaggioGesuiti));
        storicoDao.save(getStoricoBy(matteoParo, lodare, 10,InvioSpedizione.AdpSede,TipoEstrattoConto.OmaggioGesuiti));

        storicoDao.save(getStoricoBy(davidePalma, messaggio, 10,InvioSpedizione.AdpSede,TipoEstrattoConto.OmaggioCuriaGeneralizia));
        storicoDao.save(getStoricoBy(micheleSantoro, blocchetti, 1));
        storicoDao.save(getStoricoBy(micheleSantoro, pasqualinaSantoro, blocchetti, 2));        

    }
    private void loadAnagrafica() {
        antonioRusso=getAR();
        anagraficaDao.save(antonioRusso);
        diocesiMilano=getDiocesiMi();
        anagraficaDao.save(diocesiMilano);
        gabrielePizzo=getGP();
        anagraficaDao.save(gabrielePizzo);
        matteoParo = getMP();
        anagraficaDao.save(matteoParo);
        davidePalma = getDP();
        anagraficaDao.save(davidePalma);
        micheleSantoro = getMS();
        anagraficaDao.save(micheleSantoro);
        pasqualinaSantoro = getPS();
        anagraficaDao.save(pasqualinaSantoro);
    }

    private void loadSampleData() {
        
        abbonamentoDao.save(getAbbonamentoMs(micheleSantoro, messaggio,lodare,blocchetti,estratti));
        abbonamentoDao.save(getAbbonamentoGp(gabrielePizzo, estratti, antonioRusso, matteoParo, messaggio,lodare,blocchetti,estratti));
        EstrattoConto spedizioneGptoar = estrattoContoDao.findByDestinatario(antonioRusso).iterator().next();
        spedizioneGptoar.setNumero(3);
        estrattoContoDao.save(spedizioneGptoar);

        Abbonamento abbonamentoDp =getAbbonamentoDp(davidePalma, Mese.MAGGIO,blocchetti);
        abbonamentoDao.save(abbonamentoDp);
        Incasso incasso = getIncasso5(abbonamentoDp.getTotale(), abbonamentoDp.getCampo());
        incassoDao.save(incasso);
        incasso.getVersamenti().stream().forEach(v-> {
            versamentoDao.save(
                           Smd.incassa(incasso,v, abbonamentoDp));
        });
        incassoDao.save(incasso);
        abbonamentoDao.save(abbonamentoDp);
        
        getAbbonamentiIncassi(antonioRusso, micheleSantoro, davidePalma, matteoParo, blocchetti, estratti, lodare)
            .stream().forEach(a->abbonamentoDao.save(a));        
        getIncassi()
            .stream().forEach(c -> incassoDao.save(c));

        campagnaDao.save(
                 Smd.generaCampagna(
                    getCampagnaBy(Anno.ANNO2018),
                    anagraficaDao.findAll(),
                    storicoDao.findAll(),
                    pubblicazioneDao.findAll()
                )
             );

        campagnaDao.save(
                         Smd.generaCampagna(
                            getCampagnaBy(Anno.ANNO2019),
                            anagraficaDao.findAll(),
                            storicoDao.findAll(),
                            pubblicazioneDao.findAll()
                        )
                     );

        Smd.generaOperazioni(pubblicazioneDao.findAll(),
                             abbonamentoDao.findByAnno(Anno.ANNO2017),
                             Anno.ANNO2017,
                             EnumSet.allOf(Mese.class)).forEach(p -> {
                                 operazioneDao.save(p);
                             });
 
        Smd.generaOperazioni(pubblicazioneDao.findAll(),
                             abbonamentoDao.findByAnno(Anno.ANNO2018),
                             Anno.ANNO2018,
                             EnumSet.allOf(Mese.class)).forEach(p -> {
                                 operazioneDao.save(p);
                             });

        Smd.generaOperazioni(pubblicazioneDao.findAll(),
                             abbonamentoDao.findByAnno(Anno.ANNO2019),
                             Anno.ANNO2019,
                             EnumSet.allOf(Mese.class)).forEach(p -> {
                                 operazioneDao.save(p);
                             });

        Smd.generaProspetti(pubblicazioneDao.findAll(),
                            abbonamentoDao.findByAnno(Anno.ANNO2017),
                            Anno.ANNO2017, EnumSet.allOf(Mese.class),
                            EnumSet.allOf(TipoEstrattoConto.class)).forEach(p -> {
                                prospettoDao.save(p);
                            });

        Smd.generaProspetti(pubblicazioneDao.findAll(),
                            abbonamentoDao.findByAnno(Anno.ANNO2018),
                            Anno.ANNO2018, EnumSet.allOf(Mese.class),
                            EnumSet.allOf(TipoEstrattoConto.class)).forEach(p -> {
                                prospettoDao.save(p);
                            });
        Smd.generaProspetti(pubblicazioneDao.findAll(),
                            abbonamentoDao.findByAnno(Anno.ANNO2019),
                            Anno.ANNO2019, EnumSet.allOf(Mese.class),
                            EnumSet.allOf(TipoEstrattoConto.class)).forEach(p -> {
                                prospettoDao.save(p);
                            });
        
        operazioneDao.findAll()
            .stream()
            .filter(o -> o.getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()
                          && o.getDefinitivo() == -1 )
            .forEach(o -> {
                o.setDefinitivo(o.getStimato()+3);
                operazioneDao.save(o);
                log.info(o.toString());
            });
        operazioneDao.findByAnno(Smd.getAnnoCorrente())
            .stream()
            .filter(o -> o.getMese().getPosizione() <= Smd.getMeseCorrente().getPosizione()
                    && o.getDefinitivo() == -1)
            .forEach(o -> {
                o.setDefinitivo(o.getStimato()+3);
                operazioneDao.save(o);
                log.info(o.toString());
            });
        
        UserInfo adp = new UserInfo("adp", passwordEncoder.encode("adp"), Role.USER);
        adp.setLocked(true);
        userInfoDao.save(adp);
        log.info("creato user adp/adp");
   
    }    
}
