package com.example.MathruAI_BackEnd.entity.recoveryTracking;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
    name = "recovery_records",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"patient_id", "day_number"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Integer dayNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recovery_record_completed_tasks", joinColumns = @JoinColumn(name = "recovery_record_id"))
    @Column(name = "task_id")
    @Builder.Default
    private List<String> completedTaskIds = new java.util.ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String dailyNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
