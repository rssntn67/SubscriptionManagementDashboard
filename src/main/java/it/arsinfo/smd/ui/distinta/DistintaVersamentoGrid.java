package it.arsinfo.smd.ui.distinta;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.DistintaVersamento;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class DistintaVersamentoGrid extends SmdGrid<DistintaVersamento> {

    private final FooterRow gridfooter;
    public DistintaVersamentoGrid(String gridname) {
        super(new Grid<>(DistintaVersamento.class),gridname);
        getGrid().addColumn("dettagli");
        getGrid().addColumn("importo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("incassato",EuroConverter.getEuroRenderer());
        getGrid().addColumn("residuo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("dataContabile");

        getGrid().setColumnOrder(
        		"dettagli",
        		"importo",
        		"incassato",
        		"residuo", 
                "dataContabile"
    		);
        gridfooter = getGrid().prependFooterRow();
    }
    @Override
    public void populate(List<DistintaVersamento> items) {
        super.populate(items);
        gridfooter.getCell("dettagli").setHtml("<strong>"+getLastDate(items)+" Totali:</strong>");
        gridfooter.getCell("importo").setHtml("<b>"+getImportoTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassatoTotale(items).toString()+"</b>");
        gridfooter.getCell("residuo").setHtml("<b>"+getResiduoTotale(items).toString()+"</b>");
    }
    
    private BigDecimal getImportoTotale(List<DistintaVersamento> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (DistintaVersamento incasso:incassi) {
            importo=importo.add(incasso.getImporto());
        }
        return importo;
    }
    
    private BigDecimal getIncassatoTotale(List<DistintaVersamento> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (DistintaVersamento incasso:incassi) {
            importo=importo.add(incasso.getIncassato());
        }
        return importo;
    }

    private BigDecimal getResiduoTotale(List<DistintaVersamento> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (DistintaVersamento incasso:incassi) {
            importo=importo.add(incasso.getResiduo());
        }
        return importo;
    }
    
    private String getLastDate(List<DistintaVersamento> incassi) {
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date datainizio=null;
        Date datafine=null;
        for (DistintaVersamento incasso:incassi) {
            if (datafine == null) {
                datafine = incasso.getDataContabile();
                datainizio = incasso.getDataContabile();
                continue;
            }
            if (incasso.getDataContabile().after(datafine)  ) {
                datafine = incasso.getDataContabile();
            }
            if (incasso.getDataContabile().before(datainizio)  ) {
                datainizio = incasso.getDataContabile();
            }

        }
        if (datafine == null) {
            return "";
        }
 
        String inizio = dateformat.format(datainizio);
        String fine = dateformat.format(datafine);
        
        if (inizio.equals(fine)) {
            return inizio;
        }
        return String.format("da %s a %s", inizio,fine);
    }

}
