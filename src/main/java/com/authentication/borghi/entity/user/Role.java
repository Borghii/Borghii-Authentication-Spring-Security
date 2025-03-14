package com.authentication.borghi.entity.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    public Role(User user, String roleName) {
        this.user = user;
        this.roleName = roleName;
    }

    @Id
    @Column(name = "id_role")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRole;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "role", nullable = false)
    private String roleName;


}
