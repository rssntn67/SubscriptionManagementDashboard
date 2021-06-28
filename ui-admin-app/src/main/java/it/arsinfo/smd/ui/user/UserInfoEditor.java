package it.arsinfo.smd.ui.user;


import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.*;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.service.api.UserInfoService;
import it.arsinfo.smd.ui.security.SecurityUtils;
import it.arsinfo.smd.ui.vaadin.SmdEntityEditor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;


public class UserInfoEditor extends SmdEntityEditor<UserInfo> {

    /**
     * 
     */
    private final TextField username = new TextField("username");
    private final ComboBox<Role> role = new ComboBox<>("Selezionare il ruolo",EnumSet.allOf(Role.class));
    private final PasswordField password = new PasswordField("password");
    private final PasswordField confirm = new PasswordField("confirm");

    private boolean persisted;
    private final UserInfoService dao;
    public UserInfoEditor(UserInfoService dao, PasswordEncoder passwordEncoder) {
        super(dao, new Binder<>(UserInfo.class));
        this.dao=dao;
        ComboBox<UserInfo.Provider> provider = new ComboBox<>("Provider",EnumSet.allOf(UserInfo.Provider.class));
        provider.setReadOnly(true);
        setComponents(getActions(),
                      new HorizontalLayout(username,role,provider),
                      new HorizontalLayout(password,confirm));
        
        getBinder().forField(username).asRequired().bind("username");
        getBinder().forField(role).asRequired().bind("role");
        getBinder().forField(provider).asRequired().bind("provider");

        Validator<String> passwordValidator = new Validator<String>() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public ValidationResult apply(String value, ValueContext context) {
                if (persisted && value.isEmpty()) {
                    return ValidationResult.ok();
                }
                if (SecurityUtils.verify(password.getValue())) {
                    return ValidationResult.ok();
                }
                return ValidationResult.error("la password deve avere minimo 8 caratteri, contenere almeno un numero, almeno un carattere minuscolo, almeno un carattere maiuscolo e almeno nun carattere speciale");
            }
        };
        getBinder().forField(password).withValidator(passwordValidator)
        .bind(
              bean -> "",
                 (bean, value) -> {
                                 if (!value.isEmpty()) {
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
            dao.findByUsernameAndProvider(username.getValue(), UserInfo.Provider.LOCAL) != null) {
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
        super.save();
        Notification.show("Utente salvato",Notification.Type.HUMANIZED_MESSAGE);
    }

    @Override
    public void focus(boolean persisted, UserInfo obj) {
        this.persisted = persisted;
        confirm.clear();
        username.setReadOnly(persisted);
        role.setReadOnly(persisted && obj.getUsername().equals("admin"));
        password.setRequiredIndicatorVisible(!persisted);
        if (obj.getProvider() != UserInfo.Provider.LOCAL) {
            getSave().setEnabled(false);
            getDelete().setEnabled(false);
            password.setVisible(false);
            confirm.setVisible(false);
            role.setReadOnly(true);
        } else {
            getSave().setEnabled(true);
            getDelete().setEnabled(true);
            password.setVisible(true);
            confirm.setVisible(true);
            role.setReadOnly(false);
        }
    }


}
