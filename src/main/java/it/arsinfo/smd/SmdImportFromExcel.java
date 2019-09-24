package it.arsinfo.smd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;

public class SmdImportFromExcel {

    public static final String ABBONATI_ESTERO = "data/ABBONATIESTERO2020.xls";
    public static final String ELENCO_ABBONATI = "data/ELENCOABBONATI2020-060919.xls";
    public static final String ARCHIVIO_CLIENTI = "data/ARCHIVIOCLIENTI-11092019.xls";
    public static final String CAMPAGNA_2020 = "data/CA2020COMPLETA.xls";
    public static final String BENEFICIARI_2020 = "data/BENEFICIARI2020.xls";
    public static final String ABBONATI_ITA_ESTERO = "data/ABBONATITALIABENEFESTERO-10092019.xls";
    public static AreaSpedizione getAreaSpedizione(String annazion) {
        if (annazion.equals("RSM") || annazion.equals("ITA") || annazion.equals("CVC")) {
                return AreaSpedizione.Italia;
        } else if (annazion.equals("GBR") || annazion.equals("FRA")
                    || annazion.equals("ROM") || annazion.equals("CZE")
                    || annazion.equals("HRV") || annazion.equals("LTU")
                    || annazion.equals("LTU") || annazion.equals("NLD")
                    || annazion.equals("LUX") || annazion.equals("CHE")
                    || annazion.equals("POL") || annazion.equals("ALB")
                    || annazion.equals("DEU") || annazion.equals("ESP")
                    || annazion.equals("CYP") || annazion.equals("BEL")
                    || annazion.equals("SVN") || annazion.equals("IRL")
                    || annazion.equals("ISR") || annazion.equals("PRT")
                    || annazion.equals("SYR") || annazion.equals("EGY")
                    || annazion.equals("TUR") || annazion.equals("LBY")
                    || annazion.equals("GRC")) {
            return AreaSpedizione.EuropaBacinoMediterraneo;
        } 
        return AreaSpedizione.AmericaAfricaAsia;

    }
    public static String getCapFromAnlocali(String anlocali) {
        return anlocali.substring(0,5);
    }

    public static String getCittaFromAnlocali(String anlocali) {
        if (anlocali.contains("(")) {
            return anlocali.substring(6,anlocali.indexOf("(")-1);
        }
        return anlocali.substring(6);
    }

    public static String getProvinciaFromAnlocali(String anlocali) {
        if (anlocali.contains("(") && anlocali.contains(")")) {
            return anlocali.substring(anlocali.indexOf("(")+1,anlocali.indexOf(")"));
        }
        if (anlocali.contains("(")) {
            return anlocali.substring(anlocali.indexOf("(")+1);
        }
        return "";
    }

