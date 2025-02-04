package com.example.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity
@Table(name = "user_details")
public class User {
    @Id
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;

    @OneToMany(mappedBy = "user",cascade= CascadeType.ALL)
    @JsonIgnore
    private List<Trip> trips;

}
