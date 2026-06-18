package mg.itu.aquanova.security.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRoleModels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private UserModels user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private RoleModels role;

    public UserRoleModels() {
    }

    public UserRoleModels(UserModels user, RoleModels role) {
        this.user = user;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModels getUser() {
        return user;
    }

    public void setUser(UserModels user) {
        this.user = user;
    }

    public RoleModels getRole() {
        return role;
    }

    public void setRole(RoleModels role) {
        this.role = role;
    }
    
}