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

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;

@RunWith(SpringRunner.class)
public class SmdImportTest {

    @Test
    public void testImportArchivioClienti() throws Exception {
        assertEquals(35629, SmdImportFromExcel.importArchivioClienti().size());
    }

    @Test
    public void testImportElencoAbbonati() throws Exception {
        assertEquals(7863, SmdImportFromExcel.importElencoAbbonati().size());
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
        assertEquals(12029, benRowMap.size());
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
        assertEquals(481, rows.size());
        assertEquals(481,SmdImportFromExcel.importOmaggio(rows).size());
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
        assertEquals(5, rows.size());
        assertEquals(5,SmdImportFromExcel.importOmaggio(rows).size());
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
        Map<String, Anagrafica> aeMap =SmdImportFromExcel.importAbbonatiEstero(SmdImportFromExcel.getAbbonatiEstero());
        SmdImportFromExcel.fixAbbonatiEstero(eaMap, aeMap);
        for (String ancodice : aeMap.keySet()) {
            assertTrue(anagraficaMap.containsKey(ancodice));
            assertFalse(caMap.containsKey(ancodice));
            Anagrafica eaAna = eaMap.get(ancodice);
            Anagrafica aeAna = aeMap.get(ancodice);
            checkAnagrafica(eaAna, aeAna);
        }
        int i = anagraficaMap.size();
        System.out.println("Elenco Abbonati: Anagrafica size: " + i);
        
        //Beneficiari Ita Estero
        Map<String, Anagrafica> acMap = SmdImportFromExcel.importArchivioClienti();
        Map<String, Anagrafica> aiMap = SmdImportFromExcel.importAbbonatiItaEstero(SmdImportFromExcel.getAbbonatiItaEstero());
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
        Map<String, Anagrafica> abMap = SmdImportFromExcel.importBeneficiari(SmdImportFromExcel.getBeneficiari());
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


        //Omaggi Gesuiti Messaggi
        List<Anagrafica> omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioGesuitiMessaggio2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Messaggi: Anagrafica size: " + i);
        
        //Omaggi Messaggi
        omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioMessaggio2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Messaggi: Anagrafica size: " + i);
        
        //Omaggi Blocchetti
        omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioBlocchetti2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Blocchetti: Anagrafica size: " + i);

        //Omaggi Gesuiti Blocchetti
        omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioGesuitiBlocchetti2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Blocchetti: Anagrafica size: " + i);

        //Omaggi Lodare
        omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioLodare2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Lodare: Anagrafica size: " + i);

        //Omaggi Gesuiti Manifesti
        omaggi = SmdImportFromExcel.importOmaggio(SmdImportFromExcel.getOmaggioGesuitiManifesti2020());
        checkOmaggioBeforeFix(omaggi);
        i+=checkOmaggio(SmdImportFromExcel.fixOmaggio(anagraficaMap, acMap, omaggi),anagraficaMap,acMap);
        System.out.println("+Omaggi Gesuiti Manifesti: Anagrafica size: " + i);

        assertEquals(i, anagraficaMap.size());
        
        Pubblicazione messaggio = SmdLoadSampleData.getMessaggio();
        Pubblicazione lodare = SmdLoadSampleData.getLodare();
        Pubblicazione blocchetti = SmdLoadSampleData.getBlocchetti();
        Pubblicazione estratti = SmdLoadSampleData.getEstratti();
        BigDecimal valoreCampagna = BigDecimal.ZERO;
        int items = 0;
        int mitems = 0;
        int bitems = 0;
        int litems = 0;
        int eitems = 0;
        for (String cod :rowMap.keySet() )
        {
            System.out.println("----"+cod+"----");
            Row row = rowMap.get(cod);
            BigDecimal totale = BigDecimal.ZERO;
            Integer num = SmdImportFromExcel.processRowCampagnaMessaggioNum(row);
            BigDecimal costo = SmdImportFromExcel.processRowCampagnaMessaggioCosto(row);
            System.out.println("----Messaggio----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), messaggio.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                mitems = mitems+num;
            }
            num = SmdImportFromExcel.processRowCampagnaLodareNum(row);
            costo = SmdImportFromExcel.processRowCampagnaLodareCosto(row);
            System.out.println("----Lodare----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), lodare.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                litems = litems+num;
            }
            num = SmdImportFromExcel.processRowCampagnaBlocchettiNum(row);
            costo = SmdImportFromExcel.processRowCampagnaBlocchettiCosto(row);
            System.out.println("----Blocchetti----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                if ( costo.doubleValue() != blocchetti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue()) {
                    System.out.println("----Scontati 20% 5.6----");
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
            System.out.println("----Manifesti----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), estratti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                eitems = eitems+num;
            }
            BigDecimal totcosto = SmdImportFromExcel.processRowCampagnaTotaleCosto(row);
            System.out.println("----Totale----");
            System.out.println(totcosto);
            assertEquals(totale.doubleValue(), totcosto.doubleValue(),0);
            System.out.println("----"+cod+"----");
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

        List<Storico> storici = SmdImportFromExcel.getStoriciFromCampagna2010(rowMap, anagraficaMap, messaggio, lodare, blocchetti, estratti);
        assertEquals(items, storici.size());
        
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
