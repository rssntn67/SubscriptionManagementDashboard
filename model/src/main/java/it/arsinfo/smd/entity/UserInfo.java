package it.arsinfo.smd.entity;

import java.util.*;

import javax.persistence.*;


@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"username","provider"})
})
public class UserInfo implements SmdEntity {

    public static String[] getRoleNames() {
        return Arrays.stream(Role.values()).map(Enum::name).toArray(String[]::new);
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public enum Role {
        ADMIN,
        USER,
        LOCKED,
        SUBSCRIBED,
        UNSUBSCRIBED
    }

    public enum Provider {
        FACEBOOK,
        GOOGLE,
        LOCAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String passwordHash;

    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider=Provider.LOCAL;

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

	@Override
	public String getHeader() {
		return username + ":" + role;
	}
}
