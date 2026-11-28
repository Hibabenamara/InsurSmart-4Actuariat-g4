package tn.esprit.contratservice.security;

import tn.esprit.contratservice.exception.ForbiddenOperationException;
import java.util.Arrays;

public final class Roles {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String CLIENT = "CLIENT";
    public static final String AGENT = "AGENT";
    public static final String ADMIN = "ADMIN";

    private Roles() {}

    public static boolean isStaff(String role) {
        return AGENT.equalsIgnoreCase(role) || ADMIN.equalsIgnoreCase(role);
    }

    /** 403 si le role n'est pas autorise */
    public static void require(String role, String... allowed) {
        if (Arrays.stream(allowed).noneMatch(a -> a.equalsIgnoreCase(role))) {
            throw new ForbiddenOperationException("Operation reservee a : " + String.join(", ", allowed));
        }
    }

    /** 403 si un CLIENT accede a la ressource d'un autre client */
    public static void requireOwnerOrStaff(String ownerId, String requesterId, String role) {
        if (!isStaff(role) && !ownerId.equals(requesterId)) {
            throw new ForbiddenOperationException("Acces refuse : ressource d'un autre client");
        }
    }
}