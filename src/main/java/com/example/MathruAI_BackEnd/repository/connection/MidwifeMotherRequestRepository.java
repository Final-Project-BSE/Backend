package com.example.MathruAI_BackEnd.repository.connection;

import com.example.MathruAI_BackEnd.entity.connection.ConnectionRequestStatus;
import com.example.MathruAI_BackEnd.entity.connection.MidwifeMotherRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MidwifeMotherRequestRepository extends JpaRepository<MidwifeMotherRequest, Long> {

    List<MidwifeMotherRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    List<MidwifeMotherRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    @Query("""
        SELECT DISTINCT r FROM MidwifeMotherRequest r
        JOIN FETCH r.sender s
        JOIN FETCH r.receiver rec
        LEFT JOIN FETCH s.roles
        LEFT JOIN FETCH rec.roles
        WHERE s.id = :senderId
        ORDER BY r.createdAt DESC
    """)
    List<MidwifeMotherRequest> findSentWithUsers(@Param("senderId") Long senderId);

    @Query("""
        SELECT DISTINCT r FROM MidwifeMotherRequest r
        JOIN FETCH r.sender s
        JOIN FETCH r.receiver rec
        LEFT JOIN FETCH s.roles
        LEFT JOIN FETCH rec.roles
        WHERE rec.id = :receiverId
        ORDER BY r.createdAt DESC
    """)
    List<MidwifeMotherRequest> findReceivedWithUsers(@Param("receiverId") Long receiverId);

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

    boolean existsBySenderIdAndReceiverIdAndStatusOrReceiverIdAndSenderIdAndStatus(
            Long senderId,
            Long receiverId,
            ConnectionRequestStatus status1,
            Long reverseSenderId,
            Long reverseReceiverId,
            ConnectionRequestStatus status2
    );
}