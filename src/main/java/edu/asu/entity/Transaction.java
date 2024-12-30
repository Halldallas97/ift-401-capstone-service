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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated primary key
    @JsonIgnore
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String company;

    @Column(name = "sym", nullable = false)
    private String sym;

    @Column(name = "cost", nullable = false)
    private long cost;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Column(name = "sell_price", nullable = false)
    private double sellPrice;

    @JsonIgnore
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "evaluation", nullable = false)
    private double evaluation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false) // FK column in Transaction table
    private Portfolio portfolio;

}
