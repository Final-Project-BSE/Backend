package com.example.MathruAI_BackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String password;

    @Column(unique = true)
    private String nationalIdNumber;

    @Column(length = 1000)
    private String address;

    private String profileImageUrl;

    //old field kept for backward compatibility
    private String area;

    @Column(name = "district")
    private String district;

    @Column(name = "moh_area")
    private String mohArea;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_midwife_id")
    @JsonIgnore
    private User assignedMidwife;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}