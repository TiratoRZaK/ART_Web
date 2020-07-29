package com.projects.ART_Web.entities;

public enum TimeInterval {
    first_interval("C 8:00 по 13:00"),
    second_interval("C 13:00 по 18:00"),
    three_interval("C 18:00 по 22:00"),
    alter_interval("Другое");

    private String text;

    TimeInterval(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static TimeInterval fromString(String text) {
        for (TimeInterval b : TimeInterval.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
