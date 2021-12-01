package com.example.deadline;

public class Model {
    String title;
    String description;
    String deadline;
    String id;

    public Model(String id, String title, String description, String deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

