package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "member_details")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "trip_id",referencedColumnName = "tripId")
    private Trip trip;

    @OneToMany(mappedBy = "paidByMember",cascade=CascadeType.ALL)
    private List<Bill> billsPaid;

    @OneToMany(mappedBy = "member")
    private List<Expense> my_all_expenses;

    private String name;
    private double personalCosts;
}
