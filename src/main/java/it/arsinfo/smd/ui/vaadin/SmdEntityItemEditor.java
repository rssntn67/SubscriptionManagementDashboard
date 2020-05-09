package it.arsinfo.smd.ui.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.SmdServiceItemDao;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.entity.SmdEntityItems;

public abstract class SmdEntityItemEditor<I extends SmdEntity, T extends SmdEntityItems<I>>
        extends SmdEditor<T> {

    private final SmdAdd<I> itemAdd;
    private final SmdButton itemDel = new SmdButton("Rimuovi Item", VaadinIcons.TRASH);
    private final SmdButton itemSave = new SmdButton("Salva Item", VaadinIcons.TRASH);
    private final SmdGrid<I> itemGrid;
    private final SmdItemEditor<I> itemEditor;
    private final SmdEntityEditor<T> editor;
	private final VerticalLayout layout = new VerticalLayout();
    private final SmdServiceItemDao<T,I> dao;

    public SmdEntityItemEditor(SmdServiceItemDao<T,I> dao,SmdAdd<I> itemAdd, SmdGrid<I> itemGrid,
			SmdItemEditor<I> itemEditor, SmdEntityEditor<T> editor) {
		this.dao=dao;
		this.itemAdd = itemAdd;
		this.itemGrid = itemGrid;
		this.itemEditor = itemEditor;
		this.editor = editor;
		editor.getActions().addComponents(itemSave.getComponents());
		editor.getActions().addComponents(itemDel.getComponents());
		editor.getActions().addComponents(itemAdd.getComponents());
		addSmd(editor);
		addSmd(itemGrid);
		addSmd(itemEditor);
				
        itemEditor.setVisible(false);
        itemDel.disable();
        itemSave.disable();
        
        editor.setChangeHandler(() -> {
            itemEditor.setVisible(false);
            itemDel.disable();
            itemSave.disable();
        	onChange();
        });

        itemDel.setChangeHandler(() -> {
        	try {
				edit(dao.deleteItem(editor.get(), itemEditor.get()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	
        });
        
        itemSave.setChangeHandler(() -> {
        	try {
				edit(dao.saveItem(editor.get(), itemEditor.get()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	
        });
        
        itemAdd.setChangeHandler(() -> {
        	itemEditor.edit(itemAdd.generate());
            itemDel.enable();
            itemSave.enable();
        });
                
        itemGrid.setChangeHandler(() -> {
            if (itemGrid.getSelected() == null) {
            	itemEditor.setVisible(false);
                itemDel.disable();
                itemSave.disable();
        	    return;
            }
            itemEditor.edit(itemGrid.getSelected());
            itemDel.enable();
            itemSave.enable();
        });

	}
    
	public void edit(T t) {
		t.setItems(dao.getItems(t));			
		editor.edit(t);
		itemGrid.populate(t.getItems());
    	itemEditor.setVisible(false);
        itemDel.disable();
        itemSave.disable();
	}


    public void addSmd(SmdChangeHandler... smdhandlers) {
    	for (SmdChangeHandler smdhandler :smdhandlers)
    		layout.addComponents(smdhandler.getComponents());
    }

    public SmdAdd<I> getItemAdd() {
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
}
