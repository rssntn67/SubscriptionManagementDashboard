package it.arsinfo.smd.dao;

import it.arsinfo.smd.entity.RemoteUserInfo;
import it.arsinfo.smd.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemoteUserInfoDao extends JpaRepository<RemoteUserInfo, Long>{
    List<RemoteUserInfo> findByCode(String code);
    List<RemoteUserInfo> findByUserInfo(UserInfo userInfo);
    RemoteUserInfo findByUserInfoAndCode(UserInfo userInfo, String code);
}
