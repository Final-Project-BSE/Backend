package com.example.MathruAI_BackEnd.service.impl.connection;

import com.example.MathruAI_BackEnd.dto.connection.AreaMapSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AreaSearchRequestDto;
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

            if (requestRepository.existsBySenderIdAndReceiverIdAndStatus(
                    sender.getId(),
                    receiver.getId(),
                    ConnectionRequestStatus.PENDING
            )) {
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
                    .createdAt(java.time.LocalDateTime.now())
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
        entity.setRespondedAt(java.time.LocalDateTime.now());
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
        entity.setRespondedAt(java.time.LocalDateTime.now());

        return mapRequest(requestRepository.save(entity));
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
    public UserResponseDto getAssignedMidwifeForMother(Long motherUserId) {
        User motherUser = getUserOrThrow(motherUserId);

        if (!hasAnyRole(motherUser, MOTHER_ROLES)) {
            throw new RuntimeException("User is not a mother-side user.");
        }

        if (motherUser.getAssignedMidwife() == null) {
            throw new RuntimeException("No midwife assigned to this user.");
        }

        return mapUser(motherUser.getAssignedMidwife());
    }

    @Override
    public UserResponseDto updateAssignedMotherProfile(
            Long midwifeId,
            Long motherUserId,
            AssignedUserProfileUpdateRequestDto request
    ) {
        User midwife = getUserOrThrow(midwifeId);
        User motherUser = getUserOrThrow(motherUserId);

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        if (!hasAnyRole(motherUser, MOTHER_ROLES)) {
            throw new RuntimeException("Target user is not a mother-side user.");
        }

        if (motherUser.getAssignedMidwife() == null ||
                !Objects.equals(motherUser.getAssignedMidwife().getId(), midwifeId)) {
            throw new RuntimeException("This mother-side user is not assigned to the given midwife.");
        }

        if (request.getFirstName() != null) motherUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) motherUser.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) motherUser.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) motherUser.setDateOfBirth(request.getDateOfBirth());

        if (request.getNationalIdNumber() != null &&
                !request.getNationalIdNumber().equals(motherUser.getNationalIdNumber()) &&
                userRepository.existsByNationalIdNumber(request.getNationalIdNumber())) {
            throw new RuntimeException("National ID number is already in use.");
        }

        if (request.getNationalIdNumber() != null) motherUser.setNationalIdNumber(request.getNationalIdNumber());
        if (request.getAddress() != null) motherUser.setAddress(request.getAddress());
        if (request.getProfileImageUrl() != null) motherUser.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getArea() != null) motherUser.setArea(request.getArea());
        if (request.getDistrict() != null) motherUser.setDistrict(normalizeText(request.getDistrict()));
        if (request.getMohArea() != null) motherUser.setMohArea(normalizeText(request.getMohArea()));
        if (request.getLatitude() != null) motherUser.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) motherUser.setLongitude(request.getLongitude());

        return mapUser(userRepository.save(motherUser));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsersByDistrictAndMohArea(Long requesterId, AreaSearchRequestDto request) {
        User requester = getUserOrThrow(requesterId);

        validateAreaSearchRequest(request.getDistrict(), request.getMohArea());

        Set<Role> targetRoles = resolveTargetRoles(requester);

        return userRepository.findByDistrictAndMohAreaAndAnyRole(
                        normalizeText(request.getDistrict()),
                        normalizeText(request.getMohArea()),
                        targetRoles
                )
                .stream()
                .filter(user -> !Objects.equals(user.getId(), requesterId))
                .filter(user -> !isAlreadyAssignedPair(requester, user))
                .map(this::mapUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchMappableUsersByDistrictAndMohArea(Long requesterId, AreaMapSearchRequestDto request) {
        User requester = getUserOrThrow(requesterId);

        validateAreaSearchRequest(request.getDistrict(), request.getMohArea());

        Set<Role> targetRoles = resolveTargetRoles(requester);

        return userRepository.findMappableUsersByDistrictAndMohAreaAndAnyRole(
                        normalizeText(request.getDistrict()),
                        normalizeText(request.getMohArea()),
                        targetRoles
                )
                .stream()
                .filter(user -> !Objects.equals(user.getId(), requesterId))
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

    private Set<Role> resolveTargetRoles(User requester) {
        boolean requesterIsMidwife = hasRole(requester, Role.MIDWIFE);
        boolean requesterIsMotherSide = hasAnyRole(requester, MOTHER_ROLES);

        if (!requesterIsMidwife && !requesterIsMotherSide) {
            throw new RuntimeException("Only midwives and mother-side users can search.");
        }

        if (requesterIsMidwife && requesterIsMotherSide) {
            throw new RuntimeException("User cannot be both midwife and mother-side user for this connection flow.");
        }

        return requesterIsMidwife ? MOTHER_ROLES : MIDWIFE_ROLE;
    }

    private List<User> resolveTargetsByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Target email is required when method is EMAIL.");
        }

        User target = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("No user found with email: " + email));

        return List.of(target);
    }

    private List<User> resolveTargetsByArea(User sender, String area) {
        if (area == null || area.isBlank()) {
            throw new RuntimeException("Target area is required when method is AREA.");
        }

        String normalizedArea = normalizeText(area);

        if (hasRole(sender, Role.MIDWIFE)) {
            return userRepository.findByAreaAndAnyRole(normalizedArea, MOTHER_ROLES);
        } else {
            return userRepository.findByAreaAndAnyRole(normalizedArea, MIDWIFE_ROLE);
        }
    }

    private void validateSenderReceiverPair(User sender, User receiver) {
        boolean senderIsMidwife = hasRole(sender, Role.MIDWIFE);
        boolean receiverIsMidwife = hasRole(receiver, Role.MIDWIFE);
        boolean senderIsMother = hasAnyRole(sender, MOTHER_ROLES);
        boolean receiverIsMother = hasAnyRole(receiver, MOTHER_ROLES);

        if (senderIsMidwife && receiverIsMidwife) {
            throw new RuntimeException("Midwife cannot send connection request to another midwife.");
        }

        if (senderIsMother && receiverIsMother) {
            throw new RuntimeException("Mother-side users cannot send connection request to another mother-side user.");
        }

        if (!(senderIsMidwife || senderIsMother) || !(receiverIsMidwife || receiverIsMother)) {
            throw new RuntimeException("Invalid sender/receiver role combination.");
        }
    }

    private boolean isAlreadyAssignedPair(User sender, User receiver) {
        User midwife;
        User motherUser;

        if (hasRole(sender, Role.MIDWIFE) && hasAnyRole(receiver, MOTHER_ROLES)) {
            midwife = sender;
            motherUser = receiver;
        } else if (hasRole(receiver, Role.MIDWIFE) && hasAnyRole(sender, MOTHER_ROLES)) {
            midwife = receiver;
            motherUser = sender;
        } else {
            return false;
        }

        return motherUser.getAssignedMidwife() != null &&
                Objects.equals(motherUser.getAssignedMidwife().getId(), midwife.getId());
    }

    private ConnectionRequestMethod parseMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new RuntimeException("Request method is required.");
        }

        try {
            return ConnectionRequestMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid request method. Allowed values: EMAIL, AREA");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

    private boolean hasAnyRole(User user, Set<Role> roles) {
        return user.getRoles() != null && user.getRoles().stream().anyMatch(roles::contains);
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
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
}