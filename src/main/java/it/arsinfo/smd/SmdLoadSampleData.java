package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
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
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
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


public class SmdLoadSampleData implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    private final AnagraficaDao anagraficaDao; 
    private final PubblicazioneDao pubblicazioneDao;
    private final SpesaSpedizioneDao spesaSpedizioneDao;
    private final AbbonamentoDao abbonamentoDao;
    private final EstrattoContoDao estrattoContoDao;
    private final SpedizioneDao spedizioneDao;
    private final SpedizioneItemDao spedizioneItemDao;
    private final CampagnaDao campagnaDao;
    private final StoricoDao storicoDao;
    private final NotaDao notaDao;
    private final IncassoDao incassoDao; 
    private final VersamentoDao versamentoDao;
    private final OperazioneDao operazioneDao;
    private final UserInfoDao userInfoDao;

    private final PasswordEncoder passwordEncoder;
    
    private final boolean loadPubblicazioniAdp;
    private final boolean loadSampleAnagrafica;
    private final boolean loadSampleStorico;
    private final boolean loadSampleData;
    private final boolean createDemoUser;
    private final boolean createNormalUser;
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

    public static Incasso getIncassoTelematici() {
        String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1= new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        return Smd.generaIncasso(versamenti1, riepilogo1); 
        
    }
 
    public static Incasso getIncassoVenezia() {
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

        return Smd.generaIncasso(versamenti2, riepilogo2);
    
    }

    public static Incasso getIncassoFirenze() {
        String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
        Set<String> versamenti3= new HashSet<>();
        versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
        versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
        versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
        versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
        versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
        versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");

        return Smd.generaIncasso(versamenti3, riepilogo3);
    }
    public static Incasso getIncassoBari() {
        
        String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
        Set<String> versamenti4= new HashSet<>();
        versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
        versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
        versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
        versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
        versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
        versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
        versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");
        
        return Smd.generaIncasso(versamenti4, riepilogo4);
    }
    
    public static Incasso getIncassoByImportoAndCampo(BigDecimal importo,String campo) {
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

    public static List<SpesaSpedizione> getSpeseSpedizione() {
        List<SpesaSpedizione> sss = new ArrayList<>();

        SpesaSpedizione ss2 = new SpesaSpedizione();
        ss2.setRange(RangeSpeseSpedizione.Base);
        ss2.setArea(AreaSpedizione.Italia);
        ss2.setSpese(new BigDecimal("2.00"));
        sss.add(ss2);

        SpesaSpedizione ss3 = new SpesaSpedizione();
        ss3.setRange(RangeSpeseSpedizione.Da100grA200gr);
        ss3.setArea(AreaSpedizione.Italia);
        ss3.setSpese(new BigDecimal("3.00"));
        sss.add(ss3);

        SpesaSpedizione ss3bis = new SpesaSpedizione();
        ss3bis.setRange(RangeSpeseSpedizione.Da200grA350gr);
        ss3bis.setArea(AreaSpedizione.Italia);
        ss3bis.setSpese(new BigDecimal("3.00"));
        sss.add(ss3bis);

        SpesaSpedizione ss4 = new SpesaSpedizione();
        ss4.setRange(RangeSpeseSpedizione.Da350grA1Kg);
        ss4.setArea(AreaSpedizione.Italia);
        ss4.setSpese(new BigDecimal("5.00"));
        sss.add(ss4);

        SpesaSpedizione ss5 = new SpesaSpedizione();
        ss5.setRange(RangeSpeseSpedizione.Da1KgA2Kg);
        ss5.setArea(AreaSpedizione.Italia);
        ss5.setSpese(new BigDecimal("5.50"));
        sss.add(ss5);

        SpesaSpedizione ss6 = new SpesaSpedizione();
        ss6.setRange(RangeSpeseSpedizione.Da2KgA5Kg);
        ss6.setArea(AreaSpedizione.Italia);
        ss6.setSpese(new BigDecimal("7.00"));
        sss.add(ss6);

        SpesaSpedizione ss7 = new SpesaSpedizione();
        ss7.setRange(RangeSpeseSpedizione.Da5KgA10Kg);
        ss7.setArea(AreaSpedizione.Italia);
        ss7.setSpese(new BigDecimal("8.00"));
        sss.add(ss7);
        
        SpesaSpedizione ss8 = new SpesaSpedizione();
        ss8.setRange(RangeSpeseSpedizione.Da10KgA20Kg);
        ss8.setArea(AreaSpedizione.Italia);
        ss8.setSpese(new BigDecimal("9.00"));
        sss.add(ss8);

        SpesaSpedizione ss9 = new SpesaSpedizione();
        ss9.setRange(RangeSpeseSpedizione.Da20KgA30Kg);
        ss9.setArea(AreaSpedizione.Italia);
        ss9.setSpese(new BigDecimal("10.00"));
        sss.add(ss9);


        SpesaSpedizione ss10 = new SpesaSpedizione();
        ss10.setRange(RangeSpeseSpedizione.Base);
        ss10.setArea(AreaSpedizione.EuropaBacinoMediterraneo);
        ss10.setSpese(new BigDecimal("4.00"));
        sss.add(ss10);

        SpesaSpedizione ss11 = new SpesaSpedizione();
        ss11.setRange(RangeSpeseSpedizione.Da100grA200gr);;
        ss11.setArea(AreaSpedizione.EuropaBacinoMediterraneo);
        ss11.setSpese(new BigDecimal("5.70"));
        sss.add(ss11);

        SpesaSpedizione ss12 = new SpesaSpedizione();
        ss12.setRange(RangeSpeseSpedizione.Da200grA350gr);;
        ss12.setArea(AreaSpedizione.EuropaBacinoMediterraneo);
        ss12.setSpese(new BigDecimal("6.50"));
        sss.add(ss12);

        SpesaSpedizione ss13 = new SpesaSpedizione();
        ss13.setRange(RangeSpeseSpedizione.Da350grA1Kg);;
        ss13.setArea(AreaSpedizione.EuropaBacinoMediterraneo);
        ss13.setSpese(new BigDecimal("8.50"));
        sss.add(ss13);

        SpesaSpedizione ss14 = new SpesaSpedizione();
        ss14.setRange(RangeSpeseSpedizione.Da10KgA20Kg);;
        ss14.setArea(AreaSpedizione.EuropaBacinoMediterraneo);
        ss14.setSpese(new BigDecimal("13.50"));
        sss.add(ss14);

        SpesaSpedizione ss15 = new SpesaSpedizione();
        ss15.setRange(RangeSpeseSpedizione.Base);
        ss15.setArea(AreaSpedizione.AmericaAfricaAsia);
        ss15.setSpese(new BigDecimal("5.00"));
        sss.add(ss15);

        SpesaSpedizione ss16 = new SpesaSpedizione();
        ss16.setRange(RangeSpeseSpedizione.Da100grA200gr);
        ss16.setArea(AreaSpedizione.AmericaAfricaAsia);
        ss16.setSpese(new BigDecimal("8.60"));
        sss.add(ss16);

        SpesaSpedizione ss17 = new SpesaSpedizione();
        ss17.setRange(RangeSpeseSpedizione.Da200grA350gr);
        ss17.setArea(AreaSpedizione.AmericaAfricaAsia);
        ss17.setSpese(new BigDecimal("9.00"));
        sss.add(ss17);

        SpesaSpedizione ss18 = new SpesaSpedizione();
        ss18.setRange(RangeSpeseSpedizione.Da350grA1Kg);
        ss18.setArea(AreaSpedizione.AmericaAfricaAsia);
        ss18.setSpese(new BigDecimal("13.50"));
        sss.add(ss18);

        SpesaSpedizione ss19 = new SpesaSpedizione();
        ss19.setRange(RangeSpeseSpedizione.Da1KgA2Kg);
        ss19.setArea(AreaSpedizione.AmericaAfricaAsia);
        ss19.setSpese(new BigDecimal("23.00"));
        sss.add(ss19);

        return sss;

    }
    public static Pubblicazione getMessaggio() {
        Pubblicazione p = new Pubblicazione("Messaggio",
                                                    TipoPubblicazione.MENSILE);
        p.setDescrizione("Il Messaggio del Cuore di Gesù");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(80);

        p.setCostoUnitario(new BigDecimal(1.50));
        p.setAbbonamento(new BigDecimal(15.00));
        p.setAbbonamentoWeb(new BigDecimal(12.00));
        p.setAbbonamentoConSconto(new BigDecimal(15.00));
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
        p.setGrammi(160);
        
        p.setCostoUnitario(new BigDecimal(2.00));
        p.setAbbonamento(new BigDecimal(20.00));
        p.setAbbonamentoWeb(new BigDecimal(18.00));
        p.setAbbonamentoConSconto(new BigDecimal(18.00));
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
        p.setGrammi(60);

        p.setCostoUnitario(new BigDecimal(3.00));
        p.setAbbonamento(new BigDecimal(6.00));
        p.setAbbonamentoWeb(new BigDecimal(4.00));
        p.setAbbonamentoConSconto(new BigDecimal(6.00));
        p.setAbbonamentoSostenitore(new BigDecimal(12.00));

        p.setMar(true);
        p.setSet(true);
        p.setAnticipoSpedizione(3);
        return p;
    }
    
    public static Pubblicazione getEstratti() {
        Pubblicazione p = new Pubblicazione("Estratti",
                                                   TipoPubblicazione.ANNUALE);
        p.setDescrizione("Serie di dodici Manifesti del Messaggio");
        p.setActive(true);
        p.setAutore("AAVV");
        p.setEditore("ADP");
        p.setGrammi(100);
        
        p.setCostoUnitario(new BigDecimal(10.00));
        p.setAbbonamento(new BigDecimal(10.00));
        p.setAbbonamentoWeb(new BigDecimal(8.00));
        p.setAbbonamentoConSconto(new BigDecimal(10.00));
        p.setAbbonamentoSostenitore(new BigDecimal(10.00));

        p.setGen(true);
        p.setAnticipoSpedizione(6);

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

    public static Storico getStoricoBy(
            Anagrafica intestatario, 
            Anagrafica destinatario, 
            Pubblicazione pubblicazione, 
            int numero, 
            Cassa cassa,
            TipoEstrattoConto omaggio,
            Invio invio,
            InvioSpedizione invioSpedizione
        ) {
        Storico storico = new Storico(); 
        storico.setIntestatario(intestatario);
        storico.setDestinatario(destinatario);
        storico.setPubblicazione(pubblicazione);
        storico.setNumero(numero);
        storico.setTipoEstrattoConto(omaggio);
        storico.setCassa(cassa);
        storico.setInvio(invio);
        storico.setInvioSpedizione(invioSpedizione);
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

    public static List<Spedizione> getSpedizioni(List<SpedizioneItem> items, Abbonamento abb, Table<Pubblicazione,Anagrafica,Integer> table, List<SpesaSpedizione> spese) {
        final List<Spedizione> spedizioni = new ArrayList<>();
        table.cellSet().stream().forEach(te -> 
            spedizioni.addAll(
          Smd.generaSpedizioni(abb, items, Invio.Destinatario, InvioSpedizione.Spedizioniere, te.getColumnKey(), new ArrayList<>(), spese)
                    )
            );
        return spedizioni;
    }
    
    public static List<EstrattoConto> getEstrattiConto(Anno anno, 
            Mese inizio,
            Mese fine,
            Abbonamento abb, 
            Table<Pubblicazione,Anagrafica,Integer> table) {
        return table.cellSet()
        .stream().map( ect -> {
            EstrattoConto ec = new EstrattoConto();
            ec.setAbbonamento(abb);
            ec.setPubblicazione(ect.getRowKey());
            ec.setNumero(ect.getValue());
            ec.setMeseInizio(inizio);
            ec.setAnnoInizio(anno);
            ec.setMeseFine(fine);
            ec.setAnnoFine(anno);
            return ec;
        }).collect(Collectors.toList());        
    }

    public static Abbonamento getAbbonamentoBy(
            Anagrafica intestatario, 
            Anno anno, 
            Cassa cassa
            ) {
 
        final Abbonamento abb = new Abbonamento();
        abb.setAnno(anno);
        abb.setCassa(cassa);
        abb.setIntestatario(intestatario);
        abb.setCampo(Smd.generaVCampo(anno));
        return abb;   
    }

    public static EstrattoConto addEC(Abbonamento abb, Pubblicazione pubblicazione,
        Integer numero, BigDecimal importo) {
        EstrattoConto ec = new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setPubblicazione(pubblicazione);
        ec.setNumero(numero);
        ec.setImporto(importo);
        ec.setAnnoInizio(Anno.ANNO2017);
        ec.setAnnoFine(Anno.ANNO2017);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setMeseFine(Mese.DICEMBRE);
        return ec;
    }

    public SmdLoadSampleData(
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
            PasswordEncoder passwordEncoder,
            boolean loadPubblicazioniAdp,
            boolean loadSampleAnagrafica,
            boolean loadSampleStorico,
            boolean createDemoUser,
            boolean createNormalUser,
            boolean loadSampleData
    ) {
        this.anagraficaDao=anagraficaDao;
        this.storicoDao=storicoDao;
        this.notaDao=notaDao;
        this.pubblicazioneDao=pubblicazioneDao;
        this.spesaSpedizioneDao=spesaSpedizioneDao;
        this.abbonamentoDao=abbonamentoDao;
        this.estrattoContoDao=estrattoContoDao;
        this.spedizioneDao=spedizioneDao;
        this.spedizioneItemDao=spedizioneItemDao;
        this.campagnaDao=campagnaDao;
        this.incassoDao=incassoDao;
        this.versamentoDao=versamentoDao;
        this.operazioneDao=operazioneDao;
        this.userInfoDao=userInfoDao;
        this.passwordEncoder=passwordEncoder;
        this.loadPubblicazioniAdp=loadPubblicazioniAdp;
        this.loadSampleAnagrafica=loadSampleAnagrafica;
        this.loadSampleStorico=loadSampleStorico;
        this.createDemoUser=createDemoUser;
        this.createNormalUser=createNormalUser;
        this.loadSampleData=loadSampleData;
    }
    
    
    private void saveAbbonamentoMs() {
        Table<Pubblicazione, Anagrafica, Integer> spedizioni = HashBasedTable.create();
        spedizioni.put(messaggio, micheleSantoro, 1);
        spedizioni.put(lodare, micheleSantoro, 1);
        spedizioni.put(blocchetti, micheleSantoro, 1);
        spedizioni.put(estratti, micheleSantoro, 1);
                  
        Abbonamento abb =  getAbbonamentoBy(
                micheleSantoro, 
                Smd.getAnnoCorrente(), 
                Cassa.Ccp
                );
        save(Smd.getAnnoCorrente(), Mese.GENNAIO, Mese.DICEMBRE, abb, spedizioni);

    }
        
    private void saveAbbonamentoGp() {
        Table<Pubblicazione, Anagrafica, Integer> table = HashBasedTable.create();
        table.put(messaggio, gabrielePizzo, 1);
        table.put(lodare, gabrielePizzo, 1);
        table.put(blocchetti, gabrielePizzo, 1);
        table.put(estratti, gabrielePizzo, 1);
        table.put(estratti, antonioRusso, 1);
        table.put(estratti,matteoParo, 1);
        Abbonamento abb = getAbbonamentoBy(
                            gabrielePizzo, 
                            Smd.getAnnoCorrente(), 
                            Cassa.Ccp 
                            );
        save(Smd.getAnnoCorrente(), Mese.GENNAIO, Mese.DICEMBRE, abb, table);
    }
    
    private  void saveAbbonamentoDp() {
        Table<Pubblicazione, Anagrafica, Integer> table = HashBasedTable.create();
        table.put(blocchetti, davidePalma, 1);
        Abbonamento abb = getAbbonamentoBy(
                            davidePalma, 
                            Smd.getAnnoCorrente(), 
                            Cassa.Ccp
                            );        
        save(Smd.getAnnoCorrente(), Mese.MAGGIO, Mese.DICEMBRE, abb, table);
    }
    
    private void save(Anno anno, Mese inizio, Mese fine,Abbonamento abb,Table<Pubblicazione, Anagrafica, Integer> table) {
        List<EstrattoConto> estratticonto=getEstrattiConto(anno, inizio, fine, abb, table);
        List<SpedizioneItem> items = new ArrayList<>();
        for (EstrattoConto ec: estratticonto) {
            items.addAll(Smd.generaECItems(abb, ec));
        }
        final List<Spedizione> spedizioni = getSpedizioni(items, abb, table, spesaSpedizioneDao.findAll());
        save(abb, estratticonto, spedizioni);
    }

    private void save(Abbonamento abb, List<EstrattoConto> estrattiConto, List<Spedizione> spedizioni) {
        abbonamentoDao.save(abb);
        for (EstrattoConto ec: estrattiConto) {
            estrattoContoDao.save(ec);
        }
        for (Spedizione sped:spedizioni) {
            spedizioneDao.save(sped);
            for (SpedizioneItem item: sped.getSpedizioneItems()) {
                spedizioneItemDao.save(item);
            }
        }        
    }

    private void save(Abbonamento abb, EstrattoConto...contos) {
        abbonamentoDao.save(abb);
        for (EstrattoConto ec: contos) {
            estrattoContoDao.save(ec);
        }
        
    }
    public void saveAbbonamentiIncassi() {
        Abbonamento telematici001 = getAbbonamentoBy(antonioRusso);
        telematici001.setAnno(Anno.ANNO2017);
        EstrattoConto ec001t001 = addEC(telematici001, messaggio,1,new BigDecimal(15));
        telematici001.setImporto(new BigDecimal(15));
        save(telematici001,ec001t001);
        
        Abbonamento venezia002 = getAbbonamentoBy(micheleSantoro);
        venezia002.setCampo("000000018000854368");
        venezia002.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v002 = addEC(venezia002, messaggio,1,new BigDecimal(15));
        save(venezia002,ec001v002);
        
        Abbonamento venezia003 = getAbbonamentoBy(micheleSantoro);
        venezia003.setCampo("000000018000263519");
        venezia003.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v003 = addEC(venezia003, lodare,1,new BigDecimal(18));
        save(venezia003,ec001v003);

        Abbonamento venezia004 = SmdLoadSampleData.getAbbonamentoBy(micheleSantoro);
        venezia004.setCampo("000000018000254017");
        venezia004.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v004 = addEC(venezia004, messaggio,2,new BigDecimal(30));
        save(venezia004,ec001v004);

        Abbonamento venezia005 = SmdLoadSampleData.getAbbonamentoBy(micheleSantoro);
        venezia005.setCampo("000000018000761469");
        venezia005.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v005 = addEC(venezia005, messaggio,1,new BigDecimal(15));
        EstrattoConto ec002v005 = addEC(venezia005, lodare,1,new BigDecimal(16));
        EstrattoConto ec003v005 = addEC(venezia005, blocchetti,1,new BigDecimal(6));
        save(venezia005, ec001v005,ec002v005,ec003v005);

        Abbonamento venezia006 = SmdLoadSampleData.getAbbonamentoBy(micheleSantoro);
        venezia006.setCampo("000000018000253916");
        venezia006.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v006 = addEC(venezia006, blocchetti,8,new BigDecimal(48));
        save(venezia006, ec001v006);

        Abbonamento venezia007 = SmdLoadSampleData.getAbbonamentoBy(micheleSantoro);
        venezia007.setCampo("000000018000800386");
        venezia007.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v007 = addEC(venezia007, blocchetti,12,new BigDecimal(70));
        save(venezia007, ec001v007);
        
        Abbonamento venezia008 = SmdLoadSampleData.getAbbonamentoBy(micheleSantoro);
        venezia008.setCampo("000000018000508854");
        venezia008.setAnno(Anno.ANNO2017);
        EstrattoConto ec001v008 = addEC(venezia008, blocchetti,15,new BigDecimal(84));
        save(venezia008, ec001v008);

        Abbonamento firenze009 = SmdLoadSampleData.getAbbonamentoBy(davidePalma);
        firenze009.setCampo("000000018000686968");
        firenze009.setAnno(Anno.ANNO2017);
        EstrattoConto ec001f009 = addEC(firenze009, estratti,1,new BigDecimal(10));
        save(firenze009, ec001f009);
        
        Abbonamento firenze010 = SmdLoadSampleData.getAbbonamentoBy(davidePalma);
        firenze010.setCampo("000000018000198318");
        firenze010.setAnno(Anno.ANNO2017);
        EstrattoConto ec001f010 = addEC(firenze010, lodare,1,new BigDecimal(15));
        save(firenze010, ec001f010);

        Abbonamento firenze011 = SmdLoadSampleData.getAbbonamentoBy(davidePalma);
        firenze011.setCampo("000000018000201449");
        firenze011.setAnno(Anno.ANNO2017);
        EstrattoConto ec001f011 = addEC(firenze011, lodare,1,new BigDecimal(15));
        save(firenze011, ec001f011);

        Abbonamento firenze012 = SmdLoadSampleData.getAbbonamentoBy(davidePalma);
        firenze012.setAnno(Anno.ANNO2017);
        firenze012.setCampo("000000018000633491");
        EstrattoConto ec001f012 = addEC(firenze012, lodare,2,new BigDecimal(33));
        save(firenze012, ec001f012);
        
        Abbonamento firenze013 = SmdLoadSampleData.getAbbonamentoBy(davidePalma);
        firenze013.setAnno(Anno.ANNO2017);
        firenze013.setCampo("000000018000196500");
        EstrattoConto ec001f013 = addEC(firenze013, blocchetti,18,new BigDecimal(108));
        save(firenze013, ec001f013);
        
        Abbonamento bari014 = SmdLoadSampleData.getAbbonamentoBy(matteoParo);
        bari014.setAnno(Anno.ANNO2017);
        bari014.setCampo("000000018000106227");
        EstrattoConto ec001b014 = addEC(bari014, blocchetti,2,new BigDecimal(12));
        save(bari014, ec001b014);

        Abbonamento bari015 = SmdLoadSampleData.getAbbonamentoBy(matteoParo);
        bari015.setAnno(Anno.ANNO2017);
        bari015.setCampo("000000018000077317");
        EstrattoConto ec001b015 = addEC(bari015, blocchetti,6,new BigDecimal(36));
        save(bari015, ec001b015);

        Abbonamento bari016 = SmdLoadSampleData.getAbbonamentoBy(matteoParo);
        bari016.setAnno(Anno.ANNO2017);
        bari016.setCampo("000000018000125029");
        EstrattoConto ec001b016 = addEC(bari016, messaggio,4,new BigDecimal(60));
        save(bari016, ec001b016);

        Abbonamento bari017 = SmdLoadSampleData.getAbbonamentoBy(matteoParo);
        bari017.setAnno(Anno.ANNO2017);
        bari017.setCampo("000000018000065383");
        EstrattoConto ec001b017 = addEC(bari017, estratti,12,new BigDecimal(67));
        save(bari017, ec001b017);

    }

    private void loadSpeseSpedizione() {
        getSpeseSpedizione().stream().forEach(ss -> spesaSpedizioneDao.save(ss));
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
        
        if (loadPubblicazioniAdp || loadSampleStorico || loadSampleData) {
            log.info("Start Loading Pubblicazioni Adp");
           loadPubblicazioniAdp();
           loadSpeseSpedizione();
            log.info("End Loading Pubblicazioni Adp");
        }
        
        if (loadSampleAnagrafica || loadSampleStorico ||loadSampleData) {
            log.info("Start Loading Sample Anagrafica");
            loadAnagrafica();
            log.info("End Loading Sample Anagrafica");
        }
        
        if (loadSampleStorico || loadSampleData) {
            log.info("Start Loading Sample storico");
            loadStorico();
            log.info("End Loading Sample Storico");
        }
        
        if (loadSampleData) {
            log.info("Start Loading Sample Data");
            loadSampleData();
            log.info("End Loading Sample Data");
        }        
        
        if (createDemoUser) {
            createDemoUser();
        }
        
        if (createNormalUser) {
            createNormalUser();
        }

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
        
    private void loadStorico() {
        List<Storico> storici = new ArrayList<>();
        
        storici.add(getStoricoBy(diocesiMilano,antonioRusso, messaggio, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(diocesiMilano,antonioRusso, lodare, 1,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(diocesiMilano,antonioRusso, estratti, 11,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(gabrielePizzo,gabrielePizzo, messaggio, 10,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(gabrielePizzo,gabrielePizzo, lodare, 1,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(gabrielePizzo,gabrielePizzo, blocchetti, 10,Cassa.Contrassegno,TipoEstrattoConto.Scontato,Invio.Destinatario,InvioSpedizione.Spedizioniere));

        storici.add(getStoricoBy(matteoParo,matteoParo, messaggio, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioGesuiti,Invio.Destinatario,InvioSpedizione.AdpSede));
        storici.add(getStoricoBy(matteoParo,matteoParo, lodare, 1, Cassa.Ccp,TipoEstrattoConto.OmaggioGesuiti,Invio.Destinatario,InvioSpedizione.AdpSede));

        storici.add(getStoricoBy(davidePalma,davidePalma, messaggio, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaGeneralizia,Invio.Destinatario,InvioSpedizione.AdpSede));
        
        storici.add(getStoricoBy(micheleSantoro,micheleSantoro, blocchetti, 1, Cassa.Ccp,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(getStoricoBy(micheleSantoro, pasqualinaSantoro, blocchetti, 2,Cassa.Ccp,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));        

        storici.stream().forEach(s -> {
            storicoDao.save(s);
            s.getNote().stream().forEach(n -> notaDao.save(n));
        });
    }

    private void save(Incasso incasso) {
        incassoDao.save(incasso);
        incasso.getVersamenti().stream().forEach(v -> versamentoDao.save(v));
    }
    
    private void saveIncassi() {
        save(getIncassoTelematici());
        save(getIncassoVenezia());
        save(getIncassoFirenze());
        save(getIncassoBari());        
    }
    
    private void loadSampleData() {
        
        saveAbbonamentoDp();
        saveAbbonamentoMs();
        saveAbbonamentoGp();
        saveAbbonamentiIncassi();

        saveIncassi();
        
        
        Abbonamento abbonamentoDp = abbonamentoDao.findByIntestatario(davidePalma).iterator().next();
        Incasso incasso = getIncassoByImportoAndCampo(abbonamentoDp.getTotale(), abbonamentoDp.getCampo());
        incassoDao.save(incasso);
        
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.ANNO2018);
        
        loadCampagna(Anno.ANNO2018);
        loadCampagna(Anno.ANNO2019);
        //FIXME
        /*
        pubblicazioneDao.findAll().stream().forEach(p -> 
            EnumSet.of(Anno.ANNO2016, Anno.ANNO2017,Anno.ANNO2018).stream().forEach(anno -> 
                EnumSet.allOf(Mese.class).stream().forEach(mese -> {
                    Operazione op = Smd.generaOperazione(p, estrattoContoDao.findAll(), mese, anno);
                    if (op.getStimatoSped() > 0 ) {
                        operazioneDao.save(op);                               
                    }
                })
            )
        );

        operazioneDao.findAll()
            .stream()
            .filter(o -> o.getAnno().getAnno() < Smd.getAnnoCorrente().getAnno()
                          && o.getDefinitivoSped() == -1 )
            .forEach(o -> {
                o.setDefinitivoSped(o.getStimatoSped()+30);
                o.setDefinitivoSede(o.getStimatoSede()+30);
                operazioneDao.save(o);
                log.info(o.toString());
            });
        operazioneDao.findByAnno(Smd.getAnnoCorrente())
            .stream()
            .filter(o -> o.getMese().getPosizione() < Smd.getMeseCorrente().getPosizione()
                    && o.getDefinitivoSped() == -1)
            .forEach(o -> {
                o.setDefinitivoSped(o.getStimatoSped()+30);
                o.setDefinitivoSede(o.getStimatoSede()+30);
                operazioneDao.save(o);
                log.info(o.toString());
            });
            
            */
        
   
    }   
    
    private void loadCampagna(Anno anno) {
        Campagna campagna = new Campagna();
        campagna.setAnno(anno);
        List<Abbonamento> abbonamenti = 
            Smd.generaAbbonamentiCampagna(
                                      campagna, 
                                      anagraficaDao.findAll(), 
                                      storicoDao.findAll(),
                                      pubblicazioneDao.findAll()
          );
        
        campagnaDao.save(campagna);
        for (Abbonamento abb:abbonamenti) {
            for (Storico storico: storicoDao.findByIntestatario(abb.getIntestatario()).stream().filter(s -> s.getCassa() == abb.getCassa()).collect(Collectors.toList())) {
                EstrattoConto ec = Smd.generaECDaStorico(abb,storico);
                List<SpedizioneItem> items = Smd.generaECItems(abb, ec);
                if (items.isEmpty()) {
                    continue;
                }
                List<Spedizione> spedizioni = 
                        Smd.generaSpedizioni(abb, 
                                             items, 
                                             storico.getInvio(), 
                                             storico.getInvioSpedizione(), 
                                             storico.getDestinatario(), 
                                             new ArrayList<>(),
                                             spesaSpedizioneDao.findByAreaSpedizione(
                                                         storico.getDestinatario().getAreaSpedizione()));
                abbonamentoDao.save(abb);
                estrattoContoDao.save(ec);
                spedizioni.stream().forEach(sped ->  {
                    spedizioneDao.save(sped);
                    sped.getSpedizioneItems().forEach(item -> spedizioneItemDao.save(item));;
                });
            }
            
            
        }

    }
    
    private void createDemoUser() {
        UserInfo adp = new UserInfo("adp", passwordEncoder.encode("adp"), Role.LOCKED);
        userInfoDao.save(adp);
        log.info("creato user adp/adp");
        
    }
    
    private void createNormalUser() {
        UserInfo adp = new UserInfo("user", passwordEncoder.encode("pass"), Role.USER);
        userInfoDao.save(adp);
        log.info("creato user user/pass");
        
    }

}
