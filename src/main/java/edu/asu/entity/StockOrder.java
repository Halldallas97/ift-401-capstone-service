package edu.asu.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
public class StockOrder implements Serializable {
    private int quantity;
    private String name;
    private int costPerStock;
    private int total;
    private String symbol;
    private String email;
    private int newBalance;


}
