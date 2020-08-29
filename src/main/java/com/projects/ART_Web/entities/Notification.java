package com.projects.ART_Web.entities;

import javax.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    private String title;
    private String message;
    private TypeNotification type;


    public Notification() {
    }


    public Notification(String title, String message, TypeNotification type, User owner) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPathToImage() {
        return type.getPathToImage();
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }
}
