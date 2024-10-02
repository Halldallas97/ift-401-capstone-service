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

    @Column(name = "initial_price", nullable = false)
    private long initialPrice;

    @Column(name = "strike_price", nullable = false)
    private long strikePrice;

    @Column(name = "volume", nullable = false)
    private int volume;

    // Many-to-One relationship with Portfolio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @JsonIgnore // Avoid circular references in JSON serialization
    private Portfolio portfolio;
}