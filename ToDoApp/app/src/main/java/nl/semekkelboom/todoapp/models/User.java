package nl.semekkelboom.todoapp.models;

import java.io.Serializable;

/**
 * Created by semek on 15-6-2017.
 */

public class User implements Serializable {
    private String id;
    private String email;
    private String authtoken;

    public User(String id, String email, String authtoken) {
        this.id = id;
        this.email = email;
        this.authtoken = authtoken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthtoken() {
        return authtoken;
    }
}
