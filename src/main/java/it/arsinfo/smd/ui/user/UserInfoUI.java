package it.arsinfo.smd.ui.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.URL_USER)
@Title("Amministrazione Utenti")
public class UserInfoUI extends SmdUI {

    Grid<it.arsinfo.smd.entity.UserInfo> grid;
    @Autowired
    private UserInfoDao userInfoDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 
     */
    private static final long serialVersionUID = -659806613407638574L;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Users");
        UserInfoAdd add = new UserInfoAdd("Aggiungi Utente");
        UserInfoGrid grid = new UserInfoGrid("Users");
        UserInfoEditor editor = new UserInfoEditor(userInfoDao,passwordEncoder);
        
        addSmdComponents(add,editor, grid);
        editor.setVisible(false);
        
        add.setChangeHandler(() -> {
            editor.edit(add.generate());
        });

        grid.setChangeHandler(() ->{
            if (grid.getSelected() == null) {
                editor.setVisible(false);
            } else {
                editor.edit(grid.getSelected());
            }

        }) ;
        editor.setChangeHandler(() -> {
                editor.setVisible(false);
                grid.populate(userInfoDao.findAll());
        });
        grid.populate(userInfoDao.findAll());
        
    }

}
