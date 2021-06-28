package it.arsinfo.smd.service.api;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;

import java.util.List;

public interface UserInfoService extends SmdServiceBase<UserInfo> {

	List<UserInfo> searchBy(String searchText, Role role);
	UserInfo findByUsername(String name);
	UserInfo findByUsernameAndProvider(String name, UserInfo.Provider provider);
	List<Anagrafica> findByUserInfo(UserInfo userInfo);
	void add(UserInfo userInfo, String code);
	void delete(UserInfo userInfo, String code);
}
