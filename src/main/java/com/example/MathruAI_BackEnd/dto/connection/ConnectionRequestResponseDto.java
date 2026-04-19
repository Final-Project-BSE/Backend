package com.example.MathruAI_BackEnd.dto.connection;

import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestMethod;
import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ConnectionRequestResponseDto {
    private Long id;

    private Long senderId;
    private String senderEmail;
    private String senderFirstName;
    private String senderLastName;
    private Set<?> senderRoles;

    private Long receiverId;
    private String receiverEmail;
    private String receiverFirstName;
    private String receiverLastName;
    private Set<?> receiverRoles;

    private ConnectionRequestMethod method;
    private ConnectionRequestStatus status;
    private String message;
    private String matchedArea;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}