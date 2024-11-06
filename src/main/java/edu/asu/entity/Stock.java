package edu.asu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stock {

    @Id
    @JsonIgnore
    @Column(name = "uuid", nullable = false)
    private String UUID;

    @Column(name = "company_name", nullable = false)
    private String company;

    @Column(name = "sym", nullable = false)
    private String sym;

    @Column(name = "cost", nullable = false)
    private long cost;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Column(name = "volume", nullable = false)
    private int volume;

    // Many-to-One relationship with Portfolio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @JsonIgnore // Avoid circular references in JSON serialization
    private Portfolio portfolio;
}