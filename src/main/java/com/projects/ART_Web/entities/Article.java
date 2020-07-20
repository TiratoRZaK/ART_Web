package com.projects.ART_Web.entities;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title, text, author_name, author_prof, preface;
    private byte[] main_image, author_photo;
    private int check_count = 0;
    private Date date_create;

    public Article() {
        date_create = Date.valueOf(LocalDate.now());
    }

    public Article(String title, String text, String author_name, String author_prof, String preface, byte[] main_image, byte[] author_photo) {
        this.title = title;
        this.text = text;
        this.author_name = author_name;
        this.author_prof = author_prof;
        this.preface = preface;
        this.main_image = main_image;
        this.author_photo = author_photo;
        check_count = 0;
        date_create = Date.valueOf(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_prof() {
        return author_prof;
    }

    public void setAuthor_prof(String author_prof) {
        this.author_prof = author_prof;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public byte[] getMain_image() {
        return main_image;
    }

    public void setMain_image(byte[] main_image) {
        this.main_image = main_image;
    }

    public byte[] getAuthor_photo() {
        return author_photo;
    }

    public void setAuthor_photo(byte[] author_photo) {
        this.author_photo = author_photo;
    }

    public int getCheck_count() {
        return check_count;
    }

    public void setCheck_count(int check_count) {
        this.check_count = check_count;
    }

    public Date getDate_create() {
        return date_create;
    }

    public void setDate_create(Date date_create) {
        this.date_create = date_create;
    }

    public int size(){
        return 0;
    }
}
