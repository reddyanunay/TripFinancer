package com.example.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BillRequestDTO {
    private Long billId;
    private Double billAmount;

    @JsonIgnoreProperties({"trip","billsPaid","my_all_expenses","personalCosts"})
    private Member paidByMember;

//    private Long tripId;
    @JsonIgnoreProperties({"trip","billsPaid","my_all_expenses","personalCosts","bill"})
    private List<Expense> bill_all_expenses;

    private String description;
}
