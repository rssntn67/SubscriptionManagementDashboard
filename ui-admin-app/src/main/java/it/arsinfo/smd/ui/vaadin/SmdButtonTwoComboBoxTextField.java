package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class SmdButtonTwoComboBoxTextField<T,S> extends SmdChangeHandler {

    private final Button button;
    private final ComboBox<T> tComboBox;
    private final ComboBox<S> sComboBox;
    private final TextField textFieldB;
    private final TextField textFieldA;
    private T t;
    private S s;
    private String valueA;
    private String valueB;

    public SmdButtonTwoComboBoxTextField(String p1, String p2, List<T> tItems, List<S> sItems,String bcaption,VaadinIcons bicon) {

        textFieldA = new TextField();
        textFieldA.setPlaceholder(p1);
        textFieldA.addValueChangeListener(e -> {
            valueA = e.getValue();
        });

        textFieldB = new TextField();
        textFieldB.setPlaceholder(p2);
        textFieldB.addValueChangeListener(e -> {
            valueB = e.getValue();
        });

    	tComboBox = new ComboBox<>();
        tComboBox.setPlaceholder(p2);
        tComboBox.setItems(tItems);
        tComboBox.addValueChangeListener(e -> {
            t = e.getValue();
        });

        sComboBox = new ComboBox<>();
        sComboBox.setPlaceholder(p2);
        sComboBox.setItems(sItems);
        sComboBox.addValueChangeListener(e -> {
            s = e.getValue();
        });

        button = new Button(bcaption, bicon);
        button.addClickListener(e -> onChange());

        setComponents(new HorizontalLayout(textFieldA,textFieldB,tComboBox,sComboBox),button);

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

    public TextField getTextFieldB() {
        return textFieldB;
    }

    public TextField getTextFieldA() {
        return textFieldA;
    }

    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }


}
