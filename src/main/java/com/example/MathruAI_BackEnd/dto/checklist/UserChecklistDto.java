package com.example.MathruAI_BackEnd.dto.checklist;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChecklistDto {
    private Long id;
    private String name;
    private Integer quantity;
    private String category;
    private boolean checked;
}