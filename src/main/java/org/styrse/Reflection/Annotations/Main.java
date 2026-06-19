package org.styrse.Reflection.Annotations;

public class Main {
    public static void main(String[] args) {
        SecureService realService = new SecureService();

        User admin = new User("Alice", "admin");
        User normal = new User("Bob", "user");

        Service adminProxy = new AccessController(realService, admin);
        Service userProxy = new AccessController(realService, normal);

        adminProxy.deleteAllUsers();
        userProxy.deleteAllUsers();
        userProxy.viewProfile();
        userProxy.help();
    }
}
