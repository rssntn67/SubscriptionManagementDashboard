package it.arsinfo.smd.ui.reset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.URL_RESET_PASS)
@Title("Reset Password")
public class ResetPassUI extends SmdUI {

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
        super.init(request,"Reset Password");
        ResetPassEditor editor = new ResetPassEditor(userInfoDao,passwordEncoder);
        
        addSmdComponents(editor);
        
        editor.setChangeHandler(() -> {
        });
        
        editor.edit(getLoggedInUser());
        
    }

}
