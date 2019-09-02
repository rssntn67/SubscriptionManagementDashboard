package it.arsinfo.smd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.entity.Anagrafica;


public class SmdImportFromExcel {

    public static final String ARCHIVIO_CLIENTI="/Users/antonio/Documents/ADP/Dati/ARCHIVIOCLIENTI.xls";
    
    public static Provincia getProvincia(String sigla) {
        try {
            return Provincia.valueOf(sigla);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return Provincia.ND;
    }
    public static void main(String[] args) throws IOException {
        File file = new File(ARCHIVIO_CLIENTI);
        Workbook workbook = new HSSFWorkbook(new FileInputStream(file));
        
     // Getting the Sheet at index zero
     Sheet sheet = workbook.getSheetAt(0);

     // Create a DataFormatter to format and get each cell's value as String
     DataFormatter dataFormatter = new DataFormatter();

     // 3. Or you can use Java 8 forEach loop with lambda
     Map<String,Anagrafica> userMap = new HashMap<String, Anagrafica>();
     int err = 0;

     for (Row row: sheet) {
         String picoddio = dataFormatter.formatCellValue(row.getCell(0));
         if (picoddio.equals("PICODDIO")) {
             continue;
         }
         String ancodice = dataFormatter.formatCellValue(row.getCell(1));
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
         Anagrafica anagrafica = new Anagrafica();
         anagrafica.setDiocesi(Diocesi.getDiocesiByCodice(picoddio));
         anagrafica.setProvincia(getProvincia(anprovin));
         anagrafica.setPaese(Paese.getBySigla(annazion));

         if (anagrafica.getDiocesi() == Diocesi.DIOCESISTD) {
             if (annazion.equals("RSM") ) {
                 anagrafica.setDiocesi(Diocesi.DIOCESI175);
                 /*
                 System.out.println("-----Diocesi RSM------");
                 System.out.println("ANCODICE: " + ancodice);
                 System.out.println("ANDESCRI: " + andescri);
                 System.out.println("ANINDIRI: " + anindiri);
                 System.out.println("ANLOCALI: " + anlocali);
                 System.out.println("ANPROVIN: " + anprovin);
                 System.out.println("ANNAZION: " + annazion);
                 System.out.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                 System.out.println("------------------------------");
                 System.out.println();
                 */
             } else if (!annazion.equals("ITA")) {
                 anagrafica.setDiocesi(Diocesi.DIOCESI000);
                 /*
                 System.out.println("-----Diocesi ESTERO------");
                 System.out.println("ANCODICE: " + ancodice);
                 System.out.println("ANDESCRI: " + andescri);
                 System.out.println("ANINDIRI: " + anindiri);
                 System.out.println("ANLOCALI: " + anlocali);
                 System.out.println("ANPROVIN: " + anprovin);
                 System.out.println("ANNAZION: " + annazion);
                 System.out.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                 System.out.println("------------------------------");
                 System.out.println();
                 */
             } else if (anlocali.equals("ROMA")) {
                 anagrafica.setDiocesi(Diocesi.DIOCESI168);
                 /*
                 System.out.println("-----Diocesi ROMA------");
                 System.out.println("ANCODICE: " + ancodice);
                 System.out.println("ANDESCRI: " + andescri);
                 System.out.println("ANINDIRI: " + anindiri);
                 System.out.println("ANLOCALI: " + anlocali);
                 System.out.println("ANPROVIN: " + anprovin);
                 System.out.println("ANNAZION: " + annazion);
                 System.out.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                 System.out.println("------------------------------");
                 System.out.println();
                 */
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
                 err++;
                 continue;
             }
         }
         anagrafica.setNome(andescri);
         anagrafica.setCognome(andescr2);
         anagrafica.setIndirizzo(anindiri);
         anagrafica.setCitta(anlocali);
         anagrafica.setCap(an___cap);
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
             err++;
             continue;
         } else if (annazion.equals("ITA")) {
             if (anagrafica.getDiocesi() == Diocesi.DIOCESI000 ) {
                 System.err.println("-----Nazione ITA Errata ------");
                 System.err.println("ANCODICE: " + ancodice);
                 System.err.println("ANDESCRI: " + andescri);
                 System.err.println("ANINDIRI: " + anindiri);
                 System.err.println("ANLOCALI: " + anlocali);
                 System.err.println("ANPROVIN: " + anprovin);
                 System.err.println("ANNAZION: " + annazion);
                 System.err.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                 System.err.println("------------------------------");
                 System.err.println();
                 err++;
                 continue;
             }
             if (anagrafica.getProvincia() == Provincia.ND && anagrafica.getDiocesi() != Diocesi.DIOCESI000) {
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
                     /*
                     System.out.println("-----RSM------");
                     System.out.println("ANCODICE: " + ancodice);
                     System.out.println("ANDESCRI: " + andescri);
                     System.out.println("ANINDIRI: " + anindiri);
                     System.out.println("ANLOCALI: " + anlocali);
                     System.out.println("Paese: " + anagrafica.getPaese().getNome());
                     System.out.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                     System.out.println("------------------------------");
                     System.out.println();
                     System.out.println("------------------------------");
                     System.out.println();
                     */
                 } else {
                 System.err.println("-----Provincia non Definita------");
                 System.err.println("ANCODICE: " + ancodice);
                 System.err.println("ANDESCRI: " + andescri);
                 System.err.println("ANINDIRI: " + anindiri);
                 System.err.println("ANLOCALI: " + anlocali);
                 System.err.println("ANPROVIN: " + anprovin);
                 System.err.println("ANNAZION: " + annazion);
                 System.err.println("Diocesi: " + anagrafica.getDiocesi().getDetails());                 
                 System.err.println("------------------------------");
                 System.err.println();
                 err++;
                 continue;
             }
             }
             anagrafica.setAreaSpedizione(AreaSpedizione.Italia);             
         } else if (annazion.equals("RSM")
                 || annazion.equals("CVC")
                 ) {
             anagrafica.setAreaSpedizione(AreaSpedizione.Italia);
         } else if (annazion.equals("GBR") 
                 || annazion.equals("FRA") 
                 || annazion.equals("ROM") 
                 || annazion.equals("CZE") 
                 || annazion.equals("HRV") 
                 || annazion.equals("LTU") 
                 || annazion.equals("LTU") 
                 || annazion.equals("NLD") 
                 || annazion.equals("LUX") 
                 || annazion.equals("CHE") 
                 || annazion.equals("POL") 
                 || annazion.equals("ALB") 
                 || annazion.equals("DEU")
                 || annazion.equals("ESP")
                 || annazion.equals("CYP")
                 || annazion.equals("BEL")
                 || annazion.equals("SVN")
                 || annazion.equals("IRL")
                 || annazion.equals("ISR")
                 || annazion.equals("PRT")
                 || annazion.equals("SYR")
                 || annazion.equals("EGY")
                 || annazion.equals("TUR")
                 || annazion.equals("LBY")
                 || annazion.equals("GRC")
                                 ) {
             anagrafica.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
         } else {
             anagrafica.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
         }
         anagrafica.setCodfis(ancodfis);
         anagrafica.setPiva(anpariva);
         anagrafica.setTelefono(antelefo);
         anagrafica.setCellulare(annumcel);
         anagrafica.setEmail(an_email);
         userMap.put(ancodice,anagrafica);
         
     }
     
     System.out.println("Errori Trovati: "+err);
     System.out.println("Record Validi: "+userMap.size());
    }
}
