package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
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

@RunWith(SpringRunner.class)
public class SmdImportTest {

    @Test
    public void testImportArchivioClienti() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        assertEquals(35629, smdImportFromExcel.importArchivioClienti().size());
    }

    @Test
    public void testImportElencoAbbonati() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        assertEquals(7863, smdImportFromExcel.importElencoAbbonati().size());
    }

    @Test
    public void testImportCampagna() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        
        Map<String,Row> carowMap = smdImportFromExcel.getCampagna2020();
        assertEquals(7238, carowMap.size());
        assertEquals(7238,smdImportFromExcel.importCampagna2020(carowMap).size());
    }

    @Test
    public void testImportBeneficiari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        List<Row> benRowMap = smdImportFromExcel.getBeneficiari(); 
        assertEquals(12029, benRowMap.size());
        assertEquals(7516,smdImportFromExcel.importBeneficiari(benRowMap).size());
    }

    @Test
    public void testImportItaEstero() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        List<Row> benItaEsteroRowMap = smdImportFromExcel.getAbbonatiItaEstero();
        assertEquals(14, benItaEsteroRowMap.size());
        assertEquals(19,smdImportFromExcel.importAbbonatiItaEstero(benItaEsteroRowMap).size());
    }

    @Test
    public void testImportOmaggioMessaggio() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String,Row> omaggioMesssaggioRowMap = smdImportFromExcel.getOmaggioMessaggio2020();
        assertEquals(481, omaggioMesssaggioRowMap.size());
        assertEquals(481,smdImportFromExcel.importOmaggioMessaggio2020(omaggioMesssaggioRowMap).size());
    }

    @Test
    public void testImportEstero() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String,Row> esteroRowMap = smdImportFromExcel.getAbbonatiEstero();
        assertEquals(11, esteroRowMap.size());
        smdImportFromExcel.importAbbonatiEstero(esteroRowMap);
    }

 
    @Test 
    public void testFixElencoAbbonatiCampagna() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String,Row> rowMap = smdImportFromExcel.getCampagna2020();    
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020(rowMap);
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        for (String ancodice : caMap.keySet()) {
            Anagrafica ca = caMap.get(ancodice);
            assertTrue(eaMap.keySet().contains(ancodice));
            Anagrafica ac = eaMap.get(ancodice);
            checkAnagrafica(ac, ca);
        }
    }

    @Test 
    public void testFixOmaggioMessaggio() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> elencoAbbonatiMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String,Row> rowMap = smdImportFromExcel.getCampagna2020();    
        Map<String, Anagrafica> abbonatiCampagnaMap = smdImportFromExcel.importCampagna2020(rowMap);
        smdImportFromExcel.fixElencoAbbonatiCampagna(elencoAbbonatiMap, abbonatiCampagnaMap);
        Map<String, Anagrafica> archivioClientiMap = smdImportFromExcel.importArchivioClienti();
        Map<String,Row> omaggioMessaggioRowMap = smdImportFromExcel.getOmaggioMessaggio2020();
        Map<String,Anagrafica> clientiOmaggio = smdImportFromExcel.importOmaggioMessaggio2020(omaggioMessaggioRowMap);
        for (String ancodice : clientiOmaggio.keySet()) {
            Anagrafica omaggioMessaggio = clientiOmaggio.get(ancodice);
            assertFalse(elencoAbbonatiMap.containsKey(ancodice));
            assertFalse(archivioClientiMap.containsKey(ancodice));
            assertEquals(Diocesi.DIOCESISTD, omaggioMessaggio.getDiocesi());
            assertEquals(AreaSpedizione.Italia, omaggioMessaggio.getAreaSpedizione());
            assertNull(omaggioMessaggio.getCellulare());
            assertNull(omaggioMessaggio.getTelefono());
            assertNull(omaggioMessaggio.getEmail());
            assertNull(omaggioMessaggio.getPiva());
            assertNull(omaggioMessaggio.getCodfis());
        }   
        smdImportFromExcel.fixOmaggioMessaggio(elencoAbbonatiMap, archivioClientiMap, clientiOmaggio);
        for (Anagrafica omaggioMessaggio: clientiOmaggio.values()) {
            System.out.println(omaggioMessaggio.getCodeLineBase());
            String key = omaggioMessaggio.getCodeLineBase().substring(4);
            if (omaggioMessaggio.getCodeLineBase().equals("10000000004232")) {
                key = "E"+omaggioMessaggio.getCodeLineBase().substring(5);
            }
            assertTrue(elencoAbbonatiMap.containsKey(key));
            Anagrafica anagrafica = elencoAbbonatiMap.get(key);
            checkAnagrafica(anagrafica, omaggioMessaggio);
        }
    }

    @Test
    public void testCheckAbbonatiEstero() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      

        Map<String,Row> esteroRowMap = smdImportFromExcel.getAbbonatiEstero();
        Map<String, Anagrafica> aeMap =smdImportFromExcel.importAbbonatiEstero(esteroRowMap);

        Map<String,Row> rowMap = smdImportFromExcel.getCampagna2020();    
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020(rowMap);
        smdImportFromExcel.fixAbbonatiEstero(eaMap, aeMap);
        for (String ancodice : aeMap.keySet()) {
            assertTrue(eaMap.containsKey(ancodice));
            assertFalse(caMap.containsKey(ancodice));
            Anagrafica acAna = eaMap.get(ancodice);
            Anagrafica aeAna = aeMap.get(ancodice);
            checkAnagrafica(acAna, aeAna);
        }
    }

    @Test
    public void testCheckBeneficiari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> acMap = smdImportFromExcel.importArchivioClienti();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();
        List<Row> rowMap = smdImportFromExcel.getBeneficiari();
        Map<String, Anagrafica> abMap = smdImportFromExcel.importBeneficiari(rowMap);
        smdImportFromExcel.fixBeneficiari(acMap, eaMap, abMap);
        for (String ancodice : abMap.keySet()) {
            if (eaMap.containsKey(ancodice)) {
                checkAnagrafica(eaMap.get(ancodice), abMap.get(ancodice));
            } else {
                assertTrue(acMap.containsKey(ancodice));
                checkAnagrafica(acMap.get(ancodice), abMap.get(ancodice));
            }
        }
    }

    @Test
    public void testCheckItaEsteroBeneficiari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> acMap = smdImportFromExcel.importArchivioClienti();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();
        Map<String,Row> rowMap = smdImportFromExcel.getCampagna2020();    
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020(rowMap);
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        List<Row> abbItaEsteroRowMap = smdImportFromExcel.getAbbonatiItaEstero();
        Map<String, Anagrafica> aiMap = smdImportFromExcel.importAbbonatiItaEstero(abbItaEsteroRowMap);
        smdImportFromExcel.fixBeneficiari(acMap, eaMap, aiMap);
        for (String ancodice : aiMap.keySet()) {
            if (eaMap.containsKey(ancodice)) {
                checkAnagrafica(eaMap.get(ancodice), aiMap.get(ancodice));
            } else {
                assertTrue(acMap.containsKey(ancodice));
                checkAnagrafica(acMap.get(ancodice), aiMap.get(ancodice));
            }
        }
    }

    private void checkAnagrafica(Anagrafica an1,Anagrafica an2) {
        System.out.println(an1.getCodeLineBase());
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
    
    @Test
    public void checkImportiCampagna2020() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String,Row> rowMap = smdImportFromExcel.getCampagna2020();    
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
            Integer num = smdImportFromExcel.processRowCampagnaMessaggioNum(row);
            BigDecimal costo = smdImportFromExcel.processRowCampagnaMessaggioCosto(row);
            System.out.println("----Messaggio----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), messaggio.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                mitems = mitems+num;
            }
            num = smdImportFromExcel.processRowCampagnaLodareNum(row);
            costo = smdImportFromExcel.processRowCampagnaLodareCosto(row);
            System.out.println("----Lodare----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), lodare.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                litems = litems+num;
            }
            num = smdImportFromExcel.processRowCampagnaBlocchettiNum(row);
            costo = smdImportFromExcel.processRowCampagnaBlocchettiCosto(row);
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
            num = smdImportFromExcel.processRowCampagnaManifestiNum(row);
            costo = smdImportFromExcel.processRowCampagnaManifestiCosto(row);
            System.out.println("----Manifesti----");
            System.out.println(num);
            System.out.println(costo);
            if ( num > 0) {
                assertEquals(costo.doubleValue(), estratti.getAbbonamento().multiply(new BigDecimal(num)).doubleValue(),0);
                totale = totale.add(costo);
                items++;
                eitems = eitems+num;
            }
            BigDecimal totcosto = smdImportFromExcel.processRowCampagnaTotaleCosto(row);
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
        

    }
    
}
