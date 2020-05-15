package it.arsinfo.smd.ui.incassa.abbonamento;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.OperazioneIncasso;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public abstract class IncassaAbbonamentoAbstractGrid extends SmdGrid<OperazioneIncasso> {

    private FooterRow gridfooter;

    public IncassaAbbonamentoAbstractGrid(String gridname) {
        super(new Grid<>(OperazioneIncasso.class),gridname);
    }
    
    public void prependGridFooter() {
    	gridfooter = getGrid().prependFooterRow();
    }
    
    @Override
    public void populate(List<OperazioneIncasso> items) {
        super.populate(items);
        gridfooter.getCell("operatore").setHtml("<strong>"+getLastDate(items)+"</strong>");
        gridfooter.getCell("statoOperazioneIncasso").setHtml("<strong>Totale Incassato:</strong>");
        gridfooter.getCell("importo").setHtml("<b>"+getImportoTotale(items).toString()+"</b>"); 
    }    

    public BigDecimal getImportoTotale(List<OperazioneIncasso> incassi) {
        BigDecimal importo = BigDecimal.ZERO;
        for (OperazioneIncasso incasso:incassi) {
        	switch (incasso.getStatoOperazioneIncasso()) {
			case Incasso:
        		importo=importo.add(incasso.getImporto());
				break;
			default:
				break;
			}
        }
        return importo;
    }
        
    public String getLastDate(List<OperazioneIncasso> incassi) {
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date datainizio=null;
        Date datafine=null;
        for (OperazioneIncasso incasso:incassi) {
            if (datafine == null) {
                datafine = incasso.getVersamento().getDataContabile();
                datainizio = incasso.getVersamento().getDataContabile();
                continue;
            }
            if (incasso.getVersamento().getDataContabile().after(datafine)  ) {
                datafine = incasso.getVersamento().getDataContabile();
            }
            if (incasso.getVersamento().getDataContabile().before(datainizio)  ) {
                datainizio = incasso.getVersamento().getDataContabile();
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
