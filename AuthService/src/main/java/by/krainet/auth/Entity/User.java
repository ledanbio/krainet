package by.krainet.auth.Entity;


import by.krainet.common.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email",columnList = "email"),
                @Index(name = "idx_users_username",columnList = "username")
        })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private Long id;

    @Column(name= "username",nullable = false, unique = true)
    private String username;

    @Column(name= "email",nullable = false, unique = true)
    private String email;

    @Column(name= "password", nullable = false)
    private String password;

    @Column(name= "first_name", nullable = false)
    private String firstName;

    @Column(name= "last_name", nullable = true)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private ROLE role;
}
