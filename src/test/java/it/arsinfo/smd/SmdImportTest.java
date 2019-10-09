package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.RangeSpeseSpedizione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.entity.Storico;

@RunWith(SpringRunner.class)
public class SmdImportTest {

    @Test
    public void testImportArchivioClienti() throws Exception {
        assertEquals(35629, SmdImportFromExcel.importArchivioClienti().size());
    }

    @Test
    public void testImportCategoriaBmCassa() throws Exception {
        assertEquals(32, SmdImportFromExcel.getCategoriaBmCassa().size());
    }

    @Test
    public void testImportElencoAbbonati() throws Exception {
        assertEquals(7934, SmdImportFromExcel.importElencoAbbonati().size());
    }

    @Test
    public void testImportCampagna() throws Exception {        
        Map<String,Row> carowMap = SmdImportFromExcel.getCampagna2020();
        assertEquals(7238, carowMap.size());
        assertEquals(7238,SmdImportFromExcel.importCampagna2020(carowMap).size());
    }

    @Test
    public void testImportBeneficiari() throws Exception {
        List<Row> benRowMap = SmdImportFromExcel.getBeneficiari(); 
        assertEquals(11928, benRowMap.size());
        assertEquals(7516,SmdImportFromExcel.importBeneficiari(benRowMap).size());
    }

    @Test
    public void testImportItaEstero() throws Exception {
        List<Row> benItaEsteroRowMap = SmdImportFromExcel.getAbbonatiItaEstero();
        assertEquals(14, benItaEsteroRowMap.size());
        assertEquals(19,SmdImportFromExcel.importAbbonatiItaEstero(benItaEsteroRowMap).size());
    }

