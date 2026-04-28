package com.example.MathruAI_BackEnd.repository.appointment;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequest;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, String> {

    Optional<AppointmentRequest> findByRequestIdAndMidwife(String requestId, User midwife);

    List<AppointmentRequest> findByMidwifeOrderByRequestedAtDesc(User midwife);

    List<AppointmentRequest> findByMidwifeAndStatusOrderByRequestedAtDesc(User midwife, AppointmentRequestStatus status);
}
