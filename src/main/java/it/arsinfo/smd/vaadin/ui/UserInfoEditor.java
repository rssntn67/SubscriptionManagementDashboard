package it.arsinfo.smd.vaadin.ui;


import java.util.EnumSet;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.security.SecurityUtils;
import it.arsinfo.smd.vaadin.model.SmdEditor;


public class UserInfoEditor extends SmdEditor<UserInfo> {

    /**
     * 
     */
    private final TextField username = new TextField("username");
    private final ComboBox<Role> role = new ComboBox<Role>("Selezionare il ruolo",EnumSet.allOf(Role.class));
    private final PasswordField password = new PasswordField("password");
    private final PasswordField confirm = new PasswordField("confirm");

    private final Button resetPassword = new Button("Reset Password");

    private boolean passwordRequired;
    public UserInfoEditor(UserInfoDao repo, PasswordEncoder passwordEncoder) {
        super(repo, new Binder<>(UserInfo.class));
        setComponents(getActions(),username,role,password,confirm,resetPassword);
        
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
        resetPassword.addClickListener(e->resetPassword());

        setVisible(false);
    }
    
    private void resetPassword() {
        if (password.isEmpty()) {
            Notification.show("Il reset della Password è fallito",
                              "il campo password deve essere valorizzato",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        
        if (!SecurityUtils.verify(password.getValue())) {
            Notification.show("Il reset della Password è fallito",
                              "la password deve avere minimo 8 caratteri, contenere almeno un numero, almeno un carattere minuscolo, almeno un carattere maiuscolo e almeno nun carattere speciale",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }

        if (confirm.isEmpty())  {
            Notification.show("Il reset della Password è fallito",
                              "il campo confirm deve essere valorizzato",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        
        if (!confirm.getValue().equals(password.getValue())) {
            Notification.show("Il reset della Password è fallito",
                              "le password non corrispondono",
                              Notification.Type.HUMANIZED_MESSAGE);
            return;
        }
        super.save();
        Notification.show("Password aggiornato",Notification.Type.HUMANIZED_MESSAGE);
    }

    @Override 
    public void save() {
        resetPassword();
    }

    @Override
    public void focus(boolean persisted, UserInfo obj) {
        this.passwordRequired = !persisted;
        password.setRequiredIndicatorVisible(passwordRequired);
    }
    
    private Validator<String> passwordValidator = new Validator<String>() {

        BeanValidator passwordBeanValidator = new BeanValidator(UserInfo.class, "passwordHash");

        @Override
        public ValidationResult apply(String value, ValueContext context) {
                if (!passwordRequired && value.isEmpty()) {
                        // No password required and field is empty
                        // OK regardless of other restrictions as the empty value will
                        // not be used
                        return ValidationResult.ok();
                } else {
                        return passwordBeanValidator.apply(value, context);
                }
        }
};


}
