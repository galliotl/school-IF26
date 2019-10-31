package utt.if26.bardcamp.models;

public class User {
    private String picPath;
    private int id;
    private String firstName;
    private String lastName;

    public User(String picPath, String firstName, String lastName, int id) {
        this.picPath = picPath;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}