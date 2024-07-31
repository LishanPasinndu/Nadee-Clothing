package lk.jiat.eshop.model;

public class Message {

    private String user;
    private String message;
    private String date;
    private String time;

    public Message() {
    }

    public Message(String user, String message, String date, String time) {
        this.user = user;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
