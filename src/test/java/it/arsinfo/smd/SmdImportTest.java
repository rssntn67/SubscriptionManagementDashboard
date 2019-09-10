package it.arsinfo.smd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import it.arsinfo.smd.entity.Anagrafica;

@RunWith(SpringRunner.class)
public class SmdImportTest {

    @Test
    public void testImportAnagraficaArchivio() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        smdImportFromExcel.importArchivioClienti();
    }
    
    @Test
    public void testImportAnagraficaCampagna() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        smdImportFromExcel.importCampagna2020();
    }

    @Test 
    public void testFixes() throws Exception {
        SmdImportFromExcel smdImportFromExcel = new SmdImportFromExcel();
        Map<String, Anagrafica> acMap = smdImportFromExcel.importArchivioClienti();      
        Map<String, Anagrafica> caMap = smdImportFromExcel.importCampagna2020();
        
        int i=0;
        for (String ancodice : caMap.keySet()) {
            i++;
            Anagrafica caAnagrafica = caMap.get(ancodice);
            assertTrue(acMap.keySet().contains(ancodice));
            Anagrafica acAnagrafica = acMap.get(ancodice);
            assertEquals(caAnagrafica.getCodeLineBase(), acAnagrafica.getCodeLineBase());
            if (ancodice.equals("0000021498") 
                || ancodice.equals("0000012046")
                || ancodice.equals("0000069601")
                || ancodice.equals("0000003829")
                || ancodice.equals("0000074200")
                || ancodice.equals("0000068384")
                || ancodice.equals("0000013286")
                || ancodice.equals("0000022412")
                || ancodice.equals("0000011717")
                || ancodice.equals("0000070010")
                || ancodice.equals("0000067739")
                || ancodice.equals("0000067635")
                || ancodice.equals("0000017386")
                || ancodice.equals("0000012586")
                || ancodice.equals("0000018764")
                || ancodice.equals("0000072215")
                || ancodice.equals("0000012843")
                || ancodice.equals("0000023556")
                || ancodice.equals("0000005674")
                || ancodice.equals("0000073184")
                || ancodice.equals("0000068599")
                || ancodice.equals("0000062486")
                || ancodice.equals("0000012438")
                ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setDenominazione(acAnagrafica.getDenominazione());
                caAnagrafica.setNome(acAnagrafica.getNome());
            }
            assertEquals(caAnagrafica.getDenominazione(), acAnagrafica.getDenominazione());

            
            if (ancodice.equals("0000071821") 
                    || ancodice.equals("0000026435") 
                    || ancodice.equals("0000015653")
                    || ancodice.equals("0000018136")
                    || ancodice.equals("0000006176")
                    || ancodice.equals("0000015907")
                    || ancodice.equals("0000011628")
                    || ancodice.equals("0000029845")
                    || ancodice.equals("0000005884")
                    || ancodice.equals("0000022397")
                    || ancodice.equals("0000007088")
                    || ancodice.equals("0000004835")
                    || ancodice.equals("0000059960")
                    || ancodice.equals("0000004892")
                    || ancodice.equals("0000070325")
                    || ancodice.equals("0000010067")
                    || ancodice.equals("0000004244")
                    || ancodice.equals("0000004098")
                    || ancodice.equals("0000074822")
                    || ancodice.equals("0000064058")
                    || ancodice.equals("0000072596")
                    || ancodice.equals("0000004457")
                    || ancodice.equals("0000010123")
                    || ancodice.equals("0000010104")
                    || ancodice.equals("0000007662")
                    || ancodice.equals("0000011192")
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setNome(acAnagrafica.getNome());
                caAnagrafica.setCitta(acAnagrafica.getCitta());
                caAnagrafica.setCap(acAnagrafica.getCap());
                caAnagrafica.setIndirizzo(acAnagrafica.getIndirizzo());
            }
            assertEquals(caAnagrafica.getNome(), acAnagrafica.getNome());
            
            assertEquals(caAnagrafica.getPaese(), acAnagrafica.getPaese());
            if (ancodice.equals("0000013374") 
                ||ancodice.equals("0000064543") 
                ||ancodice.equals("0000015427") 
                ||ancodice.equals("0000011361") 
                ||ancodice.equals("0000074822") 
                ||ancodice.equals("0000011192") 
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setProvincia(acAnagrafica.getProvincia());
                caAnagrafica.setCitta(acAnagrafica.getCitta());
                caAnagrafica.setIndirizzo(acAnagrafica.getIndirizzo());
                caAnagrafica.setCap(acAnagrafica.getCap());
            }
            assertEquals(caAnagrafica.getProvincia(), acAnagrafica.getProvincia());
            
            if (ancodice.equals("0000063661")
                || ancodice.equals("0000011798")
                || ancodice.equals("0000065672")
                || ancodice.equals("0000017604")
                || ancodice.equals("0000017622")
                || ancodice.equals("0000017678")
                ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setCitta(acAnagrafica.getCitta());
                caAnagrafica.setIndirizzo(acAnagrafica.getIndirizzo());
                caAnagrafica.setCap(acAnagrafica.getCap());
            }
            if (ancodice.equals("0000016209")
                || ancodice.equals("0000015153")
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated acAnagrafica");
                acAnagrafica.setCitta(caAnagrafica.getCitta());
                }
            assertEquals(caAnagrafica.getCitta(), acAnagrafica.getCitta());

            if (ancodice.equals("0000020195")
                ||   ancodice.equals("0000069121") 
                ||   ancodice.equals("0000067234")
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setCap(acAnagrafica.getCap());
                caAnagrafica.setIndirizzo(acAnagrafica.getIndirizzo());
            }
            assertEquals(caAnagrafica.getCap(), acAnagrafica.getCap());
            
            if (ancodice.equals("0000020142") 
               || ancodice.equals("0000003649")
               || ancodice.equals("0000022428")
               || ancodice.equals("0000016527")
               || ancodice.equals("0000011988")
               || ancodice.equals("0000063155")
               || ancodice.equals("0000007085")
               || ancodice.equals("0000016818")
               || ancodice.equals("0000003551")
               || ancodice.equals("0000008250")
               || ancodice.equals("0000040414")
               || ancodice.equals("0000016185")
               || ancodice.equals("0000067648")
               || ancodice.equals("0000017474")
               || ancodice.equals("0000023556")
               || ancodice.equals("0000004417")
               || ancodice.equals("0000016407")
               || ancodice.equals("0000073184")
               || ancodice.equals("0000013534")
               || ancodice.equals("0000063811")
               || ancodice.equals("0000014742")
               || ancodice.equals("0000018603")
               || ancodice.equals("0000006535")
               ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                caAnagrafica.setIndirizzo(acAnagrafica.getIndirizzo());
            }
            if (ancodice.equals("0000061880") 
            || ancodice.equals("0000066055")
            || ancodice.equals("0000005008")
            || ancodice.equals("0000012438")
            || ancodice.equals("0000006605")
            || ancodice.equals("0000020992")
               ) {
                System.out.println(ancodice+"--->"+i+"--->Updated acAnagrafica");
                acAnagrafica.setIndirizzo(caAnagrafica.getIndirizzo());
            }
            assertEquals(caAnagrafica.getIndirizzo(), acAnagrafica.getIndirizzo());
            
            if (ancodice.equals("0000069501") 
                    || ancodice.equals("0000022252")
                    || ancodice.equals("0000072596")) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                acAnagrafica.setIndirizzoSecondaRiga(caAnagrafica.getIndirizzoSecondaRiga());
            }
            assertEquals(caAnagrafica.getIndirizzoSecondaRiga(), acAnagrafica.getIndirizzoSecondaRiga());
        }
    }
    
}
