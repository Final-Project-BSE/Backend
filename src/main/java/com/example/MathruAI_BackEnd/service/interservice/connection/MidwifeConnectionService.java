package com.example.MathruAI_BackEnd.service.interservice.connection;

import com.example.MathruAI_BackEnd.dto.connection.AreaMapSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AreaSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedPatientDetailResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedUserProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.ConnectionRequestResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.MapUserResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.SendConnectionRequestDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;

import java.util.List;

public interface MidwifeConnectionService {

    List<ConnectionRequestResponseDto> sendRequest(Long senderId, SendConnectionRequestDto request);

    ConnectionRequestResponseDto approveRequest(Long requestId, Long approverUserId);

    ConnectionRequestResponseDto rejectRequest(Long requestId, Long approverUserId);

    ConnectionRequestResponseDto cancelRequest(Long requestId, Long requesterUserId);

    void cancelAssignedMidwifeForMother(Long motherUserId, Long requesterUserId);

    void cancelAssignedMotherForMidwife(Long midwifeId, Long motherUserId);

    List<ConnectionRequestResponseDto> getSentRequests(Long userId);

    List<ConnectionRequestResponseDto> getReceivedRequests(Long userId);

    List<UserResponseDto> getAssignedUsersForMidwife(Long midwifeId);

    AssignedPatientDetailResponseDto getAssignedPatientDetail(Long midwifeId, Long motherUserId);

    UserResponseDto getAssignedMidwifeForMother(Long motherUserId);

    UserResponseDto updateAssignedMotherProfile(
            Long midwifeId,
            Long motherUserId,
            AssignedUserProfileUpdateRequestDto request
    );

    List<UserResponseDto> searchUsersByDistrictAndMohArea(Long requesterId, AreaSearchRequestDto request);

    List<UserResponseDto> searchMappableUsersByDistrictAndMohArea(Long requesterId, AreaMapSearchRequestDto request);

    List<MapUserResponseDto> getAllMappableOppositeUsers(Long requesterId);
}