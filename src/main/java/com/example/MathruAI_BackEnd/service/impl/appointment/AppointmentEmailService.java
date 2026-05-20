package com.example.MathruAI_BackEnd.service.impl.appointment;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEmailService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public void sendAppointmentCreatedEmails(Appointment appointment) {
        sendAfterCommit(() -> {
            User midwife = appointment.getMidwife();
            User patient = appointment.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Scheduled",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment has been scheduled.\n\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Scheduled for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "An appointment has been scheduled for your patient.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentCanceledEmails(Appointment appointment) {
        sendAfterCommit(() -> {
            User midwife = appointment.getMidwife();
            User patient = appointment.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Canceled",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment has been canceled.\n\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Canceled for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "An appointment has been canceled.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentCompletedEmails(Appointment appointment) {
        sendAfterCommit(() -> {
            User midwife = appointment.getMidwife();
            User patient = appointment.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Completed",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment has been marked as completed.\n\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Completed for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "You marked this appointment as completed.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentDeletedEmails(Appointment appointment) {
        sendAfterCommit(() -> {
            User midwife = appointment.getMidwife();
            User patient = appointment.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Record Deleted",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "An appointment record has been deleted from the system.\n\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Record Deleted for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "An appointment record has been deleted.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + appointmentDetails(appointment)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentRequestCreatedEmails(AppointmentRequest request) {
        sendAfterCommit(() -> {
            User midwife = request.getMidwife();
            User patient = request.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Request Submitted",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment request has been submitted. Your midwife will review it.\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "New Appointment Request from " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "You have received a new appointment request.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + requestDetails(request)
                            + "\n\nPlease review the request in MathruAI.\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentRequestAcceptedEmails(AppointmentRequest request) {
        sendAfterCommit(() -> {
            User midwife = request.getMidwife();
            User patient = request.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Request Accepted",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment request has been accepted.\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Request Accepted for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "You accepted this appointment request.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAppointmentRequestDeclinedEmails(AppointmentRequest request) {
        sendAfterCommit(() -> {
            User midwife = request.getMidwife();
            User patient = request.getPatient();

            sendToPatient(
                    patient,
                    "Appointment Request Declined",
                    "Dear " + displayName(patient) + ",\n\n"
                            + "Your appointment request has been declined.\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );

            sendToMidwife(
                    midwife,
                    "Appointment Request Declined for " + displayName(patient),
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "You declined this appointment request.\n\n"
                            + "Patient: " + displayName(patient) + "\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    private void sendToPatient(User patient, String subject, String body) {
        sendEmail(patient, subject, body);
    }

    private void sendToMidwife(User midwife, String subject, String body) {
        sendEmail(midwife, subject, body);
    }

    private void sendEmail(User user, String subject, String body) {
        if (!mailEnabled) {
            log.info("Mail disabled. Skipping email: {}", subject);
            return;
        }

        if (user == null || isBlank(user.getEmail())) {
            log.warn("Skipping email because recipient is missing. Subject: {}", subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            if (!isBlank(fromEmail)) {
                message.setFrom(fromEmail);
            }

            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send appointment email to {}. Subject: {}",
                    user.getEmail(),
                    subject,
                    ex
            );
        }
    }

    private void sendAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            task.run();
                        }
                    }
            );
            return;
        }

        task.run();
    }

    private String appointmentDetails(Appointment appointment) {
        return "Date: " + formatDate(appointment.getAppointmentDate()) + "\n"
                + "Time: " + formatTime(appointment.getStartTime()) + "\n"
                + "Type: " + formatType(appointment.getAppointmentType()) + "\n"
                + "Location: " + safeText(appointment.getLocation()) + "\n"
                + "Notes: " + safeText(appointment.getNotes());
    }

    private String requestDetails(AppointmentRequest request) {
        return "Date: " + formatDate(request.getAppointmentDate()) + "\n"
                + "Time: " + formatTime(request.getStartTime()) + "\n"
                + "Type: " + formatType(request.getAppointmentType()) + "\n"
                + "Location: " + safeText(request.getLocation()) + "\n"
                + "Notes: " + safeText(request.getNotes());
    }

    private String displayName(User user) {
        if (user == null) {
            return "User";
        }

        String fullName = ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        if (!isBlank(user.getEmail())) {
            return user.getEmail();
        }

        return "User";
    }

    private String formatDate(LocalDate date) {
        return date == null ? "Not specified" : date.format(DATE_FORMATTER);
    }

    private String formatTime(LocalTime time) {
        return time == null ? "Not specified" : time.format(TIME_FORMATTER);
    }

    private String formatType(Object value) {
        if (value == null) {
            return "Not specified";
        }

        String raw = String.valueOf(value).toLowerCase().replace("_", " ");
        String[] parts = raw.split(" ");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }

            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }

    private String safeText(String value) {
        return isBlank(value) ? "Not specified" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}