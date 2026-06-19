package org.styrse.Reflection.Annotations;

public class User {
    private final String name;
    private final String role;

    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
