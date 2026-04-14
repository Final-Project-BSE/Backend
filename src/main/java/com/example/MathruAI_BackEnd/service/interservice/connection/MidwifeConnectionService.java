package com.example.MathruAI_BackEnd.service.interservice.connection;

import com.example.MathruAI_BackEnd.dto.connection.AreaMapSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AreaSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedUserProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.ConnectionRequestResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.SendConnectionRequestDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;

import java.util.List;

public interface MidwifeConnectionService {

    List<ConnectionRequestResponseDto> sendRequest(Long senderId, SendConnectionRequestDto request);

    ConnectionRequestResponseDto approveRequest(Long requestId, Long approverUserId);

    ConnectionRequestResponseDto rejectRequest(Long requestId, Long approverUserId);

    List<ConnectionRequestResponseDto> getSentRequests(Long userId);

    List<ConnectionRequestResponseDto> getReceivedRequests(Long userId);

    List<UserResponseDto> getAssignedUsersForMidwife(Long midwifeId);

    UserResponseDto getAssignedMidwifeForMother(Long motherUserId);

    UserResponseDto updateAssignedMotherProfile(
            Long midwifeId,
            Long motherUserId,
            AssignedUserProfileUpdateRequestDto request
    );

    List<UserResponseDto> searchUsersByDistrictAndMohArea(Long requesterId, AreaSearchRequestDto request);

    List<UserResponseDto> searchMappableUsersByDistrictAndMohArea(Long requesterId, AreaMapSearchRequestDto request);
}