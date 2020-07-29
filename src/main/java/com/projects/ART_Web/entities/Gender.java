package com.projects.ART_Web.entities;

public enum Gender {
    Male("Мужчина"),
    Female("Женщина"),
    Alien("Другое");

    private String text;

    Gender(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Gender fromString(String text) {
        for (Gender b : Gender.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}

