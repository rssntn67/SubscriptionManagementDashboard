package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.repository.OperazioneDao;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.repository.SpedizioneDao;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.repository.VersamentoDao;

public class SmdLoadSampleData implements Runnable {

    private AnagraficaDao anagraficaDao; 
    private StoricoDao storicoDao;
    private PubblicazioneDao pubblicazioneDao;
    private AbbonamentoDao abbonamentoDao;
    private SpedizioneDao spedizioneDao;
    private CampagnaDao campagnaDao;
    private IncassoDao incassoDao; 
    private VersamentoDao versamentoDao;
    private OperazioneDao prospettoDao;
    
    public SmdLoadSampleData(
            AnagraficaDao anagraficaDao, 
            StoricoDao storicoDao,
            PubblicazioneDao pubblicazioneDao, 
            AbbonamentoDao abbonamentoDao,
            SpedizioneDao spedizioneDao,
            CampagnaDao campagnaDao, 
            IncassoDao incassoDao, 
            VersamentoDao versamentoDao,
            OperazioneDao operazioneDao
    ) {
        this.anagraficaDao=anagraficaDao;
        this.storicoDao=storicoDao;
        this.pubblicazioneDao=pubblicazioneDao;
        this.abbonamentoDao=abbonamentoDao;
        this.spedizioneDao=spedizioneDao;
        this.campagnaDao=campagnaDao;
        this.incassoDao=incassoDao;
        this.versamentoDao=versamentoDao;
        this.prospettoDao=operazioneDao;
        
    }
    
