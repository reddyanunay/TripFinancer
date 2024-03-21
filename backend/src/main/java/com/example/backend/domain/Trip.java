package com.example.backend.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "trip_details")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trip_id;
    private String trip_name;
    private int no_of_people;
    private double total_cost;
    private int no_of_bills;

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL)
    private List<Bill> allBills;

    @OneToMany(mappedBy = "trip",cascade=CascadeType.ALL)
    private List<Member> allMembers;
}
