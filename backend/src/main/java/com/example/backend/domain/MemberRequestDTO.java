package com.example.backend.domain;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberRequestDTO {
    private Long memberId;
    private String name;
}
