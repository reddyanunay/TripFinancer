package com.example.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "bill_details")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private Double billAmount;

    @ManyToOne
    @JoinColumn(name = "paid_by_member_id", referencedColumnName = "memberId")
    @JsonIgnoreProperties({"billsPaid", "paidByMember", "trip", "my_all_expenses","personalCosts"})
    private Member paidByMember;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "tripId")
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses","allBills","no_of_bills","no_of_people","total_cost","allMembers"})
    private Trip trip;

    @OneToMany(mappedBy = "bill",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses","bill"})
    private List<Expense> bill_all_expenses;

    private String description;
}
