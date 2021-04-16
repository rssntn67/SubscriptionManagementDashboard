package it.arsinfo.smd.ui.service.api;

import java.util.List;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;

public interface UserInfoService extends SmdServiceBase<UserInfo> {

	List<UserInfo> searchBy(String searchText, Role role);
	UserInfo findByUsername(String name);
	
}
