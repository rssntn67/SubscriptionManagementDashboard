package it.arsinfo.smd;

import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.helper.SmdHelper;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.dto.RivistaAbbonamentoAggiorna;
import it.arsinfo.smd.service.dto.SpedizioneWithItems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class SmdUnitTests {
    
    private static final Logger log = LoggerFactory.getLogger(SmdUnitTests.class);

    private static RivistaAbbonamento crea(Abbonamento abb,Pubblicazione p, TipoAbbonamentoRivista tipo) {
        Anno anno = Anno.getAnnoProssimo();
        Mese mese = Mese.getMeseCorrente();
        if (mese.getPosizione()+p.getAnticipoSpedizione() > 12) {
            anno=Anno.getAnnoSuccessivo(anno);
        }
        RivistaAbbonamento ec =  new RivistaAbbonamento();
        ec.setAbbonamento(abb);
        ec.setNumero(10);
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
        Assertions.assertEquals(EnumSet.allOf(Mese.class), pe.getMesiPubblicazione());

        
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
        Assertions.assertEquals(11, pd.getMesiPubblicazione().size());
        Assertions.assertFalse(pd.getMesiPubblicazione().contains(Mese.AGOSTO));

        Pubblicazione pa = new Pubblicazione("pa", TipoPubblicazione.SEMESTRALE);
        pa.setFeb(true);
        pa.setAgo(true);
        Assertions.assertEquals(2, pa.getMesiPubblicazione().size());
        Assertions.assertTrue(pa.getMesiPubblicazione().contains(Mese.AGOSTO));
        Assertions.assertTrue(pa.getMesiPubblicazione().contains(Mese.FEBBRAIO));
        
        Pubblicazione pb = new Pubblicazione("pb", TipoPubblicazione.SEMESTRALE);
        pb.setMar(true);
        pb.setSet(true);
        Assertions.assertEquals(2, pb.getMesiPubblicazione().size());
        Assertions.assertTrue(pb.getMesiPubblicazione().contains(Mese.MARZO));
        Assertions.assertTrue(pb.getMesiPubblicazione().contains(Mese.SETTEMBRE));

        Pubblicazione pc = new Pubblicazione("pc", TipoPubblicazione.ANNUALE);
        pc.setApr(true);
        Assertions.assertEquals(1, pc.getMesiPubblicazione().size());
        Assertions.assertTrue(pc.getMesiPubblicazione().contains(Mese.APRILE));
        
    }

    @Test
    public void testGenerateCodeLine() {
        Set<String> campi = new HashSet<>();
        for (int i=0; i< 200000;i++) {
        Anagrafica a = SmdHelper.getAnagraficaBy(""+i, ""+i);
        String codeLine = Abbonamento.generaCodeLine(Anno.ANNO2019,a);
        Assertions.assertEquals("19", codeLine.substring(0, 2));
        Assertions.assertTrue(Abbonamento.checkCodeLine(codeLine));
        Assertions.assertTrue(campi.add(codeLine));
        }
        Assertions.assertEquals(200000, campi.size());
    }

    @Test
    public void testAnnoMese() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int anno = now.get(Calendar.YEAR);
        Assertions.assertEquals(anno-1, Anno.getAnnoPassato().getAnno());
        Assertions.assertEquals(anno, Anno.getAnnoCorrente().getAnno());
        Assertions.assertEquals(anno+1, Anno.getAnnoProssimo().getAnno());
        int mese = now.get(Calendar.MONTH);
        // Mese comincia da 0 
        Assertions.assertEquals(mese+1, Mese.getMeseCorrente().getPosizione());
        
    }
    
    private void verificaImportoAbbonamentoAnnuale(Abbonamento abb, RivistaAbbonamento ec) {
        Assertions.assertEquals(0.0,abb.getSpese().doubleValue(),0);
        Assertions.assertTrue(Smd.isAbbonamentoAnnuale(ec));
        Assertions.assertEquals(abb, ec.getAbbonamento());
        switch (ec.getTipoAbbonamentoRivista()) {
            case OmaggioCuriaDiocesiana:
                Assertions.assertEquals(0.0,abb.getImporto().doubleValue(),0);
            break;
            case OmaggioGesuiti:
            case OmaggioCuriaGeneralizia:
            case OmaggioDirettoreAdp:
            case OmaggioEditore:
                Assertions.assertEquals(0.0,ec.getImporto().doubleValue(),0);
            break;
            case Ordinario:
                Assertions.assertEquals(ec.getPubblicazione().getAbbonamento().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
                break;
            case Sostenitore:
                Assertions.assertEquals(ec.getPubblicazione().getAbbonamentoSostenitore().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
                break;
            case Scontato:
                Assertions.assertEquals(ec.getPubblicazione().getAbbonamentoConSconto().multiply(new BigDecimal(ec.getNumero())).doubleValue()
                         ,ec.getImporto().doubleValue(),0);
                break;
            case Web:
                Assertions.assertEquals(ec.getPubblicazione().getAbbonamentoWeb().multiply(new BigDecimal(ec.getNumero())).doubleValue()
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
        Assertions.assertEquals(2, messaggio.getAnticipoSpedizione());
        RivistaAbbonamento ec = new RivistaAbbonamento();
        ec.setPubblicazione(messaggio);
        ec.setNumero(10);
        Anno anno = Anno.getAnnoProssimo();
        anno = Anno.getAnnoSuccessivo(anno);
        ec.setMeseFine(Mese.GENNAIO);
        ec.setMeseFine(Mese.MARZO);
        ec.setAnnoInizio(anno);
        ec.setAnnoFine(anno);
        Assertions.assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        ec.setDestinatario(SmdHelper.getAnagraficaBy("AAAA", "BBBBB"));
        ec.setInvioSpedizione(InvioSpedizione.Spedizioniere);
        List<SpedizioneWithItems> spedizioni =
                Smd.genera(
                         abb, 
                         ec,
                        new ArrayList<>(),spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.forEach(sped -> items.addAll(sped.getSpedizioneItems()));
        
        Assertions.assertEquals(3, items.size());
        Assertions.assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        Assertions.assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        Assertions.assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        Assertions.assertEquals(BigDecimal.ZERO, abb.getSpese());
        Assertions.assertEquals(abb, ec.getAbbonamento());
        Assertions.assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            Assertions.assertEquals(anno, item.getAnnoPubblicazione());
            Assertions.assertEquals(ec, item.getRivistaAbbonamento());
            Assertions.assertEquals(10, item.getNumero().intValue());
           
            log.info(item.toString());
        }
        
        Assertions.assertEquals(3, spedizioni.size());
        for (SpedizioneWithItems spedizione: spedizioni) {
            Assertions.assertEquals(abb, spedizione.getSpedizione().getAbbonamento());
            log.info(spedizione.toString());
        }
        
    }
    
    @Test
    public void testCreaSpedizioneUnica() {
        List<SpesaSpedizione> spese = SmdHelper.getSpeseSpedizione();
        Abbonamento abb = new Abbonamento();
        abb.setIntestatario(SmdHelper.getAnagraficaBy("a", "b"));
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Assertions.assertEquals(2, messaggio.getAnticipoSpedizione());
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
        Assertions.assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        List<SpedizioneWithItems> spedizioni = 
                Smd.genera(abb, 
                                     ec,
                                     new ArrayList<>(),
                                     spese);
        
        final List<SpedizioneItem> items = new ArrayList<>();
        spedizioni.forEach(sped -> items.addAll(sped.getSpedizioneItems()));

        Assertions.assertEquals(3, items.size());
        Assertions.assertEquals(TipoAbbonamentoRivista.Ordinario, ec.getTipoAbbonamentoRivista());
        Assertions.assertEquals(messaggio.getCostoUnitario().multiply(new BigDecimal(10)).doubleValue()*items.size(), ec.getImporto().doubleValue(),0);
        Assertions.assertEquals(abb.getImporto().doubleValue(), ec.getImporto().doubleValue(),0);
        Assertions.assertEquals(abb, ec.getAbbonamento());
        Assertions.assertEquals(items.size()*10, ec.getNumeroTotaleRiviste().intValue());
        for (SpedizioneItem item: items) {
            Assertions.assertEquals(anno, item.getAnnoPubblicazione());
            Assertions.assertEquals(ec, item.getRivistaAbbonamento());
            Assertions.assertEquals(10, item.getNumero().intValue());
            log.info(item.toString());
        }
        Assertions.assertEquals(1, spedizioni.size());
        SpedizioneWithItems spedwi = spedizioni.iterator().next();
        log.info(spedwi.getSpedizione().toString());
        Assertions.assertEquals(spedwi.getSpedizione().getSpesePostali().doubleValue(), abb.getSpese().doubleValue(),0);
        Assertions.assertEquals(Mese.getMeseCorrente(), spedwi.getSpedizione().getMeseSpedizione());
        Assertions.assertEquals(Anno.getAnnoCorrente(), spedwi.getSpedizione().getAnnoSpedizione());
        Assertions.assertEquals(ec.getNumeroTotaleRiviste()*messaggio.getGrammi(), spedwi.getSpedizione().getPesoStimato().intValue());
        
        SpesaSpedizione ss = Smd.getSpesaSpedizione(spese, AreaSpedizione.Italia, RangeSpeseSpedizione.getByPeso(spedwi.getSpedizione().getPesoStimato()));
        Assertions.assertEquals(ss.getSpese().doubleValue(), spedwi.getSpedizione().getSpesePostali().doubleValue(),0);
       
        Assertions.assertEquals(items.size(), spedwi.getSpedizioneItems().size());
    }

    @Test 
    public void testGetAnnoMeseMapAlfa() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.OTTOBRE;
        Anno annof=Anno.ANNO2020;
        Mese mesef=Mese.GENNAIO;
        
        Map<Anno,EnumSet<Mese>> map = RivistaAbbonamento.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        Assertions.assertEquals(2, map.size());
        Assertions.assertTrue(map.containsKey(Anno.ANNO2019));
        Assertions.assertTrue(map.containsKey(Anno.ANNO2020));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        Assertions.assertEquals(3, riviste2019.size());
        Assertions.assertTrue(riviste2019.contains(Mese.OTTOBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.DICEMBRE));
        EnumSet<Mese> riviste2020 = map.get(Anno.ANNO2020);
        Assertions.assertEquals(1, riviste2020.size());
        Assertions.assertTrue(riviste2020.contains(Mese.GENNAIO));
        
    }

    @Test 
    public void testGetAnnoMeseMapBeta() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.NOVEMBRE;
        Anno annof=Anno.ANNO2020;
        Mese mesef=Mese.SETTEMBRE;
        
        Map<Anno,EnumSet<Mese>> map = RivistaAbbonamento.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        Assertions.assertEquals(2, map.size());
        Assertions.assertTrue(map.containsKey(Anno.ANNO2019));
        Assertions.assertTrue(map.containsKey(Anno.ANNO2020));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        Assertions.assertEquals(2, riviste2019.size());
        Assertions.assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.DICEMBRE));
        EnumSet<Mese> riviste2020 = map.get(Anno.ANNO2020);
        Assertions.assertEquals(8, riviste2020.size());
        Assertions.assertTrue(riviste2020.contains(Mese.GENNAIO));
        Assertions.assertTrue(riviste2020.contains(Mese.FEBBRAIO));
        Assertions.assertTrue(riviste2020.contains(Mese.MARZO));
        Assertions.assertTrue(riviste2020.contains(Mese.APRILE));
        Assertions.assertTrue(riviste2020.contains(Mese.MAGGIO));
        Assertions.assertTrue(riviste2020.contains(Mese.GIUGNO));
        Assertions.assertTrue(riviste2020.contains(Mese.LUGLIO));
        Assertions.assertTrue(riviste2020.contains(Mese.SETTEMBRE));
        
    }

    @Test 
    public void testGetAnnoMeseMapGamma() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anno annoi=Anno.ANNO2019;
        Mese mesei=Mese.GENNAIO;
        Anno annof=Anno.ANNO2019;
        Mese mesef=Mese.DICEMBRE;
        
        Map<Anno,EnumSet<Mese>> map = RivistaAbbonamento.getAnnoMeseMap(mesei,annoi,mesef,annof,messaggio);
        Assertions.assertEquals(1, map.size());
        Assertions.assertTrue(map.containsKey(Anno.ANNO2019));
        EnumSet<Mese> riviste2019 = map.get(Anno.ANNO2019);
        Assertions.assertEquals(11, riviste2019.size());
        Assertions.assertTrue(riviste2019.contains(Mese.GENNAIO));
        Assertions.assertTrue(riviste2019.contains(Mese.FEBBRAIO));
        Assertions.assertTrue(riviste2019.contains(Mese.MARZO));
        Assertions.assertTrue(riviste2019.contains(Mese.APRILE));
        Assertions.assertTrue(riviste2019.contains(Mese.MAGGIO));
        Assertions.assertTrue(riviste2019.contains(Mese.GIUGNO));
        Assertions.assertTrue(riviste2019.contains(Mese.LUGLIO));
        Assertions.assertTrue(riviste2019.contains(Mese.SETTEMBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.OTTOBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.NOVEMBRE));
        Assertions.assertTrue(riviste2019.contains(Mese.DICEMBRE));
    }

    @Test
    public void testRimuoviRivistaConSpedizioniInviate() {
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
        spedizioniwithitems.forEach(sped -> items.addAll(sped.getSpedizioneItems()));
        
        log.info("generato: {}", abb);
        log.info("numeroriviste: " + numeroRiviste + " Costo Unitario:" +  messaggio.getCostoUnitario());
        Assertions.assertEquals(numeroRiviste, ec1.getNumeroTotaleRiviste().intValue());
        Assertions.assertEquals(numeroRiviste*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        Assertions.assertEquals(numeroSpedizioni, spedizioniwithitems.size());
        Assertions.assertEquals(numeroRiviste, items.size());
        //Assertions.assertEquals(2.0, abb.getSpese().doubleValue(),0);

        for (SpedizioneWithItems spedw:spedizioniwithitems) {
            Spedizione sped = spedw.getSpedizione();
            if (sped.getMeseSpedizione() == meseA  
            		&& sped.getInvioSpedizione() == InvioSpedizione.AdpSede) {
                Assertions.assertEquals((numeroRivisteSpedizionePosticipata)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(numeroRivisteSpedizionePosticipata, spedw.getSpedizioneItems().size());
                for (SpedizioneItem item : spedw.getSpedizioneItems()) {
                    Assertions.assertTrue(item.isPosticipata());
                    Assertions.assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());
                }
            } else if (sped.getMeseSpedizione() == meseA 
            		&& sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                Assertions.assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(1, spedw.getSpedizioneItems().size());
                SpedizioneItem item = spedw.getSpedizioneItems().iterator().next();
                Assertions.assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());
                Assertions.assertFalse(item.isPosticipata());
            } else if (sped.getMeseSpedizione() == meseB 	
            		&& sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                Assertions.assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(1, spedw.getSpedizioneItems().size());
                SpedizioneItem item = spedw.getSpedizioneItems().iterator().next();
                Assertions.assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());
                Assertions.assertFalse(item.isPosticipata());
            } else {
                Assertions.fail();
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
        Assertions.assertEquals(numeroRiviste-numeroRivisteSpedizionePosticipata-numeroRivisteSpedizioneMeseA, aggiorna.getItemsToDelete().size());

        BigDecimal ss = BigDecimal.ZERO;
        for (SpedizioneWithItems ssp:aggiorna.getSpedizioniToSave()) {
            Spedizione sped= ssp.getSpedizione();
            if (sped.getMeseSpedizione() == meseA && sped.getInvioSpedizione() == InvioSpedizione.AdpSede) {
                ss = sped.getSpesePostali();
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	Assertions.assertEquals(StatoSpedizione.INVIATA, item.getStatoSpedizione());
                }
                Assertions.assertEquals((numeroRivisteSpedizionePosticipata)*messaggio.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(numeroRivisteSpedizionePosticipata, ssp.getSpedizioneItems().size());
            } else if (sped.getMeseSpedizione() == meseA && sped.getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	Assertions.assertEquals(StatoSpedizione.INVIATA, item.getStatoSpedizione());
                }
                Assertions.assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(1, ssp.getSpedizioneItems().size());
        	} else if (sped.getMeseSpedizione() == meseB ) {            
                for (SpedizioneItem item: ssp.getSpedizioneItems()) {
                	Assertions.assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione());
                }
                Assertions.assertEquals(0, sped.getPesoStimato().intValue());
                Assertions.assertEquals(0, ssp.getSpedizioneItems().size());
            } else {
                Assertions.fail();
            }
            ssp.getSpedizioneItems().forEach(item -> log.info(item.toString()));
        }
        
        Assertions.assertEquals(0,aggiorna.getRivisteToDelete().size());
        Assertions.assertEquals(1, aggiorna.getRivisteToSave().size());
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        Assertions.assertEquals(1, rivista.getNumero().intValue());
        Abbonamento abbonamento = aggiorna.getAbbonamentoToSave();
        Assertions.assertNotNull(abbonamento);
        Assertions.assertEquals(numeroRivisteSpedizioneMeseA+numeroRivisteSpedizionePosticipata, rivista.getNumeroTotaleRiviste().intValue());
        Assertions.assertEquals(ss.doubleValue(), abbonamento.getSpese().doubleValue(),0);
        Assertions.assertEquals(rivista.getImporto().doubleValue(), abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(messaggio.getCostoUnitario().doubleValue()*(numeroRivisteSpedizioneMeseA+numeroRivisteSpedizionePosticipata), rivista.getImporto().doubleValue(),0);
        

    }
    
    @Test
    public void testRimuoviRivista() {
        
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
                     new ArrayList<>(),
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

        
        spedizioni.forEach(spwi -> {
            Spedizione sped= spwi.getSpedizione();
            spwi.getSpedizioneItems()
            .forEach(item -> 
            Assertions.assertEquals(StatoSpedizione.PROGRAMMATA, item.getStatoSpedizione()));
            Assertions.assertEquals(1, spwi.getSpedizioneItems().size());
            Assertions.assertEquals(InvioSpedizione.Spedizioniere, sped.getInvioSpedizione());
            switch (sped.getMeseSpedizione()) {
                case OTTOBRE:
                case NOVEMBRE:
                case DICEMBRE:
                case GENNAIO:
                case FEBBRAIO:
                case MARZO:
                case APRILE:
                case GIUGNO:
                    break;
                default:
                    Assertions.fail();
                    break;
            }
        });

        final List<SpedizioneItem> ec1items = new ArrayList<>();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec1).forEach(ec1items::add));

        final List<SpedizioneItem> ec2items = new ArrayList<>();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec2).forEach(ec2items::add));
        
        final List<SpedizioneItem> ec3items = new ArrayList<>();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().stream().filter(item -> item.getRivistaAbbonamento() == ec3).forEach(ec3items::add));

        log.info(abb.toString());
        Assertions.assertEquals(BigDecimal.ZERO, abb.getSpese());
        Assertions.assertEquals(6*messaggio.getCostoUnitario().doubleValue(), ec1.getImporto().doubleValue(),0);
        Assertions.assertEquals(6*lodare.getCostoUnitario().doubleValue(), ec2.getImporto().doubleValue(),0);
        Assertions.assertEquals(blocchetti.getAbbonamento().doubleValue(), ec3.getImporto().doubleValue(),0);
        Assertions.assertEquals(14, spedizioni.size());
        Assertions.assertEquals(6, ec1items.size());
        Assertions.assertEquals(6, ec2items.size());
        Assertions.assertEquals(2, ec3items.size());
        
        //FIRST operation Delete ec2 lodare
        RivistaAbbonamentoAggiorna aggiorna = Smd.rimuovi(abb,ec2, spedizioni,SmdHelper.getSpeseSpedizione());
        Assertions.assertEquals(6, aggiorna.getItemsToDelete().size());
        
        for (SpedizioneItem item: aggiorna.getItemsToDelete()){
            Assertions.assertEquals(ec2, item.getRivistaAbbonamento());
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
            Assertions.assertEquals(1, spwi.getSpedizioneItems().size());
            switch (sped.getMeseSpedizione()) {
                case OTTOBRE:
                    Assertions.assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                    spedizioneblocchetticount++;
                    break;
                case NOVEMBRE:
                case DICEMBRE:
                case GENNAIO:
                case FEBBRAIO:
                case MARZO:
                    Assertions.assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                    spedizionemessaggiocount++;
                    break;
                case APRILE:
                SpedizioneItem item = spwi.getSpedizioneItems().iterator().next();
                if (item.getPubblicazione().equals(blocchetti)) {
                    Assertions.assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                    spedizioneblocchetticount++;
                } else if (item.getPubblicazione().equals(messaggio)) {
                    Assertions.assertEquals(messaggio.getGrammi(), sped.getPesoStimato().intValue());
                    spedizionemessaggiocount++;
                } else {
                    Assertions.fail();
                }
                break;
            default:
                Assertions.fail();
                break;
            }
        }
        Assertions.assertEquals(2, spedizioneblocchetticount);
        Assertions.assertEquals(6, spedizionemessaggiocount);
        Assertions.assertEquals(6, spedizionelodarecount);
        Assertions.assertEquals(0, ec2.getNumeroTotaleRiviste().intValue());
        Assertions.assertEquals(0, ec2.getImporto().doubleValue(),0);
        

        aggiorna = Smd.rimuovi(abb,ec1, spedizioni,SmdHelper.getSpeseSpedizione());
        Assertions.assertEquals(6, aggiorna.getItemsToDelete().size());

        for (SpedizioneItem item: aggiorna.getItemsToDelete()){
            Assertions.assertEquals(ec1, item.getRivistaAbbonamento());
        }
        spedizioni.forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
            log.info(sped.toString());
            spwi.getSpedizioneItems().forEach(item -> log.info(item.toString()));
            switch (sped.getMeseSpedizione()) {
            case OTTOBRE:
                Assertions.assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                Assertions.assertEquals(1, spwi.getSpedizioneItems().size());
                break;
            case NOVEMBRE:
                case DICEMBRE:
                case GENNAIO:
                case FEBBRAIO:
                case MARZO:
                case GIUGNO:
                    Assertions.assertEquals(0, sped.getPesoStimato().intValue());
                Assertions.assertEquals(0, spwi.getSpedizioneItems().size());
                break;
                case APRILE:
            	if (spwi.getSpedizioneItems().size() == 0) {
            		break;
            	}
                Assertions.assertEquals(blocchetti.getGrammi(), sped.getPesoStimato().intValue());
                break;
                default:
                Assertions.fail();
                break;
            }
        });
        Assertions.assertEquals(0, ec1.getNumeroTotaleRiviste().intValue());
        Assertions.assertEquals(0, ec1.getImporto().doubleValue(),0);

        aggiorna = Smd.rimuovi(abb,ec3, spedizioni,SmdHelper.getSpeseSpedizione());
        for (SpedizioneItem item: aggiorna.getItemsToDelete()) {
            Assertions.assertEquals(ec3, item.getRivistaAbbonamento());
        }
        Assertions.assertEquals(2, aggiorna.getItemsToDelete().size());

        spedizioni.forEach(spwi -> {
            Spedizione sped = spwi.getSpedizione();
            Assertions.assertEquals(0, spwi.getSpedizioneItems().size());
            Assertions.assertEquals(0, sped.getPesoStimato().intValue());
        });
        Assertions.assertEquals(0, ec3.getNumeroTotaleRiviste().intValue());
        Assertions.assertEquals(0, ec3.getImporto().doubleValue(),0);
        log.info(abb.toString());
        
        Assertions.assertEquals(abb.getTotale().doubleValue(), 0,0);
    }

    @Test
    public void testImportoAbbonamentoStd() {
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Anagrafica ar = SmdHelper.getAR();
        EnumSet.allOf(TipoAbbonamentoRivista.class).forEach(tpec -> {
            Abbonamento abb = new Abbonamento();
            abb.setIntestatario(ar);
            RivistaAbbonamento ec = crea(abb,messaggio, tpec);
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
        .forEach(sped -> sped.getSpedizioneItems()
        		.stream()
        		.filter(item -> item.getRivistaAbbonamento() == ec)
        		.forEach(items::add)
        		);

        BigDecimal speseSped = BigDecimal.ZERO;
        for (SpedizioneWithItems sped: spedizioni) {
        	speseSped = speseSped.add(sped.getSpedizione().getSpesePostali());
    	}
        Assertions.assertEquals(p.getMesiPubblicazione().size(), items.size());
        Assertions.assertTrue(Smd.isAbbonamentoAnnuale(ec));
        
        Assertions.assertEquals(p.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(speseSped.doubleValue(), abb.getSpeseEstero().doubleValue(),0);
        Assertions.assertEquals(p.getMesiPubblicazione().size(), spedizioni.size());
        
        SpesaSpedizione spesa = 
                Smd.getSpesaSpedizione(
                           spese, 
                           AreaSpedizione.AmericaAfricaAsia, 
                           RangeSpeseSpedizione.getByPeso(p.getGrammi())
                           );
        spedizioni.forEach(sped ->{
            log.info(sped.toString());
            Assertions.assertEquals(p.getGrammi(), sped.getSpedizione().getPesoStimato().intValue());
            Assertions.assertEquals(spesa.getSpese().doubleValue(), sped.getSpedizione().getSpesePostali().doubleValue(),0);
            sped.getSpedizioneItems().forEach( item -> log.info(item.toString()));
        });
        Assertions.assertEquals(0, abb.getSpese().doubleValue(),0);
        log.info(abb.toString());
    }
    
    @Test
    public void testAggiornaException() {
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
        spedizioni.forEach(sped -> {
            log.info(sped.toString());
            sped.getSpedizioneItems().forEach(item -> {
                log.info(item.toString());
                items.add(item); 
                Assertions.assertEquals(15, item.getNumero().intValue());
            });
        });
        Assertions.assertEquals(8, items.size());
        Assertions.assertEquals(8, spedizioni.size());

        RivistaAbbonamento ec2 = new RivistaAbbonamento();
        ec2.setAbbonamento(abb);
        ec2.setPubblicazione(lodare);
        ec2.setMeseInizio(Mese.GENNAIO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.SETTEMBRE);
        ec2.setAnnoFine(anno);
        ec2.setDestinatario(tizio);
        ec2.setNumero(10);
        Assertions.assertNotEquals(ec2, ec1);
        
        ec2.setPubblicazione(messaggio);
        ec2.setMeseInizio(Mese.MARZO);
        ec2.setAnnoInizio(anno);
        ec2.setMeseFine(Mese.GIUGNO);
        ec2.setAnnoFine(anno);
        Assertions.assertNotEquals(ec2, ec1);

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),null,3,ec1.getTipoAbbonamentoRivista());
            Assertions.fail();
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,0,ec1.getTipoAbbonamentoRivista());
            Assertions.fail();
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        try {
            Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,3,null);
            Assertions.fail();
        } catch (UnsupportedOperationException e) {
            log.info(e.getMessage());
        }

        Assertions.assertEquals(8, items.size());
        Assertions.assertEquals(8, spedizioni.size());
    }

    @Test
    public void testAggiornaNumero() {
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
        spedizioni.forEach(sped -> sped.getSpedizioneItems().forEach(item -> {
            items.add(item);
            Assertions.assertEquals(15, item.getNumero().intValue());
        }));
        Assertions.assertEquals(8, items.size());
        Assertions.assertEquals(8, spedizioni.size());
        Assertions.assertEquals(0, abb.getSpese().doubleValue(),0);
        Assertions.assertEquals(15*8*messaggio.getCostoUnitario().doubleValue(), abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(abb.getImporto().doubleValue(), ec1.getImporto().doubleValue(),0);
        RivistaAbbonamento ec2 = ec1.clone();
        ec2.setTipoAbbonamentoRivista(ec1.getTipoAbbonamentoRivista());
        ec2.setNumero(10);
        Assertions.assertEquals(ec2, ec1);

        RivistaAbbonamentoAggiorna aggiorna = Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,10,ec1.getTipoAbbonamentoRivista());
        
        Assertions.assertEquals(0, aggiorna.getItemsToDelete().size());
        Assertions.assertNotNull(aggiorna.getAbbonamentoToSave());
        Assertions.assertEquals(8, aggiorna.getSpedizioniToSave().size());
        Assertions.assertEquals(1, aggiorna.getRivisteToSave().size());
        
        Assertions.assertEquals(0, aggiorna.getAbbonamentoToSave().getSpese().doubleValue(),0);
        Assertions.assertEquals(10*8*messaggio.getCostoUnitario().doubleValue(), aggiorna.getAbbonamentoToSave().getImporto().doubleValue(),0);
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        Assertions.assertNotNull(rivista);
        Assertions.assertEquals(aggiorna.getAbbonamentoToSave().getImporto().doubleValue(), rivista.getImporto().doubleValue(),0);
        Assertions.assertEquals(8, aggiorna.getSpedizioniToSave().size());
        
        items.clear();
        aggiorna.getSpedizioniToSave().forEach(sped -> sped.getSpedizioneItems().forEach(item -> {
            items.add(item);
            Assertions.assertSame(rivista, item.getRivistaAbbonamento());
            Assertions.assertEquals(rivista, item.getRivistaAbbonamento());
            Assertions.assertEquals(10, item.getNumero().intValue());
        }));
        Assertions.assertEquals(8, items.size());
    }
    
    @Test
    public void testAggiornaTipoAbbonamentoRivista() {
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
        spedizioni.forEach(sped -> sped.getSpedizioneItems().forEach(item -> {
            items.add(item);
            Assertions.assertEquals(1, item.getNumero().intValue());
        }));
        Assertions.assertEquals(11, items.size());
        Assertions.assertEquals(11, spedizioni.size());
        Assertions.assertEquals(messaggio.getAbbonamento().doubleValue(), ec1.getImporto().doubleValue(),0);
        Assertions.assertEquals(messaggio.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
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
        Assertions.assertEquals(ec2, ec1);

        RivistaAbbonamentoAggiorna aggiorna = Smd.aggiorna(abb,spedizioni,SmdHelper.getSpeseSpedizione(),ec1,1,TipoAbbonamentoRivista.OmaggioCuriaDiocesiana);
        Assertions.assertEquals(0, aggiorna.getSpedizioniToSave().size());
        Assertions.assertEquals(0, aggiorna.getItemsToDelete().size());
        Assertions.assertEquals(1, aggiorna.getRivisteToSave().size());
        Assertions.assertNotNull(aggiorna.getAbbonamentoToSave());
        RivistaAbbonamento rivista = aggiorna.getRivisteToSave().iterator().next();
        Assertions.assertEquals(0, rivista.getImporto().doubleValue(),0);
        Assertions.assertEquals(0, aggiorna.getAbbonamentoToSave().getImporto().doubleValue(),0);
        
        items.clear();
        spedizioni.forEach(sped -> sped.getSpedizioneItems().forEach(item -> {
            items.add(item);
            Assertions.assertEquals(1, item.getNumero().intValue());
        }));
        Assertions.assertEquals(11, items.size());
        Assertions.assertEquals(11, spedizioni.size());
    }


    @Test
    public void testGeneraCampagnaGP() {
        List<Anagrafica> anagrafiche = new ArrayList<>();
        Anagrafica gabrielePizzo = SmdHelper.getGP();
        anagrafiche.add(gabrielePizzo);
        Pubblicazione messaggio = SmdHelper.getMessaggio();
        Pubblicazione lodare =SmdHelper.getLodare();
        Pubblicazione blocchetti = SmdHelper.getBlocchetti();
        Pubblicazione estratti = SmdHelper.getEstratti();
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
        Assertions.assertEquals(1, abbonamenti.size());
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
        Assertions.assertEquals(25, spedizioni.size());
        spedizioni.forEach(sped -> {
        	Assertions.assertEquals(1, sped.getSpedizioneItems().size());
        	Assertions.assertEquals(0, sped.getSpedizioniPosticipate().size());
        });
        Assertions.assertEquals(10*blocchetti.getAbbonamentoConSconto().doubleValue()+10*messaggio.getAbbonamento().doubleValue()+lodare.getAbbonamento().doubleValue(), abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(Smd.contrassegno.doubleValue(),abb.getSpese().doubleValue(),0);
        
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
        
        Assertions.assertEquals(blocchetti.getAbbonamento().doubleValue()*15, abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(abb.getTotale().doubleValue(), abb.getImporto().doubleValue(),0);
        
        abb.setIncassato(blocchetti.getAbbonamento().multiply(new BigDecimal(13)));
        Assertions.assertEquals(Incassato.SiConDebito, Abbonamento.getStatoIncasso(abb, new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        abb.setIncassato(blocchetti.getAbbonamento().multiply(new BigDecimal(11)));
        Assertions.assertEquals(Incassato.Parzialmente, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

		double costoUno = ec1.getImporto().doubleValue()/(ec1.getNumero());
		Assertions.assertEquals(blocchetti.getAbbonamento().doubleValue(), costoUno,0);
		Assertions.assertEquals(4*costoUno, abb.getResiduo().doubleValue(),0);

    	
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
        
        Assertions.assertEquals(messaggio.getAbbonamento().doubleValue()*14, abb.getImporto().doubleValue(),0);
        
        abb.setIncassato(messaggio.getAbbonamento().multiply(new BigDecimal(12)));
        Assertions.assertEquals(Incassato.SiConDebito, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        abb.setIncassato(messaggio.getAbbonamento().multiply(new BigDecimal(10)));
        Assertions.assertEquals(Incassato.Parzialmente, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

		double costoUno = ec1.getImporto().doubleValue()/(ec1.getNumero());
		
		Assertions.assertEquals(messaggio.getAbbonamento().doubleValue(), costoUno,0);
		Assertions.assertEquals(4*costoUno, abb.getResiduo().doubleValue(),0);
    	
    }

    @Test
    public void testGeneraCampagnaAR() {
        Anagrafica antonioRusso = SmdHelper.getAR();
        Anagrafica diocesiMilano = SmdHelper.getDiocesiMi();
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
        Assertions.assertEquals(1, abbonamenti.size());
        Abbonamento abb = abbonamenti.iterator().next();
        List<SpedizioneWithItems> spedizioni = new ArrayList<>();
        for (Storico storico:storici) {
        	log.info("testGeneraCampagnaAR: genera Rivista abbonamento from Storico {}", storico);
            RivistaAbbonamento ec = Smd.genera(abb, storico);
            Assertions.assertEquals(1, ec.getNumero().intValue());
            Assertions.assertEquals(0, ec.getImporto().doubleValue(),0);
            Assertions.assertEquals(0, abb.getImporto().doubleValue(),0);
            Assertions.assertEquals(0,abb.getSpese().doubleValue(),0);
        	log.info("testGeneraCampagnaAR: Rivista abbonamento {}", ec);
            spedizioni = Smd.genera(abb, ec, spedizioni, SmdHelper.getSpeseSpedizione());
        	log.info("testGeneraCampagnaAR: spedizioni {}", spedizioni.size());
        }      
        Assertions.assertEquals(26, spedizioni.size());
        Assertions.assertEquals(0, abb.getImporto().doubleValue(),0);
        Assertions.assertEquals(0,abb.getSpese().doubleValue(),0);
        
        spedizioni.forEach(sped -> {
        	log.info(sped.getSpedizione().toString());
        	Assertions.assertEquals(1, sped.getSpedizioneItems().size());
        	Assertions.assertEquals(0, sped.getSpedizioniPosticipate().size());
        });


    }

    @Test
    public void testStatoIncassato() {
        Abbonamento abb = new Abbonamento();
        Assertions.assertEquals(Incassato.Si, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));
        
        abb.setImporto(new BigDecimal(10));
        Assertions.assertEquals(Incassato.No, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));
        
        abb.setIncassato(new BigDecimal(10));
        Assertions.assertEquals(Incassato.Si, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        abb.setIncassato(new BigDecimal(7));
        Assertions.assertEquals(Incassato.SiConDebito, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        abb.setSpese(new BigDecimal(3));
        abb.setIncassato(new BigDecimal(10));
        Assertions.assertEquals(Incassato.SiConDebito, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        abb.setIncassato(new BigDecimal(6));
        Assertions.assertEquals(Incassato.Parzialmente, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));
        
        abb.setImporto(new BigDecimal(70));
        abb.setIncassato(new BigDecimal(60));
        abb.setSpese(BigDecimal.ZERO);
        Assertions.assertEquals(Incassato.SiConDebito, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));
        
        abb.setIncassato(new BigDecimal(54));
        Assertions.assertEquals(Incassato.Parzialmente, Abbonamento.getStatoIncasso(abb,new BigDecimal("70.0"), new BigDecimal("0.8"),new BigDecimal("7.0")));

        
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
        Assertions.assertEquals(Paese.IT, paese);
        paese = Paese.getBySigla("CAN");
        Assertions.assertEquals(Paese.CA, paese);
        paese = Paese.getBySigla("RSM");
        Assertions.assertEquals(Paese.SM, paese);
        paese = Paese.getBySigla("SRM");
        Assertions.assertEquals(Paese.SM, paese);
        
        paese = Paese.valueOf("IT");
        Assertions.assertEquals(Paese.IT, paese);
        paese = Paese.getByNome("Italia");
        Assertions.assertEquals(Paese.IT, paese);
        
    }
        
    @Test
    public void testIncassaEsatto() {
    	Abbonamento abbonamento = new Abbonamento();
    	abbonamento.setImporto(new BigDecimal("200.00"));
    	DistintaVersamento incasso = new DistintaVersamento();
    	incasso.setImporto(new BigDecimal("200.00"));
    	
    	Versamento versamento1 = new Versamento(incasso);
    	versamento1.setImporto(new BigDecimal("200.00"));
    	
    	BigDecimal incassato = Smd.incassa(incasso, versamento1, abbonamento);
    	Assertions.assertEquals(200.00,incassato.doubleValue(),0);
    	Assertions.assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(200.00,versamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento1, abbonamento, incassato);
    	Assertions.assertEquals(0.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,incasso.getIncassato().doubleValue(),0);

    	
    }

    @Test
    public void testIncassaMultipli() {
    	Abbonamento abbonamento = new Abbonamento();
    	abbonamento.setImporto(new BigDecimal("200.00"));
    	DistintaVersamento incasso = new DistintaVersamento();
    	incasso.setImporto(new BigDecimal("215.00"));
    	
    	Versamento versamento1 = new Versamento(incasso);
    	versamento1.setImporto(new BigDecimal("180.00"));
    	Versamento versamento2 = new Versamento(incasso);
    	versamento2.setImporto(new BigDecimal("35.00"));
    	
    	BigDecimal incasso1 = Smd.incassa(incasso, versamento1, abbonamento);
    	Assertions.assertEquals(180.00,incasso1.doubleValue(),0);
    	Assertions.assertEquals(180.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(180.00,versamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(180.00,incasso.getIncassato().doubleValue(),0);
           	
    	BigDecimal incasso2 = Smd.incassa(incasso, versamento2, abbonamento);
    	Assertions.assertEquals(20.00,incasso2.doubleValue(),0);
    	Assertions.assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(200.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso3 = Smd.incassa(incasso, versamento1, abbonamento);
    	BigDecimal incasso4 = Smd.incassa(incasso, versamento2, abbonamento);
    	Assertions.assertEquals(0.00,incasso3.doubleValue(),0);
       	Assertions.assertEquals(0.00,incasso4.doubleValue(),0);
       	Assertions.assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(180.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento1, abbonamento,versamento1.getImporto());
       	Assertions.assertEquals(20.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(20.00, versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(20.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso5 = Smd.incassa(incasso, versamento2, abbonamento);
       	Assertions.assertEquals(15.00,incasso5.doubleValue(),0);
       	Assertions.assertEquals(35.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(35.00, versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(35.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incasso6 = Smd.incassa(incasso, versamento1, abbonamento);
       	Assertions.assertEquals(165.00,incasso6.doubleValue(),0);
       	Assertions.assertEquals(200.00,abbonamento.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(165.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(35.00, versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(200.00,incasso.getIncassato().doubleValue(),0);

    }
    
    @Test
    public void testDissocia() {
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
    	Assertions.assertEquals(100.00,incassato1.doubleValue(),0);
    	Assertions.assertEquals(100.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,incasso.getIncassato().doubleValue(),0);
           	
    	BigDecimal incassato2=  Smd.incassa(incasso, versamento2, abbonamento2);
    	Assertions.assertEquals(135.00,incassato2.doubleValue(),0);
    	Assertions.assertEquals(100.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(135.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(235.00,incasso.getIncassato().doubleValue(),0);
    	
    	BigDecimal incassato3 =  Smd.incassa(incasso, versamento3, abbonamento1);
    	Assertions.assertEquals(80.00,incassato3.doubleValue(),0);
    	Assertions.assertEquals(180.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(135.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(80.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(315.00,incasso.getIncassato().doubleValue(),0);

    	BigDecimal incassato4 =  Smd.incassa(incasso, versamento3, abbonamento2);
    	Assertions.assertEquals(90.00,incassato4.doubleValue(),0);
    	Assertions.assertEquals(180.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(170.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(405.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento3, abbonamento1, new BigDecimal("170.00"));
    	Assertions.assertEquals(10.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(135.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(235.00,incasso.getIncassato().doubleValue(),0);

    	Smd.storna(incasso, versamento2, abbonamento1,new BigDecimal("10.00"));
    	Assertions.assertEquals(0.00,abbonamento1.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(225.00,abbonamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(100.00,versamento1.getIncassato().doubleValue(),0);
      	Assertions.assertEquals(125.00,versamento2.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(0.00,versamento3.getIncassato().doubleValue(),0);
    	Assertions.assertEquals(225.00,incasso.getIncassato().doubleValue(),0);

    	
    }

   @Test
   public void testNumberFormat() {
	   NumberFormat nf = NumberFormat.getInstance(Locale.ITALIAN);
	   nf.setMinimumFractionDigits(2);
	   nf.setMaximumFractionDigits(2);
	   BigDecimal alfa = new BigDecimal("10.5");
	   
	   Assertions.assertEquals("10,50", nf.format(alfa));
   }

   @Test
   public void testValuta() {
       String saldo = NumberFormat.getCurrencyInstance(Locale.ITALY).format(new BigDecimal("17.89"));
        log.info(saldo);
   }

}
