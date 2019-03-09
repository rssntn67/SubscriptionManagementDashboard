package it.arsinfo.smd.vaadin;

import com.vaadin.ui.VerticalLayout;

public abstract class SmdChangeHandler extends VerticalLayout {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ChangeHandler changeHandler;
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
    
    public  void onChange() {
        changeHandler.onChange();
    }

}
