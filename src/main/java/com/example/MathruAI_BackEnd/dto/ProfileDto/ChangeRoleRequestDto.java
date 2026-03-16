package com.example.MathruAI_BackEnd.dto.ProfileDto;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChangeRoleRequestDto {
    private Set<Role> roles;
}