    @Test
    public void testImportOmaggioMessaggio() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioMessaggio2020();
        assertEquals(478, rows.size());
        assertEquals(478,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportOmaggioGesuitiMessaggio() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioGesuitiMessaggio2020();
        assertEquals(16, rows.size());
        assertEquals(16,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportOmaggioBlocchetti() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioBlocchetti2020();
        assertEquals(4, rows.size());
        assertEquals(4,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportOmaggioGesuitiBlocchetti() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioGesuitiBlocchetti2020();
        assertEquals(5, rows.size());
        assertEquals(5,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportOmaggioLodare() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioLodare2020();
        assertEquals(4, rows.size());
        assertEquals(4,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportOmaggioGesuitiManifesti() throws Exception {
        List<Row> rows = SmdImportFromExcel.getOmaggioGesuitiManifesti2020();
        assertEquals(2, rows.size());
        assertEquals(2,SmdImportFromExcel.importOmaggio(rows).size());
    }

    @Test
    public void testImportEstero() throws Exception {
        Map<String,Row> esteroRowMap = SmdImportFromExcel.getAbbonatiEstero();
        assertEquals(11, esteroRowMap.size());
        SmdImportFromExcel.importAbbonatiEstero(esteroRowMap);
    }

 
    @Test 
    public void testImportAnagraficaAdp() throws Exception {
        Map<String, Anagrafica> eaMap = SmdImportFromExcel.importElencoAbbonati();      
        Map<String,Row> rowMap = SmdImportFromExcel.getCampagna2020();    
        Map<String, Anagrafica> caMap = SmdImportFromExcel.importCampagna2020(rowMap);
        SmdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        for (String ancodice : caMap.keySet()) {
            Anagrafica ca = caMap.get(ancodice);
            assertTrue(eaMap.keySet().contains(ancodice));
            Anagrafica ac = eaMap.get(ancodice);
            checkAnagrafica(ac, ca);
        }
        Map<String,Anagrafica>  anagraficaMap = new HashMap<String, Anagrafica>();
        for (String key: eaMap.keySet()) {
            anagraficaMap.put(key, eaMap.get(key));
        }

        // Abbonamenti Estero
        Map<String,Row> aeRowMap = SmdImportFromExcel.getAbbonatiEstero();
        Map<String, Anagrafica> aeMap =SmdImportFromExcel.importAbbonatiEstero(aeRowMap);
        SmdImportFromExcel.fixAbbonatiEstero(eaMap, aeMap);
        for (String ancodice : aeMap.keySet()) {
            assertTrue(anagraficaMap.containsKey(ancodice));
            assertFalse(caMap.containsKey(ancodice));
            Anagrafica eaAna = eaMap.get(ancodice);
            Anagrafica aeAna = aeMap.get(ancodice);            
        }
        int i = anagraficaMap.size();
        System.out.println("Elenco Abbonati: Anagrafica size: " + i);
        
        //Beneficiari Ita Estero
        Map<String, Anagrafica> acMap = SmdImportFromExcel.importArchivioClienti();
        List<Row> airows =SmdImportFromExcel.getAbbonatiItaEstero();
        Map<String, Anagrafica> aiMap = SmdImportFromExcel.importAbbonatiItaEstero(airows);
        SmdImportFromExcel.fixBeneficiari(acMap, eaMap, aiMap);
        int j =0;
        for (String ancodice : aiMap.keySet()) {
            if (anagraficaMap.containsKey(ancodice)) {
                checkAnagrafica(anagraficaMap.get(ancodice), aiMap.get(ancodice));
            } else {
                assertTrue(acMap.containsKey(ancodice));
                checkAnagrafica(acMap.get(ancodice), aiMap.get(ancodice));
                anagraficaMap.put(ancodice, aiMap.get(ancodice));
                j++;
            }
        }
        i+=j;
        System.out.println("Beneficiari Ita Estero: Aggiunti: " + j);

        //Beneficiari
        List<Row> abrows = SmdImportFromExcel.getBeneficiari();
        Map<String, Anagrafica> abMap = SmdImportFromExcel.importBeneficiari(abrows);
        SmdImportFromExcel.fixBeneficiari(acMap, eaMap, abMap);
        int k=0;
        for (String ancodice : abMap.keySet()) {
            if (anagraficaMap.containsKey(ancodice)) {
                checkAnagrafica(eaMap.get(ancodice), anagraficaMap.get(ancodice));
            } else {
                assertTrue(acMap.containsKey(ancodice));
                checkAnagrafica(acMap.get(ancodice), abMap.get(ancodice));
                anagraficaMap.put(ancodice, abMap.get(ancodice));
                k++;
            }
        }
        i+=k;
        System.out.println("Beneficiari Ita: Aggiunti: " + k);


        //Omaggi Messaggi
        List<Row> omrows = SmdImportFromExcel.getOmaggioMessaggio2020();
        List<Anagrafica> omlist = SmdImportFromExcel.importOmaggio(omrows);
        checkOmaggioBeforeFix(omlist);
        Map<String,Anagrafica> omMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omlist);
        i+=checkOmaggio(omMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Messaggi: Anagrafica size: " + i);
        
        //Omaggi Gesuiti Messaggi
        List<Row> ogmrows = SmdImportFromExcel.getOmaggioGesuitiMessaggio2020();
        List<Anagrafica> ogmlist = SmdImportFromExcel.importOmaggio(ogmrows);
        checkOmaggioBeforeFix(ogmlist);
        Map<String,Anagrafica> ogmMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, ogmlist);
        i+=checkOmaggio(ogmMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Messaggi: Anagrafica size: " + i);
        
        //Omaggi Blocchetti
        List<Row> obrows = SmdImportFromExcel.getOmaggioBlocchetti2020();
        List<Anagrafica> oblist = SmdImportFromExcel.importOmaggio(obrows);
        checkOmaggioBeforeFix(oblist);
        Map<String,Anagrafica> obMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, oblist);
        i+=checkOmaggio(obMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Blocchetti: Anagrafica size: " + i);

        //Omaggi Gesuiti Blocchetti
        List<Row> ogbrows = SmdImportFromExcel.getOmaggioGesuitiBlocchetti2020();
        List<Anagrafica> ogblist = SmdImportFromExcel.importOmaggio(ogbrows);
        checkOmaggioBeforeFix(ogblist);
        Map<String,Anagrafica> ogbMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, ogblist);
        i+=checkOmaggio(ogbMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Blocchetti: Anagrafica size: " + i);

        //Omaggi Lodare
        List<Row> olrows = SmdImportFromExcel.getOmaggioLodare2020();
        List<Anagrafica> ollist = SmdImportFromExcel.importOmaggio(olrows);
        checkOmaggioBeforeFix(ollist);
        Map<String,Anagrafica> olMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, ollist); 
        i+=checkOmaggio(olMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Lodare: Anagrafica size: " + i);

        //Omaggi Gesuiti Manifesti
        List<Row> ogerows = SmdImportFromExcel.getOmaggioGesuitiManifesti2020();
        List<Anagrafica> ogelist = SmdImportFromExcel.importOmaggio(ogerows);
        checkOmaggioBeforeFix(ogelist);
        Map<String,Anagrafica> ogeMap = SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, ogelist);
        i+=checkOmaggio(ogeMap,anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Manifesti: Anagrafica size: " + i);

        assertEquals(i, anagraficaMap.size());
        
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        
        // check campagna
        BigDecimal valoreCampagna = BigDecimal.ZERO;
        int items = 0;
        int mitems = 0;
        int bitems = 0;
        int litems = 0;
        int eitems = 0;
        for (String cod :rowMap.keySet() )
        {
            Row row = rowMap.get(cod);
            BigDecimal totale = BigDecimal.ZERO;
            Integer num = SmdImportFromExcel.processRowCampagnaMessaggioNum(row);
            BigDecimal costo = SmdImportFromExcel.processRowCampagnaMessaggioCosto(row);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), messaggio.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                mitems = mitems+num;
            }
            num = SmdImportFromExcel.processRowCampagnaLodareNum(row);
            costo = SmdImportFromExcel.processRowCampagnaLodareCosto(row);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), lodare.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                litems = litems+num;
            }
            num = SmdImportFromExcel.processRowCampagnaBlocchettiNum(row);
            costo = SmdImportFromExcel.processRowCampagnaBlocchettiCosto(row);
            if ( num > 0) {
                if ( costo.doubleValue() != blocchetti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue()) {
                    assertEquals(costo.doubleValue(), blocchetti.getAbbonamentoConSconto().multiply(new BigDecimal(num)).doubleValue(),0);
                } else {
                    assertEquals(costo.doubleValue(), blocchetti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                }
                totale = totale.add(costo);
                items++;
                bitems = bitems+num;
            }
            num = SmdImportFromExcel.processRowCampagnaManifestiNum(row);
            costo = SmdImportFromExcel.processRowCampagnaManifestiCosto(row);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), estratti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                eitems = eitems+num;
            }
            BigDecimal totcosto = SmdImportFromExcel.processRowCampagnaTotaleCosto(row);
            assertEquals(totale.doubleValue(), totcosto.doubleValue(),0);
            valoreCampagna = valoreCampagna.add(totcosto);
        }                
        System.out.println("----Totale Campagna----");
        System.out.println(valoreCampagna);
        System.out.println("----Totale items----");
        System.out.println(items);
        System.out.println("----Totale Messaggio----");
        System.out.println(mitems);
        System.out.println("----Totale Lodare----");
        System.out.println(litems);
        System.out.println("----Totale Blocchetti----");
        System.out.println(bitems);
        System.out.println("----Totale Manifesti----");
        System.out.println(eitems);        
        //Storici campagna
        List<Storico> storicicampagna = SmdImportFromExcel.getStoriciFromCampagna2010(rowMap, anagraficaMap, messaggio, lodare, blocchetti, estratti);
        assertEquals(items, storicicampagna.size());
        
        
        //storico Beneficiari
        Set<String> bmcassa = SmdImportFromExcel.getCategoriaBmCassa();
        for (String codbmcassa: bmcassa) {
            if (abMap.containsKey(codbmcassa)) {
                assertTrue(anagraficaMap.containsKey(codbmcassa));
            }
        }
        
        for (Row row : abrows) {
            String ancodice = SmdImportFromExcel.getAncodiceFromBeneficiari(row);
            assertTrue(anagraficaMap.containsKey(ancodice));
            int qnt = SmdImportFromExcel.getQuantitaFromBeneficiari(row);
            assertTrue(qnt >=1);
            BigDecimal prezzo = SmdImportFromExcel.getPrezzoFromBeneficiari(row);
                       
            if (messaggio.getAbbonamento().compareTo(prezzo) == 0) {
                //System.out.println("messaggio");
            } else if (blocchetti.getAbbonamento().compareTo(prezzo) == 0) {
                //System.out.println("blocchetti");
            } else if (lodare.getAbbonamento().compareTo(prezzo) == 0) {
                //System.out.println("lodare");
            } else if (estratti.getAbbonamento().compareTo(prezzo) == 0) {
                //System.out.println("estratti");
            } else if (blocchetti.getAbbonamentoConSconto().compareTo(prezzo) == 0) {
                //System.out.println("blocchetti con sconto");
            } else {
                System.out.println(ancodice);
                System.out.println("spese: " + prezzo);
            }
            String bancodice = SmdImportFromExcel.getBancodiceFromBeneficiari(row);
            if (bancodice.trim().equals("")) {
                continue;
            }
            assertTrue(anagraficaMap.containsKey(bancodice));
        }
        List<Storico> storicofrombeneficiari = 
                SmdImportFromExcel.
                getStoriciFromBeneficiari2010(
                  abrows, anagraficaMap, messaggio, lodare, blocchetti, estratti, bmcassa);
        assertEquals(abrows.size()-6, storicofrombeneficiari.size());

        //Storici abbonati Estero
        int itemstoricoabbestero =0;
        for (String ancod: aeRowMap.keySet()) {
            assertTrue(anagraficaMap.containsKey(ancod));
            Anagrafica intestatario = anagraficaMap.get(ancod);
            AreaSpedizione area = intestatario.getAreaSpedizione();
            Row row = aeRowMap.get(ancod);
            int peso = 0;
            int numeroSped = 0;
            BigDecimal spese = SmdImportFromExcel.getSpeseAbbEstero(row);
            BigDecimal spesecalc = BigDecimal.ZERO;
            if (SmdImportFromExcel.getIdEstrattiAbbEstero(row) == 3) {
                int qnt = SmdImportFromExcel.getQtaEstrattiAbbEstero(row);
                BigDecimal costo = SmdImportFromExcel.getCostoEstrattiAbbEstero(row);
                assertEquals(qnt*estratti.getAbbonamento().doubleValue(), costo.doubleValue(),0);
                peso = qnt*estratti.getGrammi();
                numeroSped = estratti.getMesiPubblicazione().size();
                RangeSpeseSpedizione range = RangeSpeseSpedizione.getByPeso(peso);
                SpesaSpedizione spesaSped = Smd.getSpesaSpedizione(SmdLoadSampleData.getSpeseSpedizione(), area, range);
                spesecalc =spesecalc.add(spesaSped.getSpese().multiply(new BigDecimal(numeroSped)));
                itemstoricoabbestero++;
            }
            if (SmdImportFromExcel.getIdBlocchettiAbbEstero(row) == 2) {
                int qnt = SmdImportFromExcel.getQtaBlocchettiAbbEstero(row);
                BigDecimal costo = SmdImportFromExcel.getCostoBlocchettiAbbEstero(row);
                assertEquals(qnt*blocchetti.getAbbonamento().doubleValue(), costo.doubleValue(),0);
                peso = qnt*blocchetti.getGrammi();
                numeroSped = blocchetti.getMesiPubblicazione().size();
                RangeSpeseSpedizione range = RangeSpeseSpedizione.getByPeso(peso);
                SpesaSpedizione spesaSped = Smd.getSpesaSpedizione(SmdLoadSampleData.getSpeseSpedizione(), area, range);
                spesecalc =spesecalc.add(spesaSped.getSpese().multiply(new BigDecimal(numeroSped)));
                itemstoricoabbestero++;
            }
            if (SmdImportFromExcel.getIdMessaggioAbbEstero(row) == 1) {
                int qnt = SmdImportFromExcel.getQtaMessaggioAbbEstero(row);
                BigDecimal costo = SmdImportFromExcel.getCostoMessaggioAbbEstero(row);
                assertEquals(qnt*messaggio.getAbbonamento().doubleValue(), costo.doubleValue(),0);
                peso = qnt*messaggio.getGrammi();
                numeroSped = messaggio.getMesiPubblicazione().size();
                RangeSpeseSpedizione range = RangeSpeseSpedizione.getByPeso(peso);
                SpesaSpedizione spesaSped = Smd.getSpesaSpedizione(SmdLoadSampleData.getSpeseSpedizione(), area, range);
                spesecalc =spesecalc.add(spesaSped.getSpese().multiply(new BigDecimal(numeroSped)));
                itemstoricoabbestero++;
            }
            if (ancod.equals("0000025049")) {
                System.err.println(ancod);
                System.err.println("spese calcolate: "+spesecalc.doubleValue());
                System.err.println("spese riportate: "+spese.doubleValue());
                continue;
            }
            assertEquals(spesecalc.doubleValue(), spese.doubleValue(),2);                        
        }
        List<Storico> storicoestero = SmdImportFromExcel.getStoriciFromEstero2010(aeRowMap, anagraficaMap, messaggio, lodare, blocchetti, estratti);
        assertEquals(itemstoricoabbestero, storicoestero.size());
        
        // Storici abbonati ita estero
        for (Row row: airows) {
            String ancodint = SmdImportFromExcel.getAnCodiceIntestatarioAbbonatiItaEstero(row);
            assertTrue(anagraficaMap.containsKey(ancodint));
            String ancoddst = SmdImportFromExcel.getAnCodiceDestinatatarioAbbonatiItaEstero(row);
            assertTrue(anagraficaMap.containsKey(ancoddst));
            String pubstr = SmdImportFromExcel.getTestataFromAbbonatiItaEstero(row);
            int qnt = SmdImportFromExcel.getQuantFromAbbonatiItaEstero(row);
            BigDecimal importo = SmdImportFromExcel.getImportoRivistaFromAbbonatiItaEstero(row);
            BigDecimal spese = SmdImportFromExcel.getSpeseFromAbbonatiItaEstero(row);
            AreaSpedizione area = SmdImportFromExcel.getAreaSpedizioneDestinatarioAbbonatiEstero(row);
            int peso = 0;
            int numeroSped = 0;
            if (pubstr.equalsIgnoreCase("messaggio")) {
                assertEquals(messaggio.getAbbonamento().doubleValue(), importo.doubleValue(),0);
                peso = qnt*messaggio.getGrammi();
                numeroSped = messaggio.getMesiPubblicazione().size();
            } else if (pubstr.equalsIgnoreCase("blocchetti")) {
                assertEquals(blocchetti.getAbbonamento().doubleValue(), importo.doubleValue(),0);
                peso = qnt*blocchetti.getGrammi();
                numeroSped = blocchetti.getMesiPubblicazione().size();
            } else if (pubstr.equalsIgnoreCase("lodare")) {
                assertEquals(lodare.getAbbonamento().doubleValue(), importo.doubleValue(),0);                
                peso = qnt*lodare.getGrammi();
                numeroSped = lodare.getMesiPubblicazione().size();
            } else {
                assertTrue(false);
            }
            RangeSpeseSpedizione range = RangeSpeseSpedizione.getByPeso(peso);
            SpesaSpedizione spesaSped = Smd.getSpesaSpedizione(SmdLoadSampleData.getSpeseSpedizione(), area, range);
            assertEquals(numeroSped*spesaSped.getSpese().doubleValue(), spese.doubleValue(),2);                        
        }        
        
        List<Storico> storiciitaestero =  SmdImportFromExcel.getStoriciFromItaEstero2010(airows, anagraficaMap, messaggio, lodare, blocchetti, estratti);
        assertEquals(airows.size(), storiciitaestero.size());
        
        List<Storico> storiciom = 
            SmdImportFromExcel
            .getStoriciFromOmaggio(
                   omrows, 
                   anagraficaMap, 
                   omMap, 
                   messaggio, 
                   InvioSpedizione.Spedizioniere, 
                   TipoEstrattoConto.OmaggioCuriaDiocesiana);
        assertEquals(omrows.size(), storiciom.size());

        List<Storico> storiciol = 
                SmdImportFromExcel
                .getStoriciFromOmaggio(
                       olrows, 
                       anagraficaMap, 
                       olMap, 
                       lodare, 
                       InvioSpedizione.Spedizioniere, 
                       TipoEstrattoConto.OmaggioCuriaDiocesiana);
        assertEquals(olrows.size(), storiciol.size());

        List<Storico> storiciob = 
                SmdImportFromExcel
                .getStoriciFromOmaggio(
                       obrows, 
                       anagraficaMap, 
                       obMap, 
                       blocchetti, 
                       InvioSpedizione.Spedizioniere, 
                       TipoEstrattoConto.OmaggioCuriaDiocesiana);
        assertEquals(obrows.size(), storiciob.size());

        List<Storico> storiciogm = 
                SmdImportFromExcel
                .getStoriciFromOmaggio(
                       ogmrows, 
                       anagraficaMap, 
                       ogmMap, 
                       messaggio, 
                       InvioSpedizione.AdpSede, 
                       TipoEstrattoConto.OmaggioGesuiti);
            assertEquals(ogmrows.size(), storiciogm.size());

            List<Storico> storiciogb = 
                    SmdImportFromExcel
                    .getStoriciFromOmaggio(
                           ogbrows, 
                           anagraficaMap, 
                           ogbMap, 
                           blocchetti, 
                           InvioSpedizione.AdpSede, 
                           TipoEstrattoConto.OmaggioGesuiti);
            assertEquals(ogbrows.size(), storiciogb.size());

            List<Storico> storicioge = 
                        SmdImportFromExcel
                        .getStoriciFromOmaggio(
                               ogerows, 
                               anagraficaMap, 
                               ogeMap, 
                               estratti, 
                               InvioSpedizione.AdpSede, 
                               TipoEstrattoConto.OmaggioGesuiti);
            assertEquals(ogerows.size(), storicioge.size());
            
            Map<String,BigDecimal> fixSpeseEsteroMap = 
                    SmdImportFromExcel.fixSpeseEstero(aeRowMap);
            assertEquals(aeRowMap.size(), fixSpeseEsteroMap.size());
            for (String cod: fixSpeseEsteroMap.keySet()) {
                assertTrue(anagraficaMap.containsKey(cod));
            }
            
            Map<String,BigDecimal> fixSpeseItaEsteroMap = 
                    SmdImportFromExcel.fixSpeseItaEstero(airows);
            assertEquals(7, fixSpeseItaEsteroMap.size());
            for (String cod: fixSpeseItaEsteroMap.keySet()) {
                assertTrue(anagraficaMap.containsKey(cod));
            }
            
            Map<String,BigDecimal> fixSpeseBeneficiariMap = 
                    SmdImportFromExcel.fixSpeseBeneficiari(abrows, messaggio, lodare, blocchetti, estratti);
            assertEquals(6, fixSpeseBeneficiariMap.size());
            for (String cod: fixSpeseBeneficiariMap.keySet()) {
                assertTrue(anagraficaMap.containsKey(cod));
            }


    }
    
