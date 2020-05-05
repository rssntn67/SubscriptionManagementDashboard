package it.arsinfo.smd.ui.vaadin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.service.SmdService;

public abstract class SmdEditor<T extends SmdEntity>
        extends SmdChangeHandler {

    private final JpaRepository<T, Long> repositoryDao;
    private SmdService smdService;

    private T smdObj;

    private Button save = new Button("Salva", VaadinIcons.CHECK);
    private Button delete = new Button("Rimuovi", VaadinIcons.TRASH);
    private Button cancel = new Button("Annulla Modifiche");
    private Button back = new Button("Indietro");
    private HorizontalLayout actions = new HorizontalLayout(save, 
                                                            delete,
                                                            cancel,
                                                            back);

    private final Binder<T> binder;

    public SmdEditor(JpaRepository<T, Long> repositoryDao, Binder<T> binder) {

        this.repositoryDao = repositoryDao;
        this.binder = binder;

        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        delete.addStyleName(ValoTheme.BUTTON_DANGER);

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> edit(smdObj));
        back.addClickListener(e -> onChange());

    }

    public abstract void focus(boolean persisted, T obj);

    public HorizontalLayout getActions() {
        return actions;
    }

    public void delete() {
        try {
            repositoryDao.delete(smdObj);
            onChange();
        } catch (Exception e) {
            Notification.show(e.getMessage(),
                              Notification.Type.ERROR_MESSAGE);
        }
    }

    public void save() {
        try {
            repositoryDao.save(smdObj);
            onChange();
        } catch (Exception e) {
            Notification.show(e.getMessage(),
                              Notification.Type.ERROR_MESSAGE);
        }
    }

    public final void edit(T c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            smdObj = repositoryDao.findById(c.getId()).get();
        } else {
            smdObj = c;
        }
        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(smdObj);

        cancel.setEnabled(persisted);
        delete.setEnabled(persisted);
        focus(persisted, smdObj);
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

    public T get() {
        return smdObj;
    }

    public JpaRepository<T, Long> getRepositoryDao() {
        return repositoryDao;
    }
    
	public SmdService getSmdService() {
		return smdService;
	}

	public void setSmdService(SmdService smdService) {
		this.smdService = smdService;
	}

}
