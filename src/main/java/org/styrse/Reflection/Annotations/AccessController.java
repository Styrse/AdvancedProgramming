package org.styrse.Reflection.Annotations;

import java.lang.reflect.Method;

public class AccessController implements Service {
    private final Service realService;
    private final User user;

    public AccessController(Service realService, User user) {
        this.realService = realService;
        this.user = user;
    }

    @Override
    public void deleteAllUsers() {
        invokeIfAuthorized("deleteAllUsers");
    }

    @Override
    public void viewProfile() {
        invokeIfAuthorized("viewProfile");
    }

    @Override
    public void help() {
        invokeIfAuthorized("help");
    }

    private void invokeIfAuthorized(String methodName) {
        try {
            Method method = realService.getClass().getMethod(methodName);
            Role roleAnnotation = method.getAnnotation(Role.class);

            if (roleAnnotation == null || user.getRole().equals(roleAnnotation.value())) {
                System.out.println("Metode '" + methodName + "' kaldes af " + user.getName());
                method.invoke(realService);
            } else {
                System.out.println("Adgang nægtet til '" + methodName + "' for bruger '" + user.getName() + "'");
            }

        } catch (NoSuchMethodException e) {
            System.out.println("Metoden '" + methodName + "' findes ikke.");
        } catch (ReflectiveOperationException e) {
            System.out.println("Fejl under metodekald: " + e.getMessage());
        }
    }
}
