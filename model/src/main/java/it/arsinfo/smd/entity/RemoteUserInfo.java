package it.arsinfo.smd.entity;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"user_info_id","code"})
})
public class RemoteUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    private UserInfo userInfo;

    public RemoteUserInfo() {
    }

    public Long getId() {
        return id;
    }

    private String code;

    public RemoteUserInfo(UserInfo userInfo, String code) {
        this.userInfo = userInfo;
        this.code = code;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}