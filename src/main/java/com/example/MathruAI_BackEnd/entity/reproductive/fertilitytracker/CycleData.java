//package com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker;
//
//import com.example.MathruAI_BackEnd.entity.User;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "cycle_data")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CycleData {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private LocalDate lastPeriodDate;
//
//    @Column(nullable = false)
//    private int averageCycleLength;
//
//    // 🔑 Foreign key reference ONLY (no @ManyToOne)
//    @Column(name = "user_id", nullable = false)
//    private Long userId;
//
//
//
//
//}
package com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cycle_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CycleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Request data
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate lastPeriodDate;

    @Column(nullable = false)
    private int averageCycleLength;

    // 🔹 Calculated response data
    @Column(nullable = false)
    private LocalDate fertileWindowStart;

    @Column(nullable = false)
    private LocalDate fertileWindowEnd;

    @Column(nullable = false)
    private LocalDate ovulationDate;

    @Column(nullable = false)
    private LocalDate nextPeriodDate;

    @Column(nullable = false)
    private LocalDate pregnancyTestDay;
}
