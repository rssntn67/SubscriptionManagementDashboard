package it.arsinfo.smd.ui.vaadin;

import it.arsinfo.smd.entity.UserInfo;

public class UserInfoAdd extends SmdAdd<UserInfo> {

    public UserInfoAdd(String caption) {
        super(caption);
    }
    
    @Override
    public UserInfo generate() {
        return new UserInfo();
    }

}
