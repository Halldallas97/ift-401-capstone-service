package edu.asu.entity;

import jakarta.persistence.*;
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
    //todo wallet should be int not boolean.
    @Column(name = "wallet")
    private int wallet;
    // One-to-One relationship with Portfolio
    @OneToOne(mappedBy = "trader", cascade = CascadeType.ALL)
    private Portfolio portfolio;
}
