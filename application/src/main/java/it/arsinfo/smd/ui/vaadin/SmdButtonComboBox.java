package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

public class SmdButtonComboBox<T> extends SmdChangeHandler {

    private final Button button;
    private final ComboBox<T> tComboBox;
    private T object;

    public SmdButtonComboBox(String placeholder, List<T> items, String bcaption,VaadinIcons bicon) {
        tComboBox = new ComboBox<>();
        tComboBox.setPlaceholder(placeholder);
        tComboBox.setItems(items);
        tComboBox.addValueChangeListener(e -> {
            object = e.getValue();
        });
        button = new Button(bcaption, bicon);
        button.addClickListener(e -> onChange());

        setComponents(new HorizontalLayout(button,tComboBox));

    }
    
    public Button getButton() {
        return button;
    }

    public ComboBox<T> getComboBox() {
        return tComboBox;
    }
    
    public T getValue() {
        return object;
    }

}
