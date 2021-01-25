package it.arsinfo.smd.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import it.arsinfo.smd.dao.SmdService;
import it.arsinfo.smd.dao.repository.AbbonamentoDao;
import it.arsinfo.smd.dao.repository.AnagraficaDao;
import it.arsinfo.smd.dao.repository.CampagnaDao;
import it.arsinfo.smd.dao.repository.DistintaVersamentoDao;
import it.arsinfo.smd.dao.repository.NotaDao;
import it.arsinfo.smd.dao.repository.OperazioneDao;
import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.dao.repository.RivistaAbbonamentoDao;
import it.arsinfo.smd.dao.repository.SpedizioneDao;
import it.arsinfo.smd.dao.repository.SpedizioneItemDao;
import it.arsinfo.smd.dao.repository.SpesaSpedizioneDao;
import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.Smd;


public class SmdLoadSampleData implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SmdLoadSampleData.class);

    protected final SmdService smdService;
    protected final AnagraficaDao anagraficaDao; 
    protected final PubblicazioneDao pubblicazioneDao;
    private final SpesaSpedizioneDao spesaSpedizioneDao;
    protected final AbbonamentoDao abbonamentoDao;
    private final RivistaAbbonamentoDao rivistaAbbonamentoDao;
    private final SpedizioneDao spedizioneDao;
    private final SpedizioneItemDao spedizioneItemDao;
    protected final StoricoDao storicoDao;
    protected final NotaDao notaDao;
    private final DistintaVersamentoDao incassoDao; 
    private final VersamentoDao versamentoDao;
    
    protected Pubblicazione messaggio;
    protected Pubblicazione lodare;
    protected Pubblicazione estratti;
    protected Pubblicazione blocchetti;
    protected List<SpesaSpedizione> speseSped;
    
    private Anagrafica antonioRusso;
    private Anagrafica diocesiMilano;
    private Anagrafica gabrielePizzo;
    private Anagrafica matteoParo;
    private Anagrafica davidePalma;
    private Anagrafica micheleSantoro;
    private Anagrafica pasqualinaSantoro;
    private Anagrafica t001;
    private Anagrafica ve002;
    private Anagrafica ve003;
    private Anagrafica ve004;
    private Anagrafica ve005;
    private Anagrafica ve006;
    private Anagrafica ve007;
    private Anagrafica ve008;
    private Anagrafica fi009;
    private Anagrafica fi010;
    private Anagrafica fi011;
    private Anagrafica fi012;
    private Anagrafica fi013;
    private Anagrafica ba014;
    private Anagrafica ba015;
    private Anagrafica ba016;
    private Anagrafica ba017;

    public SmdLoadSampleData(
            SmdService smdService,
            AnagraficaDao anagraficaDao, 
            StoricoDao storicoDao,
            NotaDao notaDao,
            PubblicazioneDao pubblicazioneDao, 
            SpesaSpedizioneDao spesaSpedizioneDao, 
            AbbonamentoDao abbonamentoDao,
            RivistaAbbonamentoDao rivistaAbbonamentoDao,
            SpedizioneDao spedizioneDao,
            SpedizioneItemDao spedizioneItemDao,
            CampagnaDao campagnaDao, 
            DistintaVersamentoDao incassoDao, 
            VersamentoDao versamentoDao,
            OperazioneDao operazioneDao
    ) {
        this.smdService = smdService;
        this.anagraficaDao=anagraficaDao;
        this.storicoDao=storicoDao;
        this.notaDao=notaDao;
        this.pubblicazioneDao=pubblicazioneDao;
        this.spesaSpedizioneDao=spesaSpedizioneDao;
        this.abbonamentoDao=abbonamentoDao;
        this.rivistaAbbonamentoDao=rivistaAbbonamentoDao;
        this.spedizioneDao=spedizioneDao;
        this.spedizioneItemDao=spedizioneItemDao;
        this.incassoDao=incassoDao;
        this.versamentoDao=versamentoDao;
    }
    
    
    private void saveAbbonamentoMs() {
        Table<Pubblicazione, Anagrafica, Integer> spedizioni = HashBasedTable.create();
        spedizioni.put(messaggio, micheleSantoro, 1);
        spedizioni.put(lodare, micheleSantoro, 1);
        spedizioni.put(blocchetti, micheleSantoro, 1);
        spedizioni.put(estratti, micheleSantoro, 1);
                  
        Abbonamento abb =  SmdHelper.getAbbonamentoBy(
                micheleSantoro, 
                Anno.getAnnoSuccessivo(Anno.getAnnoCorrente()), 
                false
                );
        genera(Mese.GENNAIO, Mese.DICEMBRE, abb, spedizioni);

    }
        
    private void saveAbbonamentoGp() {
        Table<Pubblicazione, Anagrafica, Integer> table = HashBasedTable.create();
        table.put(messaggio, gabrielePizzo, 1);
        table.put(lodare, gabrielePizzo, 1);
        table.put(blocchetti, gabrielePizzo, 1);
        table.put(estratti, gabrielePizzo, 1);
        table.put(estratti, antonioRusso, 1);
        table.put(estratti,matteoParo, 1);
        Abbonamento abb = SmdHelper.getAbbonamentoBy(
                            gabrielePizzo, 
                            Anno.getAnnoSuccessivo(Anno.getAnnoCorrente()), 
                            false 
                            );
        genera(Mese.GENNAIO, Mese.DICEMBRE, abb, table);
    }
    
    private  void saveAbbonamentoDp() {
        Table<Pubblicazione, Anagrafica, Integer> table = HashBasedTable.create();
        table.put(blocchetti, davidePalma, 1);
        Abbonamento abb = SmdHelper.getAbbonamentoBy(
                            davidePalma, 
                            Anno.getAnnoSuccessivo(Anno.getAnnoCorrente()), 
                            false
                            );        
        genera(Mese.MAGGIO, Mese.DICEMBRE, abb, table);
    }
    
    private void genera(Mese inizio, Mese fine,Abbonamento abb,Table<Pubblicazione, Anagrafica, Integer> table) {
        Anno anno = abb.getAnno();
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();        
        for (Cell<Pubblicazione, Anagrafica, Integer> ect: table.cellSet()) {
            RivistaAbbonamento ec = new RivistaAbbonamento();
            ec.setAbbonamento(abb);
            ec.setPubblicazione(ect.getRowKey());
            ec.setNumero(ect.getValue());
            ec.setMeseInizio(inizio);
            ec.setAnnoInizio(anno);
            ec.setMeseFine(fine);
            ec.setAnnoFine(anno);
            ec.setDestinatario(ect.getColumnKey());
            spedizioni =
                  Smd.genera(
                           abb, 
                           ec,
                           spedizioni, 
                           SmdHelper.getSpeseSpedizione(), Mese.getMeseCorrente(),Anno.getAnnoCorrente()
                       
                  );
            abbonamentoDao.save(abb);
            rivistaAbbonamentoDao.save(ec);
            for (SpedizioneWithItems sped:spedizioni) {
                spedizioneDao.save(sped.getSpedizione());
                for (SpedizioneItem item: sped.getSpedizioneItems()) {
                    spedizioneItemDao.save(item);
                }
            }        

            
        }        
    }

    private void save(Abbonamento abb, RivistaAbbonamento...contos) {
        for (RivistaAbbonamento ec:contos) {
            abb.setImporto(abb.getImporto().add(ec.getImporto()));
        }
        abbonamentoDao.save(abb);
        for (RivistaAbbonamento ec: contos) {
            rivistaAbbonamentoDao.save(ec);
        }
        
    }
    public void saveAbbonamentiIncassi() {
        Abbonamento telematici001 = SmdHelper.getAbbonamentoBy(t001);
        telematici001.setAnno(Anno.ANNO2017);
        telematici001.setCodeLine("000000018000792609");
        RivistaAbbonamento ec001t001 = SmdHelper.addEC(telematici001, messaggio,1,new BigDecimal(15));
        save(telematici001,ec001t001);
        
        Abbonamento venezia002 = SmdHelper.getAbbonamentoBy(ve002);
        venezia002.setCodeLine("000000018000854368");
        venezia002.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v002 = SmdHelper.addEC(venezia002, messaggio,1,new BigDecimal(15));
        save(venezia002,ec001v002);
        
        Abbonamento venezia003 = SmdHelper.getAbbonamentoBy(ve003);
        venezia003.setCodeLine("000000018000263519");
        venezia003.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v003 = SmdHelper.addEC(venezia003, lodare,1,new BigDecimal(18));
        save(venezia003,ec001v003);

        Abbonamento venezia004 = SmdHelper.getAbbonamentoBy(ve004);
        venezia004.setCodeLine("000000018000254017");
        venezia004.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v004 = SmdHelper.addEC(venezia004, messaggio,2,new BigDecimal(30));
        save(venezia004,ec001v004);

        Abbonamento venezia005 = SmdHelper.getAbbonamentoBy(ve005);
        venezia005.setCodeLine("000000018000761469");
        venezia005.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v005 = SmdHelper.addEC(venezia005, messaggio,1,new BigDecimal(15));
        RivistaAbbonamento ec002v005 = SmdHelper.addEC(venezia005, lodare,1,new BigDecimal(16));
        RivistaAbbonamento ec003v005 = SmdHelper.addEC(venezia005, blocchetti,1,new BigDecimal(6));
        save(venezia005, ec001v005,ec002v005,ec003v005);

        Abbonamento venezia006 = SmdHelper.getAbbonamentoBy(ve006);
        venezia006.setCodeLine("000000018000253916");
        venezia006.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v006 = SmdHelper.addEC(venezia006, blocchetti,8,new BigDecimal(48));
        save(venezia006, ec001v006);

        Abbonamento venezia007 = SmdHelper.getAbbonamentoBy(ve007);
        venezia007.setCodeLine("000000018000800386");
        venezia007.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v007 = SmdHelper.addEC(venezia007, blocchetti,12,new BigDecimal(70));
        save(venezia007, ec001v007);
        
        Abbonamento venezia008 = SmdHelper.getAbbonamentoBy(ve008);
        venezia008.setCodeLine("000000018000508854");
        venezia008.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001v008 = SmdHelper.addEC(venezia008, blocchetti,15,new BigDecimal(84));
        save(venezia008, ec001v008);

        Abbonamento firenze009 = SmdHelper.getAbbonamentoBy(fi009);
        firenze009.setCodeLine("000000018000686968");
        firenze009.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001f009 = SmdHelper.addEC(firenze009, estratti,1,new BigDecimal(10));
        save(firenze009, ec001f009);
        
        Abbonamento firenze010 = SmdHelper.getAbbonamentoBy(fi010);
        firenze010.setCodeLine("000000018000198318");
        firenze010.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001f010 = SmdHelper.addEC(firenze010, lodare,1,new BigDecimal(15));
        save(firenze010, ec001f010);

        Abbonamento firenze011 = SmdHelper.getAbbonamentoBy(fi011);
        firenze011.setCodeLine("000000018000201449");
        firenze011.setAnno(Anno.ANNO2017);
        RivistaAbbonamento ec001f011 = SmdHelper.addEC(firenze011, lodare,1,new BigDecimal(15));
        save(firenze011, ec001f011);

        Abbonamento firenze012 = SmdHelper.getAbbonamentoBy(fi012);
        firenze012.setAnno(Anno.ANNO2017);
        firenze012.setCodeLine("000000018000633491");
        RivistaAbbonamento ec001f012 = SmdHelper.addEC(firenze012, lodare,2,new BigDecimal(33));
        save(firenze012, ec001f012);
        
        Abbonamento firenze013 = SmdHelper.getAbbonamentoBy(fi013);
        firenze013.setAnno(Anno.ANNO2017);
        firenze013.setCodeLine("000000018000196500");
        RivistaAbbonamento ec001f013 = SmdHelper.addEC(firenze013, blocchetti,18,new BigDecimal(108));
        save(firenze013, ec001f013);
        
        Abbonamento bari014 = SmdHelper.getAbbonamentoBy(ba014);
        bari014.setAnno(Anno.ANNO2017);
        bari014.setCodeLine("000000018000106227");
        RivistaAbbonamento ec001b014 = SmdHelper.addEC(bari014, blocchetti,2,new BigDecimal(12));
        save(bari014, ec001b014);

        Abbonamento bari015 = SmdHelper.getAbbonamentoBy(ba015);
        bari015.setAnno(Anno.ANNO2017);
        bari015.setCodeLine("000000018000077317");
        RivistaAbbonamento ec001b015 = SmdHelper.addEC(bari015, blocchetti,6,new BigDecimal(36));
        save(bari015, ec001b015);

        Abbonamento bari016 = SmdHelper.getAbbonamentoBy(ba016);
        bari016.setAnno(Anno.ANNO2017);
        bari016.setCodeLine("000000018000125029");
        RivistaAbbonamento ec001b016 = SmdHelper.addEC(bari016, messaggio,4,new BigDecimal(60));
        save(bari016, ec001b016);

        Abbonamento bari017 = SmdHelper.getAbbonamentoBy(ba017);
        bari017.setAnno(Anno.ANNO2017);
        bari017.setCodeLine("000000018000065383");
        RivistaAbbonamento ec001b017 = SmdHelper.addEC(bari017, estratti,12,new BigDecimal(67));
        save(bari017, ec001b017);

    }

    protected void loadSpeseSpedizione() {
        speseSped = SmdHelper.getSpeseSpedizione();
        speseSped.stream().forEach(ss -> spesaSpedizioneDao.save(ss));
    }

    protected void loadPubblicazioniAdp() {
        messaggio = SmdHelper.getMessaggio();
        lodare = SmdHelper.getLodare();
        blocchetti = SmdHelper.getBlocchetti();
        estratti = SmdHelper.getEstratti();
        
        pubblicazioneDao.save(messaggio);
        pubblicazioneDao.save(lodare);
        pubblicazioneDao.save(blocchetti);
        pubblicazioneDao.save(estratti);
    }
    
    @Override
    public void run() {
        
    log.info("Start Loading Pubblicazioni Adp");
    loadPubblicazioniAdp();
    loadSpeseSpedizione();
    log.info("End Loading Pubblicazioni Adp");
        
    log.info("Start Loading Sample Anagrafica");
    loadAnagrafica();
    log.info("End Loading Sample Anagrafica");
        
        
    log.info("Start Loading Sample storico");
    loadStorico();
    log.info("End Loading Sample Storico");
        
    log.info("Start Loading Sample Data");
    loadSampleData();
    log.info("End Loading Sample Data");
                        
    }
    

    private void loadAnagrafica() {
        
        diocesiMilano=SmdHelper.getDiocesiMi();
        anagraficaDao.save(diocesiMilano);

        antonioRusso=SmdHelper.getAR();
        antonioRusso.setCo(diocesiMilano);
        anagraficaDao.save(antonioRusso);
        
        gabrielePizzo=SmdHelper.getGP();
        anagraficaDao.save(gabrielePizzo);
        
        matteoParo = SmdHelper.getMP();
        anagraficaDao.save(matteoParo);
        
        davidePalma = SmdHelper.getDP();
        anagraficaDao.save(davidePalma);
        
        micheleSantoro = SmdHelper.getMS();
        anagraficaDao.save(micheleSantoro);
        
        pasqualinaSantoro = SmdHelper.getPS();
        anagraficaDao.save(pasqualinaSantoro);
        
        t001=SmdHelper.getT001();
        anagraficaDao.save(t001);
        ve002 = SmdHelper.getVe002();
        anagraficaDao.save(ve002);
        ve003 = SmdHelper.getVe003();
        anagraficaDao.save(ve003);
        ve004 = SmdHelper.getVe004();
        anagraficaDao.save(ve004);
        ve005 = SmdHelper.getVe005();
        anagraficaDao.save(ve005);
        ve006 = SmdHelper.getVe006();
        anagraficaDao.save(ve006);
        ve007 = SmdHelper.getVe007();
        anagraficaDao.save(ve007);
        ve008 = SmdHelper.getVe008();
        anagraficaDao.save(ve008);
        fi009 = SmdHelper.getFi009();
        anagraficaDao.save(fi009);
        fi010 = SmdHelper.getFi010();
        anagraficaDao.save(fi010);
        fi011 = SmdHelper.getFi011();
        anagraficaDao.save(fi011);
        fi012 = SmdHelper.getFi012();
        anagraficaDao.save(fi012);
        fi013 = SmdHelper.getFi013();
        anagraficaDao.save(fi013);
        
        ba014 = SmdHelper.getBa014();
        anagraficaDao.save(ba014);
        ba015 = SmdHelper.getBa015();
        anagraficaDao.save(ba015);
        ba016 = SmdHelper.getBa016();
        anagraficaDao.save(ba016);
        ba017 = SmdHelper.getBa017();
        anagraficaDao.save(ba017);


    }
        
    private void loadStorico() {
        List<Storico> storici = new ArrayList<>();
        
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, messaggio, 10,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, lodare, 1,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 10,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, estratti, 11,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, messaggio, 10,true,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, lodare, 1,true,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, blocchetti, 10,true,TipoAbbonamentoRivista.Scontato,InvioSpedizione.Spedizioniere));

        storici.add(SmdHelper.getStoricoBy(matteoParo,matteoParo, messaggio, 10,false,TipoAbbonamentoRivista.OmaggioGesuiti,InvioSpedizione.AdpSedeNoSpese));
        storici.add(SmdHelper.getStoricoBy(matteoParo,matteoParo, lodare, 1, false,TipoAbbonamentoRivista.OmaggioGesuiti,InvioSpedizione.AdpSedeNoSpese));

        storici.add(SmdHelper.getStoricoBy(davidePalma,davidePalma, messaggio, 10,false,TipoAbbonamentoRivista.OmaggioCuriaGeneralizia,InvioSpedizione.AdpSedeNoSpese));
        
        storici.add(SmdHelper.getStoricoBy(micheleSantoro,micheleSantoro, blocchetti, 1, false,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(micheleSantoro, pasqualinaSantoro, blocchetti, 2,false,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));        

        storici.stream().forEach(s -> {
            storicoDao.save(s);
            notaDao.save(SmdHelper.getNota(s));
        });
    }

    private void save(DistintaVersamento incasso) {
        incassoDao.save(incasso);
        incasso.getItems().stream().forEach(v -> versamentoDao.save(v));
    }
    
    private void saveIncassi() {
        save(SmdHelper.getIncassoTelematici());
        save(SmdHelper.getIncassoVenezia());
        save(SmdHelper.getIncassoFirenze());
        save(SmdHelper.getIncassoBari());        
    }
    
    private void loadSampleData() {
        
        saveAbbonamentoDp();
        
        saveAbbonamentoMs();
        saveAbbonamentoGp();
        saveAbbonamentiIncassi();

        saveIncassi();
        
        
        Abbonamento abbonamentoDp = abbonamentoDao.findByIntestatario(davidePalma).iterator().next();
        DistintaVersamento incasso = SmdHelper.getIncassoByImportoAndCodeLine(abbonamentoDp.getTotale(), abbonamentoDp.getCodeLine());
        save(incasso);                   
    }   
            
}
