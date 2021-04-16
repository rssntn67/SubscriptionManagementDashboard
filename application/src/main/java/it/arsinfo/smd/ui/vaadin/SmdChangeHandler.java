package it.arsinfo.smd.ui.vaadin;

import java.util.Arrays;
import java.util.stream.Stream;

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

    public void addComponents(Component ... components) {
    	if (this.components == null)
    		this.components = components;
    	else
    		this.components=Stream.concat(Arrays.stream(this.components), Arrays.stream(components)).toArray(Component[]::new);	
    }
    
    public void setComponents(Component ... components) {
        this.components = components;
    }

}
