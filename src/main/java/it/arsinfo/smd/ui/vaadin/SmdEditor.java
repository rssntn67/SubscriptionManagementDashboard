package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdEditor<T extends SmdEntity>
        extends SmdChangeHandler {

    	private HorizontalLayout actions = new HorizontalLayout();

		public abstract void edit(T c);
		
	    public HorizontalLayout getActions() {
	        return actions;
	    }

}