    public static Provincia getProvincia(String sigla) {
        try {
            return Provincia.valueOf(sigla);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return Provincia.ND;
    }

    public static Anagrafica getAnagraficaByAncodcon(String pncodcon) throws UnsupportedOperationException {
        Anagrafica a = new Anagrafica();
        if (pncodcon.startsWith("E")) {
            a.setCodeLineBase("10000" + pncodcon.substring(1));
        } else {
            a.setCodeLineBase("0000" + pncodcon);
        }
        String codeLine = Abbonamento.generaCodeLine(Anno.ANNO2019,
                                                     a);
        if (!Abbonamento.checkCodeLine(codeLine)) {
            System.err.println("----- codeLine invalid ------");
            System.err.println("ANCODICE: " + pncodcon);
            System.err.println("------------------------------");
            System.out.println();
            throw new UnsupportedOperationException();
        }
        return a;
    }
    
    public static void populateAnagraficaEstero(Anagrafica a, String ancodice, String destitolo,String andescri, String andescr2, String anindiri, String anindir1,
            String anlocali,String annazion) {
        a.setTitolo(TitoloAnagrafica.getByIntestazione(destitolo));        
        if (a.getTitolo() == TitoloAnagrafica.Nessuno) {
            System.err.println("-----Anagrafica Titolo error------");
            System.err.println(a.getCodeLineBase());
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }
        a.setDenominazione(andescri);
        a.setNome(andescr2);
        a.setIndirizzo(anindiri);
        a.setCitta(anlocali);
        a.setDiocesi(Diocesi.DIOCESI000);
        a.setPaese(Paese.getBySigla(annazion));
        if (a.getPaese() == Paese.ND) {
            System.err.println("-----Paese non Definito------");
            System.err.println(ancodice);
            System.err.println(andescri);
            System.err.println(anindiri);
            System.err.println(anlocali);
            System.err.println(annazion);
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }
        a.setAreaSpedizione(getAreaSpedizione(annazion));
    }
        
    public static void populateAnagraficaCampagna(Anagrafica a, String destitolo,
            String andescri, String andescr2, String anindiri, String anindir1,
            String anlocali) throws UnsupportedOperationException {

        a.setTitolo(TitoloAnagrafica.getByIntestazione(destitolo));        
        
        if (a.getTitolo() == TitoloAnagrafica.Nessuno) {
            System.err.println("-----Anagrafica Titolo error------");
            System.err.println(a.getCodeLineBase());
            System.err.println(a.getCitta());
            System.err.println(a.getCap());
            System.err.println(a.getProvincia());
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }

        a.setDenominazione(andescri);
        a.setNome(andescr2);
        
        a.setCitta(getCittaFromAnlocali(anlocali));
        a.setCap(getCapFromAnlocali(anlocali));
        a.setProvincia(getProvincia(getProvinciaFromAnlocali(anlocali)));
        
        try {
            Integer.getInteger(a.getCap());
        } catch (Exception e) {
            System.err.println("-----Anagrafica CAP error------");
            System.err.println(a.getCodeLineBase());
            System.err.println(a.getCitta());
            System.err.println(a.getCap());
            System.err.println(a.getProvincia());
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }

        
        a.setIndirizzo(anindiri);
        if (!anindir1.equals("")) {
            if (a.getCodeLineBase().equals("00000000012956")) {
                a.setIndirizzoSecondaRiga(null);
            } else if (a.getCodeLineBase().equals("00000000066055")) {
                    a.setIndirizzo("FRAZ.VIGHIZZOLO - VIA S. GIOVANNI 255");
            } else if (a.getCodeLineBase().equals("00000000020992")) {
                a.setIndirizzo("LOC.S.PIETRO DI BARBOZZA-STR.CHIESA 2");
            } else if (a.getCodeLineBase().equals("00000000061880")) {
                a.setIndirizzo("LOC.MAGLIO DI SOPRA - VIA MARZOTTO 2");
            } else {
                a.setIndirizzoSecondaRiga(anindir1);
            }
            System.out.println("-----Fixed ANINDIR1------");
            System.out.println(a.getCodeLineBase());
            System.out.println("ANINDIRI: " + anindiri);
            System.out.println("ANINDIR1: " + anindir1);
            System.out.println("Indirizzo1: " + a.getIndirizzo());
            System.out.println("Indirizzo2: " + a.getIndirizzoSecondaRiga());
            System.out.println("------------------------------");
            System.out.println();
        }
                       
        if (a.getProvincia() == Provincia.ND) {
            if (a.getCodeLineBase().equals("00000000015153")) {
                a.setProvincia(Provincia.RM);
                System.out.println("-----Anagrafica Provincia RM------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();
            } else if (a.getCodeLineBase().equals("00000000070340")) {
                a.setProvincia(Provincia.TV);
                System.out.println("-----Anagrafica Provincia TV------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();
            } else if (a.getCap().equals("87020")) {
                a.setProvincia(Provincia.CS);
                System.out.println("-----Anagrafica Provincia CS------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("31048")) {
                a.setProvincia(Provincia.TV);
                System.out.println("-----Anagrafica Provincia TV------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("35010")) {
                a.setProvincia(Provincia.PD);
                System.out.println("-----Anagrafica Provincia PD------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("41033")) {
                a.setProvincia(Provincia.MO);
                System.out.println("-----Anagrafica Provincia MO------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("47893")) {
                a.setPaese(Paese.SM);
                a.setCitta("BORGO MAGGIORE");
                a.setDiocesi(Diocesi.DIOCESI175);
                System.out.println("-----Anagrafica Paese San Marino------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getPaese().getNome());
                System.out.println(a.getDiocesi().getDetails());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("50038")) {
                a.setProvincia(Provincia.FI);
                System.out.println("-----Anagrafica Provincia FI------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("54028")) {
                a.setProvincia(Provincia.MS);
                System.out.println("-----Anagrafica Provincia MS------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("56035")) {
                a.setProvincia(Provincia.PI);
                System.out.println("-----Anagrafica Provincia PI------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();
            } else if (a.getCap().equals("87064")) {
                a.setProvincia(Provincia.CS);
                System.out.println("-----Anagrafica Provincia CS------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();
            } else if (a.getCap().equals("60012")) {
                a.setProvincia(Provincia.AN);
                a.setIndirizzo(a.getIndirizzo()+ " FRAZ.BRUGNETTO");
                System.out.println("-----Anagrafica Provincia AN------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getIndirizzo());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else if (a.getCap().equals("80061")) {
                a.setProvincia(Provincia.NA);
                System.out.println("-----Anagrafica Provincia NA------");
                System.out.println(a.getCodeLineBase());
                System.out.println(a.getIndirizzo());
                System.out.println(a.getCitta());
                System.out.println(a.getCap());
                System.out.println(a.getProvincia());
                System.out.println("------------------------------");
                System.out.println();                
            } else {
                System.err.println("-----Anagrafica Provincia error------");
                System.err.println(a.getCodeLineBase());
                System.err.println(a.getCitta());
                System.err.println(a.getCap());
                System.err.println(a.getProvincia());
                System.err.println("------------------------------");
                System.err.println();
                throw new UnsupportedOperationException();
            }
        }

    }
    
    private Anagrafica processRowCampagna(Row row, String pncodcon, DataFormatter dataFormatter) throws UnsupportedOperationException {

        String destitolo = dataFormatter.formatCellValue(row.getCell(1));
        String andescri = dataFormatter.formatCellValue(row.getCell(2));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(3));
        String anindiri = dataFormatter.formatCellValue(row.getCell(4));
        String anindir1 = dataFormatter.formatCellValue(row.getCell(5));
        String anlocali = dataFormatter.formatCellValue(row.getCell(6));
        
        // Check for Abbonmento and not anagrafica
        String pndessup = dataFormatter.formatCellValue(row.getCell(7));
        if (pncodcon.equals("0000004967")) {
            pndessup="000000020000730517";
            System.out.println("----- codeLine fix ------");
            System.out.println("ANCODICE: " + pncodcon);
            System.out.println("codeLine: " + pndessup);
            System.out.println("------------------------------");
            System.out.println();
        } else if (pncodcon.equals("0000048374")) {
            pndessup="000000020000730416";
            System.out.println("----- codeLine fix ------");
            System.out.println("ANCODICE: " + pncodcon);
            System.out.println("codeLine: " + pndessup);
            System.out.println("------------------------------");
            System.out.println();
        }
        if (!Abbonamento.checkCodeLine(pndessup)) {
            System.err.println("----- codeLine invalid ------");
            System.err.println("ANCODICE: " + pncodcon);
            System.err.println("ANTITOLO: " + destitolo);
            System.err.println("ANDESCRI: " + andescri);
            System.err.println("ANDESCR2: " + andescr2);
            System.err.println("ANINDIRI: " + anindiri);
            System.err.println("ANINDIR1: " + anindir1);
            System.err.println("ANLOCALI: " + anlocali);
            System.err.println("PNDESSUP: " + pndessup);
            System.err.println("------------------------------");
            System.out.println();
            throw new UnsupportedOperationException();
        }
        Anagrafica anagrafica = getAnagraficaByAncodcon(pncodcon);
        
        populateAnagraficaCampagna(anagrafica, destitolo, andescri, andescr2, anindiri, anindir1, anlocali);
        

        String msg =      dataFormatter.formatCellValue(row.getCell(8));
        String totmsg =   dataFormatter.formatCellValue(row.getCell(9));
        
        String blocchetti = dataFormatter.formatCellValue(row.getCell(10));
        String totblocchetti = dataFormatter.formatCellValue(row.getCell(11));

        String manifesti =     dataFormatter.formatCellValue(row.getCell(12));
        String totmani =       dataFormatter.formatCellValue(row.getCell(13));
        
        String lodare =        dataFormatter.formatCellValue(row.getCell(14));
        String totlodare =     dataFormatter.formatCellValue(row.getCell(15));

        String totimp =        dataFormatter.formatCellValue(row.getCell(16));
        
        String scopro =        dataFormatter.formatCellValue(row.getCell(17));
        if (!scopro.equals("")) {
            System.err.println("scopro mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        
        String totpro =        dataFormatter.formatCellValue(row.getCell(18));
        if (!totpro.equals("")) {
            System.err.println("totpro mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }

        String anno =          dataFormatter.formatCellValue(row.getCell(19));
        String test_spese =    dataFormatter.formatCellValue(row.getCell(20));
        String test_saldo =    dataFormatter.formatCellValue(row.getCell(21));
        String totabbo    =    dataFormatter.formatCellValue(row.getCell(22));
        String testo_promo=    dataFormatter.formatCellValue(row.getCell(23));
        if (!testo_promo.equals("")) {
            System.err.println("testo_promo mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }

        String totpromo1  =    dataFormatter.formatCellValue(row.getCell(24));
        if (!totpromo1.equals("")) {
            System.err.println("totpromo1 mismatch: " + pncodcon);
            throw new UnsupportedOperationException();

        }

        String pncodcon1 = dataFormatter.formatCellValue(row.getCell(25));
        if (!pncodcon.equals(pncodcon1)) {
            System.err.println("pncodcon mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String destitolo1 = dataFormatter.formatCellValue(row.getCell(26));
        if (!destitolo.equals(destitolo1)) {
            System.err.println("destitolo mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String andescri1 = dataFormatter.formatCellValue(row.getCell(27));
        if (!andescri.equals(andescri1)) {
            System.err.println("andescri mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String andescr21 = dataFormatter.formatCellValue(row.getCell(28));
        if (!andescr2.equals(andescr21)) {
            System.err.println("andescri2 mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String anindiri1 = dataFormatter.formatCellValue(row.getCell(29));
        if (!anindiri.equals(anindiri1)) {
            System.err.println("anindiri mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String vuoto2 = dataFormatter.formatCellValue(row.getCell(30));
        if (!vuoto2.equals("")) {
            System.err.println("vuoto2 mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String anlocali1 = dataFormatter.formatCellValue(row.getCell(31));
        if (!anlocali.equals(anlocali1)) {
            System.err.println("anlocali mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String pndessup1 = dataFormatter.formatCellValue(row.getCell(32));
        if (!pndessup.equals(pndessup1) && !pndessup1.equals("")) {
            System.err.println("pndessup mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String anno1 =          dataFormatter.formatCellValue(row.getCell(33));
        if (!anno.equals(anno1)) {
            System.err.println("anno mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String test_spes1 =    dataFormatter.formatCellValue(row.getCell(34));
        if (!test_spese.equals(test_spes1)) {
            System.err.println("test_spese mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String test_sald1 =    dataFormatter.formatCellValue(row.getCell(35));
        if (!test_saldo.equals(test_sald1)) {
            System.err.println("test_saldo mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String totabbo1    =    dataFormatter.formatCellValue(row.getCell(36));
        if (!totabbo.equals(totabbo1)) {
            System.err.println("totabbo mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String testo_prm1=    dataFormatter.formatCellValue(row.getCell(37));
        if (!testo_prm1.equals("")) {
            System.err.println("testo_prm1 mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        String totpromo2  =    dataFormatter.formatCellValue(row.getCell(38));
        if (!totpromo2.equals("")) {
            System.err.println("totpromo2 mismatch: " + pncodcon);
            throw new UnsupportedOperationException();
        }
        
        return anagrafica;

    }

    public Map<String,Anagrafica> importCampagna2020() throws IOException {        
        DataFormatter dataFormatter = new DataFormatter();

        File ca2020 = new File(CAMPAGNA_2020);
        Workbook wbca2020 = new HSSFWorkbook(new FileInputStream(ca2020));
        
        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbca2020.getSheetAt(0)) {
            String pncodcon = dataFormatter.formatCellValue(row.getCell(0));
            if (pncodcon.endsWith("pncodcon")) {
                continue;
            }
            try {
                anagraficaMap.put(pncodcon, processRowCampagna(row,pncodcon,dataFormatter));                
            } catch (UnsupportedOperationException e) {
                errors.add(pncodcon);
                continue;
            }
        }
        System.out.println("Campagna 2020 -  Errori Trovati: "
                + errors.size());
        System.out.println("Campagna 2020 -  Clienti Trovati: "
                + anagraficaMap.size());
        return anagraficaMap;
    }
        
    public Map<String,Anagrafica> importArchivioClienti() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ARCHIVIO_CLIENTI);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String picoddio = dataFormatter.formatCellValue(row.getCell(0));
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            if (picoddio.equalsIgnoreCase("PICODDIO")) {
                continue;
            }
            try {
                anagraficaMap.put(ancodice, processRowAnagrafica(row, picoddio,ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        System.out.println("Archivio Clienti Errori Trovati: "
                + errors.size());
        System.out.println("Archivio Clienti Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public Map<String,Anagrafica> importElencoAbbonati() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ELENCO_ABBONATI);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String picoddio = dataFormatter.formatCellValue(row.getCell(0));
            String ancodice = dataFormatter.formatCellValue(row.getCell(1));
            if (picoddio.equalsIgnoreCase("PICODDIO")) {
                continue;
            }
            try {
                anagraficaMap.put(ancodice, processRowAnagrafica(row, picoddio,ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        System.out.println("Elenco Abbonanti Errori Trovati: "
                + errors.size());
        System.out.println("Elenco Abbonati Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }
    
    public Map<String,Anagrafica> importBeneficiari() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(BENEFICIARI_2020);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodiceI = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodiceI.equalsIgnoreCase("codice Intestatario")) {
                continue;
            }
            if (ancodiceI.trim().equalsIgnoreCase("")) {
                continue;
            }
            String destinatI = dataFormatter.formatCellValue(row.getCell(1));
            String ancodiceD = dataFormatter.formatCellValue(row.getCell(2));
            String destinatD = dataFormatter.formatCellValue(row.getCell(3));
            String messaggio = dataFormatter.formatCellValue(row.getCell(4));
            String blocchetti = dataFormatter.formatCellValue(row.getCell(5));
            String lodare = dataFormatter.formatCellValue(row.getCell(6));
            String manifesti = dataFormatter.formatCellValue(row.getCell(7));

            try {
                Anagrafica destinatario = getAnagraficaByAncodcon(ancodiceD);
                destinatario.setDenominazione(destinatD);
                anagraficaMap.put(ancodiceD, destinatario);
            } catch (UnsupportedOperationException e) {
                errors.add(ancodiceD);
                continue;
            }
        }

        System.out.println("Beneficiari Errori Trovati: "
                + errors.size());
        System.out.println("Beneficiari Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public Map<String,Anagrafica> importIntestatari() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(BENEFICIARI_2020);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodiceI = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodiceI.equalsIgnoreCase("codice Intestatario")) {
                continue;
            }
            if (ancodiceI.trim().equalsIgnoreCase("")) {
                continue;
            }
            String destinatI = dataFormatter.formatCellValue(row.getCell(1));
            String ancodiceD = dataFormatter.formatCellValue(row.getCell(2));
            String destinatD = dataFormatter.formatCellValue(row.getCell(3));
            String messaggio = dataFormatter.formatCellValue(row.getCell(4));
            String blocchetti = dataFormatter.formatCellValue(row.getCell(5));
            String lodare = dataFormatter.formatCellValue(row.getCell(6));
            String manifesti = dataFormatter.formatCellValue(row.getCell(7));

            try {
                Anagrafica intestatario = getAnagraficaByAncodcon(ancodiceI);
                intestatario.setDenominazione(destinatI);
                anagraficaMap.put(ancodiceI, intestatario);
            } catch (UnsupportedOperationException e) {
                errors.add(ancodiceI);
                continue;
            }
        }

        System.out.println("Intestatari Errori Trovati: "
                + errors.size());
        System.out.println("Intestatari Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public Map<String,Anagrafica> importItaEsteroIntestatari() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ABBONATI_ITA_ESTERO);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodiceI = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodiceI.equals("COD.")) {
                continue;
            }
            if (ancodiceI.trim().equalsIgnoreCase("")) {
                break;
            }
            ancodiceI="00000"+ancodiceI;
            String destinatI = dataFormatter.formatCellValue(row.getCell(1));
            String ancodiceD = dataFormatter.formatCellValue(row.getCell(2));
            
            String destinatD = dataFormatter.formatCellValue(row.getCell(3));
            String descri = dataFormatter.formatCellValue(row.getCell(4));
            String indiri = dataFormatter.formatCellValue(row.getCell(5));
            String locali = dataFormatter.formatCellValue(row.getCell(6));
            String nazion = dataFormatter.formatCellValue(row.getCell(7));
            String testat = dataFormatter.formatCellValue(row.getCell(8));
            String quanti = dataFormatter.formatCellValue(row.getCell(9));
            String impori = dataFormatter.formatCellValue(row.getCell(10));
            String spese = dataFormatter.formatCellValue(row.getCell(11));

            try {
                Anagrafica intestatario = getAnagraficaByAncodcon(ancodiceI);
                intestatario.setDenominazione(destinatI);
                anagraficaMap.put(ancodiceI, intestatario);
            } catch (UnsupportedOperationException e) {
                errors.add(ancodiceI);
                continue;
            }
        }

        System.out.println("Intestatari ITA Estero Errori Trovati: "
                + errors.size());
        System.out.println("Intestatari ITA Estero Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    public Map<String,Anagrafica> importItaEsteroBeneficiari() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ABBONATI_ITA_ESTERO);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodiceI = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodiceI.equals("COD.")) {
                continue;
            }
            if (ancodiceI.trim().equalsIgnoreCase("")) {
                break;
            }
            String destinatI = dataFormatter.formatCellValue(row.getCell(1));
            String ancodiceD = "00000"+dataFormatter.formatCellValue(row.getCell(2));
            String destinatD = dataFormatter.formatCellValue(row.getCell(3));
            String descri = dataFormatter.formatCellValue(row.getCell(4));
            String indiri = dataFormatter.formatCellValue(row.getCell(5));
            String locali = dataFormatter.formatCellValue(row.getCell(6));
            String nazion = dataFormatter.formatCellValue(row.getCell(7));
            String testat = dataFormatter.formatCellValue(row.getCell(8));
            String quanti = dataFormatter.formatCellValue(row.getCell(9));
            String impori = dataFormatter.formatCellValue(row.getCell(10));
            String spese = dataFormatter.formatCellValue(row.getCell(11));

            try {
                Anagrafica beneficiario = getAnagraficaByAncodcon(ancodiceD);
                beneficiario.setDenominazione(destinatI);
                anagraficaMap.put(ancodiceD, beneficiario);
            } catch (UnsupportedOperationException e) {
                errors.add(ancodiceD);
                continue;
            }
        }

        System.out.println("Beneficiari ITA Estero Errori Trovati: "
                + errors.size());
        System.out.println("Beneficiari ITA Estero Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }

    
    public Map<String,Anagrafica> importAbbonatiEstero() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ac = new File(ABBONATI_ESTERO);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        Map<String, Anagrafica> anagraficaMap = new HashMap<>();
        for (Row row : wbac.getSheetAt(0)) {
            String ancodice = dataFormatter.formatCellValue(row.getCell(0));
            if (ancodice.equalsIgnoreCase("ancodice")) {
                continue;
            }
            try {
                anagraficaMap.put(ancodice, processRowAbbonatiEstero(row, ancodice, dataFormatter));
            } catch (UnsupportedOperationException e) {
                errors.add(ancodice);
                continue;
            }
        }

        System.out.println("Abbonanti Estero Errori Trovati: "
                + errors.size());
        System.out.println("Abbonati Estero Trovati: "
                + anagraficaMap.size());
        
        return anagraficaMap;
    }
    
    public Anagrafica processRowAbbonatiEstero(Row row, String ancodice,DataFormatter dataFormatter) throws UnsupportedOperationException {
        
        String andescri = dataFormatter.formatCellValue(row.getCell(1));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(2));
        String anindiri = dataFormatter.formatCellValue(row.getCell(3));
        String anindir1 = dataFormatter.formatCellValue(row.getCell(4));
        String anlocali = dataFormatter.formatCellValue(row.getCell(6));
        String annazion = dataFormatter.formatCellValue(row.getCell(8));        
        String destitolo = dataFormatter.formatCellValue(row.getCell(10));

        Anagrafica anagrafica = getAnagraficaByAncodcon(ancodice);
        
        populateAnagraficaEstero(anagrafica, ancodice,destitolo, andescri, andescr2, anindiri, anindir1, anlocali, annazion);
        return anagrafica;        
    }

    public Anagrafica processRowAnagrafica(Row row, String picoddio,String ancodice,DataFormatter dataFormatter) throws UnsupportedOperationException {
                    
        String andescri = dataFormatter.formatCellValue(row.getCell(2));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(3));
        String anindiri = dataFormatter.formatCellValue(row.getCell(4));
        String anlocali = dataFormatter.formatCellValue(row.getCell(5));
        String anprovin = dataFormatter.formatCellValue(row.getCell(6));
        String an___cap = dataFormatter.formatCellValue(row.getCell(7));
        String annazion = dataFormatter.formatCellValue(row.getCell(8));
        String ancodfis = dataFormatter.formatCellValue(row.getCell(9));
        String anpariva = dataFormatter.formatCellValue(row.getCell(10));
        String antelefo = dataFormatter.formatCellValue(row.getCell(11));
        //String antipcon = dataFormatter.formatCellValue(row.getCell(12));
        String annumcel = dataFormatter.formatCellValue(row.getCell(13));
        String an_email = dataFormatter.formatCellValue(row.getCell(14));
        //String abcodese = dataFormatter.formatCellValue(row.getCell(15));
        
        Anagrafica anagrafica = getAnagraficaByAncodcon(ancodice);
        anagrafica.setDenominazione(andescri);
        anagrafica.setNome(andescr2);
        anagrafica.setIndirizzo(anindiri);
        anagrafica.setCitta(anlocali);
        if (!an___cap.trim().equals("")) {
            anagrafica.setCap(an___cap);
        }
        anagrafica.setProvincia(getProvincia(anprovin));
        anagrafica.setCodfis(ancodfis);
        anagrafica.setPiva(anpariva);
        anagrafica.setTelefono(antelefo);
        anagrafica.setCellulare(annumcel);
        anagrafica.setEmail(an_email);    
        anagrafica.setPaese(Paese.getBySigla(annazion));
        anagrafica.setAreaSpedizione(getAreaSpedizione(annazion));
        if (!checkDiocesi(anagrafica, 
                          annazion, 
                          ancodice, 
                          andescri, 
                          anindiri, 
                          anlocali,
                          anprovin,
                          picoddio)) {
                throw new UnsupportedOperationException();
        }

        if (anagrafica.getPaese() == Paese.ND) {
            System.err.println("-----Paese non Definito------");
            System.err.println(ancodice);
            System.err.println(andescri);
            System.err.println(anindiri);
            System.err.println(anlocali);
            System.err.println(anprovin);
            System.err.println(annazion);
            System.err.println(picoddio);
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }
        
        if (annazion.equals("ITA") && anagrafica.getDiocesi() == Diocesi.DIOCESI000) {
            System.err.println("-----Nazione ITA Errata ------");
            System.err.println("ANCODICE: " + ancodice);
            System.err.println("ANDESCRI: " + andescri);
            System.err.println("ANINDIRI: " + anindiri);
            System.err.println("ANLOCALI: " + anlocali);
            System.err.println("ANPROVIN: " + anprovin);
            System.err.println("ANNAZION: " + annazion);
            System.err.println("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            System.err.println("------------------------------");
            System.err.println();
            throw new UnsupportedOperationException();
        }
        
        if (!checkProvincia(anagrafica, annazion, 
                          ancodice, 
                          andescri, 
                          anindiri, 
                          anlocali,
                          anprovin,
                          picoddio)) {
                throw new UnsupportedOperationException();
        }
        
        return anagrafica;
        
    }
        
    private static boolean checkDiocesi(Anagrafica anagrafica, 
            String annazion, 
            String ancodice, 
            String andescri, 
            String anindiri, 
            String anlocali,
            String anprovin,
            String picoddio) {
        if (anagrafica.getDiocesi() == Diocesi.DIOCESISTD) {
            anagrafica.setDiocesi(Diocesi.getDiocesiByCodice(picoddio));
        }
        
        if (ancodice.equals("0000062854")) {
            anagrafica.setDiocesi(Diocesi.DIOCESI193);
            System.out.println("-----Diocesi 193------");
            System.out.println("ANCODICE: " + ancodice);
            System.out.println("Provincia: "
                    + anagrafica.getProvincia().getNome());
            System.out.println("ANNAZION: " + annazion);
            System.out.println("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            System.out.println("------------------------------");
            System.out.println();
        } else if (ancodice.equals("0000072701")) {
            anagrafica.setDiocesi(Diocesi.DIOCESI194);
            System.out.println("-----Diocesi 194------");
            System.out.println("ANCODICE: " + ancodice);
            System.out.println("ANDESCRI: " + andescri);
            System.out.println("ANINDIRI: " + anindiri);
            System.out.println("ANLOCALI: " + anlocali);
            System.out.println("ANPROVIN: " + anprovin);
            System.out.println("ANNAZION: " + annazion);
            System.out.println("Diocesi: "
                    + anagrafica.getDiocesi().getDetails());
            System.out.println("------------------------------");
            System.out.println();
        }

        if (anagrafica.getDiocesi() == Diocesi.DIOCESISTD) {
            if (annazion.equals("RSM")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI175);
                System.out.println("-----Diocesi RSM------");
                System.out.println("ANCODICE: " + ancodice);
                System.out.println("ANDESCRI: " + andescri);
                System.out.println("ANINDIRI: " + anindiri);
                System.out.println("ANLOCALI: " + anlocali);
                System.out.println("ANPROVIN: " + anprovin);
                System.out.println("ANNAZION: " + annazion);
                System.out.println("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                System.out.println("------------------------------");
                System.out.println();
            } else if (!annazion.equals("ITA")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI000);
                System.out.println("-----Diocesi ESTERO------");
                System.out.println("ANCODICE: " + ancodice);
                System.out.println("ANDESCRI: " + andescri);
                System.out.println("ANINDIRI: " + anindiri);
                System.out.println("ANLOCALI: " + anlocali);
                System.out.println("ANPROVIN: " + anprovin);
                System.out.println("ANNAZION: " + annazion);
                System.out.println("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                System.out.println("------------------------------");
                System.out.println();
            } else if (anlocali.equals("ROMA")) {
                anagrafica.setDiocesi(Diocesi.DIOCESI168);
                System.out.println("-----Diocesi ROMA------");
                System.out.println("ANCODICE: " + ancodice);
                System.out.println("ANDESCRI: " + andescri);
                System.out.println("ANINDIRI: " + anindiri);
                System.out.println("ANLOCALI: " + anlocali);
                System.out.println("ANPROVIN: " + anprovin);
                System.out.println("ANNAZION: " + annazion);
                System.out.println("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                System.out.println("------------------------------");
                System.out.println();
            } else {
                System.err.println("-----Diocesi non Definita------");
                System.err.println("ANCODICE: " + ancodice);
                System.err.println("ANDESCRI: " + andescri);
                System.err.println("ANINDIRI: " + anindiri);
                System.err.println("ANLOCALI: " + anlocali);
                System.err.println("ANPROVIN: " + anprovin);
                System.err.println("ANNAZION: " + annazion);
                System.err.println("PICODDIO: " + picoddio);
                System.err.println("------------------------------");
                System.err.println();
                return false;
            }
        }
        return true;
    }

    private static boolean checkProvincia( Anagrafica anagrafica, 
    String annazion, 
    String ancodice, 
    String andescri, 
    String anindiri, 
    String anlocali,
    String anprovin,
    String picoddio) {
        if (annazion.equals("ITA") && anagrafica.getProvincia() == Provincia.ND) {
            if (anlocali.equals("ROMA")) {
                anagrafica.setProvincia(Provincia.RM);
            } else if (anlocali.equals("MESSINA")) {
                anagrafica.setProvincia(Provincia.ME);
            } else if (anlocali.equals("ALESSANDRIA")) {
                anagrafica.setProvincia(Provincia.AL);
            } else if (anlocali.equals("SAVONA")) {
                anagrafica.setProvincia(Provincia.SV);
            } else if (anlocali.equals("PERUGIA")) {
                anagrafica.setProvincia(Provincia.PG);
            } else if (anlocali.equals("REGGIO CALABRIA")) {
                anagrafica.setProvincia(Provincia.RC);
            } else if (anlocali.equals("PESARO")) {
                anagrafica.setProvincia(Provincia.PU);
            } else if (anlocali.equals("PARMA")) {
                anagrafica.setProvincia(Provincia.PR);
            } else if (anlocali.equals("MANTOVA")) {
                anagrafica.setProvincia(Provincia.MN);
            } else if (anlocali.equals("TORINO")) {
                anagrafica.setProvincia(Provincia.TO);
            } else if (anlocali.equals("BOLOGNA")) {
                anagrafica.setProvincia(Provincia.BO);
            } else if (anlocali.equals("MILANO")) {
                anagrafica.setProvincia(Provincia.MI);
            } else if (anlocali.equals("NAPOLI")) {
                anagrafica.setProvincia(Provincia.NA);
            } else if (anlocali.equals("PADOVA")) {
                anagrafica.setProvincia(Provincia.PD);
            } else if (anlocali.equals("FIRENZE")) {
                anagrafica.setProvincia(Provincia.FI);
            } else if (anlocali.equals("CATANZARO")) {
                anagrafica.setProvincia(Provincia.CZ);
            } else if (anlocali.equals("GENOVA")) {
                anagrafica.setProvincia(Provincia.GE);
            } else if (anlocali.equals("LIVORNO")) {
                anagrafica.setProvincia(Provincia.LI);
            } else if (anlocali.equals("NUORO")) {
                anagrafica.setProvincia(Provincia.NU);
            } else if (anlocali.equals("MATERA")) {
                anagrafica.setProvincia(Provincia.MT);
            } else if (anlocali.equals("COSENZA")) {
                anagrafica.setProvincia(Provincia.CS);
            } else if (anlocali.equals("FERRARA")) {
                anagrafica.setProvincia(Provincia.FE);
            } else if (anlocali.equals("RAVENNA")) {
                anagrafica.setProvincia(Provincia.RA);
            } else if (anlocali.equals("TARANTO")) {
                anagrafica.setProvincia(Provincia.TA);
            } else if (anlocali.equals("TRIESTE")) {
                anagrafica.setProvincia(Provincia.TS);
            } else if (anlocali.equals("SIENA")) {
                anagrafica.setProvincia(Provincia.SI);
            } else if (anlocali.equals("BARI")) {
                anagrafica.setProvincia(Provincia.BA);
            } else if (anlocali.equals("COMO")) {
                anagrafica.setProvincia(Provincia.CO);
            } else if (anlocali.equals("PALERMO")) {
                anagrafica.setProvincia(Provincia.PA);
            } else if (anlocali.equals("VENEZIA")) {
                anagrafica.setProvincia(Provincia.VE);
            } else if (anlocali.equals("L'AQUILA")) {
                anagrafica.setProvincia(Provincia.AQ);
            } else if (anlocali.equals("CHIETI")) {
                anagrafica.setProvincia(Provincia.CH);
            } else if (anlocali.equals("PAVIA")) {
                anagrafica.setProvincia(Provincia.PV);
            } else if (anlocali.equals("SACRAMORA DI RIMINI")) {
                anagrafica.setProvincia(Provincia.RN);
                anagrafica.setCitta("RIMINI");
            } else if (anlocali.equals("GROSIO")) {
                anagrafica.setProvincia(Provincia.SO);
            } else if (anagrafica.getDiocesi() == Diocesi.DIOCESI175) {
                anagrafica.setPaese(Paese.SM);
                System.out.println("-----Paese RSM------");
                System.out.println("ANCODICE: " + ancodice);
                System.out.println("ANDESCRI: " + andescri);
                System.out.println("ANINDIRI: " + anindiri);
                System.out.println("ANLOCALI: " + anlocali);
                System.out.println("Paese: "
                        + anagrafica.getPaese().getNome());
                System.out.println("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                System.out.println("------------------------------");
                System.out.println();
            } else {
                System.err.println("-----Provincia non Definita------");
                System.err.println("ANCODICE: " + ancodice);
                System.err.println("ANDESCRI: " + andescri);
                System.err.println("ANINDIRI: " + anindiri);
                System.err.println("ANLOCALI: " + anlocali);
                System.err.println("ANPROVIN: " + anprovin);
                System.err.println("ANNAZION: " + annazion);
                System.err.println("Diocesi: "
                        + anagrafica.getDiocesi().getDetails());
                System.err.println("------------------------------");
                System.err.println();
                return false;
            }
        }
        return true;
   
    }
    public void fixAbbonatiEstero(Map<String, Anagrafica> acMap, Map<String, Anagrafica> aeMap) {
        int i=0;
        for (String ancodice : aeMap.keySet()) {
            i++;
            Anagrafica ae = aeMap.get(ancodice);
            Anagrafica ac = acMap.get(ancodice);
            ae.setDiocesi(ac.getDiocesi());
            ae.setCodfis(ac.getCodfis());
            ae.setPiva(ac.getPiva());
            ae.setTelefono(ac.getTelefono());
            ae.setCellulare(ac.getCellulare());
            ae.setEmail(ac.getEmail());    
     
            ac.setTitolo(ae.getTitolo());
        }
        
    }

    public void fixBeneficiari(Map<String, Anagrafica> acMap, Map<String, Anagrafica> eaMap,Map<String, Anagrafica> abMap) {
        for (String ancodice: abMap.keySet()) {
            if (eaMap.containsKey(ancodice)) {
                abMap.put(ancodice, eaMap.get(ancodice));
            } else if (acMap.containsKey(ancodice)) {
                abMap.put(ancodice, acMap.get(ancodice));
            } else {
                System.err.println(ancodice);
            }
        }
    }

    
    public void fixIntestatari(Map<String, Anagrafica> acMap, Map<String, Anagrafica> iaMap) {
        for (String ancodice: iaMap.keySet()) {
            Anagrafica ac = acMap.get(ancodice);
            iaMap.put(ancodice, ac);
        }
    }

    public void fixElencoAbbonatiCampagna(Map<String, Anagrafica> acMap, Map<String, Anagrafica> caMap) {
        int i=0;
        for (String ancodice : caMap.keySet()) {
            i++;
            Anagrafica ca = caMap.get(ancodice);
            Anagrafica ac = acMap.get(ancodice);
            
            ca.setDiocesi(ac.getDiocesi());
            ca.setCodfis(ac.getCodfis());
            ca.setPiva(ac.getPiva());
            ca.setTelefono(ac.getTelefono());
            ca.setCellulare(ac.getCellulare());
            ca.setEmail(ac.getEmail());    
     
            ac.setTitolo(ca.getTitolo());

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
                ca.setDenominazione(ac.getDenominazione());
                ca.setNome(ac.getNome());
            }
            
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
                ca.setNome(ac.getNome());
                ca.setCitta(ac.getCitta());
                ca.setCap(ac.getCap());
                ca.setIndirizzo(ac.getIndirizzo());
            }            
            if (ancodice.equals("0000013374") 
                ||ancodice.equals("0000064543") 
                ||ancodice.equals("0000015427") 
                ||ancodice.equals("0000011361") 
                ||ancodice.equals("0000074822") 
                ||ancodice.equals("0000011192") 
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                ca.setProvincia(ac.getProvincia());
                ca.setCitta(ac.getCitta());
                ca.setIndirizzo(ac.getIndirizzo());
                ca.setCap(ac.getCap());
            }
            
            if (ancodice.equals("0000063661")
                || ancodice.equals("0000011798")
                || ancodice.equals("0000065672")
                || ancodice.equals("0000017604")
                || ancodice.equals("0000017622")
                || ancodice.equals("0000017678")
                ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                ca.setCitta(ac.getCitta());
                ca.setIndirizzo(ac.getIndirizzo());
                ca.setCap(ac.getCap());
            }
            if (ancodice.equals("0000016209")
                || ancodice.equals("0000015153")
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated acAnagrafica");
                ac.setCitta(ca.getCitta());
                }

            if (ancodice.equals("0000020195")
                ||   ancodice.equals("0000069121") 
                ||   ancodice.equals("0000067234")
                    ) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                ca.setCap(ac.getCap());
                ca.setIndirizzo(ac.getIndirizzo());
            }
            
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
                ca.setIndirizzo(ac.getIndirizzo());
            }
            if (ancodice.equals("0000061880") 
            || ancodice.equals("0000066055")
            || ancodice.equals("0000005008")
            || ancodice.equals("0000012438")
            || ancodice.equals("0000006605")
            || ancodice.equals("0000020992")
               ) {
                System.out.println(ancodice+"--->"+i+"--->Updated acAnagrafica");
                ac.setIndirizzo(ca.getIndirizzo());
            }
            
            if (ancodice.equals("0000069501") 
                    || ancodice.equals("0000022252")
                    || ancodice.equals("0000072596")) {
                System.out.println(ancodice+"--->"+i+"--->Updated caAnagrafica");
                ac.setIndirizzoSecondaRiga(ca.getIndirizzoSecondaRiga());
            }
        }

    }

}
