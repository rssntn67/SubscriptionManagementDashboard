package it.arsinfo.smd.vaadin.ui;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.vaadin.model.SmdGrid;


public class UserInfoGrid extends SmdGrid<UserInfo> {


    public UserInfoGrid(String gridname) {
        super(new Grid<>(UserInfo.class),gridname);

        setColumns("username","role","data");

    }
    
}
