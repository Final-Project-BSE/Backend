package com.example.MathruAI_BackEnd.entity.checklist;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "checklist_id"})
        }
)
public class UserChecklistStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private boolean checked;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;
}