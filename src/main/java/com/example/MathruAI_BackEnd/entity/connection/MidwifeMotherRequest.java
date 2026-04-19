package com.example.MathruAI_BackEnd.entity.connection;

import com.example.MathruAI_BackEnd.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "midwife_mother_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidwifeMotherRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who sent the request
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    // who receives the request
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionRequestMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionRequestStatus status;

    @Column(length = 1000)
    private String message;

    // used when the request was created by matching area
    private String matchedArea;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;
}