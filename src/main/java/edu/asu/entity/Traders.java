package edu.asu.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Builder
@Data
public class Traders implements Serializable {
    List<Trader> traders;
}
