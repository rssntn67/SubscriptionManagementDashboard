package it.arsinfo.smd.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;

public interface UserInfoDao extends JpaRepository<UserInfo, Long>{
    UserInfo findByUsernameAndProvider(String username, UserInfo.Provider provider);
    List<UserInfo> findByUsernameContainingIgnoreCase(String username);
    List<UserInfo> findByRole(Role role);
    List<UserInfo> findByUsernameContainingIgnoreCaseAndRole(String username, Role role);
}
