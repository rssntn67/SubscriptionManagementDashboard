package it.arsinfo.smd.ui.vaadin;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import org.springframework.util.StringUtils;
import org.vaadin.haijian.Exporter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public abstract class SmdGrid<T>
        extends SmdChangeHandler {

    private final Grid<T> grid;
    private final SingleSelect<T> selected;
    private final String gridName;
    Label itemNumber = new Label();
    private Integer size = 0;

    public SmdGrid(Grid<T> grid, String gridName) {

        Button downloadAsExcel = new Button("Download As Excel");
        Button downloadAsCSV = new Button("Download As CSV");

        downloadAsExcel.setHeight("22px");
        downloadAsExcel.setWidth("200px");
        downloadAsCSV.setHeight("22px");
        downloadAsCSV.setWidth("200px");


    	StreamResource excelStreamResource = 
                new StreamResource((StreamResource.StreamSource) () -> 
                    Exporter.exportAsExcel(grid), "smd"+gridName+"-"+generateRandom()+".xls");
        FileDownloader excelFileDownloader = 
                new FileDownloader(excelStreamResource);

        StreamResource csvStreamResource = 
                new StreamResource((StreamResource.StreamSource) () -> 
                    Exporter.exportAsCSV(grid), "smd"+gridName+"-"+generateRandom()+".csv");
        
        FileDownloader csvFileDownloader = new FileDownloader(csvStreamResource);
        excelFileDownloader.extend(downloadAsExcel);
        csvFileDownloader.extend(downloadAsCSV);

        this.grid = grid;
        this.gridName = gridName;
        this.grid.setWidth("100%");
        this.grid.setColumns();

        selected = this.grid.asSingleSelect();
        selected.addValueChangeListener(e -> onChange());
        
        setComponents(this.grid,new HorizontalLayout(this.itemNumber,downloadAsExcel,downloadAsCSV));
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

	public static String generateRandom() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
