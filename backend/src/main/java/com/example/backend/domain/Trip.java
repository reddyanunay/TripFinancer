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

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL)
    private List<Bill> allBills;

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL)
    private List<Member> allMembers;
}
