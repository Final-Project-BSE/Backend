package com.example.MathruAI_BackEnd.entity.appointment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED;

    @JsonValue
    public String toJson() {
        if (this == CANCELLED) {
            return "CANCELED";
        }
        return name();
    }

    @JsonCreator
    public static AppointmentStatus fromJson(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();
        if ("CANCELED".equals(normalized)) {
            return CANCELLED;
        }

        return AppointmentStatus.valueOf(normalized);
    }
}
