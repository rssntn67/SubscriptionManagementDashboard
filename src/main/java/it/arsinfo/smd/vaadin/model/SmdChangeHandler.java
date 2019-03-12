package it.arsinfo.smd.vaadin.model;

import com.vaadin.ui.Component;

public abstract class SmdChangeHandler {

    /**
     * 
     */
    private ChangeHandler changeHandler;
    /**
     * 
     */
    private Component[] components;
    
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
    
    public void setVisible(boolean visible) {
        for (Component component:components) {
            component.setVisible(visible);
        }
    }

    public Component[] getComponents() {
        return components;
    }

    public void setComponents(Component ... components) {
        this.components = components;
    }

}
