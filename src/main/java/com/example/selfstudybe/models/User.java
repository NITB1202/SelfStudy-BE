package com.example.selfstudybe.models;

import com.example.selfstudybe.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "User_email_key", columnNames = {"email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @NotNull
    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "avatar_link", length = Integer.MAX_VALUE)
    private String avatarLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}