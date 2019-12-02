package it.arsinfo.smd.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.arsinfo.smd.SmdEntity;


@Entity
public class UserInfo implements SmdEntity {

    public static String[] getRoleNames() {
        return Arrays.stream(Role.values()).map(Enum::name).toArray(String[]::new);
    }    

    public enum Role {
        ADMIN,
        USER,
        LOCKED,
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String passwordHash;

    @Column(unique=true)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

    public UserInfo() {
        username = "user";
        role = Role.USER;
    }

    public UserInfo(String name, String password, Role role) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(password);
        Objects.requireNonNull(role);

        this.username = name;
        this.passwordHash = password;
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", username=" + username + ", role=" + role + "]";
	}
}
