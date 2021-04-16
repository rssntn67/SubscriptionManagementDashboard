package it.arsinfo.smd.ui.storico;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.vaadin.SmdAddItem;

public class NotaAdd extends SmdAddItem<Nota,Storico> {

	private Storico storico;
	private UserInfo user;
    public NotaAdd(String caption) {
        super(caption);
    }

    @Override
    public Nota generate() {
    	Nota nota = new Nota();
    	nota.setStorico(storico);
    	nota.setOperatore(user.getUsername());
    	return nota;
    }

	@Override
	public void set(Storico t) {
		storico = t;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}
	
	

}
