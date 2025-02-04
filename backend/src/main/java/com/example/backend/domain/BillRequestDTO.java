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
    private Long trip;//tripId
    private Double billAmount;

    private Long paidByMemberId;

    private List<ExpenseRequestDTO> allExpenses;

    private String description;

    //billAmount
    //billId
    //description
    //members
    //paidByMember
    //trip
}
