package com.example.backend.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "member_details")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToMany(mappedBy = "paidByMember",cascade=CascadeType.ALL)
    private List<Bill> billsPaid;

    @OneToMany(mappedBy = "member")
    private List<Expense> my_all_expenses;

    private String name;
    private double personalCosts;
}
