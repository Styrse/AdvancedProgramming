package org.styrse.Reflection.Annotations;

public class SecureService implements Service {

    @Role("admin")
    @Override
    public void deleteAllUsers() {
        System.out.println("Alle brugere er slettet.");
    }

    @Role("user")
    @Override
    public void viewProfile() {
        System.out.println("Profil vist.");
    }

    @Override
    public void help() {
        System.out.println("Hjælp åbnet.");
    }
}
