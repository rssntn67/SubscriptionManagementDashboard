package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;

public interface UserInfoServiceDao extends SmdServiceDao<UserInfo> {

	List<UserInfo> searchBy(String searchText, Role role);
	UserInfo findByUsername(String name);
	
}
