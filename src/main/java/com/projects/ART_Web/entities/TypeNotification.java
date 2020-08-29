package com.projects.ART_Web.entities;

public enum TypeNotification {
    Personal("src/main/resources/static/img/notifications/personal.svg"),
    System("src/main/resources/static/img/notifications/system.svg"),
    Common("src/main/resources/static/img/notifications/common.svg");

    String pathToImage;

    private TypeNotification(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public String getPathToImage(){
        return pathToImage;
    }
}