package com.example.MathruAI_BackEnd.service.impl.connection;

import com.example.MathruAI_BackEnd.dto.connection.AreaMapSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AreaSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedPatientDetailResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedUserProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.ConnectionRequestResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.SendConnectionRequestDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestMethod;
import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestStatus;
import com.example.MathruAI_BackEnd.entity.connection.MidwifeMotherRequest;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.connection.MidwifeMotherRequestRepository;
import com.example.MathruAI_BackEnd.service.interservice.connection.MidwifeConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MidwifeConnectionServiceImpl implements MidwifeConnectionService {

    private final UserRepository userRepository;
    private final MidwifeMotherRequestRepository requestRepository;

    private static final Set<Role> MOTHER_ROLES = Set.of(
            Role.HOPE_TO_PREGNANT_MOTHER,
            Role.PREGNANT_MOTHER,
            Role.POST_PREGNANT_MOTHER
    );

    private static final Set<Role> MIDWIFE_ROLE = Set.of(Role.MIDWIFE);

    @Override
    public List<ConnectionRequestResponseDto> sendRequest(Long senderId, SendConnectionRequestDto request) {
        User sender = getUserOrThrow(senderId);

        ConnectionRequestMethod method = parseMethod(request.getMethod());

        boolean senderIsMidwife = hasRole(sender, Role.MIDWIFE);
        boolean senderIsMotherSide = hasAnyRole(sender, MOTHER_ROLES);

        if (!senderIsMidwife && !senderIsMotherSide) {
            throw new RuntimeException("Only midwives and mother-side users can send connection requests.");
        }

        if (senderIsMidwife && senderIsMotherSide) {
            throw new RuntimeException("User cannot be both midwife and mother-side user for this connection flow.");
        }

        List<User> targets = switch (method) {
            case EMAIL -> resolveTargetsByEmail(request.getTargetEmail());
            case AREA -> resolveTargetsByArea(sender, request.getTargetArea());
        };

        if (targets.isEmpty()) {
            throw new RuntimeException("No matching users found for the given request.");
        }

        List<ConnectionRequestResponseDto> responses = new ArrayList<>();

        for (User receiver : targets) {
            validateSenderReceiverPair(sender, receiver);

            if (Objects.equals(sender.getId(), receiver.getId())) {
                continue;
            }

            if (hasActivePendingRequestBetween(sender.getId(), receiver.getId())) {
                continue;
            }

            if (isAlreadyAssignedPair(sender, receiver)) {
                continue;
            }

            MidwifeMotherRequest entity = MidwifeMotherRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .method(method)
                    .status(ConnectionRequestStatus.PENDING)
                    .message(request.getMessage())
                    .matchedArea(method == ConnectionRequestMethod.AREA ? normalizeText(request.getTargetArea()) : null)
                    .createdAt(LocalDateTime.now())
                    .respondedAt(null)
                    .build();

            responses.add(mapRequest(requestRepository.save(entity)));
        }

        if (responses.isEmpty()) {
            throw new RuntimeException("No new requests were created. They may already exist or users may already be assigned.");
        }

        return responses;
    }

    @Override
    public ConnectionRequestResponseDto approveRequest(Long requestId, Long approverUserId) {
        MidwifeMotherRequest entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found with id: " + requestId));

        if (!Objects.equals(entity.getReceiver().getId(), approverUserId)) {
            throw new RuntimeException("Only the receiver can approve this request.");
        }

        if (entity.getStatus() != ConnectionRequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be approved.");
        }

        User sender = entity.getSender();
        User receiver = entity.getReceiver();

        validateSenderReceiverPair(sender, receiver);

        User midwife;
        User motherUser;

        if (hasRole(sender, Role.MIDWIFE)) {
            midwife = sender;
            motherUser = receiver;
        } else {
            midwife = receiver;
            motherUser = sender;
        }

        if (motherUser.getAssignedMidwife() != null &&
                !Objects.equals(motherUser.getAssignedMidwife().getId(), midwife.getId())) {
            throw new RuntimeException("This user is already assigned to another midwife.");
        }

        motherUser.setAssignedMidwife(midwife);
        userRepository.save(motherUser);

        entity.setStatus(ConnectionRequestStatus.APPROVED);
        entity.setRespondedAt(LocalDateTime.now());
        requestRepository.save(entity);

        return mapRequest(entity);
    }

    @Override
    public ConnectionRequestResponseDto rejectRequest(Long requestId, Long approverUserId) {
        MidwifeMotherRequest entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found with id: " + requestId));

        if (!Objects.equals(entity.getReceiver().getId(), approverUserId)) {
            throw new RuntimeException("Only the receiver can reject this request.");
        }

        if (entity.getStatus() != ConnectionRequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be rejected.");
        }

        entity.setStatus(ConnectionRequestStatus.REJECTED);
        entity.setRespondedAt(LocalDateTime.now());

        return mapRequest(requestRepository.save(entity));
    }

    @Override
    public ConnectionRequestResponseDto cancelRequest(Long requestId, Long requesterUserId) {
        MidwifeMotherRequest entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found with id: " + requestId));

        if (!Objects.equals(entity.getSender().getId(), requesterUserId)) {
            throw new RuntimeException("Only the sender can cancel this request.");
        }

        if (entity.getStatus() != ConnectionRequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled.");
        }

        entity.setStatus(ConnectionRequestStatus.CANCELLED);
        entity.setRespondedAt(LocalDateTime.now());

        return mapRequest(requestRepository.save(entity));
    }

    @Override
    public void cancelAssignedMidwifeForMother(Long motherUserId, Long requesterUserId) {
        User mother = getUserOrThrow(motherUserId);

        if (!hasAnyRole(mother, MOTHER_ROLES)) {
            throw new RuntimeException("User is not a mother-side user.");
        }

        if (!Objects.equals(mother.getId(), requesterUserId)) {
            throw new RuntimeException("Only the mother-side user can cancel their assigned midwife.");
        }

        if (mother.getAssignedMidwife() == null) {
            throw new RuntimeException("No midwife is assigned to this user.");
        }

        mother.setAssignedMidwife(null);
        userRepository.save(mother);
    }

    @Override
    public void cancelAssignedMotherForMidwife(Long midwifeId, Long motherUserId) {
        User midwife = getUserOrThrow(midwifeId);
        User mother = getUserOrThrow(motherUserId);

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        if (!hasAnyRole(mother, MOTHER_ROLES)) {
            throw new RuntimeException("Selected user is not a mother-side user.");
        }

        if (mother.getAssignedMidwife() == null ||
                !Objects.equals(mother.getAssignedMidwife().getId(), midwifeId)) {
            throw new RuntimeException("This mother is not assigned to the given midwife.");
        }

        mother.setAssignedMidwife(null);
        userRepository.save(mother);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionRequestResponseDto> getSentRequests(Long userId) {
        return requestRepository.findBySenderIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionRequestResponseDto> getReceivedRequests(Long userId) {
        return requestRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAssignedUsersForMidwife(Long midwifeId) {
        User midwife = getUserOrThrow(midwifeId);

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        return userRepository.findAssignedUsersForMidwife(midwifeId, MOTHER_ROLES)
                .stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssignedPatientDetailResponseDto getAssignedPatientDetail(Long midwifeId, Long motherUserId) {
        User patient = getAssignedPatientOrThrow(midwifeId, motherUserId);
        return mapAssignedPatientDetail(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getAssignedMidwifeForMother(Long motherUserId) {
        User mother = getUserOrThrow(motherUserId);

        if (!hasAnyRole(mother, MOTHER_ROLES)) {
            throw new RuntimeException("User is not a mother-side user.");
        }

        if (mother.getAssignedMidwife() == null) {
            throw new RuntimeException("No midwife is assigned to this user.");
        }

        return mapUser(mother.getAssignedMidwife());
    }

    @Override
    public UserResponseDto updateAssignedMotherProfile(
            Long midwifeId,
            Long motherUserId,
            AssignedUserProfileUpdateRequestDto request
    ) {
        User mother = getAssignedPatientOrThrow(midwifeId, motherUserId);

        if (request.getFirstName() != null) mother.setFirstName(request.getFirstName());
        if (request.getLastName() != null) mother.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) mother.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) mother.setDateOfBirth(request.getDateOfBirth());

        if (request.getNationalIdNumber() != null &&
                !request.getNationalIdNumber().equals(mother.getNationalIdNumber()) &&
                userRepository.existsByNationalIdNumber(request.getNationalIdNumber())) {
            throw new RuntimeException("National ID number is already in use.");
        }

        if (request.getNationalIdNumber() != null) mother.setNationalIdNumber(request.getNationalIdNumber());
        if (request.getAddress() != null) mother.setAddress(request.getAddress());
        if (request.getProfileImageUrl() != null) mother.setProfileImageUrl(request.getProfileImageUrl());

        if (request.getArea() != null) mother.setArea(request.getArea());
        if (request.getDistrict() != null) mother.setDistrict(normalizeText(request.getDistrict()));
        if (request.getMohArea() != null) mother.setMohArea(normalizeText(request.getMohArea()));
        if (request.getLatitude() != null) mother.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) mother.setLongitude(request.getLongitude());

        return mapUser(userRepository.save(mother));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsersByDistrictAndMohArea(Long requesterId, AreaSearchRequestDto request) {
        User requester = getUserOrThrow(requesterId);

        validateAreaSearchRequest(request.getDistrict(), request.getMohArea());

        Collection<Role> targetRoles = resolveOppositeRolesForSearch(requester);

        return userRepository.findByDistrictAndMohAreaAndAnyRole(
                        normalizeText(request.getDistrict()),
                        normalizeText(request.getMohArea()),
                        targetRoles
                )
                .stream()
                .filter(user -> !Objects.equals(user.getId(), requesterId))
                .filter(user -> !hasActivePendingRequestBetween(requesterId, user.getId()))
                .filter(user -> !isAlreadyAssignedPair(requester, user))
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchMappableUsersByDistrictAndMohArea(Long requesterId, AreaMapSearchRequestDto request) {
        User requester = getUserOrThrow(requesterId);

        validateAreaSearchRequest(request.getDistrict(), request.getMohArea());

        Collection<Role> targetRoles = resolveOppositeRolesForSearch(requester);

        return userRepository.findMappableUsersByDistrictAndMohAreaAndAnyRole(
                        normalizeText(request.getDistrict()),
                        normalizeText(request.getMohArea()),
                        targetRoles
                )
                .stream()
                .filter(user -> !Objects.equals(user.getId(), requesterId))
                .filter(user -> !hasActivePendingRequestBetween(requesterId, user.getId()))
                .filter(user -> !isAlreadyAssignedPair(requester, user))
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    private void validateAreaSearchRequest(String district, String mohArea) {
        if (district == null || district.isBlank()) {
            throw new RuntimeException("District is required.");
        }
        if (mohArea == null || mohArea.isBlank()) {
            throw new RuntimeException("MOH area is required.");
        }
    }

    private Collection<Role> resolveOppositeRolesForSearch(User requester) {
        boolean requesterIsMidwife = hasRole(requester, Role.MIDWIFE);
        boolean requesterIsMotherSide = hasAnyRole(requester, MOTHER_ROLES);

        if (!requesterIsMidwife && !requesterIsMotherSide) {
            throw new RuntimeException("User does not belong to a valid connection role.");
        }

        if (requesterIsMidwife && requesterIsMotherSide) {
            throw new RuntimeException("User cannot be both midwife and mother-side user for this connection flow.");
        }

        return requesterIsMidwife ? MOTHER_ROLES : MIDWIFE_ROLE;
    }

    private List<User> resolveTargetsByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Target email is required for EMAIL method.");
        }

        return userRepository.findByEmail(email.trim())
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    private List<User> resolveTargetsByArea(User sender, String area) {
        if (area == null || area.isBlank()) {
            throw new RuntimeException("Target area is required for AREA method.");
        }

        Collection<Role> targetRoles = hasRole(sender, Role.MIDWIFE) ? MOTHER_ROLES : MIDWIFE_ROLE;
        return userRepository.findByAreaAndAnyRole(normalizeText(area), targetRoles)
                .stream()
                .filter(user -> !Objects.equals(user.getId(), sender.getId()))
                .filter(user -> !hasActivePendingRequestBetween(sender.getId(), user.getId()))
                .filter(user -> !isAlreadyAssignedPair(sender, user))
                .toList();
    }

    private void validateSenderReceiverPair(User sender, User receiver) {
        boolean senderIsMidwife = hasRole(sender, Role.MIDWIFE);
        boolean receiverIsMidwife = hasRole(receiver, Role.MIDWIFE);

        boolean senderIsMotherSide = hasAnyRole(sender, MOTHER_ROLES);
        boolean receiverIsMotherSide = hasAnyRole(receiver, MOTHER_ROLES);

        if (senderIsMidwife == receiverIsMidwife) {
            throw new RuntimeException("Connection must happen between a midwife and a mother-side user.");
        }

        if (senderIsMotherSide == receiverIsMotherSide) {
            throw new RuntimeException("Connection must happen between opposite roles.");
        }
    }

    private boolean hasActivePendingRequestBetween(Long userAId, Long userBId) {
        return requestRepository.existsBySenderIdAndReceiverIdAndStatusOrReceiverIdAndSenderIdAndStatus(
                userAId,
                userBId,
                ConnectionRequestStatus.PENDING,
                userAId,
                userBId,
                ConnectionRequestStatus.PENDING
        );
    }

    private boolean isAlreadyAssignedPair(User sender, User receiver) {
        User midwife;
        User mother;

        if (hasRole(sender, Role.MIDWIFE) && hasAnyRole(receiver, MOTHER_ROLES)) {
            midwife = sender;
            mother = receiver;
        } else if (hasRole(receiver, Role.MIDWIFE) && hasAnyRole(sender, MOTHER_ROLES)) {
            midwife = receiver;
            mother = sender;
        } else {
            return false;
        }

        return mother.getAssignedMidwife() != null
                && Objects.equals(mother.getAssignedMidwife().getId(), midwife.getId());
    }

    private User getAssignedPatientOrThrow(Long midwifeId, Long motherUserId) {
        User midwife = getUserOrThrow(midwifeId);
        User mother = getUserOrThrow(motherUserId);

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        if (!hasAnyRole(mother, MOTHER_ROLES)) {
            throw new RuntimeException("Selected user is not a mother-side user.");
        }

        if (mother.getAssignedMidwife() == null ||
                !Objects.equals(mother.getAssignedMidwife().getId(), midwifeId)) {
            throw new RuntimeException("This patient is not assigned to the given midwife.");
        }

        return mother;
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private ConnectionRequestMethod parseMethod(String method) {
        try {
            return ConnectionRequestMethod.valueOf(method.trim().toUpperCase());
        } catch (Exception ex) {
            throw new RuntimeException("Invalid connection request method. Allowed values: EMAIL, AREA");
        }
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

    private boolean hasAnyRole(User user, Collection<Role> roles) {
        return user.getRoles() != null && user.getRoles().stream().anyMatch(roles::contains);
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private UserResponseDto mapUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getNationalIdNumber(),
                user.getAddress(),
                user.getProfileImageUrl(),
                user.getArea(),
                user.getDistrict(),
                user.getMohArea(),
                user.getLatitude(),
                user.getLongitude(),
                user.getAssignedMidwife() != null ? user.getAssignedMidwife().getId() : null,
                user.getRoles()
        );
    }

    private AssignedPatientDetailResponseDto mapAssignedPatientDetail(User user) {
        return AssignedPatientDetailResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .nationalIdNumber(user.getNationalIdNumber())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .area(user.getArea())
                .district(user.getDistrict())
                .mohArea(user.getMohArea())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .assignedMidwifeId(user.getAssignedMidwife() != null ? user.getAssignedMidwife().getId() : null)
                .roles(user.getRoles())
                .canEditProfile(true)
                .canViewHealthRecords(true)
                .canViewFertility(true)
                .canViewRiskPredictions(true)
                .build();
    }

    private ConnectionRequestResponseDto mapRequest(MidwifeMotherRequest entity) {
        return ConnectionRequestResponseDto.builder()
                .id(entity.getId())
                .senderId(entity.getSender().getId())
                .senderEmail(entity.getSender().getEmail())
                .senderFirstName(entity.getSender().getFirstName())
                .senderLastName(entity.getSender().getLastName())
                .senderRoles(entity.getSender().getRoles())
                .receiverId(entity.getReceiver().getId())
                .receiverEmail(entity.getReceiver().getEmail())
                .receiverFirstName(entity.getReceiver().getFirstName())
                .receiverLastName(entity.getReceiver().getLastName())
                .receiverRoles(entity.getReceiver().getRoles())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .matchedArea(entity.getMatchedArea())
                .createdAt(entity.getCreatedAt())
                .respondedAt(entity.getRespondedAt())
                .build();
    }
}