package com.example.MathruAI_BackEnd.dto.connection;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class MapUserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private String address;
    private String area;
    private String district;
    private String mohArea;

    private Double latitude;
    private Double longitude;

    private Long assignedMidwifeId;
    private String assignedMidwifeName;

    private Set<Role> roles;
}