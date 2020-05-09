package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdEditor<T extends SmdEntity>
        extends SmdChangeHandler {

		public abstract void edit(T c);
}
