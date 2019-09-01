package it.arsinfo.smd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
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
        
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
        
        /*
        =============================================================
        Iterating over all the sheets in the workbook (Multiple ways)
        =============================================================
     */

     // 1. You can obtain a sheetIterator and iterate over it
     System.out.println("Retrieving Sheets using Iterator");
     for (int i =0 ; i<workbook.getNumberOfSheets();i++) {
         Sheet sheet = workbook.getSheetAt(i);
         System.out.println("=> " + sheet.getSheetName());
     }

     /*
        ==================================================================
        Iterating over all the rows and columns in a Sheet (Multiple ways)
        ==================================================================
     */

     // Getting the Sheet at index zero
     Sheet sheet = workbook.getSheetAt(0);

     // Create a DataFormatter to format and get each cell's value as String
     DataFormatter dataFormatter = new DataFormatter();

     // 3. Or you can use Java 8 forEach loop with lambda
     Map<String,Anagrafica> userMap = new HashMap<String, Anagrafica>();
     System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
     sheet.forEach(row -> {
         String picoddio = dataFormatter.formatCellValue(row.getCell(0));
         if (picoddio.equals("PICODDIO")) {
             return;
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
//         System.out.println(ancodice);
         Anagrafica anagrafica = new Anagrafica();
         anagrafica.setDiocesi(Diocesi.getDiocesiByCodice(picoddio));
         anagrafica.setNome(andescri);
         anagrafica.setCognome(andescr2);
         anagrafica.setIndirizzo(anindiri);
         anagrafica.setCitta(anlocali);
         anagrafica.setCap(an___cap);
         //FIXME 
         anagrafica.setPaese(Paese.getBySigla(annazion));
         if (anagrafica.getPaese() == Paese.ND) {
           System.err.println("nazione:" + annazion);
           System.err.println(ancodice);
           return;
         } else if (annazion.equals("ITA") 
                 || annazion.equals("RSM")
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
                 ||annazion.equals("DEU")
                 ||annazion.equals("ESP")
                 ||annazion.equals("CYP")
                 ||annazion.equals("BEL")
                 ||annazion.equals("SVN")
                 ||annazion.equals("IRL")
                 ||annazion.equals("ISR")
                 ||annazion.equals("PRT")
                 ||annazion.equals("SYR")
                 ||annazion.equals("EGY")
                 ||annazion.equals("TUR")
                 ||annazion.equals("LBY")
                 ||annazion.equals("GRC")
                                 ) {
             anagrafica.setAreaSpedizione(AreaSpedizione.EuropaBacinoMediterraneo);
         } else {
             anagrafica.setAreaSpedizione(AreaSpedizione.AmericaAfricaAsia);
             System.out.println(ancodice);
             System.out.println(anagrafica.getPaese().getNome());
             System.out.println();
         }
         anagrafica.setProvincia(getProvincia(anprovin));
         anagrafica.setCodfis(ancodfis);
         anagrafica.setPiva(anpariva);
         anagrafica.setTelefono(antelefo);
         anagrafica.setCellulare(annumcel);
         anagrafica.setEmail(an_email);
//         System.out.println(anagrafica);
         userMap.put(ancodice,anagrafica);
//         row.forEach(cell -> {
//             System.out.print(cellValue + "\t");
//         });
     });
 }
}
