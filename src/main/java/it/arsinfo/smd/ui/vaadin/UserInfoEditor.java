package it.arsinfo.smd.ui.vaadin;


import java.util.EnumSet;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.ui.security.SecurityUtils;


public class UserInfoEditor extends SmdEditor<UserInfo> {

    /**
     * 
     */
    private final TextField username = new TextField("username");
    private final ComboBox<Role> role = new ComboBox<Role>("Selezionare il ruolo",EnumSet.allOf(Role.class));
    private final PasswordField password = new PasswordField("password");
    private final PasswordField confirm = new PasswordField("confirm");

    private boolean persisted;
    public UserInfoEditor(UserInfoDao repo, PasswordEncoder passwordEncoder) {
        super(repo, new Binder<>(UserInfo.class));
        setComponents(getActions(),
                      new HorizontalLayout(username,role),
                      new HorizontalLayout(password,confirm));
        
        getBinder().forField(username).asRequired().bind("username");
        getBinder().forField(role).asRequired().bind("role");
        getBinder().forField(password).withValidator(passwordValidator)
        .bind(
              bean -> "",
                 (bean, value) -> {
                                 if (value.isEmpty()) {
                                     } else {
                                 bean.setPasswordHash(passwordEncoder.encode(value));
                                     }
                                 });

        setVisible(false);
    }
    
    @Override 
    public void save() {
        if (!persisted && username.isEmpty()) {
            Notification.show("Utente non salvato",
                              "nuovo utente richiede set della username",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        
        if (!persisted &&
            ((UserInfoDao)getRepositoryDao()).findByUsername(username.getValue()) != null) {
            Notification.show("Utente non salvato",
                              "username esiste",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        
        if (!persisted && password.isEmpty()) {
            Notification.show("Utente non salvato",
                              "nuovo utente richiede set della password",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        if (!password.isEmpty() && !SecurityUtils.verify(password.getValue())) {
            Notification.show("Utente non salvato, il set della Password è fallito",
                              "la password deve avere minimo 8 caratteri, contenere almeno un numero, almeno un carattere minuscolo, almeno un carattere maiuscolo e almeno nun carattere speciale",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        if ((!password.isEmpty()) && confirm.isEmpty())  {
            Notification.show("Utente non salvato, reset della Password fallito",
                              "il campo confirm deve essere valorizzato",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        
        if (!confirm.getValue().equals(password.getValue())) {
            Notification.show("Utente non salvato, reset della Password fallito",
                              "le password non corrispondono",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        if (get().getId() == null ) {
            
        }
        super.save();
        Notification.show("Utente salvato",Notification.Type.HUMANIZED_MESSAGE);
    }

    @Override
    public void focus(boolean persisted, UserInfo obj) {
        this.persisted = persisted;
        confirm.clear();
        username.setReadOnly(persisted);
        password.setRequiredIndicatorVisible(!persisted);
    }
    
    private Validator<String> passwordValidator = new Validator<String>() {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        BeanValidator passwordBeanValidator = new BeanValidator(UserInfo.class, "passwordHash");

        @Override
        public ValidationResult apply(String value, ValueContext context) {
            if (persisted && value.isEmpty()) {
                return ValidationResult.ok();
            }            
            if (!SecurityUtils.verify(password.getValue())) {
                return ValidationResult.error("la password deve avere minimo 8 caratteri, contenere almeno un numero, almeno un carattere minuscolo, almeno un carattere maiuscolo e almeno nun carattere speciale");
            }
            return passwordBeanValidator.apply(value, context);
        }
};


}
