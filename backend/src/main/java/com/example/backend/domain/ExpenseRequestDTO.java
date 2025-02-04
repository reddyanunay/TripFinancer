package com.example.backend.domain;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExpenseRequestDTO {
    private Long memberId;
    private String name;
    private double amount;//shares
}
