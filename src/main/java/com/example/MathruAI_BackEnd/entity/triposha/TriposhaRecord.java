package com.example.MathruAI_BackEnd.entity.triposha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TriposhaRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Long midwifeId;

    private LocalDate distributionDate;
    private int quantity;

    private String status; // GIVEN, PENDING, MISSED

    private LocalDate nextDueDate;

    private String notes;
}