    private void checkOmaggioBeforeFix(List<Anagrafica> clientiOmaggio) {
        for (Anagrafica omaggioMessaggio : clientiOmaggio) {
            assertEquals(Diocesi.DIOCESISTD, omaggioMessaggio.getDiocesi());
            assertEquals(AreaSpedizione.Italia, omaggioMessaggio.getAreaSpedizione());
            assertFalse(TitoloAnagrafica.Nessuno ==  omaggioMessaggio.getTitolo());
            assertNull(omaggioMessaggio.getCellulare());
            assertNull(omaggioMessaggio.getTelefono());
            assertNull(omaggioMessaggio.getEmail());
            assertNull(omaggioMessaggio.getPiva());
            assertNull(omaggioMessaggio.getCodfis());
        }           
    }

    private int checkOmaggio(
            Map<String,Anagrafica> clientiOmaggioMap,
            Map<String,Anagrafica> anagraficaMap,
            Map<String,Anagrafica> archivioClientiMap) throws Exception {
        
        int i=0;
        for (String ancodice: clientiOmaggioMap.keySet()) {
            Anagrafica omaggioMessaggio = clientiOmaggioMap.get(ancodice);
            assertFalse(Diocesi.DIOCESISTD == omaggioMessaggio.getDiocesi());
            assertEquals(AreaSpedizione.Italia, omaggioMessaggio.getAreaSpedizione());
            assertFalse(TitoloAnagrafica.Nessuno ==  omaggioMessaggio.getTitolo());
            assertNotNull(omaggioMessaggio.getCellulare());
            assertNotNull(omaggioMessaggio.getTelefono());
            assertNotNull(omaggioMessaggio.getEmail());
            assertNotNull(omaggioMessaggio.getPiva());
            assertNotNull(omaggioMessaggio.getCodfis());
            if (anagraficaMap.containsKey(ancodice)) {
                Anagrafica anagElenco = anagraficaMap.get(ancodice);
                checkAnagrafica(anagElenco, omaggioMessaggio);
            } else {
                assertTrue(archivioClientiMap.containsKey(ancodice));
                Anagrafica anagArchivio = archivioClientiMap.get(ancodice);
                checkAnagrafica(anagArchivio, omaggioMessaggio);
                anagraficaMap.put(ancodice, omaggioMessaggio);
                i++;
            }
        }
        System.out.println("added:  n." +i);
        return i;
    }

