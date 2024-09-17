package com.example.backend.domain;

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
@Table(name = "trip_details")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;
    private String trip_name;
    private int no_of_people;
    private double total_cost;
    private int no_of_bills;

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses"})
    private List<Bill> allBills;

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses","personalCosts","my_all_expenses","billsPaid"})
    private List<Member> allMembers;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    @JsonIgnoreProperties({"billAmount", "paidByMember", "trip", "bill_all_expenses","allBills","no_of_bills","no_of_people","total_cost","allMembers"})
    private User user;
    public void updateTotalCostAndBillCount(double billAmount) {
        // Update the total cost
        this.total_cost += billAmount;

        // Adjust the number of bills
        if (billAmount > 0) {
            this.no_of_bills += 1;
        } else if (billAmount < 0 && this.no_of_bills > 0) {
            this.no_of_bills -= 1;
        }
    }
}
