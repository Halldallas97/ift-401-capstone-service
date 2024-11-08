package edu.asu.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Data
public class Stocks implements Serializable {
    private List<Stock> stocks;
}
