package it.arsinfo.smd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.UserInfo;

public interface UserInfoDao extends JpaRepository<UserInfo, Long>{
    UserInfo findByUsername(String username);
}