    private void checkAnagrafica(Anagrafica an1,Anagrafica an2) {
        //System.out.println(an1.getCodeLineBase());
        assertEquals(an1.getCodeLineBase(), an2.getCodeLineBase());
        assertNotNull(an1.getDiocesi());
        assertEquals(an1.getDenominazione(), an2.getDenominazione());
        assertEquals(an1.getNome(), an2.getNome());
        assertEquals(an1.getPaese(), an2.getPaese());
        assertEquals(an1.getProvincia(), an2.getProvincia());
        assertEquals(an1.getCitta(), an2.getCitta());
        assertEquals(an1.getCap(), an2.getCap());            
        assertEquals(an1.getIndirizzo(), an2.getIndirizzo());
        assertEquals(an1.getIndirizzoSecondaRiga(), an2.getIndirizzoSecondaRiga());
        assertEquals(an1.getAreaSpedizione(), an2.getAreaSpedizione());
        assertEquals(an1.getDiocesi(), an2.getDiocesi());
        assertEquals(an1.getTitolo(), an2.getTitolo());        
        assertEquals(an1.getCellulare(), an2.getCellulare());        
        assertEquals(an1.getCodfis(), an2.getCodfis());        
        assertEquals(an1.getEmail(), an2.getEmail());        
        assertEquals(an1.getPiva(), an2.getPiva());        
        assertEquals(an1.getTelefono(), an2.getTelefono());        
    }    
}
