package com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker;

import com.example.MathruAI_BackEnd.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userSub; // keep if you use it in repository queries

    @Column(nullable = false)
    private LocalDate lastPeriodDate;

    @Column(nullable = false)
    private int averageCycleLength;

    private LocalDate fertileWindowStart;
    private LocalDate fertileWindowEnd;
    private LocalDate ovulationDate;
    private LocalDate nextPeriodDate;
    private LocalDate pregnancyTestDay;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
