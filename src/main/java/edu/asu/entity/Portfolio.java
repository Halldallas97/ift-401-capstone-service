package edu.asu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Portfolio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the portfolio ID
    private Long id;

    // Many-to-One relationship with Trader (foreign key to Trader)
    @OneToOne
    @JoinColumn(name = "trader_email", referencedColumnName = "email") // References Trader's email as foreign key
    private Trader trader;

    // One-to-Many relationship with Stock
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stocks;
}
