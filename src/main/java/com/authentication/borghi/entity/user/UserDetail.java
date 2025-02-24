package com.authentication.borghi.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_details")
public class UserDetail {

    public UserDetail(String name, String surname, User user) {
        this.name = name;
        this.surname = surname;
        this.user = user;
    }

    @Id
    @Column(name = "id_user_details")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUserDetails;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

}
