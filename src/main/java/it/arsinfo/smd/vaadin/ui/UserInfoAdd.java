package it.arsinfo.smd.vaadin.ui;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.vaadin.model.SmdAdd;

public class UserInfoAdd extends SmdAdd<UserInfo> {

    public UserInfoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public UserInfo generate() {
        return new UserInfo();
    }

}
