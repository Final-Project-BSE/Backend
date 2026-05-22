package com.example.MathruAI_BackEnd.entity.appointment;

import com.example.MathruAI_BackEnd.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "appointments",
    indexes = {
        @Index(name = "idx_appt_midwife_user_date", columnList = "midwife_id,user_id,appointment_date"),
        @Index(name = "idx_appt_midwife_status_date", columnList = "midwife_id,status,appointment_date"),
        @Index(name = "idx_appt_user_status_date", columnList = "user_id,status,appointment_date")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_appointment_midwife_date_start",
            columnNames = {"midwife_id", "appointment_date", "start_time"}
        )
    }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "midwife_id", nullable = false)
    private User midwife;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User patient;

    //Backward compatibility for old schema where patient_id is NOT NULL
    @Column(name = "patient_id")
    private Long legacyPatientId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    //Backward compatibility for old schema where appointment_time is NOT NULL
    @Column(name = "appointment_time")
    private LocalTime legacyAppointmentTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType appointmentType;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void syncLegacyTimeColumn() {
        this.legacyAppointmentTime = this.startTime;
        this.legacyPatientId = this.patient != null ? this.patient.getId() : null;
    }
}