    @Override
    public void run() {
        Pubblicazione messaggio = new Pubblicazione("Messaggio",
                                                    TipoPubblicazione.MENSILE);
        messaggio.setActive(true);
        messaggio.setAutore("AAVV");
        messaggio.setCostoUnitario(new BigDecimal(1.25));
        messaggio.setCostoScontato(new BigDecimal(1.25));
        messaggio.setEditore("ADP");
        messaggio.setMese(Mese.GENNAIO);
        pubblicazioneDao.save(messaggio);

        Pubblicazione lodare = new Pubblicazione("Lodare e Servire",
                                                 TipoPubblicazione.MENSILE);
        lodare.setActive(true);
        lodare.setAutore("AAVV");
        lodare.setCostoUnitario(new BigDecimal(1.50));
        lodare.setCostoScontato(new BigDecimal(1.50));
        lodare.setEditore("ADP");
        lodare.setMese(Mese.GENNAIO);
        pubblicazioneDao.save(lodare);

        Pubblicazione blocchetti = new Pubblicazione("Blocchetti",
                                                     TipoPubblicazione.SEMESTRALE);
        blocchetti.setActive(true);
        blocchetti.setAutore("AAVV");
        blocchetti.setCostoUnitario(new BigDecimal(3.00));
        blocchetti.setCostoScontato(new BigDecimal(2.40));
        blocchetti.setEditore("ADP");
        blocchetti.setMese(Mese.MARZO);
        pubblicazioneDao.save(blocchetti);


        Pubblicazione estratti = new Pubblicazione("Estratti",
                                                   TipoPubblicazione.ANNUALE);
        estratti.setActive(true);
        estratti.setAutore("AAVV");
        estratti.setCostoUnitario(new BigDecimal(10.00));
        estratti.setCostoScontato(new BigDecimal(10.00));
        estratti.setEditore("ADP");
        estratti.setMese(Mese.LUGLIO);
        pubblicazioneDao.save(estratti);

        // save a couple of customers

        Anagrafica ar = new Anagrafica("Antonio", "Russo");
        ar.setDiocesi(Diocesi.DIOCESI116);
        ar.setIndirizzo("Piazza Duomo 1");
        ar.setCitta("Milano");
        ar.setCap("20100");
        ar.setEmail("ar@arsinfo.it");
        ar.setTelefono("+3902000009");
        ar.setTitolo(TitoloAnagrafica.Vescovo);
        ar.setRegioneVescovi(Regione.LOMBARDIA);
        anagraficaDao.save(ar);
        
        Storico arlodare = new Storico(ar, lodare, 1);
        arlodare.setOmaggio(Omaggio.CuriaDiocesiana);
        storicoDao.save(arlodare);
        
        Storico armessaggio = new Storico(ar, messaggio, 1);
        armessaggio.setOmaggio(Omaggio.CuriaDiocesiana);
        storicoDao.save(armessaggio);

        Storico arblocchetti = new Storico(ar, blocchetti, 1);
        arblocchetti.setOmaggio(Omaggio.CuriaDiocesiana);
        storicoDao.save(arblocchetti);

        Storico arestratti = new Storico(ar, estratti, 1);
        arestratti.setOmaggio(Omaggio.CuriaDiocesiana);
        storicoDao.save(arestratti);

        Anagrafica gp = new Anagrafica("Gabriele", "Pizzo");
        gp.setDiocesi(Diocesi.DIOCESI116);
        gp.setIndirizzo("Piazza Sant'Ambrogio 1");
        gp.setCitta("Milano");
        gp.setCap("20110");
        gp.setEmail("gp@arsinfo.it");
        gp.setTelefono("+3902000010");
        anagraficaDao.save(gp);

        Storico gpblocchetti = new Storico(gp, blocchetti, 10);
        gpblocchetti.setOmaggio(Omaggio.ConSconto);
        gpblocchetti.setCassa(Cassa.Contrassegno);
        storicoDao.save(gpblocchetti);
        
        Storico gpmessaggio = new Storico(gp, messaggio, 1);
        gpmessaggio.setCassa(Cassa.Contrassegno);
        storicoDao.save(gpmessaggio);

        Anagrafica mp = new Anagrafica("Matteo", "Paro");
        mp.setDiocesi(Diocesi.DIOCESI168);
        mp.setIndirizzo("Piazza del Gesu' 1");
        mp.setCitta("Roma");
        mp.setCap("00192");
        mp.setEmail("mp@arsinfo.it");
        mp.setTelefono("+3906000020");
        anagraficaDao.save(mp);
        
        Storico mpmessaggio = new Storico(mp, messaggio, 10);
        mpmessaggio.setOmaggio(Omaggio.Gesuiti);
        mpmessaggio.setInvio(Invio.AdpSede);
        storicoDao.save(mpmessaggio);

        Anagrafica dp = new Anagrafica("Davide", "Palma");
        dp.setDiocesi(Diocesi.DIOCESI168);
        dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
        dp.setCitta("Roma");
        dp.setCap("00195");
        dp.setEmail("dp@arsinfo.it");
        dp.setTelefono("+3906000020");
        dp.setDirettoreDiocesiano(true);
        dp.setRegioneDirettoreDiocesano(Regione.LAZIO);
        anagraficaDao.save(dp);
        
        Storico dpmessaggio = new Storico(dp, messaggio, 1);
        dpmessaggio.setOmaggio(Omaggio.CuriaGeneralizia);
        dpmessaggio.setInvio(Invio.AdpSede);
        storicoDao.save(dpmessaggio);

        Anagrafica ms = new Anagrafica("Michele", "Santoro");
        ms.setDiocesi(Diocesi.DIOCESI126);
        ms.setIndirizzo("Via Duomo 10");
        ms.setCitta("Napoli");
        ms.setCap("80135");
        ms.setEmail("ms@arsinfo.it");
        ms.setTelefono("+39081400022");
        anagraficaDao.save(ms);

        Anagrafica ps = new Anagrafica("Pasqualina", "Santoro");
        ps.setDiocesi(Diocesi.DIOCESI126);
        ps.setIndirizzo("Piazza Dante 10");
        ps.setCitta("Napoli");
        ps.setCap("80135");
        ps.setEmail("arsinfo@adp.it");
        ps.setTelefono("+39081400023");
        anagraficaDao.save(ps);

        
        storicoDao.save(new Storico(ms, blocchetti, 10));
        storicoDao.save(new Storico(ms, ps, blocchetti, 5));
        
        Abbonamento abbonamentoMd = new Abbonamento(ms);
        Smd.addSpedizione(abbonamentoMd,blocchetti,ms,1);
        Smd.addSpedizione(abbonamentoMd,lodare,ms,1);
        Smd.addSpedizione(abbonamentoMd,estratti,ms,1);
        Smd.addSpedizione(abbonamentoMd,messaggio,ms,1);
        abbonamentoMd.setCampo(Smd.generateCampo(abbonamentoMd.getAnno(),
                                             abbonamentoMd.getInizio(),
                                             abbonamentoMd.getFine()));
        Smd.calcoloCostoAbbonamento(abbonamentoMd);
        abbonamentoDao.save(abbonamentoMd);

        Abbonamento abbonamentoCo = new Abbonamento(gp);
        Smd.addSpedizione(abbonamentoCo,blocchetti,gp,10);
        Smd.addSpedizione(abbonamentoCo,lodare,gp,10);
        Smd.addSpedizione(abbonamentoCo,estratti,gp,5);
        Smd.addSpedizione(abbonamentoCo,messaggio,gp,5);
        Smd.addSpedizione(abbonamentoCo,blocchetti,mp,10);
        Smd.addSpedizione(abbonamentoCo,blocchetti,ar,10);
        abbonamentoCo.setAnno(Anno.ANNO2018);
        abbonamentoCo.setCampo(Smd.generateCampo(abbonamentoCo.getAnno(),
                                             abbonamentoCo.getInizio(),
                                             abbonamentoCo.getFine()));
        Smd.calcoloCostoAbbonamento(abbonamentoCo);
        abbonamentoDao.save(abbonamentoCo);

        Abbonamento abbonamentoDp = new Abbonamento(dp);
        Smd.addSpedizione(abbonamentoDp,blocchetti,dp,10);
        abbonamentoDp.setInizio(Mese.MAGGIO);
        abbonamentoDp.setSpese(new BigDecimal("3.75"));
        abbonamentoDp.setCampo(Smd.generateCampo(abbonamentoDp.getAnno(),
                                             abbonamentoDp.getInizio(),
                                             abbonamentoDp.getFine()));
        Smd.calcoloCostoAbbonamento(abbonamentoDp);
        abbonamentoDao.save(abbonamentoDp);
        
        Abbonamento telematici001 = new Abbonamento(ar);
        Smd.addSpedizione(telematici001, blocchetti,ar,1);
        telematici001.setCosto(new BigDecimal(15));
        telematici001.setCampo("000000018000792609");
        abbonamentoDao.save(telematici001);
        
        Abbonamento venezia002 = new Abbonamento(ms);
        Smd.addSpedizione(venezia002, blocchetti,ms,1);
        venezia002.setCosto(new BigDecimal(15));
        venezia002.setCampo("000000018000854368");
        abbonamentoDao.save(venezia002);

        Abbonamento venezia003 = new Abbonamento(ms);
        Smd.addSpedizione(venezia003, blocchetti,ms,1);
        venezia003.setCosto(new BigDecimal(18));
        venezia003.setCampo("000000018000263519");
        abbonamentoDao.save(venezia003);

        Abbonamento venezia004 = new Abbonamento(ms);
        Smd.addSpedizione(venezia004, blocchetti,ms,2);
        venezia004.setCosto(new BigDecimal(30));
        venezia004.setCampo("000000018000254017");
        abbonamentoDao.save(venezia004);

        Abbonamento venezia005 = new Abbonamento(ms);
        Smd.addSpedizione(venezia005, blocchetti,ms,2);
        venezia005.setCosto(new BigDecimal(37));
        venezia005.setCampo("000000018000761469");
        abbonamentoDao.save(venezia005);

        Abbonamento venezia006 = new Abbonamento(ms);
        Smd.addSpedizione(venezia006, blocchetti,ms,3);
        venezia006.setCosto(new BigDecimal(48));
        venezia006.setCampo("000000018000253916");
        abbonamentoDao.save(venezia006);

        Abbonamento venezia007 = new Abbonamento(ms);
        Smd.addSpedizione(venezia007, blocchetti,ms,10);
        venezia007.setCosto(new BigDecimal(70));
        venezia007.setCampo("000000018000800386");
        abbonamentoDao.save(venezia007);
        
        Abbonamento venezia008 = new Abbonamento(ms);
        Smd.addSpedizione(venezia008, blocchetti,ms,15);
        venezia008.setCosto(new BigDecimal(84));
        venezia008.setCampo("000000018000508854");
        abbonamentoDao.save(venezia008);

        Abbonamento firenze009 = new Abbonamento(dp);
        Smd.addSpedizione(firenze009, estratti,dp,1);
        firenze009.setCosto(new BigDecimal(10));
        firenze009.setCampo("000000018000686968");
        abbonamentoDao.save(firenze009);
        
        Abbonamento firenze010 = new Abbonamento(dp);
        Smd.addSpedizione(firenze010, lodare,dp,1);
        firenze010.setCosto(new BigDecimal(15));
        firenze010.setCampo("000000018000198318");
        abbonamentoDao.save(firenze010);

        Abbonamento firenze011 = new Abbonamento(dp);
        Smd.addSpedizione(firenze011, lodare,dp,1);
        firenze011.setCosto(new BigDecimal(15));
        firenze011.setCampo("000000018000201449");
        abbonamentoDao.save(firenze011);

        Abbonamento firenze012 = new Abbonamento(dp);
        Smd.addSpedizione(firenze012, lodare,dp,3);
        firenze012.setCosto(new BigDecimal(33));
        firenze012.setCampo("000000018000633491");
        abbonamentoDao.save(firenze012);
        
        Abbonamento firenze013 = new Abbonamento(dp);
        Smd.addSpedizione(firenze013, lodare,dp,10);
        Smd.addSpedizione(firenze013, estratti,dp,10);
        Smd.addSpedizione(firenze013, blocchetti,dp,10);
        firenze013.setCosto(new BigDecimal(108));
        firenze013.setCampo("000000018000196500");
        abbonamentoDao.save(firenze013);
        
        Abbonamento bari014 = new Abbonamento(mp);
        Smd.addSpedizione(bari014, lodare,mp,1);
        bari014.setCosto(new BigDecimal(12));
        bari014.setCampo("000000018000106227");
        abbonamentoDao.save(bari014);

        Abbonamento bari015 = new Abbonamento(mp);
        Smd.addSpedizione(bari015, lodare,mp,3);
        bari015.setCosto(new BigDecimal(36));
        bari015.setCampo("000000018000077317");
        abbonamentoDao.save(bari015);

        Abbonamento bari016 = new Abbonamento(mp);
        Smd.addSpedizione(bari016, lodare,mp,5);
        bari016.setCosto(new BigDecimal(60));
        bari016.setCampo("000000018000125029");
        abbonamentoDao.save(bari016);

        Abbonamento bari017 = new Abbonamento(mp);
        Smd.addSpedizione(bari017, estratti,mp,10);
        bari017.setCosto(new BigDecimal(67));
        bari017.setCampo("000000018000065383");
        abbonamentoDao.save(bari017);


        Campagna campagna2018=new Campagna();
        campagna2018.setAnno(Anno.ANNO2018);
        campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,messaggio));
        campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,lodare));
        campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,blocchetti));
        campagna2018.addCampagnaItem(new CampagnaItem(campagna2018,estratti));

        Smd.generaCampagna(campagna2018, storicoDao.findAll(), new ArrayList<>());
        campagnaDao.save(campagna2018);

        Campagna campagna2019=new Campagna();
        campagna2019.setAnno(Anno.ANNO2019);
        campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,messaggio));
        campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,lodare));
        campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,blocchetti));
        campagna2019.addCampagnaItem(new CampagnaItem(campagna2019,estratti));

        Smd.generaCampagna(campagna2019, storicoDao.findAll(), new ArrayList<>());
        campagnaDao.save(campagna2019);

        Campagna campagna2020=new Campagna();
        campagna2020.setAnno(Anno.ANNO2020);
        campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,messaggio));
        campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,lodare));
        campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,blocchetti));
        campagna2020.addCampagnaItem(new CampagnaItem(campagna2020,estratti));

        Smd.generaCampagna(campagna2020, storicoDao.findAll(), new ArrayList<>());
        campagnaDao.save(campagna2020);

        String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1= new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        Incasso incasso1 = Smd.generateIncasso(versamenti1, riepilogo1); 
        incassoDao.save(incasso1);
        
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

        Incasso incasso2 = Smd.generateIncasso(versamenti2, riepilogo2);
        incassoDao.save(incasso2);
        
        String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
        Set<String> versamenti3= new HashSet<>();
        versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
        versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
        versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
        versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
        versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
        versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");

        Incasso incasso3 = Smd.generateIncasso(versamenti3, riepilogo3);
        incassoDao.save(incasso3);
        
        String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
        Set<String> versamenti4= new HashSet<>();
        versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
        versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
        versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
        versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
        versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
        versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
        versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");
        
        Incasso incasso4=Smd.generateIncasso(versamenti4, riepilogo4);
        incassoDao.save(incasso4);
        
        
        
        Incasso incasso5 = new Incasso();
        incasso5.setCassa(Cassa.Contrassegno);
        incasso5.setCcp(Ccp.DUE);
        
        Versamento versamentoIncasso5 = new Versamento(incasso5,abbonamentoDp.getTotale());
        versamentoIncasso5.setCampo(abbonamentoDp.getCampo());
        versamentoIncasso5.setDataPagamento(incasso5.getDataContabile());
        versamentoIncasso5.setOperazione("Assegno n.0002889893819813 Banca Popolare di Chiavari");
        incasso5.addVersamento(versamentoIncasso5);
        Smd.calcoloImportoIncasso(incasso5);
        incassoDao.save(incasso5);
        
        versamentoDao.save(
               Smd.incassa(incasso5,versamentoIncasso5, abbonamentoDp));
        incassoDao.save(incasso5);
        abbonamentoDao.save(abbonamentoDp);
                
        Smd.generaOperazioni(estratti, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2018).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(estratti, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2019).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(estratti, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(estratti),Anno.ANNO2020).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        
        Smd.generaOperazioni(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2018).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2019).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(blocchetti, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(blocchetti),Anno.ANNO2020).stream().forEach(p -> {
            prospettoDao.save(p);
        });

        Smd.generaOperazioni(lodare, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2018).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(lodare, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2019).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(lodare, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(lodare),Anno.ANNO2020).stream().forEach(p -> {
            prospettoDao.save(p);
        });

        Smd.generaOperazioni(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2018), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2018).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2019), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2019).stream().forEach(p -> {
            prospettoDao.save(p);
        });
        Smd.generaOperazioni(messaggio, abbonamentoDao.findByAnno(Anno.ANNO2020), spedizioneDao.findByPubblicazione(messaggio),Anno.ANNO2020).stream().forEach(p -> {
            prospettoDao.save(p);
        });

    }

}
