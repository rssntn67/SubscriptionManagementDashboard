package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.vaadin.model.SmdGrid;

public class IncassoGrid extends SmdGrid<Incasso> {

    private final FooterRow gridfooter;
    public IncassoGrid(String gridname) {
        super(new Grid<>(Incasso.class),gridname);

        setColumns("dataContabile","incassato","importo","residuo","cassa", 
                  "dettagli");
        gridfooter = getGrid().prependFooterRow();
    }
    @Override
    public void populate(List<Incasso> items) {
        super.populate(items);
        gridfooter.getCell("dataContabile").setHtml("<strong>"+getLastDate(items)+" Totali:</strong>");
        gridfooter.getCell("importo").setHtml("<b>"+getImportoTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassatoTotale(items).toString()+"</b>");
        gridfooter.getCell("residuo").setHtml("<b>"+getResiduoTotale(items).toString()+"</b>");
        gridfooter.getCell("cassa").setHtml("-------");
        gridfooter.getCell("dettagli").setHtml("-------");

    }
    
    private BigDecimal getImportoTotale(List<Incasso> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Incasso incasso:incassi) {
            importo=importo.add(incasso.getImporto());
        }
        return importo;
    }
    
    private BigDecimal getIncassatoTotale(List<Incasso> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Incasso incasso:incassi) {
            importo=importo.add(incasso.getIncassato());
        }
        return importo;
    }

    private BigDecimal getResiduoTotale(List<Incasso> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (Incasso incasso:incassi) {
            importo=importo.add(incasso.getResiduo());
        }
        return importo;
    }
    
    private String getLastDate(List<Incasso> incassi) {
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date datainizio=null;
        Date datafine=null;
        for (Incasso incasso:incassi) {
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
