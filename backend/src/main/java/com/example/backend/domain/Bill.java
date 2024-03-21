package com.example.backend.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "bill_details")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private Double billAmount;

    @ManyToOne
    @JoinColumn(name = "paid_by_member_id", referencedColumnName = "memberId")
    private Member paidByMember;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "tripId")
    private Trip trip;

    @OneToMany(mappedBy = "bill")
    private List<Expense> bill_all_expenses;

    private String description;
}
