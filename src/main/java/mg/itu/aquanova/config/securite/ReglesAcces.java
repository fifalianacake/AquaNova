package mg.itu.aquanova.config.securite;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ReglesAcces {

    private static final Set<String> TOUS =
            Set.of(Roles.ADMIN, Roles.RESPONSABLE, Roles.TECHNICIEN);
    private static final Set<String> ADMIN_SEUL =
            Set.of(Roles.ADMIN);
    private static final Set<String> ADMIN_ET_RESPONSABLE =
            Set.of(Roles.ADMIN, Roles.RESPONSABLE);

    private static final List<String> PUBLIC = List.of(
            "/login", "/register", "/logout", "/acces-refuse",
            "/css/", "/js/", "/images/", "/favicon.ico", "/error");

    private static final List<Regle> REGLES = List.of(

            // Comptes et rôles : administration pure
            new Regle("/users", ADMIN_SEUL),
            new Regle("/roles", ADMIN_SEUL),
            new Regle("/user-roles", ADMIN_SEUL),

            // Paramètres système : ils pilotent les seuils d'alerte et les calculs
            new Regle("/parametres-systeme", ADMIN_ET_RESPONSABLE),

            // Finance : Admin et responsable
            new Regle("/ventes", ADMIN_ET_RESPONSABLE),
            new Regle("/clients", ADMIN_ET_RESPONSABLE),
            new Regle("/achats", ADMIN_ET_RESPONSABLE),
            new Regle("/depenses", ADMIN_ET_RESPONSABLE),
            new Regle("/fournisseurs", ADMIN_ET_RESPONSABLE),
            new Regle("/achats-depenses", ADMIN_ET_RESPONSABLE),
            new Regle("/categories-depenses", ADMIN_ET_RESPONSABLE),
            new Regle("/finance", ADMIN_ET_RESPONSABLE),

            // Référentiels
            new Regle("/bassins", ADMIN_ET_RESPONSABLE),
            new Regle("/types-bassins", ADMIN_ET_RESPONSABLE),
            new Regle("/statuts-bassins", ADMIN_ET_RESPONSABLE),
            new Regle("/especes", ADMIN_ET_RESPONSABLE),
            new Regle("/stade-croissance", ADMIN_ET_RESPONSABLE),
            new Regle("/aliments", ADMIN_ET_RESPONSABLE),
            new Regle("/types-aliments", ADMIN_ET_RESPONSABLE),
            new Regle("/types-recoltes", ADMIN_ET_RESPONSABLE),
            new Regle("/statut-lots", ADMIN_ET_RESPONSABLE),
            new Regle("/types-evenements-lot", ADMIN_ET_RESPONSABLE),
            new Regle("/types-clients", ADMIN_ET_RESPONSABLE),
            new Regle("/types-equipements", ADMIN_ET_RESPONSABLE),
            new Regle("/types-traitements-eau", ADMIN_ET_RESPONSABLE));

    public boolean estPublique(String uri) {
        return PUBLIC.stream().anyMatch(prefixe -> uri.equals(prefixe) || uri.startsWith(prefixe));
    }

    /** @return true si le rôle donné peut atteindre cette URL. */
    public boolean estAutorise(String role, String uri) {
        if (role == null) {
            return false;
        }
        return rolesAutorises(uri).contains(role);
    }

    private Set<String> rolesAutorises(String uri) {
        for (Regle regle : REGLES) {
            if (uri.equals(regle.prefixe()) || uri.startsWith(regle.prefixe() + "/")) {
                return regle.roles();
            }
        }
        return TOUS;
    }

    private record Regle(String prefixe, Set<String> roles) {
    }
}
