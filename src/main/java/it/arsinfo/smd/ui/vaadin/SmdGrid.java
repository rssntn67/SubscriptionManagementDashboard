package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.util.StringUtils;
import org.vaadin.haijian.Exporter;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import it.arsinfo.smd.SmdEntity;

public abstract class SmdGrid<T extends SmdEntity>
        extends SmdChangeHandler {

    private final Grid<T> grid;
    private T selected;
    private final String gridName;
    Label itemNumber = new Label();
    private Integer size = 0;
    public SmdGrid(Grid<T> grid, String gridName) {
        Button downloadAsExcel = new Button("Download As Excel");
        Button downloadAsCSV = new Button("Download As CSV");

        StreamResource excelStreamResource = 
                new StreamResource((StreamResource.StreamSource) () -> 
                    Exporter.exportAsExcel(grid), "smd"+gridName+".xls");
        FileDownloader excelFileDownloader = 
                new FileDownloader(excelStreamResource);
                    excelFileDownloader.extend(downloadAsExcel);

        StreamResource csvStreamResource = 
                new StreamResource((StreamResource.StreamSource) () -> 
                    Exporter.exportAsCSV(grid), "smd"+gridName+".csv");
        
        FileDownloader csvFileDownloader = new FileDownloader(csvStreamResource);
        csvFileDownloader.extend(downloadAsCSV);

        this.grid = grid;
        this.gridName = gridName;
        this.grid.setWidth("100%");

        this.grid.asSingleSelect().addValueChangeListener(e -> {
            selected = e.getValue();
            onChange();
        });
        
        setComponents(this.grid,new HorizontalLayout(this.itemNumber,downloadAsExcel,downloadAsCSV));
    }
        

    public void setColumns(String...columnIds) {
        grid.setColumns(columnIds);
        if (!StringUtils.isEmpty(gridName)) {
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
        return selected;
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


	public Label getItemNumber() {
		return itemNumber;
	}


	public Integer getSize() {
		return size;
	}
}
