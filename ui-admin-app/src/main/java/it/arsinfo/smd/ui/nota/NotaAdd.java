package it.arsinfo.smd.ui.nota;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class NotaAdd extends SmdAdd<Nota> {

	private UserInfo user; 
    public NotaAdd(String caption) {
        super(caption);
    }

    @Override
    public Nota generate() {
    	Nota nota = new Nota();
    	nota.setOperatore(user.getUsername());
        return nota;
    }

	public void setUser(UserInfo user) {
		this.user = user;
	}

}
