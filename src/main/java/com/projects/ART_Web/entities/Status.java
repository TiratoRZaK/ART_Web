package com.projects.ART_Web.entities;

public enum Status {
    created("Рассматривается"),
    accepted("Ожидает предоплату"),
    paid("Подтверждена"),
    canceled("Отклонена");

    private String text;

    Status(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Status fromString(String text) {
        for (Status b : Status.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
