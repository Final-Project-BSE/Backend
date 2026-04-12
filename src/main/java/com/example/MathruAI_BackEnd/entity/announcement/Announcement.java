package com.example.MathruAI_BackEnd.entity.announcement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int announcementId;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;

    private LocalDateTime createdAt;

    private boolean active;

}