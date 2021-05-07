package it.arsinfo.smd.ui.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification;

import it.arsinfo.smd.service.api.SmdServiceItem;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;

public class SmdEntityItemEditor<I extends SmdEntity, T extends SmdEntityItems<I>>
        extends SmdEditor<T> {

    private final SmdAddItem<I,T> itemAdd;
    private final SmdButton itemDel;  
    private final SmdButton itemSave; 
    private final SmdGrid<I> itemGrid;
    private final SmdItemEditor<I> itemEditor;
    private final SmdEntityEditor<T> editor;    
    private final SmdServiceItem<T,I> dao;

    private static final Logger log = LoggerFactory.getLogger(SmdEntityItemEditor.class);

    public SmdEntityItemEditor(SmdServiceItem<T,I> dao, SmdAddItem<I,T> itemAdd, SmdButton itemDel, SmdButton itemSave, SmdGrid<I> itemGrid,
                               SmdItemEditor<I> itemEditor, SmdEntityEditor<T> editor) {
		this.dao=dao;
		this.itemAdd = itemAdd;
		this.itemDel=itemDel;
		this.itemSave=itemSave;
		this.itemGrid = itemGrid;
		this.itemEditor = itemEditor;
		this.editor = editor;

		disableItems();
        editor.setChangeHandler(() -> {
        	disableItems();
        	this.onChange();
        });

        itemDel.setChangeHandler(() -> {
        	try {
				edit(dao.deleteItem(editor.get(), itemEditor.get()));
			} catch (Exception e) {
	            Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
	            log.error("itemDel: {}", e.getMessage(),e);
			}        	
        });
        
        itemSave.setChangeHandler(() -> {
        	try {
				edit(dao.saveItem(editor.get(), itemEditor.get()));
			} catch (Exception e) {
				Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
	            log.error("itemSave: {}", e.getMessage(),e);
			}        	
        });
        
        itemAdd.setChangeHandler(() -> {
        	itemEditor.edit(itemAdd.generate());
        	enableItems();
        });
                
        itemGrid.setChangeHandler(() -> {
            if (itemGrid.getSelected() == null) {
            	disableItems();
        	    return;
            }
            itemEditor.edit(itemGrid.getSelected());
            enableItems();
        });

	}
    
    public SmdEntityItemEditor(SmdServiceItem<T,I> dao, SmdButton itemSave, SmdGrid<I> itemGrid,
                               SmdItemEditor<I> itemEditor, SmdEntityEditor<T> editor) {
		this.dao=dao;
		this.itemSave=itemSave;
		this.itemGrid = itemGrid;
		this.itemEditor = itemEditor;
		this.editor = editor;
		this.itemAdd=null;
		this.itemDel=null;

		disableItems();
        editor.setChangeHandler(() -> {
        	disableItems();
        	this.onChange();
        });
        
        itemSave.setChangeHandler(() -> {
        	try {
				edit(dao.saveItem(editor.get(), itemEditor.get()));
			} catch (Exception e) {
				Notification.show(e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
	            log.error("itemSave: {}", e.getMessage(),e);
			}        	
        });
                        
        itemGrid.setChangeHandler(() -> {
            if (itemGrid.getSelected() == null) {
            	disableItems();
        	    return;
            }
            itemEditor.edit(itemGrid.getSelected());
            enableItems();
        });

	}

    
	public void edit(T t) {
		editor.edit(t);
		if (t.getId() != null) {
			editor.get().setItems(dao.getItems(t));	
		}
		itemGrid.populate(editor.get().getItems());
		if (itemAdd != null)
			itemAdd.set(editor.get());
    	itemEditor.setVisible(false);
    	if (itemDel != null)
    		itemDel.disable();
        itemSave.disable();
        editor.getSave().setEnabled(true);
        editor.getDelete().setEnabled(true);

	}

    public SmdAddItem<I,T> getItemAdd() {
		return itemAdd;
	}

	public SmdGrid<I> getItemGrid() {
		return itemGrid;
	}

	public SmdItemEditor<I> getItemEditor() {
		return itemEditor;
	}

	public SmdEntityEditor<T> getEditor() {
		return editor;
	}

	public SmdButton getItemDel() {
		return itemDel;
	}

	public SmdButton getItemSave() {
		return itemSave;
	}
	
	private void enableItems() {
		if (itemDel != null)
			itemDel.enable();
        itemSave.enable();
        editor.getSave().setEnabled(false);
        editor.getDelete().setEnabled(false);


	}
	
	private void disableItems() {
        itemEditor.setVisible(false);
		if (itemDel != null)
			itemDel.disable();
        itemSave.disable();
        editor.getSave().setEnabled(true);
        editor.getDelete().setEnabled(true);		
	}
}
