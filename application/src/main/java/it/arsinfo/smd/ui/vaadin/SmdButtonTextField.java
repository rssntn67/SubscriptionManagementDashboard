package it.arsinfo.smd.ui.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class SmdButtonTextField extends SmdChangeHandler {

    private final Button button;
    private final TextField textField;
    private String value;

    public SmdButtonTextField(String placeholder, String bcaption,VaadinIcons bicon) {
        textField = new TextField();
        textField.setPlaceholder(placeholder);
        textField.addValueChangeListener(e -> {
            value = e.getValue();
        });
        button = new Button(bcaption, bicon);
        button.addClickListener(e -> onChange());
        setComponents(new HorizontalLayout(button,textField));

    }
    
    public Button getButton() {
        return button;
    }

    public TextField getTextField() {
        return textField;
    }
    
    public String getValue() {
        return value;
    }

}
