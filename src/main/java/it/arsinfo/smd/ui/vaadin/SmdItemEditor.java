package it.arsinfo.smd.ui.vaadin;

import com.vaadin.data.Binder;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdItemEditor<T extends SmdEntity>
        extends SmdEditor<T> {

	private T smdObj;

    private final Binder<T> binder;

    public SmdItemEditor(Binder<T> binder) {

        this.binder = binder;

    }

    public abstract void focus(boolean persisted, T obj);


    public final void edit(T c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        smdObj = c;
        binder.setBean(smdObj);

        focus(persisted, smdObj);
        setVisible(true);
    }

    public Binder<T> getBinder() {
        return binder;
    }

    public T get() {
        return smdObj;
    }

}
