package it.arsinfo.smd.vaadin;

import com.vaadin.ui.VerticalLayout;

public abstract class SmdEditor extends VerticalLayout {

    ChangeHandler changeHandler;
    /**
     * 
     */
    
    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

}
