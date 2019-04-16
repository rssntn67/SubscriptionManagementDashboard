package it.arsinfo.smd.ui.vaadin;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.UserInfo;


public class UserInfoGrid extends SmdGrid<UserInfo> {


    public UserInfoGrid(String gridname) {
        super(new Grid<>(UserInfo.class),gridname);

        setColumns("username","role","data");

    }
    
}
