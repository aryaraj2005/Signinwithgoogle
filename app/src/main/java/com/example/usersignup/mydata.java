package com.example.usersignup;

public class mydata {
    String name;
    String course;
    String Contact;
    String  image;

    public mydata(String name, String course, String contact, String image) {
        this.name = name;
        this.course = course;
        Contact = contact;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
