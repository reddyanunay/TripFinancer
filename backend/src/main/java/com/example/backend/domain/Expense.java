package com.example.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_details")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private double share;
}
