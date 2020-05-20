package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class SmdButtonTwoComboBox<T,S> extends SmdChangeHandler {

    private final Button button;
    private final ComboBox<T> tComboBox;
    private final ComboBox<S> sComboBox;
    private final TextField textField;
    private T t;
    private S s;
    private String value;

    public SmdButtonTwoComboBox(String placeholder, List<T> tItems, List<S> sItems,String bcaption,VaadinIcons bicon) {

        textField = new TextField();
        textField.setPlaceholder(placeholder);
        textField.addValueChangeListener(e -> {
            value = e.getValue();
        });

    	tComboBox = new ComboBox<>();
        tComboBox.setPlaceholder(placeholder);
        tComboBox.setItems(tItems);
        tComboBox.addValueChangeListener(e -> {
            t = e.getValue();
        });

        sComboBox = new ComboBox<>();
        sComboBox.setPlaceholder(placeholder);
        sComboBox.setItems(sItems);
        sComboBox.addValueChangeListener(e -> {
            s = e.getValue();
        });

        button = new Button(bcaption, bicon);
        button.addClickListener(e -> onChange());

        setComponents(new HorizontalLayout(button,textField,tComboBox,sComboBox));

    }
    
    public Button getButton() {
        return button;
    }

    public ComboBox<T> getTComboBox() {
        return tComboBox;
    }

    public ComboBox<S> getSComboBox() {
        return sComboBox;
    }

    public T getTValue() {
        return t;
    }
    
    public S getSValue() {
        return s;
    }

    public TextField getTextField() {
        return textField;
    }
    
    public String getValue() {
        return value;
    }


}
