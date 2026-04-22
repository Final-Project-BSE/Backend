package com.example.MathruAI_BackEnd.repository.connection;

import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestStatus;
import com.example.MathruAI_BackEnd.entity.connection.MidwifeMotherRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MidwifeMotherRequestRepository extends JpaRepository<MidwifeMotherRequest, Long> {

    List<MidwifeMotherRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    List<MidwifeMotherRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    Optional<MidwifeMotherRequest> findBySenderIdAndReceiverIdAndStatus(
            Long senderId,
            Long receiverId,
            ConnectionRequestStatus status
    );

    boolean existsBySenderIdAndReceiverIdAndStatus(
            Long senderId,
            Long receiverId,
            ConnectionRequestStatus status
    );

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    boolean existsByReceiverIdAndSenderId(Long receiverId, Long senderId);

    boolean existsBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            Long senderId,
            Long receiverId,
            Long reverseSenderId,
            Long reverseReceiverId
    );
}