package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.entity.Anagrafica;

@RunWith(SpringRunner.class)
public class SmdImportTest {

    @Test
    public void testImportArchivioClienti() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        smdImportFromExcel.importArchivioClienti();
    }

    @Test
    public void testImportElencoAbboinati() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        smdImportFromExcel.importElencoAbbonati();
    }

    @Test
    public void testImportAll() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.importIntestatari();
        smdImportFromExcel.importBeneficiari();
        smdImportFromExcel.importAbbonatiEstero();
        smdImportFromExcel.importItaEsteroIntestatari();
        smdImportFromExcel.importItaEsteroBeneficiari();
    }
    
 
    @Test 
    public void testFixesElencoAbbonatiCampagna() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        for (String ancodice : caMap.keySet()) {
            Anagrafica ca = caMap.get(ancodice);
            assertTrue(eaMap.keySet().contains(ancodice));
            Anagrafica ac = eaMap.get(ancodice);
            checkAnagrafica(ac, ca);
        }
    }
    
    @Test
    public void testFixAbbonatiEstero() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String, Anagrafica> aeMap = smdImportFromExcel.importAbbonatiEstero();
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
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
    public void testFixBeneficiari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> acMap = smdImportFromExcel.importArchivioClienti();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        Map<String, Anagrafica> abMap = smdImportFromExcel.importBeneficiari();
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
    public void testFixIntestatari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        Map<String, Anagrafica> aiMap = smdImportFromExcel.importIntestatari();
        smdImportFromExcel.fixIntestatari(eaMap, aiMap);
        for (String ancodice : aiMap.keySet()) {
            assertTrue(eaMap.containsKey(ancodice));
            checkAnagrafica(eaMap.get(ancodice), aiMap.get(ancodice));
        }
    }

    @Test
    public void testFixItaEsteroBeneficiari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> acMap = smdImportFromExcel.importArchivioClienti();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        Map<String, Anagrafica> abMap = smdImportFromExcel.importItaEsteroBeneficiari();
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
    public void testFixItaEsteroIntestatari() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> eaMap = smdImportFromExcel.importElencoAbbonati();      
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        smdImportFromExcel.fixElencoAbbonatiCampagna(eaMap, caMap);
        Map<String, Anagrafica> aiMap = smdImportFromExcel.importItaEsteroIntestatari();
        smdImportFromExcel.fixIntestatari(eaMap, aiMap);
        for (String ancodice : aiMap.keySet()) {
            assertTrue(eaMap.containsKey(ancodice));
            checkAnagrafica(eaMap.get(ancodice), aiMap.get(ancodice));
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
    
}
