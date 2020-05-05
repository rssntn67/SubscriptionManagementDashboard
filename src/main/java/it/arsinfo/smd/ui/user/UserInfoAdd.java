package it.arsinfo.smd.ui.user;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.ui.vaadin.SmdAdd;

public class UserInfoAdd extends SmdAdd<UserInfo> {

    public UserInfoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public UserInfo generate() {
        return new UserInfo();
    }

}
