package it.arsinfo.smd.vaadin;

import com.vaadin.ui.VerticalLayout;

public abstract class SmdEditor extends VerticalLayout {

    ChangeHandler changeHandler;
    /**
     * 
     */
    private static final long serialVersionUID = 6613896001702800464L;
    
    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

}
