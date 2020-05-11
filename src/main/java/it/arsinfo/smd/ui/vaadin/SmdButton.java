package it.arsinfo.smd.ui.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;

public class SmdButton extends SmdChangeHandler {

    private final Button button;

    public SmdButton(String caption, VaadinIcons icon) {
        button = new Button(caption, icon);
        button.addClickListener(e -> onChange());
        setComponents(button);

    }
    
    public Button getButton() {
        return button;
    }
    
    public void enable() {
    	button.setEnabled(true);
    }
    
    public void disable() {
    	button.setEnabled(false);
    }
    

}
