package com.authentication.borghi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

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
