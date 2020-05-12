package it.arsinfo.smd.ui.user;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.UserInfoServiceDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class UserInfoSearch extends SmdSearch<UserInfo> {

    private String searchText;
    private Role role;

    private final UserInfoServiceDao dao;
    public UserInfoSearch(UserInfoServiceDao dao) {
        super(dao);
        this.dao=dao;
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
    	return dao.searchBy(searchText,role);
     }

}
