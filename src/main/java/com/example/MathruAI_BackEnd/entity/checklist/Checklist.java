package com.example.MathruAI_BackEnd.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long midwifeId;

    private String name;

    private Integer quantity;

    private String category;
}