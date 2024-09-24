package edu.asu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Trader implements Serializable {
    @Id
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "admin", nullable = false)
    private boolean admin;
}
