package edu.asu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
public class Transactions implements Serializable {
    List<Transaction> transactionList;
}
