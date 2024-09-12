package com.example.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "expense_details")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne
    @JoinColumn(name = "bill_id",referencedColumnName = "billId")
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses"})
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses","billsPaid", "personalCosts","my_all_expenses"})
    private Member member;

    private double share;
}
