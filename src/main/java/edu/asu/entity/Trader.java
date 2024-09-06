package edu.asu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String id;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "first_name", nullable = false)
    private String fname;
    @Column(name = "last_name", nullable = false)
    private String lname;
    @Column(name = "email", nullable = false)
    private String email;
}
