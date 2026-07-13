package mg.itu.aquanova.config.navigation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.aquanova.config.securite.ReglesAcces;
import mg.itu.aquanova.config.securite.Roles;

@ControllerAdvice
public class NavigationControllerAdvice {

    private final ReglesAcces reglesAcces;

    public NavigationControllerAdvice(ReglesAcces reglesAcces) {
        this.reglesAcces = reglesAcces;
    }

    @ModelAttribute("menuNavigation")
    public List<NavigationItemDTO> menuNavigation(HttpServletRequest request) {
        List<NavigationItemDTO> menu = filtrerParRole(construireMenu(), roleCourant(request));
        marquerActif(menu, request.getRequestURI());
        return menu;
    }

    /** Exposé aux vues pour masquer les boutons réservés à l'administrateur. */
    @ModelAttribute("estAdmin")
    public boolean estAdmin(HttpServletRequest request) {
        return Roles.ADMIN.equals(roleCourant(request));
    }

    /** Exposé aux vues : l'accès au métier « argent » et aux référentiels. */
    @ModelAttribute("peutGererLeMetier")
    public boolean peutGererLeMetier(HttpServletRequest request) {
        String role = roleCourant(request);
        return Roles.ADMIN.equals(role) || Roles.RESPONSABLE.equals(role);
    }

    private String roleCourant(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (String) session.getAttribute("role") : null;
    }

    /**
     * Le menu est filtré avec la MÊME table de règles que l'intercepteur : un lien visible
     * correspond donc toujours à une URL réellement autorisée, et un lien masqué à une URL
     * réellement bloquée. Masquer un menu ne protège rien — c'est l'intercepteur qui protège ;
     * ce filtrage évite simplement de proposer des portes fermées.
     */
    private List<NavigationItemDTO> filtrerParRole(List<NavigationItemDTO> menu, String role) {
        List<NavigationItemDTO> visible = new ArrayList<>();

        for (NavigationItemDTO section : menu) {
            List<NavigationItemDTO> sousItems = section.getSousItems().stream()
                    .filter(item -> reglesAcces.estAutorise(role, item.getUrl()))
                    .toList();

            if (!sousItems.isEmpty()) {
                visible.add(new NavigationItemDTO(section.getLibelle(), section.getIcone(), sousItems));
            }
        }
        return visible;
    }

    /** Tailles de page proposées par le pied de tableau standard (fragments/tableau :: tableFooter). */
    @ModelAttribute("pageSizes")
    public List<Integer> pageSizes() {
        return List.of(5, 10, 20, 50, 100);
    }

    private List<NavigationItemDTO> construireMenu() {
        return List.of(
                new NavigationItemDTO("Production", "bi-droplet-half", List.of(
                        new NavigationItemDTO("Lots", "/lots"),
                        new NavigationItemDTO("Récoltes", "/recoltes"),
                        new NavigationItemDTO("Prévisions récoltes", "/production/previsions-recoltes"),
                        new NavigationItemDTO("Transferts", "/transferts"),
                        new NavigationItemDTO("Mortalités", "/mortalites"),
                        new NavigationItemDTO("Journaux des lots", "/journaux-lots"))),

                new NavigationItemDTO("Alimentation", "bi-basket2", List.of(
                        new NavigationItemDTO("Distributions", "/distributions"),
                        new NavigationItemDTO("Stocks", "/stocks"),
                        new NavigationItemDTO("Mouvements de stock", "/stocks/mouvements"),
                        new NavigationItemDTO("Prévision consommation", "/prevision"))),

                new NavigationItemDTO("Sanitaire & Équipements", "bi-heart-pulse", List.of(
                        new NavigationItemDTO("Relevés d'eau", "/releves-eau"),
                        new NavigationItemDTO("Traitements d'eau", "/traitements-eau"),
                        new NavigationItemDTO("Types de traitement", "/types-traitements-eau"),
                        new NavigationItemDTO("Équipements", "/equipements"),
                        new NavigationItemDTO("Maintenances", "/maintenances"))),

                new NavigationItemDTO("Achats & Dépenses", "bi-cart3", List.of(
                        new NavigationItemDTO("Achats d'alevins", "/achats/alevins"),
                        new NavigationItemDTO("Achats de provende", "/achats/provende"),
                        new NavigationItemDTO("Dépenses", "/depenses"),
                        new NavigationItemDTO("Catégories de dépenses", "/categories-depenses"),
                        new NavigationItemDTO("Fournisseurs", "/fournisseurs"),
                        new NavigationItemDTO("Historique", "/achats-depenses/historique"))),

                new NavigationItemDTO("Ventes", "bi-currency-exchange", List.of(
                        new NavigationItemDTO("Ventes", "/ventes"),
                        new NavigationItemDTO("Clients", "/clients"),
                        new NavigationItemDTO("Dashboard", "/ventes/dashboard"))),

                new NavigationItemDTO("Finance", "bi-graph-up-arrow", List.of(
                        new NavigationItemDTO("Dashboard financier", "/finance/dashboard"),
                        new NavigationItemDTO("Marge brute par lot", "/finance/lots"),
                        new NavigationItemDTO("Prévisions financières", "/finance/previsions"))),

                new NavigationItemDTO("Alertes", "bi-bell", List.of(
                        new NavigationItemDTO("Alertes actives", "/alertes"),
                        new NavigationItemDTO("Historique des alertes", "/alertes/historique"))),

                new NavigationItemDTO("Import / Export", "bi-file-earmark-spreadsheet", List.of(
                        new NavigationItemDTO("Importer des données", "/imports"))),

                new NavigationItemDTO("Référentiel", "bi-collection", List.of(
                        new NavigationItemDTO("Bassins", "/bassins"),
                        new NavigationItemDTO("Types de bassin", "/types-bassins"),
                        new NavigationItemDTO("Statuts de bassin", "/statuts-bassins"),
                        new NavigationItemDTO("Espèces", "/especes"),
                        new NavigationItemDTO("Stades de croissance", "/stade-croissance"),
                        new NavigationItemDTO("Aliments", "/aliments"),
                        new NavigationItemDTO("Types d'aliment", "/types-aliments"),
                        new NavigationItemDTO("Types de récolte", "/types-recoltes"),
                        new NavigationItemDTO("Statuts de lot", "/statut-lots"),
                        new NavigationItemDTO("Types d'événement lot", "/types-evenements-lot"),
                        new NavigationItemDTO("Types de client", "/types-clients"),
                        new NavigationItemDTO("Types d'équipement", "/types-equipements"))),

                new NavigationItemDTO("Administration", "bi-gear", List.of(
                        new NavigationItemDTO("Paramètres système", "/parametres-systeme"),
                        new NavigationItemDTO("Utilisateurs", "/users"),
                        new NavigationItemDTO("Rôles", "/roles"),
                        new NavigationItemDTO("Attribution des rôles", "/user-roles"))));
    }

    private void marquerActif(List<NavigationItemDTO> menu, String uri) {
        NavigationItemDTO meilleur = null;
        int longueurMax = -1;

        for (NavigationItemDTO section : menu) {
            for (NavigationItemDTO item : section.getSousItems()) {
                String url = item.getUrl();
                boolean correspond = uri.equals(url) || uri.startsWith(url + "/");
                if (correspond && url.length() > longueurMax) {
                    meilleur = item;
                    longueurMax = url.length();
                }
            }
        }

        if (meilleur != null) {
            meilleur.setActif(true);
        }
    }
}
