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
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
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
import it.arsinfo.smd.entity.SpedizioneWithItems;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;

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
        Smd.genera(abb, ec, new ArrayList<>(), SmdHelper.getSpeseSpedizione());

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
    public void testGenerateCodeLine() {
        Set<String> campi = new HashSet<>();
        for (int i=0; i< 200000;i++) {
        Anagrafica a = SmdHelper.getAnagraficaBy(""+i, ""+i);
        String codeLine = Abbonamento.generaCodeLine(Anno.ANNO2019,a);
        assertEquals("19", codeLine.substring(0, 2));
        assertTrue(Abbonamento.checkCodeLine(codeLine));
        assertTrue(campi.add(codeLine));
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
        List<SpesaSpedizione> spese = SmdHelper.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(SmdHelper.getAnagraficaBy("tizio", "caio"));
        Pubblicazione messaggio = SmdHelper.getMessaggio();
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
        ec.setDestinatario(SmdHelper.getAnagraficaBy("AAAA", "BBBBB"));
        ec.setInvio(Invio.Destinatario);
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                         abb, 
                         ec,
                         new ArrayList<SpedizioneWithItems>(),spese);
        
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
        for (SpedizioneWithItems spedizione: spedizioni) {
            assertEquals(abb, spedizione.getSpedizione().getAbbonamento());
            log.info(spedizione.toString());
        }
        
    }
    
    @Test
    public void testCreaSpedizioneUnica() {
        List<SpesaSpedizione> spese = SmdHelper.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(SmdHelper.getAnagraficaBy("a", "b"));
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        assertEquals(2, messaggio.getAnticipoSpedizione());
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoPassato();
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        ec.setDestinatario(SmdHelper.getAnagraficaBy("k", "h"));
        ec.setInvio(Invio.Destinatario);
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        assertEquals(TipoEstrattoConto.Ordinario, ec.getTipoEstrattoConto());
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(abb, 
                                     ec,
                                     new ArrayList<SpedizioneWithItems>(),
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
        SpedizioneWithItems spedwi = spedizioni.iterator().next();
        log.info(spedwi.getSpedizione().toString());
        assertEquals(spedwi.getSpedizione().getSpesePostali().doubleValue(), abb.getSpese().doubleValue(),0);
        assertEquals(Mese.getMeseCorrente(), spedwi.getSpedizione().getMeseSpedizione());
        assertEquals(Anno.getAnnoCorrente(), spedwi.getSpedizione().getAnnoSpedizione());
        assertEquals(ec.getNumeroTotaleRiviste()*messaggio.getGrammi(), spedwi.getSpedizione().getPesoStimato().intValue());
        
        SpesaSpedizione ss = Smd.getSpesaSpedizione(spese, AreaSpedizione.Italia, RangeSpeseSpedizione.getByPeso(spedwi.getSpedizione().getPesoStimato()));
        assertEquals(ss.getSpese().doubleValue(), spedwi.getSpedizione().getSpesePostali().doubleValue(),0);
       
        assertEquals(items.size(), spedwi.getSpedizioneItems().size());
    }

    @Test 
    public void testGetAnnoMeseMapAlfa() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.OTTOBRE;
        Anno annof=Anno.ANNO2020;
        Mese mesef=Mese.GENNAIO;
        
        Map<Anno,EnumSet<Mese>> map = Smd.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(Anno.ANNO2019));
        assertTrue(map.containsKey(Anno.ANNO2020));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        assertEquals(3, riviste2019.size());
        assertTrue(riviste2019.contains(Mese.OTTOBRE));
        assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        assertTrue(riviste2019.contains(Mese.DICEMBRE));
        EnumSet<Mese> riviste2020 = map.get(Anno.ANNO2020);
        assertEquals(1, riviste2020.size());
        assertTrue(riviste2020.contains(Mese.GENNAIO));
        
    }

    @Test 
    public void testGetAnnoMeseMapBeta() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.NOVEMBRE;
        Anno annof=Anno.ANNO2020;
        Mese mesef=Mese.SETTEMBRE;
        
        Map<Anno,EnumSet<Mese>> map = Smd.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(Anno.ANNO2019));
        assertTrue(map.containsKey(Anno.ANNO2020));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        assertEquals(2, riviste2019.size());
        assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        assertTrue(riviste2019.contains(Mese.DICEMBRE));
        EnumSet<Mese> riviste2020 = map.get(Anno.ANNO2020);
        assertEquals(8, riviste2020.size());
        assertTrue(riviste2020.contains(Mese.GENNAIO));
        assertTrue(riviste2020.contains(Mese.FEBBRAIO));
        assertTrue(riviste2020.contains(Mese.MARZO));
        assertTrue(riviste2020.contains(Mese.APRILE));
        assertTrue(riviste2020.contains(Mese.MAGGIO));
        assertTrue(riviste2020.contains(Mese.GIUGNO));
        assertTrue(riviste2020.contains(Mese.LUGLIO));
        assertTrue(riviste2020.contains(Mese.SETTEMBRE));
        
    }

    @Test 
    public void testGetAnnoMeseMapGamma() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.GENNAIO;
        Anno annof=Anno.ANNO2019;
        Mese mesef=Mese.DICEMBRE;
        
        Map<Anno,EnumSet<Mese>> map = Smd.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(Anno.ANNO2019));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        assertEquals(11, riviste2019.size());
        assertTrue(riviste2019.contains(Mese.GENNAIO));
        assertTrue(riviste2019.contains(Mese.FEBBRAIO));
        assertTrue(riviste2019.contains(Mese.MARZO));
        assertTrue(riviste2019.contains(Mese.APRILE));
        assertTrue(riviste2019.contains(Mese.MAGGIO));
        assertTrue(riviste2019.contains(Mese.GIUGNO));
        assertTrue(riviste2019.contains(Mese.LUGLIO));
        assertTrue(riviste2019.contains(Mese.SETTEMBRE));
        assertTrue(riviste2019.contains(Mese.OTTOBRE));
        assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        assertTrue(riviste2019.contains(Mese.DICEMBRE));   
    }

    @Test
    public void testRimuoviECConSpedizioniInviate() {
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoCorrente(), Cassa.Ccp);
        int numeroRiviste =0;
        Anno annoi = Anno.getAnnoCorrente();
        Anno annof = Anno.getAnnoCorrente();
        Mese meseA= Mese.getMeseCorrente();
        if (messaggio.getMesiPubblicazione().contains(meseA)) {
            numeroRiviste++;
            log.info(meseA.getNomeBreve() + annof.getAnno()+" numeroriviste: " + numeroRiviste );
        }

        Mese meseB= Mese.getMeseSuccessivo(meseA);
        if (meseB == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseB)) {
            numeroRiviste++;
            log.info(meseB.getNomeBreve() + annof.getAnno()+" numeroriviste: " + numeroRiviste );
        }
        
        Mese meseC= Mese.getMeseSuccessivo(meseB);
        if (meseC == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseC)) {
            numeroRiviste++;
            log.info(meseC.getNomeBreve() + annof.getAnno()+" numeroriviste: " + numeroRiviste );
        }

        Mese meseD= Mese.getMeseSuccessivo(meseC);
        if (meseD == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseD)) {
            numeroRiviste++;
            log.info(meseD.getNomeBreve() + annof.getAnno()+" numeroriviste: " + numeroRiviste );
        }

        EstrattoConto ec1 = new EstrattoConto();
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(meseA);
        ec1.setAnnoInizio(annoi);
        ec1.setMeseFine(meseD);
        ec1.setAnnoFine(annof);
        ec1.setDestinatario(tizio);
        ec1.setInvio(Invio.Destinatario);
        ec1.setInvioSpedizione(InvioSpedizione.Spedizioniere);

        List<SpedizioneWithItems> spedwi = 
                Smd.genera(abb,ec1,new ArrayList<>(),SmdHelper.getSpeseSpedizione());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedwi.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));
        
        log.info(abb.toString());
        log.info("numeroriviste: " + numeroRiviste + " Costo Unitario:" +  messaggio.getCostoUnitario());
        assertEquals(numeroRiviste, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(numeroRiviste*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(2, spedwi.size());
        assertEquals(numeroRiviste, items.size());
        assertEquals(3.0, abb.getSpese().doubleValue(),0);

        for (SpedizioneWithItems spedw:spedwi) {
            Spedizione sped = spedw.getSpedizione();
            assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
            log.info(sped.toString());
            spedw.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            
            if (sped.getMeseSpedizione() == meseA) {
                assertEquals((numeroRiviste-1)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRiviste-1, spedw.getSpedizioneItems().size());
                for (SpedizioneItem item : spedw.getSpedizioneItems()) {
                    assertTrue(item.isPosticipata());
                }
            } else if (sped.getMeseSpedizione() == meseB ) {
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spedw.getSpedizioneItems().size());
                SpedizioneItem item = spedw.getSpedizioneItems().iterator().next();
                assertTrue(!item.isPosticipata());
            } else { 
                assertTrue(false);
            }
        }
        for (SpedizioneWithItems ssp:spedwi) {
            Spedizione sped= ssp.getSpedizione();
            if (sped.getMeseSpedizione() == meseA) {
                sped.setStatoSpedizione(StatoSpedizione.INVIATA);
            }
        }
        
        List<SpedizioneItem> deletedItems = Smd.rimuoviEC(abb, ec1, spedwi, SmdHelper.getSpeseSpedizione());
        assertEquals(1, deletedItems.size());

        BigDecimal ss = BigDecimal.ZERO;
        for (SpedizioneWithItems ssp:spedwi) {
            Spedizione sped= ssp.getSpedizione();
            log.info(sped.toString());
            if (sped.getMeseSpedizione() == meseA) {
                ss = sped.getSpesePostali();
                assertEquals(StatoSpedizione.INVIATA, sped.getStatoSpedizione());
                assertEquals((numeroRiviste-1)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRiviste-1, ssp.getSpedizioneItems().size());            
            } else if (sped.getMeseSpedizione() == meseB ) {
                assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, ssp.getSpedizioneItems().size());
            } else { 
                assertTrue(false);
            }
            ssp.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
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
       
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        
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

        
        spedizioni.stream().forEach(spwi -> {
            Spedizione sped= spwi.getSpedizione();
            assertEquals(StatoSpedizione.PROGRAMMATA, sped.getStatoSpedizione());
            log.info(sped.toString());
            spwi.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case NOVEMBRE:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            case MARZO:
                assertEquals(messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(blocchetti.getGrammi()+messaggio.getGrammi()+lodare.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(3, spwi.getSpedizioneItems().size());
                break;
            case GIUGNO:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
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
        List<SpedizioneItem> deleted = Smd.rimuoviEC(abb,ec2, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, deleted.size());
        
        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec2, item.getEstrattoConto());
        }
        spedizioni.stream().forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
            log.info(sped.toString());
            spwi.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case NOVEMBRE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case MARZO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(messaggio.getGrammi()+blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(2, spwi.getSpedizioneItems().size());
                break;
            default:
                assertTrue(false);
                break;
            }
        });
        assertEquals(0, ec2.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec2.getImporto().doubleValue(),0);
        

        deleted = Smd.rimuoviEC(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, deleted.size());

        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec1, item.getEstrattoConto());
        }
        spedizioni.stream().forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
            log.info(sped.toString());
            spwi.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case NOVEMBRE:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
                break;
            case DICEMBRE:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
                break;
            case GENNAIO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
                break;
            case FEBBRAIO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
               break;
            case MARZO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
                break;
            case APRILE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case GIUGNO:
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, spwi.getSpedizioneItems().size());
                break;
            default:
                assertTrue(false);
                break;
            }
        });
        assertEquals(0, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec1.getImporto().doubleValue(),0);

        deleted = Smd.rimuoviEC(abb,ec3, spedizioni,SmdHelper.getSpeseSpedizione());
        for (SpedizioneItem item: deleted){
            log.info("delete: " + item.toString());
            assertEquals(ec3, item.getEstrattoConto());
        }
        assertEquals(2, deleted.size());

        spedizioni.stream().forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
            log.info(spwi.toString());
            assertEquals(0, spwi.getSpedizioneItems().size());
            assertEquals(0, sped.getPesoStimato().intValue());
        });
        assertEquals(0, ec3.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec3.getImporto().doubleValue(),0);
        log.info(abb.toString());
        
        assertEquals(abb.getTotale().doubleValue(), 0,0);
    }

    @Test
    public void testImportoAbbonamentoStd() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anagrafica ar = SmdHelper.getAR();
        EnumSet.allOf(TipoEstrattoConto.class).stream().forEach(tpec -> {
            Abbonamento abb = new Abbonamento();
            abb.setIntestatario(ar);
            EstrattoConto ec = crea(abb,messaggio, tpec, 10); 
            verificaImportoAbbonamentoAnnuale(abb,ec);
        });
    }
    

    @Test
    public void testCostiAbbonamentoEsteroStd() {
        Pubblicazione p = SmdHelper.getMessaggio();
        Anno anno = Anno.getAnnoProssimo();
        Mese mese = Mese.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        Abbonamento abb = new Abbonamento();
        Anagrafica intestatario = SmdHelper.getAnagraficaBy("Tizius", "Sempronius");
        intestatario.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
        abb.setIntestatario(intestatario);
        EstrattoConto ec = new EstrattoConto();
        ec.setPubblicazione(p);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        ec.setMeseInizio(Mese.GENNAIO);
        ec.setMeseFine(Mese.DICEMBRE);
        ec.setDestinatario(intestatario);

        List<SpesaSpedizione> spese = SmdHelper.getSpeseSpedizione();

        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(abb, 
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
            assertEquals(p.getGrammi(), sped.getSpedizione().getPesoStimato().intValue());
            assertEquals(spesa.getSpese().doubleValue(), sped.getSpedizione().getSpesePostali().doubleValue(),0);
            sped.getSpedizioneItems().stream().forEach( item -> log.info(item.toString()));
        });
        assertEquals(spesa.getSpese().doubleValue()*p.getMesiPubblicazione().size(), abb.getSpese().doubleValue(),0);
        log.info(abb.toString());
    }
    
    @Test
    public void testAggiornaEC() {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), Cassa.Ccp);
        
        EstrattoConto ec1 = new EstrattoConto();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.SETTEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(15);
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());
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
            Smd.aggiornaEC(abb, ec1, spedizioni,SmdHelper.getSpeseSpedizione());
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }
        ec1.setPubblicazione(messaggio);
        ec1.setNumero(10);
        
        List<SpedizioneItem> rimItems = Smd.aggiornaEC(abb, ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        
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
        Anagrafica gabrielePizzo = SmdHelper.getGP();
        anagrafiche.add(gabrielePizzo);
        List<Pubblicazione> pubblicazioni = new ArrayList<>();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare =SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        Pubblicazione estratti = SmdHelper.getEstratti();
        pubblicazioni.add(messaggio);
        pubblicazioni.add(lodare);
        pubblicazioni.add(blocchetti);
        pubblicazioni.add(estratti);
        List<Storico> storici = new ArrayList<>();
        
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, messaggio, 10,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, lodare, 1,Cassa.Contrassegno,TipoEstrattoConto.Ordinario,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, blocchetti, 10,Cassa.Contrassegno,TipoEstrattoConto.Scontato,Invio.Destinatario,InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoSuccessivo(Anno.getAnnoProssimo()));
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
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
            EstrattoConto ec = Smd.generaECDaStorico(abb, storico);
            spedizioni = Smd.genera(abb, ec, spedizioni, SmdHelper.getSpeseSpedizione());
            log.info(ec.toString());
            log.info(abb.toString());
        }                
        assertEquals(13, spedizioni.size());
        spedizioni.stream().forEach(sped -> log.info(sped.toString()));
        assertEquals(10*blocchetti.getAbbonamentoConSconto().doubleValue()+10*messaggio.getAbbonamento().doubleValue()+lodare.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(Smd.contrassegno.doubleValue(),abb.getSpese().doubleValue(),0);
        
    }
    

    @Test
    public void testGeneraCampagnaAR() {
        List<Anagrafica> anagrafiche = new ArrayList<>();
        Anagrafica antonioRusso = SmdHelper.getAR();
        Anagrafica diocesiMilano = SmdHelper.getDiocesiMi();
        anagrafiche.add(diocesiMilano);
        anagrafiche.add(antonioRusso);
        List<Pubblicazione> pubblicazioni = new ArrayList<>();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare =SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        Pubblicazione estratti = SmdHelper.getEstratti();
        pubblicazioni.add(messaggio);
        pubblicazioni.add(lodare);
        pubblicazioni.add(blocchetti);
        pubblicazioni.add(estratti);
        List<Storico> storici = new ArrayList<>();
        
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, messaggio, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, lodare, 1,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 10,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, estratti, 11,Cassa.Ccp,TipoEstrattoConto.OmaggioCuriaDiocesiana, Invio.Intestatario,InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoSuccessivo(Anno.getAnnoProssimo()));
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
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
            EstrattoConto ec = Smd.generaECDaStorico(abb, storico);
            spedizioni = Smd.genera(abb, ec, spedizioni, SmdHelper.getSpeseSpedizione());
            log.info(abb.toString());
            log.info(ec.toString());
        }      
        
        spedizioni.stream().forEach(sped -> log.info(sped.getSpedizione().toString()));
        assertEquals(13, spedizioni.size());
        assertEquals(0, abb.getImporto().doubleValue(),0);
        assertEquals(0,abb.getSpese().doubleValue(),0);


    }

    @Test
    public void testStatoIncassato() {
        Abbonamento abb = new Abbonamento();
        assertEquals(Incassato.Omaggio, abb.getStatoIncasso());
       
        abb.setImporto(new BigDecimal(10));
        assertEquals(Incassato.No, abb.getStatoIncasso());
        
        Versamento versamento = new Versamento();
        abb.setVersamento(versamento);
        assertEquals(Incassato.No, abb.getStatoIncasso());

        abb.setIncassato(new BigDecimal(10));
        assertEquals(Incassato.Si, abb.getStatoIncasso());

        abb.setIncassato(new BigDecimal(7));
        assertEquals(Incassato.SiConDebito, abb.getStatoIncasso());

        abb.setIncassato(new BigDecimal(8));
        assertEquals(Incassato.SiConDebito, abb.getStatoIncasso());

        abb.setIncassato(new BigDecimal(6));
        assertEquals(Incassato.Parzialmente, abb.getStatoIncasso());        
        
    }
    
    @Test
    public void testLogger() {
        
        String value ="smd";
        log.trace("doStuff needed more information - {}", value);
        log.debug("doStuff needed to debug - {}", value);
        log.info("doStuff took input - {}", value);
        log.warn("doStuff needed to warn - {}", value);
        log.error("doStuff encountered an error with value - {}", value);
    }

    @Test
    public void testGetPaeseBy() {
        Paese paese = Paese.getBySigla("ITA");
        assertEquals(Paese.IT, paese);
        paese = Paese.getBySigla("CAN");
        assertEquals(Paese.CA, paese);
        paese = Paese.getBySigla("RSM");
        assertEquals(Paese.SM, paese);
        paese = Paese.getBySigla("SRM");
        assertEquals(Paese.SM, paese);
        
        paese = Paese.valueOf("IT");
        assertEquals(Paese.IT, paese);
        paese = Paese.getByNome("Italia");
        assertEquals(Paese.IT, paese);
        
    }
    
    @Test 
    public void testGeneraCodeLineBase() {
        SmdImportAdp.getAnagraficaByAncodcon(Anagrafica.generaCodeLineBase());
    }
    
}
