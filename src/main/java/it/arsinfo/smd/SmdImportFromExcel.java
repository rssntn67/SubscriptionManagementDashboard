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

    public static final String ARCHIVIO_CLIENTI = "/Users/antonio/Documents/ADP/Dati/ARCHIVIOCLIENTI.xls";
    public static final String CA_2020 = "/Users/antonio/Documents/ADP/Dati/CA2020COMPLETA.xls";

    final Map<String, Anagrafica> campagnaUserMap = new HashMap<String, Anagrafica>();

    public static void main(String[] args) { 
        SmdImportFromExcel imp = new SmdImportFromExcel();
        try {
            imp.importCA2010Excelfile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }                
    }
    
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
            return anlocali.substring(6,anlocali.indexOf("("));
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
            System.err.println("----- codeLineBase invalid ------");
            System.err.println("ANCODICE: " + pncodcon);
            System.err.println("------------------------------");
            System.out.println();
            throw new UnsupportedOperationException();
        }
        return a;
    }
    
    public static void populateAnagraficaCampagna(Anagrafica a, String destitolo,
            String andescri, String andescr2, String anindiri, String anindir1,
            String anlocali) throws UnsupportedOperationException {
//FIXME
        a.setTitolo(TitoloAnagrafica.getByIntestazione(destitolo));
        a.setNome(andescri);
        a.setCognome(andescr2);
        String indirizzo = anindiri+anindir1;
        if (!anindir1.equals("")) {
            System.out.println("-----anindir1 ------");
            System.out.println("ANCODICE: " + a.getCodeLineBase());
            System.out.println("ANTITOLO: " + destitolo);
            System.out.println("ANDESCRI: " + andescri);
            System.out.println("ANDESCR2: " + andescr2);
            System.out.println("ANINDIRI: " + anindiri);
            System.out.println("ANINDIR1: " + anindir1);
            System.out.println("ANLOCALI: " + anlocali);
            System.out.println("------------------------------");
            if (a.getCodeLineBase().equals("00000000072596")) {
                
            }
        }
        a.setIndirizzo(indirizzo);
               
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
    
    private void processCARow(Row row, DataFormatter dataFormatter) throws UnsupportedOperationException {
        String pncodcon = dataFormatter.formatCellValue(row.getCell(0));
        if (pncodcon.endsWith("pncodcon")) {
            return;
        }
        if (campagnaUserMap.containsKey(pncodcon)) {
            System.err.println("duplicated: " + pncodcon);
            return;
        }
        String destitolo = dataFormatter.formatCellValue(row.getCell(1));
        String andescri = dataFormatter.formatCellValue(row.getCell(2));
        String andescr2 = dataFormatter.formatCellValue(row.getCell(3));
        String anindiri = dataFormatter.formatCellValue(row.getCell(4));
        String anindir1 = dataFormatter.formatCellValue(row.getCell(5));
        String anlocali = dataFormatter.formatCellValue(row.getCell(6));
        
        String pndessup = dataFormatter.formatCellValue(row.getCell(7));
        // Check for Abbonmento and not anagrafica
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
        Anagrafica a = getAnagraficaByAncodcon(pncodcon);
        
        populateAnagraficaCampagna(a, destitolo, andescri, andescr2, anindiri, anindir1, anlocali);
        

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
        if (!pndessup.equals(pndessup1)) {
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

        campagnaUserMap.put(pncodcon, a);

    }

    
    public void importCA2010Excelfile() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();

        File ca2020 = new File(CA_2020);
        Workbook wbca2020 = new HSSFWorkbook(new FileInputStream(ca2020));
        
        Set<String> caerrors = new HashSet<>();

        for (Row row : wbca2020.getSheetAt(0)) {
            try {
                processCARow(row,dataFormatter);                
            } catch (UnsupportedOperationException e) {
                String pncodcon = dataFormatter.formatCellValue(row.getCell(0));
                caerrors.add(pncodcon);
                continue;
            }
        }

        File ac = new File(ARCHIVIO_CLIENTI);
        Workbook wbac = new HSSFWorkbook(new FileInputStream(ac));

        Set<String> errors = new HashSet<>();
        for (Row row : wbac.getSheetAt(0)) {
            try {
                processRowArchivioClienti(row, dataFormatter);                
            } catch (UnsupportedOperationException e) {
                String pncodcon = dataFormatter.formatCellValue(row.getCell(0));
                errors.add(pncodcon);
                continue;
            }
        }
        System.out.println("Campagna 2020 -  Errori Trovati: "
                + caerrors.size());

        System.out.println("Archivio Clienti Errori Trovati: "
                + errors.size());
        
        System.out.println("Campagna 2020 -  Record Trovati: "
                + campagnaUserMap.size());        

    }

    public void processRowArchivioClienti(Row row, DataFormatter dataFormatter) throws UnsupportedOperationException {
        String picoddio = dataFormatter.formatCellValue(row.getCell(0));
        if (picoddio.equals("PICODDIO")) {
            return;
        }
        
            
        String ancodice = dataFormatter.formatCellValue(row.getCell(1));
        if (!campagnaUserMap.containsKey(ancodice)) {
            return;
        }
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
        String antipcon = dataFormatter.formatCellValue(row.getCell(12));
        String annumcel = dataFormatter.formatCellValue(row.getCell(13));
        String an_email = dataFormatter.formatCellValue(row.getCell(14));
        String abcodese = dataFormatter.formatCellValue(row.getCell(15));
        
        Anagrafica anagrafica = campagnaUserMap.get(ancodice);


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
        anagrafica.setCodfis(ancodfis);
        anagrafica.setPiva(anpariva);
        anagrafica.setTelefono(antelefo);
        anagrafica.setCellulare(annumcel);
        anagrafica.setEmail(an_email);        
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

    public Map<String, Anagrafica> getCampagnaUserMap() {
        return campagnaUserMap;
    }

}
