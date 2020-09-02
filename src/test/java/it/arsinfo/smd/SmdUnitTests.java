package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Incassato;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.data.SpedizioneWithItems;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.helper.SmdHelper;
import it.arsinfo.smd.service.Smd;

@RunWith(SpringRunner.class)
public class SmdUnitTests {
    
    private static final Logger log = LoggerFactory.getLogger(SmdUnitTests.class);

    private static RivistaAbbonamento crea(Abbonamento abb,Pubblicazione p, TipoAbbonamentoRivista tipo, int numero) {
        Anno anno = Anno.getAnnoProssimo();
        Mese mese = Mese.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        RivistaAbbonamento ec =  new RivistaAbbonamento();
        ec.setAbbonamento(abb);
        ec.setNumero(numero);
        ec.setPubblicazione(p);
        ec.setTipoAbbonamentoRivista(tipo);
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
    
    private void verificaImportoAbbonamentoAnnuale(Abbonamento abb, RivistaAbbonamento ec) {
        assertEquals(0.0,abb.getSpese().doubleValue(),0);
        assertEquals(true, Smd.isAbbonamentoAnnuale(ec));
        assertEquals(abb, ec.getAbbonamento());
        switch (ec.getTipoAbbonamentoRivista()) {
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
        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoProssimo();
        anno = Anno.getAnnoSuccessivo(anno);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        ec.setDestinatario(SmdHelper.getAnagraficaBy("AAAA", "BBBBB"));
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                         abb, 
                         ec,
                         new ArrayList<SpedizioneWithItems>(),spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));
        
        assertEquals(3, items.size());
        assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(BigDecimal.ZERO, abb.getSpese());
        assertEquals(abb, ec.getAbbonamento());
        assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec, item.getRivistaAbbonamento());
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
        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoPassato();
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        ec.setDestinatario(SmdHelper.getAnagraficaBy("k", "h"));
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(abb, 
                                     ec,
                                     new ArrayList<SpedizioneWithItems>(),
                                     spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));

        assertEquals(3, items.size());
        assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        assertEquals(abb, ec.getAbbonamento());
        assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            assertEquals(anno, item.getAnnoPubblicazione());
            assertEquals(ec, item.getRivistaAbbonamento());
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
    public void testRimuoviRivistaConSpedizioniInviate() throws Exception{
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoCorrente(), false);
        int numeroRiviste =0;
        int numeroRivisteSpedizioneMeseA=0;
        int numeroRivisteSpedizionePosticipata=0;
        int numeroSpedizioni=1;
        Anno annoi = Anno.getAnnoCorrente();
        Anno annof = Anno.getAnnoCorrente();
        Mese meseA= Mese.getMeseCorrente();

        if (messaggio.getMesiPubblicazione().contains(meseA)) {
            numeroRiviste++;
            numeroRivisteSpedizionePosticipata++;
            log.info("Aggiunto {} {} {}: numriv {}, numSped {}, numRivSpedPost {} ",messaggio.getNome(),meseA.getNomeBreve(),annof.getAnnoAsString(),numeroRiviste,numeroSpedizioni,numeroRivisteSpedizionePosticipata);
        } else {
            log.info("Non Esiste {} {} {}: ",messaggio.getNome(),meseA.getNomeBreve(),annof.getAnnoAsString());        	
        }

        Mese meseB= Mese.getMeseSuccessivo(meseA);
        if (meseB == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseB)) {
            numeroRiviste++;
            numeroRivisteSpedizionePosticipata++;
            if (numeroSpedizioni == 0) {
            	numeroSpedizioni++;
            }
            log.info("Aggiunto {} {} {}: numriv {}, numSped {}, numRivSpedPost {} ",messaggio.getNome(),meseB.getNomeBreve(),annof.getAnnoAsString(),numeroRiviste,numeroSpedizioni,numeroRivisteSpedizionePosticipata);
        } else {
            log.info("Non Esiste {} {} {}: ",messaggio.getNome(),meseB.getNomeBreve(),annof.getAnnoAsString());        	
        }
        
        Mese meseC= Mese.getMeseSuccessivo(meseB);
        if (meseC == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseC)) {
            numeroRiviste++;
            numeroSpedizioni++;
            numeroRivisteSpedizioneMeseA++;
            log.info("Aggiunto {} {} {}: numriv {}, numSped {}, numRivSpedPost {} ",messaggio.getNome(),meseC.getNomeBreve(),annof.getAnnoAsString(),numeroRiviste,numeroSpedizioni,numeroRivisteSpedizionePosticipata);
        } else {
            log.info("Non Esiste {} {} {}: ",messaggio.getNome(),meseC.getNomeBreve(),annof.getAnnoAsString());        	
        }

        Mese meseD= Mese.getMeseSuccessivo(meseC);
        if (meseD == Mese.GENNAIO) {
            annof = Anno.getAnnoProssimo();
        }
        if (messaggio.getMesiPubblicazione().contains(meseD)) {
            numeroRiviste++;
            numeroSpedizioni++;
            log.info("Aggiunto {} {} {}: numriv {}, numSped {}, numRivSpedPost {} ",messaggio.getNome(),meseD.getNomeBreve(),annof.getAnnoAsString(),numeroRiviste,numeroSpedizioni,numeroRivisteSpedizionePosticipata);
        } else {
            log.info("Non Esiste {} {} {}: ",messaggio.getNome(),meseD.getNomeBreve(),annof.getAnnoAsString());        	
        }

        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(meseA);
        ec1.setAnnoInizio(annoi);
        ec1.setMeseFine(meseD);
        ec1.setAnnoFine(annof);
        ec1.setDestinatario(tizio);
        ec1.setInvioSpedizione(InvioSpedizione.Spedizioniere);

        List<SpedizioneWithItems> spedizioniwithitems = 
                Smd.genera(abb,ec1,new ArrayList<>(),SmdHelper.getSpeseSpedizione());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioniwithitems.stream().forEach(sped -> sped.getSpedizioneItems().stream().forEach(item -> items.add(item)));
        
        log.info("generato: {}", abb);
        log.info("numeroriviste: " + numeroRiviste + " Costo Unitario:" +  messaggio.getCostoUnitario());
        assertEquals(numeroRiviste, ec1.getNumeroTotaleRiviste().intValue());
        assertEquals(numeroRiviste*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(numeroSpedizioni, spedizioniwithitems.size());
        assertEquals(numeroRiviste, items.size());
        assertEquals(3.0, abb.getSpese().doubleValue(),0);

        for (SpedizioneWithItems spedw:spedizioniwithitems) {
            Spedizione sped = spedw.getSpedizione();
            if (sped.getMeseSpedizione() == meseA  
            		&& sped.getInvioSpedizione() == InvioSpedizione.AdpSede) {
                assertEquals((numeroRivisteSpedizionePosticipata)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRivisteSpedizionePosticipata, spedw.getSpedizioneItems().size());
                for (SpedizioneItem item : spedw.getSpedizioneItems()) {
                    assertTrue(item.isPosticipata());
                    assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());            
                }
            } else if (sped.getMeseSpedizione() == meseA 
            		&& sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spedw.getSpedizioneItems().size());
                SpedizioneItem item = spedw.getSpedizioneItems().iterator().next();
                assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());            
                assertTrue(!item.isPosticipata());
            } else if (sped.getMeseSpedizione() == meseB 	
            		&& sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, spedw.getSpedizioneItems().size());
                SpedizioneItem item = spedw.getSpedizioneItems().iterator().next();
                assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());            
                assertTrue(!item.isPosticipata());
            } else { 
                assertTrue(false);
            }
        }
        for (SpedizioneWithItems ssp:spedizioniwithitems) {
            Spedizione sped= ssp.getSpedizione();
            if (sped.getMeseSpedizione() == meseA) {
            	ssp.getSpedizioneItems().forEach(item -> 
                item.setStatoSpedizione(StatoSpedizione.INVIATA));
            }
        }
        
        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abb, ec1, spedizioniwithitems, SmdHelper.getSpeseSpedizione());
        assertEquals(numeroRiviste-numeroRivisteSpedizionePosticipata-numeroRivisteSpedizioneMeseA, aggiorna.getItemsToDelete().size());

        BigDecimal ss = BigDecimal.ZERO;
        for (SpedizioneWithItems ssp:aggiorna.getSpedizioniToSave()) {
            Spedizione sped= ssp.getSpedizione();
            if (sped.getMeseSpedizione() == meseA && sped.getInvioSpedizione() == InvioSpedizione.AdpSede) {
                ss = sped.getSpesePostali();
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	assertEquals(StatoSpedizione.INVIATA, item.getStatoSpedizione());
                }
                assertEquals((numeroRivisteSpedizionePosticipata)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(numeroRivisteSpedizionePosticipata, ssp.getSpedizioneItems().size());            
            } else if (sped.getMeseSpedizione() == meseA && sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	assertEquals(StatoSpedizione.INVIATA, item.getStatoSpedizione());
                }
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                assertEquals(1, ssp.getSpedizioneItems().size());            	
        	} else if (sped.getMeseSpedizione() == meseB ) {            
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());
                }
                assertEquals(0, sped.getPesoStimato().intValue());
                assertEquals(0, ssp.getSpedizioneItems().size());
            } else { 
                assertTrue(false);
            }
            ssp.getSpedizioneItems().stream().forEach(item -> log.info(item.toString()));
        }
        
        assertEquals(0,aggiorna.getRivisteToDelete().size());
        assertEquals(1, aggiorna.getRivisteToSave().size());
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        assertEquals(1, rivista.getNumero().intValue());
        Abbonamento abbonamento = aggiorna.getAbbonamentoToSave();
        assertNotNull(abbonamento);
        assertEquals(numeroRivisteSpedizioneMeseA+numeroRivisteSpedizionePosticipata, rivista.getNumeroTotaleRiviste().intValue());
        assertEquals(ss.doubleValue(), abbonamento.getSpese().doubleValue(),0);
        assertEquals(rivista.getImporto().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(messaggio.getCostoUnitario().doubleValue()*(numeroRivisteSpedizioneMeseA+numeroRivisteSpedizionePosticipata), rivista.getImporto().doubleValue(),0);
        

    }
    
    @Test
    public void testRimuoviRivista() throws Exception {
        
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
       
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.GIUGNO);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        RivistaAbbonamento ec3 = new RivistaAbbonamento();
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
            spwi.getSpedizioneItems()
            .forEach(item -> 
            assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione()));
            assertEquals(1, spwi.getSpedizioneItems().size());
            assertEquals(InvioSpedizione.Spedizioniere, sped.getInvioSpedizione());
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                break;
            case NOVEMBRE:
                break;
            case DICEMBRE:
                break;
            case GENNAIO:
                break;
            case FEBBRAIO:
                break;
            case MARZO:
                break;
            case APRILE:
                break;
            case GIUGNO:
                break;
            default:
                assertTrue(false);
                break;
            }
        });

        final List<SpedizioneItem> ec1items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec1).forEach(item -> ec1items.add(item)));

        final List<SpedizioneItem> ec2items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec2).forEach(item -> ec2items.add(item)));
        
        final List<SpedizioneItem> ec3items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec3).forEach(item -> ec3items.add(item)));

        log.info(abb.toString());
        assertEquals(BigDecimal.ZERO, abb.getSpese());
        assertEquals(6*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(6*lodare.getCostoUnitario().doubleValue(), ec2.getImporto().doubleValue(),0);
        assertEquals(blocchetti.getAbbonamento().doubleValue(), ec3.getImporto().doubleValue(),0);
        assertEquals(14, spedizioni.size());
        assertEquals(6, ec1items.size());
        assertEquals(6, ec2items.size());
        assertEquals(2, ec3items.size());
        
        //FIRST operation Delete ec2 lodare
        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abb,ec2, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, aggiorna.getItemsToDelete().size());
        
        for (SpedizioneItem item: aggiorna.getItemsToDelete()){
            assertEquals(ec2, item.getRivistaAbbonamento());
        }
        int spedizionelodarecount=0;
        int spedizionemessaggiocount=0;
        int spedizioneblocchetticount=0;
        for (SpedizioneWithItems spwi: aggiorna.getSpedizioniToSave()) {
            Spedizione sped = spwi.getSpedizione();
            if (spwi.getSpedizioneItems().size() == 0) {
            	spedizionelodarecount++;
            	continue;
            }
            assertEquals(1, spwi.getSpedizioneItems().size());
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                spedizioneblocchetticount++;
                break;
            case NOVEMBRE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                spedizionemessaggiocount++;
                break;
            case DICEMBRE:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                spedizionemessaggiocount++;
                break;
            case GENNAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                spedizionemessaggiocount++;
                break;
            case FEBBRAIO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                spedizionemessaggiocount++;
                break;
            case MARZO:
                assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                spedizionemessaggiocount++;
                break;
            case APRILE:
                SpedizioneItem item = spwi.getSpedizioneItems().iterator().next();
                if (item.getPubblicazione().equals(blocchetti)) {
                    assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                    spedizioneblocchetticount++;
                } else if (item.getPubblicazione().equals(messaggio)) {
                    assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                    spedizionemessaggiocount++;
                } else {
                	assertTrue(false);
                }
                break;
            default:
                assertTrue(false);
                break;
            }
        }
        assertEquals(2, spedizioneblocchetticount);
        assertEquals(6, spedizionemessaggiocount);
        assertEquals(6, spedizionelodarecount);
        assertEquals(0, ec2.getNumeroTotaleRiviste().intValue());
        assertEquals(0, ec2.getImporto().doubleValue(),0);
        

        aggiorna = Smd.rimuovi(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        assertEquals(6, aggiorna.getItemsToDelete().size());

        for (SpedizioneItem item: aggiorna.getItemsToDelete()){
            assertEquals(ec1, item.getRivistaAbbonamento());
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
            	if (spwi.getSpedizioneItems().size() == 0) {
            		break;
            	}
                assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
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

        aggiorna = Smd.rimuovi(abb,ec3, spedizioni,SmdHelper.getSpeseSpedizione());
        for (SpedizioneItem item: aggiorna.getItemsToDelete()) {
            assertEquals(ec3, item.getRivistaAbbonamento());
        }
        assertEquals(2, aggiorna.getItemsToDelete().size());

        spedizioni.stream().forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
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
        EnumSet.allOf(TipoAbbonamentoRivista.class).stream().forEach(tpec -> {
            Abbonamento abb = new Abbonamento();
            abb.setIntestatario(ar);
            RivistaAbbonamento ec = crea(abb,messaggio, tpec, 10); 
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
        RivistaAbbonamento ec = new RivistaAbbonamento();
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
        spedizioni
        .stream()
        .forEach(sped -> sped.getSpedizioneItems()
        		.stream()
        		.filter(item -> item.getRivistaAbbonamento() == ec)
        		.forEach(item -> items.add(item))
        		);

        BigDecimal speseSped = BigDecimal.ZERO;
        for (SpedizioneWithItems sped: spedizioni) {
        	speseSped = speseSped.add(sped.getSpedizione().getSpesePostali());
    	}
        assertEquals(p.getMesiPubblicazione().size(), items.size());
        assertTrue(Smd.isAbbonamentoAnnuale(ec));
        
        assertEquals(p.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(speseSped.doubleValue(), abb.getSpeseEstero().doubleValue(),0);
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
        assertEquals(0, abb.getSpese().doubleValue(),0);
        log.info(abb.toString());
    }
    
    @Test
    public void testAggiornaException() throws Exception {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare = SmdHelper.getLodare();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(),false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
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

        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.SETTEMBRE);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        ec2.setNumero(10);
        assertTrue(!ec1.equals(ec2));
        
        ec2.setPubblicazione(messaggio);
        ec2.setMeseInizio(Mese.MARZO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);
        assertTrue(!ec1.equals(ec2));

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),null,3,ec1.getTipoAbbonamentoRivista());
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,0,ec1.getTipoAbbonamentoRivista());
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,3,null);
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        assertEquals(8, items.size());
        assertEquals(8, spedizioni.size());        
    }

    @Test
    public void testAggiornaNumero() throws Exception {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(),false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
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
            sped.getSpedizioneItems().stream().forEach(item -> {
                items.add(item); 
                assertEquals(15, item.getNumero().intValue());
            });
        });
        assertEquals(8, items.size());
        assertEquals(8, spedizioni.size());
        assertEquals(0, abb.getSpese().doubleValue(),0);
        assertEquals(15*8*messaggio.getCostoUnitario().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(abb.getImporto().doubleValue(), ec1.getImporto().doubleValue(),0);
        RivistaAbbonamento ec2 = ec1.clone();
        ec2.setTipoAbbonamentoRivista(ec1.getTipoAbbonamentoRivista());
        ec2.setNumero(10);
        assertTrue(ec1.equals(ec2));

        RivistaAbbonamentoAggiorna aggiorna = Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,10,ec1.getTipoAbbonamentoRivista());
        
        assertEquals(0, aggiorna.getItemsToDelete().size());
        assertNotNull(aggiorna.getAbbonamentoToSave());
        assertEquals(8, aggiorna.getSpedizioniToSave().size());
        assertEquals(1, aggiorna.getRivisteToSave().size());
        
        assertEquals(0, aggiorna.getAbbonamentoToSave().getSpese().doubleValue(),0);
        assertEquals(10*8*messaggio.getCostoUnitario().doubleValue(), aggiorna.getAbbonamentoToSave().getImporto().doubleValue(),0);
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        assertNotNull(rivista);
        assertEquals(aggiorna.getAbbonamentoToSave().getImporto().doubleValue(), rivista.getImporto().doubleValue(),0);
        assertEquals(8, aggiorna.getSpedizioniToSave().size());        
        
        items.clear();
        aggiorna.getSpedizioniToSave().stream().forEach(sped -> {
            sped.getSpedizioneItems().stream().forEach(item -> {
                items.add(item);
                assertTrue(rivista == item.getRivistaAbbonamento());
                assertEquals(rivista, item.getRivistaAbbonamento());
                assertEquals(10, item.getNumero().intValue());
            });
        });
        assertEquals(8, items.size());
    }
    
    @Test
    public void testAggiornaTipoAbbonamentoRivista() throws Exception {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        
        Anagrafica tizio = SmdHelper.getGP();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(),false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(1);
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.stream().forEach(sped -> {
            sped.getSpedizioneItems().stream().forEach(item -> {
                items.add(item); 
                assertEquals(1, item.getNumero().intValue());
            });
        });
        assertEquals(11, items.size());
        assertEquals(11, spedizioni.size());
        assertEquals(messaggio.getAbbonamento().doubleValue(), ec1.getImporto().doubleValue(),0);
        assertEquals(messaggio.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(messaggio);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.DICEMBRE);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        ec2.setNumero(1);
        ec2.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.OmaggioCuriaDiocesiana);
        assertTrue(ec1.equals(ec2));

        RivistaAbbonamentoAggiorna aggiorna = Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,1,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana);
        assertEquals(0, aggiorna.getSpedizioniToSave().size());        
        assertEquals(0, aggiorna.getItemsToDelete().size());
        assertEquals(1, aggiorna.getRivisteToSave().size());
        assertNotNull(aggiorna.getAbbonamentoToSave());
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        assertEquals(0, rivista.getImporto().doubleValue(),0);
        assertEquals(0, aggiorna.getAbbonamentoToSave().getImporto().doubleValue(),0);
        
        items.clear();
        spedizioni.stream().forEach(sped -> {
            sped.getSpedizioneItems().stream().forEach(item -> {
                items.add(item);
                assertEquals(1, item.getNumero().intValue());
            });
        });
        assertEquals(11, items.size());
        assertEquals(11, spedizioni.size());        
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
        
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, messaggio, 10,true,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, lodare, 1,true,TipoAbbonamentoRivista.Ordinario,InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(gabrielePizzo,gabrielePizzo, blocchetti, 10,true,TipoAbbonamentoRivista.Scontato,InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoSuccessivo(Anno.getAnnoProssimo()));
        CampagnaItem ciestratti = new CampagnaItem();
        ciestratti.setCampagna(campagna);
        ciestratti.setPubblicazione(estratti);
        campagna.addCampagnaItem(ciestratti);
        CampagnaItem ciblocchetti = new CampagnaItem();
        ciblocchetti.setCampagna(campagna);
        ciblocchetti.setPubblicazione(blocchetti);
        campagna.addCampagnaItem(ciblocchetti);
        CampagnaItem cilodare = new CampagnaItem();
        cilodare.setCampagna(campagna);
        cilodare.setPubblicazione(lodare);
        campagna.addCampagnaItem(cilodare);
        CampagnaItem cimessaggio = new CampagnaItem();
        cimessaggio.setCampagna(campagna);
        cimessaggio.setPubblicazione(messaggio);
        campagna.addCampagnaItem(cimessaggio);
        
        List<Abbonamento> abbonamenti = Smd.genera(campagna, anagrafiche, storici);
        assertEquals(1, abbonamenti.size());
        for (Abbonamento abb: abbonamenti) {
            log.info(abb.getIntestatario().toString());
            log.info(abb.toString());
        }
        Abbonamento abb = abbonamenti.iterator().next();
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
            RivistaAbbonamento ec = Smd.genera(abb, storico);
            spedizioni = Smd.genera(abb, ec, spedizioni, SmdHelper.getSpeseSpedizione());
        }                
        assertEquals(25, spedizioni.size());
        spedizioni.stream().forEach(sped -> {
        	assertEquals(1, sped.getSpedizioneItems().size());
        	assertEquals(0, sped.getSpedizioniPosticipate().size());
        });
        assertEquals(10*blocchetti.getAbbonamentoConSconto().doubleValue()+10*messaggio.getAbbonamento().doubleValue()+lodare.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        assertEquals(Smd.contrassegno.doubleValue(),abb.getSpese().doubleValue(),0);
        
    }
    @Test 
    public void testRicondizionaBlocchetti() {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Anagrafica tizio = SmdHelper.getAR();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(), false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(blocchetti);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(15);
        Smd.genera(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());
        
        assertEquals(blocchetti.getAbbonamento().doubleValue()*15, abb.getImporto().doubleValue(),0);
        assertEquals(abb.getTotale().doubleValue(), abb.getImporto().doubleValue(),0);
        
        abb.setIncassato(blocchetti.getAbbonamento().multiply(new BigDecimal(13)));
        assertEquals(Incassato.SiConDebito, Smd.getStatoIncasso(abb));

        abb.setIncassato(blocchetti.getAbbonamento().multiply(new BigDecimal(11)));
        assertEquals(Incassato.Parzialmente, Smd.getStatoIncasso(abb));

		double costoUno = ec1.getImporto().doubleValue()/(ec1.getNumero());
		assertEquals(blocchetti.getAbbonamento().doubleValue(), costoUno,0);
		assertEquals(4*costoUno, abb.getResiduo().doubleValue(),0);

    	
    }

    @Test 
    public void testRicondizionaMessaggio() {
        Anno anno = Anno.getAnnoSuccessivo(Anno.getAnnoProssimo());
        Anagrafica tizio = SmdHelper.getAR();
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Abbonamento abb = SmdHelper.getAbbonamentoBy(tizio, Anno.getAnnoProssimo(),false);
        
        RivistaAbbonamento ec1 = new RivistaAbbonamento();
        ec1.setAbbonamento(abb);
        ec1.setPubblicazione(messaggio);
        ec1.setMeseInizio(Mese.GENNAIO);
        ec1.setAnnoInizio(anno);
        ec1.setMeseFine(Mese.DICEMBRE);
        ec1.setAnnoFine(anno);
        ec1.setDestinatario(tizio);
        ec1.setNumero(14);
        Smd.genera(
                     abb, 
                     ec1,
                     new ArrayList<>(),
                     SmdHelper.getSpeseSpedizione());
        
        assertEquals(messaggio.getAbbonamento().doubleValue()*14, abb.getImporto().doubleValue(),0);
        
        abb.setIncassato(messaggio.getAbbonamento().multiply(new BigDecimal(12)));
        assertEquals(Incassato.SiConDebito, Smd.getStatoIncasso(abb));

        abb.setIncassato(messaggio.getAbbonamento().multiply(new BigDecimal(10)));
        assertEquals(Incassato.Parzialmente, Smd.getStatoIncasso(abb));

		double costoUno = ec1.getImporto().doubleValue()/(ec1.getNumero());
		
		assertEquals(messaggio.getAbbonamento().doubleValue(), costoUno,0);
		assertEquals(4*costoUno, abb.getResiduo().doubleValue(),0);
    	
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
        
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, messaggio, 1,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, lodare, 1,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, blocchetti, 1,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        storici.add(SmdHelper.getStoricoBy(diocesiMilano,antonioRusso, estratti, 1,false,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana, InvioSpedizione.Spedizioniere));
        
        Campagna campagna = new Campagna();
        campagna.setAnno(Anno.getAnnoSuccessivo(Anno.getAnnoProssimo()));
        for (Pubblicazione p : pubblicazioni) {
            CampagnaItem ci = new CampagnaItem();
            ci.setCampagna(campagna);
            ci.setPubblicazione(p);
            campagna.addCampagnaItem(ci);
        }
        List<Abbonamento> abbonamenti = Smd.genera(campagna, diocesiMilano, storici);
        assertEquals(1, abbonamenti.size());
        Abbonamento abb = abbonamenti.iterator().next();
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
        	log.info("testGeneraCampagnaAR: genera Rivista abbonamento from Storico {}", storico);
            RivistaAbbonamento ec = Smd.genera(abb, storico);
            assertEquals(1, ec.getNumero().intValue());
            assertEquals(0, ec.getImporto().doubleValue(),0);
            assertEquals(0, abb.getImporto().doubleValue(),0);
            assertEquals(0,abb.getSpese().doubleValue(),0);
        	log.info("testGeneraCampagnaAR: Rivista abbonamento {}", ec);
            spedizioni = Smd.genera(abb, ec, spedizioni, SmdHelper.getSpeseSpedizione());
        	log.info("testGeneraCampagnaAR: spedizioni {}", spedizioni.size());
        }      
        assertEquals(26, spedizioni.size());
        assertEquals(0, abb.getImporto().doubleValue(),0);
        assertEquals(0,abb.getSpese().doubleValue(),0);
        
        spedizioni.stream().forEach(sped -> {
        	log.info(sped.getSpedizione().toString());
        	assertEquals(1, sped.getSpedizioneItems().size());
        	assertEquals(0, sped.getSpedizioniPosticipate().size());
        });


    }

    @Test
    public void testStatoIncassato() {
        Abbonamento abb = new Abbonamento();
        assertEquals(Incassato.Zero, Smd.getStatoIncasso(abb));
        
        abb.setImporto(new BigDecimal(10));
        assertEquals(Incassato.No, Smd.getStatoIncasso(abb));
        
        abb.setIncassato(new BigDecimal(10));
        assertEquals(Incassato.Si, Smd.getStatoIncasso(abb));

        abb.setIncassato(new BigDecimal(7));
        assertEquals(Incassato.SiConDebito, Smd.getStatoIncasso(abb));

        abb.setSpese(new BigDecimal(3));
        abb.setIncassato(new BigDecimal(10));
        assertEquals(Incassato.SiConDebito, Smd.getStatoIncasso(abb));

        abb.setIncassato(new BigDecimal(6));
        assertEquals(Incassato.Parzialmente, Smd.getStatoIncasso(abb));
        
        abb.setImporto(new BigDecimal(70));
        abb.setIncassato(new BigDecimal(60));
        abb.setSpese(BigDecimal.ZERO);
        assertEquals(Incassato.SiConDebito, Smd.getStatoIncasso(abb));
        
        abb.setIncassato(new BigDecimal(54));
        assertEquals(Incassato.Parzialmente, Smd.getStatoIncasso(abb));

        
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
    public void testIncassaEsatto() throws Exception {
    	Abbonamento abbonamento = new Abbonamento();
    	abbonamento.setImporto(new BigDecimal("200.00"));
    	DistintaVersamento incasso = new DistintaVersamento();
    	incasso.setImporto(new BigDecimal("200.00"));
    	
    	Versamento versamento1 = new Versamento(incasso);
    	versamento1.setImporto(new BigDecimal("200.00"));
    	
    	BigDecimal incassato = Smd.incassa(incasso, versamento1, abbonamento);
    	assertEquals(200.00,incassato.doubleValue(),0);
    	assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(200.00,versamento1.getIncassato().doubleValue(),0);
    	assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento1, abbonamento, incassato);
    	assertEquals(0.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
    	assertEquals(0.00,incasso.getIncassato().doubleValue(),0);

    	
    }

    @Test
    public void testIncassaMultipli() throws Exception {
    	Abbonamento abbonamento = new Abbonamento();
    	abbonamento.setImporto(new BigDecimal("200.00"));
    	DistintaVersamento incasso = new DistintaVersamento();
    	incasso.setImporto(new BigDecimal("215.00"));
    	
    	Versamento versamento1 = new Versamento(incasso);
    	versamento1.setImporto(new BigDecimal("180.00"));
    	Versamento versamento2 = new Versamento(incasso);
    	versamento2.setImporto(new BigDecimal("35.00"));
    	
    	BigDecimal incasso1 = Smd.incassa(incasso, versamento1, abbonamento);
    	assertEquals(180.00,incasso1.doubleValue(),0);    	
    	assertEquals(180.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(180.00,versamento1.getIncassato().doubleValue(),0);
    	assertEquals(180.00,incasso.getIncassato().doubleValue(),0);
           	
    	BigDecimal incasso2 = Smd.incassa(incasso, versamento2, abbonamento);
    	assertEquals(20.00,incasso2.doubleValue(),0);    	
    	assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
      	assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	assertEquals(200.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso3 = Smd.incassa(incasso, versamento1, abbonamento);
    	BigDecimal incasso4 = Smd.incassa(incasso, versamento2, abbonamento);
    	assertEquals(0.00,incasso3.doubleValue(),0);    	
       	assertEquals(0.00,incasso4.doubleValue(),0);    	
       	assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(180.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento1, abbonamento,versamento1.getImporto());
       	assertEquals(20.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	assertEquals(20.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso5 = Smd.incassa(incasso, versamento2, abbonamento);
       	assertEquals(15.00,incasso5.doubleValue(),0);    	
       	assertEquals(35.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(35.00, versamento2.getIncassato().doubleValue(),0);
    	assertEquals(35.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso6 = Smd.incassa(incasso, versamento1, abbonamento);
       	assertEquals(165.00,incasso6.doubleValue(),0);    	
       	assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	assertEquals(165.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(35.00, versamento2.getIncassato().doubleValue(),0);
    	assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    }
    
    @Test
    public void testDissocia() throws Exception {
    	Abbonamento abbonamento1 = new Abbonamento();
    	abbonamento1.setImporto(new BigDecimal("180.00"));
    	Abbonamento abbonamento2 = new Abbonamento();
    	abbonamento2.setImporto(new BigDecimal("225.00"));

    	DistintaVersamento incasso = new DistintaVersamento();
    	
    	Versamento versamento1 = new Versamento(incasso);
    	versamento1.setImporto(new BigDecimal("100.00"));
    	Versamento versamento2 = new Versamento(incasso);
    	versamento2.setImporto(new BigDecimal("135.00"));
    	Versamento versamento3 = new Versamento(incasso);
    	versamento3.setImporto(new BigDecimal("170.00"));
       	incasso.setImporto(new BigDecimal("405.00"));
           	
    	BigDecimal incassato1 = Smd.incassa(incasso, versamento1, abbonamento1);
    	assertEquals(100.00,incassato1.doubleValue(),0);
    	assertEquals(100.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(0.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(100.00,incasso.getIncassato().doubleValue(),0);
           	
    	BigDecimal incassato2=  Smd.incassa(incasso, versamento2, abbonamento2);
    	assertEquals(135.00,incassato2.doubleValue(),0);
    	assertEquals(100.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(135.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(235.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incassato3 =  Smd.incassa(incasso, versamento3, abbonamento1);
    	assertEquals(80.00,incassato3.doubleValue(),0);
    	assertEquals(180.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(135.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(80.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(315.00,incasso.getIncassato().doubleValue(),0);

    	BigDecimal incassato4 =  Smd.incassa(incasso, versamento3, abbonamento2);
    	assertEquals(90.00,incassato4.doubleValue(),0);
    	assertEquals(180.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(170.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(405.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento3, abbonamento1, new BigDecimal("170.00"));
    	assertEquals(10.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(235.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento2, abbonamento1,new BigDecimal("10.00"));
    	assertEquals(0.00,abbonamento1.getIncassato().doubleValue(),0);
    	assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	assertEquals(125.00,versamento2.getIncassato().doubleValue(),0);
    	assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	assertEquals(225.00,incasso.getIncassato().doubleValue(),0);

    	
    }
    
   @Test
   public void testGetAnnoCorrente() throws Exception {
	   assertEquals(Anno.ANNO2020, Anno.getAnnoCorrente());
   }
   
   @Test
   public void testNumberFormat() throws Exception {
	   NumberFormat nf = NumberFormat.getInstance(Locale.ITALIAN);
	   nf.setMinimumFractionDigits(2);
	   nf.setMaximumFractionDigits(2);
	   BigDecimal alfa = new BigDecimal("10.5");
	   
	   assertEquals("10,50", nf.format(alfa));
   }

}
