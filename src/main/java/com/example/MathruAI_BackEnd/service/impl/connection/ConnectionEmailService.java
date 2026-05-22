package com.example.MathruAI_BackEnd.service.impl.connection;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.connection.MidwifeMotherRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public void sendConnectionRequestCreatedEmails(MidwifeMotherRequest request) {
        sendAfterCommit(() -> {
            User sender = request.getSender();
            User receiver = request.getReceiver();

            sendEmail(
                    receiver,
                    "New Midwife Connection Request",
                    "Dear " + displayName(receiver) + ",\n\n"
                            + displayName(sender) + " has sent you a connection request in MathruAI.\n\n"
                            + requestDetails(request)
                            + "\n\nPlease log in to MathruAI to approve or reject this request.\n\n"
                            + "Regards,\nMathruAI"
            );

            sendEmail(
                    sender,
                    "Connection Request Sent",
                    "Dear " + displayName(sender) + ",\n\n"
                            + "Your connection request has been sent successfully.\n\n"
                            + "Receiver: " + displayName(receiver) + "\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendConnectionRequestApprovedEmails(MidwifeMotherRequest request, User midwife, User mother) {
        sendAfterCommit(() -> {
            sendEmail(
                    mother,
                    "Midwife Connection Approved",
                    "Dear " + displayName(mother) + ",\n\n"
                            + "Your connection request has been approved.\n\n"
                            + "Assigned Midwife: " + displayName(midwife) + "\n"
                            + "Midwife Email: " + safeText(midwife.getEmail()) + "\n\n"
                            + "You can now access midwife-related services in MathruAI.\n\n"
                            + "Regards,\nMathruAI"
            );

            sendEmail(
                    midwife,
                    "Patient Connection Approved",
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "A patient has been connected to you successfully.\n\n"
                            + "Patient: " + displayName(mother) + "\n"
                            + "Patient Email: " + safeText(mother.getEmail()) + "\n\n"
                            + "Regards,\nMathruAI"
            );
        });
    }

    public void sendConnectionRequestRejectedEmails(MidwifeMotherRequest request) {
        sendAfterCommit(() -> {
            User sender = request.getSender();
            User receiver = request.getReceiver();

            sendEmail(
                    sender,
                    "Connection Request Rejected",
                    "Dear " + displayName(sender) + ",\n\n"
                            + "Your connection request was rejected by " + displayName(receiver) + ".\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );

            sendEmail(
                    receiver,
                    "Connection Request Rejected",
                    "Dear " + displayName(receiver) + ",\n\n"
                            + "You rejected the connection request from " + displayName(sender) + ".\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendConnectionRequestCancelledEmails(MidwifeMotherRequest request) {
        sendAfterCommit(() -> {
            User sender = request.getSender();
            User receiver = request.getReceiver();

            sendEmail(
                    receiver,
                    "Connection Request Cancelled",
                    "Dear " + displayName(receiver) + ",\n\n"
                            + displayName(sender) + " cancelled the pending connection request.\n\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );

            sendEmail(
                    sender,
                    "Connection Request Cancelled",
                    "Dear " + displayName(sender) + ",\n\n"
                            + "You cancelled your pending connection request.\n\n"
                            + "Receiver: " + displayName(receiver) + "\n"
                            + requestDetails(request)
                            + "\n\nRegards,\nMathruAI"
            );
        });
    }

    public void sendAssignedConnectionRemovedEmails(User midwife, User mother, String removedByLabel) {
        sendAfterCommit(() -> {
            sendEmail(
                    mother,
                    "Midwife Connection Removed",
                    "Dear " + displayName(mother) + ",\n\n"
                            + "Your assigned midwife connection has been removed.\n\n"
                            + "Previous Midwife: " + displayName(midwife) + "\n"
                            + "Removed By: " + removedByLabel + "\n\n"
                            + "Regards,\nMathruAI"
            );

            sendEmail(
                    midwife,
                    "Patient Connection Removed",
                    "Dear " + displayName(midwife) + ",\n\n"
                            + "A patient connection has been removed.\n\n"
                            + "Patient: " + displayName(mother) + "\n"
                            + "Removed By: " + removedByLabel + "\n\n"
                            + "Regards,\nMathruAI"
            );
        });
    }

    private void sendEmail(User user, String subject, String body) {
        if (!mailEnabled) {
            log.info("Mail disabled. Skipping email: {}", subject);
            return;
        }

        if (user == null || isBlank(user.getEmail())) {
            log.warn("Skipping email because recipient email is missing. Subject: {}", subject);
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
            log.error(
                    "Failed to send connection email to {}. Subject: {}",
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

    private String requestDetails(MidwifeMotherRequest request) {
        return "Request Method: " + safeText(request.getMethod() != null ? request.getMethod().name() : null) + "\n"
                + "Status: " + safeText(request.getStatus() != null ? request.getStatus().name() : null) + "\n"
                + "Matched Area: " + safeText(request.getMatchedArea()) + "\n"
                + "Message: " + safeText(request.getMessage());
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

    private String safeText(String value) {
        return isBlank(value) ? "Not specified" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}