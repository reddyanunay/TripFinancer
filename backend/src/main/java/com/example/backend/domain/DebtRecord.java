package com.example.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DebtRecord {
    private String from;  // Person who owes money
    private String to;    // Person who is owed money
    private double amount; // Amount owed
}
