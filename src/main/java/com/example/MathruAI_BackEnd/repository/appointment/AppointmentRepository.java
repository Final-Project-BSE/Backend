package com.example.MathruAI_BackEnd.repository.appointment;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByMidwifeAndPatientOrderByAppointmentDateAscStartTimeAsc(User midwife, User patient);

    List<Appointment> findByMidwifeAndStatusOrderByAppointmentDateAscStartTimeAsc(User midwife, AppointmentStatus status);

    List<Appointment> findByPatientAndStatusOrderByAppointmentDateAscStartTimeAsc(User patient, AppointmentStatus status);

        boolean existsByMidwifeAndAppointmentDateAndStartTimeAndStatus(
            User midwife,
            LocalDate appointmentDate,
            LocalTime startTime,
            AppointmentStatus status
    );

    boolean existsByPatientAndAppointmentDateAndStartTimeAndStatus(
            User patient,
            LocalDate appointmentDate,
            LocalTime startTime,
            AppointmentStatus status
    );

        boolean existsByMidwifeAndPatientAndAppointmentDateAndStartTimeAndStatus(
            User midwife,
            User patient,
            LocalDate appointmentDate,
            LocalTime startTime,
            AppointmentStatus status
        );

        List<Appointment> findByMidwifeAndPatientAndStatusAndAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
            User midwife,
            User patient,
            AppointmentStatus status,
            LocalDate from,
            LocalDate to
        );

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        List<Appointment> findByMidwifeAndPatientAndAppointmentDateAndStartTimeAndStatus(
            User midwife,
            User patient,
            LocalDate appointmentDate,
            LocalTime startTime,
            AppointmentStatus status
        );

            @Lock(LockModeType.PESSIMISTIC_WRITE)
            List<Appointment> findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
                User midwife,
                LocalDate appointmentDate,
                LocalTime startTime,
                AppointmentStatus status
            );

            List<Appointment> findByMidwifeAndStatusAndAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
                User midwife,
                AppointmentStatus status,
                LocalDate from,
                LocalDate to
            );

            List<Appointment> findByMidwifeAndStatusAndAppointmentDateOrderByStartTimeAsc(
                User midwife,
                AppointmentStatus status,
                LocalDate date
            );

    Optional<Appointment> findByIdAndMidwifeAndPatient(Long id, User midwife, User patient);
}
