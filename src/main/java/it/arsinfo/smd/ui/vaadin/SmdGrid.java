package it.arsinfo.smd.ui.vaadin;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import org.springframework.util.StringUtils;
import org.vaadin.haijian.Exporter;

import java.util.List;

public abstract class SmdGrid<T>
        extends SmdChangeHandler {

    private final Grid<T> grid;
    private final SingleSelect<T> selected;
    private final String gridName;
    Label itemNumber = new Label();
    private Integer size = 0;

    private final FileDownloader csvFileDownloader;
    private final FileDownloader excelFileDownloader;
    public SmdGrid(Grid<T> grid, String gridName) {


        excelFileDownloader =
                new FileDownloader(
                        new StreamResource((StreamResource.StreamSource) () ->
                        Exporter.exportAsExcel(grid), "smd"+gridName+"-"+randomAlphaNumeric(8)+".xls"));
        Button downloadAsExcel = new Button("Download As Excel");
        downloadAsExcel.setHeight("22px");
        downloadAsExcel.setWidth("200px");
        excelFileDownloader.extend(downloadAsExcel);

        csvFileDownloader =
                new FileDownloader(                new StreamResource((StreamResource.StreamSource) () ->
                        Exporter.exportAsCSV(grid), "smd"+gridName+"-"+randomAlphaNumeric(8)+".csv"));
        Button downloadAsCSV = new Button("Download As CSV");
        downloadAsCSV.setHeight("22px");
        downloadAsCSV.setWidth("200px");
        csvFileDownloader.extend(downloadAsCSV);

        downloadAsCSV.addClickListener( e -> updateCsvStreamSource());

        downloadAsExcel.addClickListener( e -> updateExcelStreamSource());

        this.grid = grid;
        this.gridName = gridName;
        this.grid.setWidth("100%");
        this.grid.setColumns();

        selected = this.grid.asSingleSelect();
        selected.addValueChangeListener(e -> onChange());
        
        setComponents(this.grid,new HorizontalLayout(this.itemNumber,downloadAsExcel,downloadAsCSV));
    }

    public void updateCsvStreamSource() {
        csvFileDownloader.setFileDownloadResource(new StreamResource((StreamResource.StreamSource) () ->
                Exporter.exportAsCSV(grid), "smd"+gridName+"-"+randomAlphaNumeric(8)+".csv"));
    }

    public void updateExcelStreamSource() {
        excelFileDownloader.setFileDownloadResource(new StreamResource((StreamResource.StreamSource) () ->
                Exporter.exportAsExcel(grid), "smd"+gridName+"-"+randomAlphaNumeric(8)+".xls"));

    }

    public void setColumns(String...columnIds) {
        grid.setColumns(columnIds);
        if (StringUtils.hasLength(gridName)) {
            grid.prependHeaderRow().join(columnIds).setText(gridName);            
        }
    }

    public void setColumnCaption(String columnId, String caption) {
        if (grid.getColumn(columnId) == null) {
            return;
        }
        grid.getColumn(columnId).setCaption(caption);
    }
    
    public void populate(List<T> items) {
        if (items == null || items.size() == 0) {
        	size = 0;
            itemNumber.setValue("");
            setVisible(false);
        } else {
            itemNumber.setValue("Trovati " + items.size() + " Record");
            grid.setItems(items);
            setVisible(true);
            size = items.size();
        }
    }

    public T getSelected() {
    	if (selected.isEmpty())
    		return null;
        return selected.getValue();
    }


    public String getGridName() {
        return gridName;
    }
    
    public void addComponentColumn(ValueProvider<T,AbstractComponent> valueprovider) {
        grid.addComponentColumn(valueprovider);
    }
    
    public Grid<T> getGrid() {
        return grid;
    }

	public Integer getSize() {
		return size;
	}

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }}
