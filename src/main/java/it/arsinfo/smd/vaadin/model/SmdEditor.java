package it.arsinfo.smd.vaadin.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdEditor<T extends SmdEntity>
        extends SmdChangeHandler {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final JpaRepository<T, Long> repositoryDao;

    private T repositoryObj;

    private Button save = new Button("Save", VaadinIcons.CHECK);
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcons.TRASH);
    private Button back = new Button("Back");
    private HorizontalLayout actions = new HorizontalLayout(save, cancel,
                                                            delete, back);

    private final Binder<T> binder;

    public SmdEditor(JpaRepository<T, Long> repositoryDao, Binder<T> binder) {

        this.repositoryDao = repositoryDao;
        this.binder = binder;

        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        delete.addStyleName(ValoTheme.BUTTON_DANGER);

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> edit(repositoryObj));
        back.addClickListener(e -> onChange());

    }

    public abstract void focus(boolean persisted, T obj);

    public HorizontalLayout getActions() {
        return actions;
    }

    public void delete() {
        repositoryDao.delete(repositoryObj);
        onChange();
    }

    public void save() {
        repositoryDao.save(repositoryObj);
        onChange();
    }

    public final void edit(T c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            repositoryObj = repositoryDao.findById(c.getId()).get();
        } else {
            repositoryObj = c;
        }
        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(repositoryObj);

        cancel.setEnabled(persisted);
        focus(persisted, repositoryObj);
        setVisible(true);
    }

    public Button getSave() {
        return save;
    }

    public Button getCancel() {
        return cancel;
    }

    public Button getDelete() {
        return delete;
    }

    public Button getBack() {
        return back;
    }

    public Binder<T> getBinder() {
        return binder;
    }

}
