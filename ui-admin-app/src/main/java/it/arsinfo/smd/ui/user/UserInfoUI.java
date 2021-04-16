package it.arsinfo.smd.ui.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.service.api.UserInfoService;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.SmdEditorUI;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.URL_ADMIN_USER)
@Title("Amministrazione Utenti")
public class UserInfoUI extends SmdEditorUI<UserInfo> {

    @Autowired
    private UserInfoService userInfoDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 
     */
    private static final long serialVersionUID = -659806613407638574L;

    @Override
    protected void init(VaadinRequest request) {
        UserInfoSearch search = new UserInfoSearch(userInfoDao);
        UserInfoAdd add = new UserInfoAdd("Aggiungi Utente");
        UserInfoGrid grid = new UserInfoGrid("Users");
        UserInfoEditor editor = new UserInfoEditor(userInfoDao,passwordEncoder);
        
        init(request,add,search, editor, grid, "Amministrazione Utenti");        
        
        addSmdComponents(editor, 
                add,
                search, 
                grid);

        editor.setVisible(false);
        
        grid.populate(search.findAll());

    }

}
