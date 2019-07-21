package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    private static final Logger log = LoggerFactory.getLogger(Smd.class);

    private static EstrattoConto crea(Abbonamento abb,Pubblicazione p, TipoEstrattoConto tipo, int numero) {
        Anno anno = Anno.getAnnoProssimo();
        Mese mese = Mese.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        EstrattoConto ec =  new EstrattoConto();
        ec.setAbbonamento(abb);
        ec.setNumero(numero);
        ec.setPubblicazione(p);
        ec.setTipoEstrattoConto(tipo);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setAnnoInizio(anno);
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setAnnoFine(anno);
        ec.setDestinatario(abb.getIntestatario());
        Smd.generaSpedizioni(abb, ec, new ArrayList<>(), SmdLoadSampleData.getSpeseSpedizione());

        return ec;
    }
    @Test
    public void testMesiPubblicazione() {
 
        Pubblicazione pe = new Pubblicazione("pe", TipoPubblicazione.MENSILE);
        pe.setGen(true);
        pe.setFeb(true);
        pe.setMar(true);
        pe.setApr(true);
        pe.setMag(true);
        pe.setGiu(true);
        pe.setLug(true);
        pe.setAgo(true);
        pe.setSet(true);
        pe.setOtt(true);
        pe.setNov(true);
        pe.setDic(true);
        assertEquals(EnumSet.allOf(Mese.class), pe.getMesiPubblicazione());

        
        Pubblicazione pd = new Pubblicazione("pd", TipoPubblicazione.MENSILE);
        pd.setGen(true);
        pd.setFeb(true);
        pd.setMar(true);
        pd.setApr(true);
        pd.setMag(true);
        pd.setGiu(true);
        pd.setLug(true);
        pd.setSet(true);
        pd.setOtt(true);
        pd.setNov(true);
        pd.setDic(true);
        assertEquals(11, pd.getMesiPubblicazione().size());
        assertFalse(pd.getMesiPubblicazione().contains(Mese.AGOSTO));

        Pubblicazione pa = new Pubblicazione("pa", TipoPubblicazione.SEMESTRALE);
        pa.setFeb(true);
        pa.setAgo(true);
        assertEquals(2, pa.getMesiPubblicazione().size());
        assertTrue(pa.getMesiPubblicazione().contains(Mese.AGOSTO));
        assertTrue(pa.getMesiPubblicazione().contains(Mese.FEBBRAIO));
        
        Pubblicazione pb = new Pubblicazione("pb", TipoPubblicazione.SEMESTRALE);
        pb.setMar(true);
        pb.setSet(true);
        assertEquals(2, pb.getMesiPubblicazione().size());
        assertTrue(pb.getMesiPubblicazione().contains(Mese.MARZO));
        assertTrue(pb.getMesiPubblicazione().contains(Mese.SETTEMBRE));

        Pubblicazione pc = new Pubblicazione("pc", TipoPubblicazione.ANNUALE);
        pc.setApr(true);
        assertEquals(1, pc.getMesiPubblicazione().size());
        assertTrue(pc.getMesiPubblicazione().contains(Mese.APRILE));
        
    }

    @Test
    public void testGenerateCampo() {
        Set<String> campi = new HashSet<>();
        for (int i=0; i< 200000;i++) {
        Anagrafica a = SmdLoadSampleData.getAnagraficaBy(""+i, ""+i);
        String campo = Abbonamento.generaCodeLine(Anno.ANNO2019,a);
        assertEquals("19", campo.substring(0, 2));
        assertTrue(Abbonamento.checkCampo(campo));
        assertTrue(campi.add(campo));
        }
        assertEquals(200000, campi.size());        
    }

    @Test
    public void testAnnoMese() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int anno = now.get(Calendar.YEAR);
        assertEquals(anno-1, Anno.getAnnoPassato().getAnno());
        assertEquals(anno, Anno.getAnnoCorrente().getAnno());
        assertEquals(anno+1, Anno.getAnnoProssimo().getAnno());
        int mese = now.get(Calendar.MONTH);
        // Mese comincia da 0 
        assertEquals(mese+1, Mese.getMeseCorrente().getPosizione());
        
    }
    
    private void verificaImportoAbbonamentoAnnuale(Abbonamento abb, EstrattoConto ec) {
        assertEquals(0.0,abb.getSpese().doubleValue(),0);
        assertEquals(true, ec.isAbbonamentoAnnuale());
        assertEquals(abb, ec.getAbbonamento());
        switch (ec.getTipoEstrattoConto()) {
        case OmaggioCuriaDiocesiana:
            assertEquals(0.0,abb.getImporto().doubleValue(),0);
           break;
        case OmaggioGesuiti:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
           break;
        case OmaggioCuriaGeneralizia:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case OmaggioDirettoreAdp:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case OmaggioEditore:
            assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
        case Ordinario:
            assertEquals(ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Sostenitore:
            assertEquals(ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Scontato:
            assertEquals(ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        case Web:
            assertEquals(ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
            break;
        default:
            break;
        }
                

    }
    
    @Test
    public void testCreaSpedizioneMessaggio() {
        List<SpesaSpedizione> spese = SmdLoadSampleData.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(SmdLoadSampleData.getAnagraficaBy("tizio", "caio"));
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(2, messaggio.getAnticipoSpedizione());
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoProssimo();
        anno = Anno.getAnnoSuccessivo(anno);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        ec.setDestinatario(SmdLoadSampleData.getAnagraficaBy("AAAA", "BBBBB"));
        ec.setInvio(Invio.Destinatario);
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(
                         abb, 
                         ec,
                         new ArrayList<Spedizione>(),spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));
        
        assertEquals(3, items.size());
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(BigDecimal.ZERO, abb.getSpese());
        assertEquals(abb, ec.getAbbonamento());
        assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec, item.getEstrattoConto());
            assertEquals(10, item.getNumero().intValue());
           
            log.info(item.toString());
        }
        
        assertEquals(3, spedizioni.size());
        for (Spedizione spedizione: spedizioni) {
            assertEquals(abb, spedizione.getAbbonamento());
            log.info(spedizione.toString());
        }
        
    }
    
    @Test
    public void testCreaSpedizioneUnica() {
        List<SpesaSpedizione> spese = SmdLoadSampleData.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(SmdLoadSampleData.getAnagraficaBy("a", "b"));
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        assertEquals(2, messaggio.getAnticipoSpedizione());
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoPassato();
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        ec.setDestinatario(SmdLoadSampleData.getAnagraficaBy("k", "h"));
        ec.setInvio(Invio.Destinatario);
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(abb, 
                                     ec,
                                     new ArrayList<Spedizione>(),
                                     spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));

        assertEquals(3, items.size());
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(abb, ec.getAbbonamento());
        assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec, item.getEstrattoConto());
            assertEquals(10, item.getNumero().intValue());           
            log.info(item.toString());
        }
        assertEquals(1, spedizioni.size());
        Spedizione spedizione = spedizioni.iterator().next();
        log.info(spedizione.toString());
        assertEquals(spedizione.getSpesePostali().doubleValue(), abb.getSpese().doubleValue(),0);
        assertEquals(Mese.getMeseCorrente(), spedizione.getMeseSpedizione());
        assertEquals(Anno.getAnnoCorrente(), spedizione.getAnnoSpedizione());
        assertEquals(ec.getNumeroTotaleRiviste()*messaggio.getGrammi(), spedizione.getPesoStimato().intValue());
        
        SpesaSpedizione ss = Smd.getSpesaSpedizione(spese, AreaSpedizione.Italia, RangeSpeseSpedizione.getByPeso(spedizione.getPesoStimato()));
        assertEquals(ss.getSpese().doubleValue(), spedizione.getSpesePostali().doubleValue(),0);
       
        assertEquals(items.size(), spedizione.getSpedizioneItems().size());
    }

    @Test
    public void testRimuoviECConSpedizioniInviate() {
        Anagrafica tizio = SmdLoadSampleData.getGP();
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Abbonamento abb = SmdLoadSampleData.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        int numeroRiviste =0;
        Anno annoi = Anno.getAnnoCorrente();
        Anno annof = Anno.getAnnoCorrente();
        Mese meseA= Mese.getMeseCorrente();
        if (messaggio.getMesiPubblicazione().contains(meseA)) {
            numeroRiviste++;
        }
        Mese meseB= Mese.getMeseSuccessivo(meseA);
        if (messaggio.getMesiPubblicazione().contains(meseB)) {
            numeroRiviste++;
        }
        if (meseB == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseB)) {
            numeroRiviste++;
        }
        Mese meseC= Mese.getMeseSuccessivo(meseB);
        if (meseC == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseC)) {
            numeroRiviste++;
        }
        Mese meseD= Mese.getMeseSuccessivo(meseC);
        if (meseD == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseD)) {
            numeroRiviste++;
        }

        EstrattoConto ec1 = new EstrattoConto();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(meseA);
        ec1.setAnnoInizio(annoi);
        ec1.setMeseFine(meseD);
        ec1.setAnnoFine(annof);
        ec1.setDestinatario(tizio);
        ec1.setInvio(Invio.Destinatario);
        ec1.setInvioSpedizione(InvioSpedizione.Spedizioniere);

        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(abb,ec1,new ArrayList<>(),SmdLoadSampleData.getSpeseSpedizione());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));
        
        log.info(abb.toString());
        assertEquals(numeroRiviste*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(2, spedizioni.size());
        assertEquals(numeroRiviste, items.size());
        assertEquals(3.0, abb.getSpese().doubleValue(),0);

        for (Spedizione sped:spedizioni) {
            assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            
            if (sped.getMeseSpedizione() == meseA) {
                assertEquals((numeroRiviste-1)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRiviste-1, sped.getSpedizioneItems().size());
                for (SpedizioneItem item : sped.getSpedizioneItems()) {
                    assertTrue(item.isPosticipata());
                }
            } else if (sped.getMeseSpedizione() == meseB ) {
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                SpedizioneItem item = sped.getSpedizioneItems().iterator().next();
                assertTrue(!item.isPosticipata());
            } else { 
                assertTrue(false);
            }
        }
        for (Spedizione sped:spedizioni) {
            if (sped.getMeseSpedizione() == meseA) {
                sped.setStatoSpedizione(StatoSpedizione.INVIATA);
            }
        }
        
        List<SpedizioneItem> deletedItems = Smd.rimuoviEC(abb, ec1, spedizioni, SmdLoadSampleData.getSpeseSpedizione());
        assertEquals(1, deletedItems.size());

        BigDecimal ss = BigDecimal.ZERO;
        for (Spedizione sped:spedizioni) {
            log.info(sped.toString());
            if (sped.getMeseSpedizione() == meseA) {
                ss = sped.getSpesePostali();
                assertEquals(StatoSpedizione.INVIATA, sped.getStatoSpedizione());
                assertEquals((numeroRiviste-1)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRiviste-1, sped.getSpedizioneItems().size());            
            } else if (sped.getMeseSpedizione() == meseB ) {
                assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
            } else { 
                assertTrue(false);
            }
            sped.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
        }
        
        log.info(ec1.toString());
        log.info(abb.toString());
        assertEquals(0, ec1.getNumero().intValue());
        assertEquals(numeroRiviste-1, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(ss.doubleValue(), abb.getSpese().doubleValue(),0);
        assertEquals(ec1.getImporto().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(messaggio.getCostoUnitario().doubleValue()*(numeroRiviste-1), ec1.getImporto().doubleValue(),0);
        

    }
    
    @Test
    public void testRimuoviEC() {
        
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
       
        Anagrafica tizio = SmdLoadSampleData.getGP();
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        
        Abbonamento abb = SmdLoadSampleData.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        
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

        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(
                     abb, 
                     ec1,
                     new ArrayList<Spedizione>(),
                     SmdLoadSampleData.getSpeseSpedizione());        
        
        spedizioni = 
                Smd.generaSpedizioni(
                     abb, 
                     ec2,
                     spedizioni,
                     SmdLoadSampleData.getSpeseSpedizione());
       
       spedizioni = 
               Smd.generaSpedizioni(
                    abb, 
                    ec3,
                    spedizioni,
                    SmdLoadSampleData.getSpeseSpedizione());

        
        spedizioni.stream().forEach(sped -> {
            assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case NOVEMBRE:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi()+blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(3, sped.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case MARZO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case GIUGNO:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            default:
                assertTrue(false);
                break;
            }
        });

        final List<SpedizioneItem> ec1items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getEstrattoConto() == ec1).forEach(item -> ec1items.add(item)));

        final List<SpedizioneItem> ec2items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getEstrattoConto() == ec2).forEach(item -> ec2items.add(item)));
        
        final List<SpedizioneItem> ec3items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getEstrattoConto() == ec3).forEach(item -> ec3items.add(item)));

        log.info(abb.toString());
        assertEquals(BigDecimal.ZERO, abb.getSpese());
        assertEquals(6*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(6*lodare.getCostoUnitario().doubleValue(), ec2.getImporto().doubleValue(),0);
        assertEquals(blocchetti.getAbbonamento().doubleValue(), ec3.getImporto().doubleValue(),0);
        assertEquals(7, spedizioni.size());
        assertEquals(6, ec1items.size());
        assertEquals(6, ec2items.size());
        assertEquals(2, ec3items.size());
        
        //FIRST operation Delete ec2 lodare
        List<SpedizioneItem> deleted = Smd.rimuoviEC(abb,ec2, spedizioni,SmdLoadSampleData.getSpeseSpedizione());
        assertEquals(6, deleted.size());
        
        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec2, item.getEstrattoConto());
        }
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case NOVEMBRE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(messaggio.getGrammi()+blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, sped.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case MARZO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case GIUGNO:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            default:
                assertTrue(false);
                break;
            }
        });
        assertEquals(0, ec2.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec2.getImporto().doubleValue(),0);
        

        deleted = Smd.rimuoviEC(abb,ec1, spedizioni,SmdLoadSampleData.getSpeseSpedizione());
        assertEquals(6, deleted.size());

        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec1, item.getEstrattoConto());
        }
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case NOVEMBRE:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
               break;
            case MARZO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, sped.getSpedizioneItems().size());
                break;
            case GIUGNO:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, sped.getSpedizioneItems().size());
                break;
            default:
                assertTrue(false);
                break;
            }
        });
        assertEquals(0, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec1.getImporto().doubleValue(),0);

        deleted = Smd.rimuoviEC(abb,ec3, spedizioni,SmdLoadSampleData.getSpeseSpedizione());
        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec3, item.getEstrattoConto());
        }
        assertEquals(2, deleted.size());

        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            assertEquals(0, sped.getSpedizioneItems().size());
            assertEquals(0, sped.getPesoStimato().intValue());
        });
        assertEquals(0, ec3.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec3.getImporto().doubleValue(),0);
        log.info(abb.toString());
        
        assertEquals(abb.getTotale().doubleValue(), 0,0);
    }

    @Test
    public void testImportoAbbonamentoStd() {
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Anagrafica ar = SmdLoadSampleData.getAR();
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            Abbonamento abb = new Abbonamento();
            abb.setIntestatario(ar);
            EstrattoConto ec = crea(abb,messaggio, tpec, 10); 
            verificaImportoAbbonamentoAnnuale(abb,ec);
        });
    }
    

    @Test
    public void testCostiAbbonamentoEsteroStd() {
        Pubblicazione p = SmdLoadSampleData.getMessaggio();
        Anno anno = Anno.getAnnoProssimo();
        Mese mese = Mese.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        Abbonamento abb = new Abbonamento();
        Anagrafica intestatario = SmdLoadSampleData.getAnagraficaBy("Tizius", "Sempronius");
        intestatario.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        abb.setIntestatario(intestatario);
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(p);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setDestinatario(intestatario);

        List<SpesaSpedizione> spese = SmdLoadSampleData.getSpeseSpedizione();

        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(abb, 
                                     ec,
                                     new ArrayList<>(), 
                                     spese
                                     );
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getEstrattoConto() == ec).forEach(item -> items.add(item)));

        assertEquals(p.getMesiPubblicazione().size(), items.size());
        assertTrue(ec.isAbbonamentoAnnuale());
        
        assertEquals(p.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(p.getMesiPubblicazione().size(), spedizioni.size());
        
        SpesaSpedizione spesa = 
                Smd.getSpesaSpedizione(
                           spese, 
                           AreaSpedizione.AmericaAfricaAsia, 
                           RangeSpeseSpedizione.getByPeso(p.getGrammi())
                           );
        spedizioni.stream().forEach(sped ->{
            log.info(sped.toString());
            assertEquals(p.getGrammi(), sped.getPesoStimato().intValue());
            assertEquals(spesa.getSpese().doubleValue(), sped.getSpesePostali().doubleValue(),0);
            sped.getSpedizioneItems().stream().forEach( item -> log.info(item.toString()));
        });
        assertEquals(spesa.getSpese().doubleValue()*p.getMesiPubblicazione().size(), abb.getSpese().doubleValue(),0);
        log.info(abb.toString());
    }
    
    @Test
    public void testAggiornaEC() {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        
        Anagrafica tizio = SmdLoadSampleData.getGP();
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        
        Abbonamento abb = SmdLoadSampleData.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        
        EstrattoConto ec1 = new EstrattoConto();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.SETTEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(15);
        List<Spedizione> spedizioni = 
                Smd.generaSpedizioni(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdLoadSampleData.getSpeseSpedizione());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> {
                log.info(item.toString());
                items.add(item); 
                assertEquals(15, item.getNumero().intValue());
            });
        });
        assertEquals(8, items.size());
        assertEquals(8, spedizioni.size());
        log.info(ec1.toString());
        log.info(abb.toString());
        
        ec1.setPubblicazione(lodare);
        ec1.setMeseInizio(Mese.MARZO);
        ec1.setMeseFine(Mese.AGOSTO);
        try {
            Smd.aggiornaEC(abb, ec1, spedizioni,SmdLoadSampleData.getSpeseSpedizione());
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }
        ec1.setPubblicazione(messaggio);
        ec1.setNumero(10);
        
        List<SpedizioneItem> rimItems = Smd.aggiornaEC(abb, ec1, spedizioni,SmdLoadSampleData.getSpeseSpedizione());
        
        rimItems.stream().forEach(item -> log.info("deleted:" + item.toString()));
        assertEquals(8, rimItems.size());
        
        items.clear();
        spedizioni.stream().forEach(sped -> {
            log.info(sped.toString());
            sped.getSpedizioneItems().stream().forEach(item -> {
                items.add(item);
                log.info(item.toString());
                assertEquals(10, item.getNumero().intValue());
            });
        });
        assertEquals(5, items.size());
        assertEquals(8, spedizioni.size());
        
        log.info(ec1.toString());
        log.info(abb.toString());
        
    }

    @Test
    public void testGeneraCampagnaGP() {
        List<Anagrafica> anagrafiche = new ArrayList<>();
        Anagrafica gabrielePizzo = SmdLoadSampleData.getGP();
        anagrafiche.add(gabrielePizzo);
        List<Pubblicazione> pubblicazioni = new ArrayList<>();
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare =SmdLoadSampleData.getLodare();
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        pubblicazioni.add(messaggio);
        pubblicazioni.add(lodare);
        pubblicazioni.add(blocchetti);
        pubblicazioni.add(estratti);
        List<Storico> storici = new ArrayList<>();
        
        storici.add(SmdLoadSampleData.getStoricoBy(gabrielePizzo,gabrielePizzo, messaggio, 10,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdLoadSampleData.getStoricoBy(gabrielePizzo,gabrielePizzo, lodare, 1,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdLoadSampleData.getStoricoBy(gabrielePizzo,gabrielePizzo, blocchetti, 10,Cassa.Contrassegno,TipoEstrattoConto.Scontato,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoProssimo());
        for (Pubblicazione p : pubblicazioni) {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
        }
        List<Abbonamento> abbonamenti = Smd.generaAbbonamentiCampagna(campagna, anagrafiche, storici, pubblicazioni);
        for (Abbonamento abb: abbonamenti) {
            log.info(abb.getIntestatario().toString());
            log.info(abb.toString());
        }
        assertEquals(1, abbonamenti.size());
        Abbonamento abb = abbonamenti.iterator().next();
        List<Spedizione> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
            EstrattoConto ec = Smd.generaECDaStorico(abb, storico);
            spedizioni = Smd.generaSpedizioni(abb, ec, spedizioni, SmdLoadSampleData.getSpeseSpedizione());
            log.info(ec.toString());
            log.info(abb.toString());
        }                
        assertEquals(12, spedizioni.size());
        spedizioni.stream().forEach(sped -> log.info(sped.toString()));
        assertEquals(10*blocchetti.getAbbonamentoConSconto().doubleValue()+10*messaggio.getAbbonamento().doubleValue()+lodare.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(Smd.contrassegno.doubleValue(),abb.getSpese().doubleValue(),0);
        
    }
    

    @Test
    public void testGeneraCampagnaAR() {
        List<Anagrafica> anagrafiche = new ArrayList<>();
        Anagrafica antonioRusso = SmdLoadSampleData.getAR();
        Anagrafica diocesiMilano = SmdLoadSampleData.getDiocesiMi();
        anagrafiche.add(diocesiMilano);
        anagrafiche.add(antonioRusso);
        List<Pubblicazione> pubblicazioni = new ArrayList<>();
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare =SmdLoadSampleData.getLodare();
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        pubblicazioni.add(messaggio);
        pubblicazioni.add(lodare);
        pubblicazioni.add(blocchetti);
        pubblicazioni.add(estratti);
        List<Storico> storici = new ArrayList<>();
        
        storici.add(SmdLoadSampleData.getStoricoBy(diocesiMilano,antonioRusso, messaggio, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdLoadSampleData.getStoricoBy(diocesiMilano,antonioRusso, lodare, 1,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdLoadSampleData.getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdLoadSampleData.getStoricoBy(diocesiMilano,antonioRusso, estratti, 11,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoProssimo());
        for (Pubblicazione p : pubblicazioni) {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
        }
        List<Abbonamento> abbonamenti = Smd.generaAbbonamentiCampagna(campagna, anagrafiche, storici, pubblicazioni);
        for (Abbonamento abb: abbonamenti) {
            log.info(abb.getIntestatario().toString());
            log.info(abb.toString());
        }
        assertEquals(1, abbonamenti.size());
        Abbonamento abb = abbonamenti.iterator().next();
        List<Spedizione> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
            EstrattoConto ec = Smd.generaECDaStorico(abb, storico);
            spedizioni = Smd.generaSpedizioni(abb, ec, spedizioni, SmdLoadSampleData.getSpeseSpedizione());
            log.info(abb.toString());
            log.info(ec.toString());
        }      
        
        assertEquals(12, spedizioni.size());
        spedizioni.stream().forEach(sped -> log.info(sped.toString()));
        assertEquals(0, abb.getImporto().doubleValue(),0);
        assertEquals(0,abb.getSpese().doubleValue(),0);


    }

  
}
