package com.example.MathruAI_BackEnd.dto.ProfileDto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChangeEmailRequestDto {
    private String newEmail;
    private String currentPassword;
}