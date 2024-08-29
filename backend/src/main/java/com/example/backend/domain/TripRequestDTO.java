package com.example.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TripRequestDTO {
    private String tripName;
    @JsonProperty("no_of_people")
    private int noOfPeople;
    private List<String> members;
}
