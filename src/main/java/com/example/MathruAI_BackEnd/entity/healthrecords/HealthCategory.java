package com.example.MathruAI_BackEnd.entity.healthrecords;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "health_categories")
public class HealthCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Column(name = "color_class")
    private String colorClass;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<HealthRecord> records;
}
