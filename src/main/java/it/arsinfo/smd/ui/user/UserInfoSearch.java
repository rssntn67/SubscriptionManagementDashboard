package it.arsinfo.smd.ui.user;

import java.util.EnumSet;
import java.util.List;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class UserInfoSearch extends SmdSearch<UserInfo> {

    private String searchText;
    private Role role;

    public UserInfoSearch(UserInfoDao userInfoDao) {
        super(userInfoDao);
        TextField filter = new TextField();
        filter.setPlaceholder("Cerca per username");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> {
            searchText = e.getValue();
            onChange();
        });

        ComboBox<Role> filterRole = new ComboBox<Role>(null,
                EnumSet.allOf(Role.class));
        filterRole.setPlaceholder("Cerca per ROLE");

        filterRole.addSelectionListener(e -> {
            if (e.getValue() == null) {
                role = null;
            } else {
                role = e.getSelectedItem().get();
            }
            onChange();
        });
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(filterRole);
        layout.addComponent(filter);

        setComponents(layout);
    }

    @Override
    public List<UserInfo> find() {
        if (StringUtils.isEmpty(searchText) && role == null) {
            return findAll();
        }
        if (StringUtils.isEmpty(searchText)) {
            return ((UserInfoDao) getRepo()).findByRole(role);
        }
        if (role == null ) {
            return ((UserInfoDao) getRepo()).findByUsernameContainingIgnoreCase(searchText);
        }
        return ((UserInfoDao) getRepo()).findByUsernameContainingIgnoreCaseAndRole(searchText, role);
     }

}
