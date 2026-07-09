package mg.itu.aquanova.config.navigation;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Injecte le menu de navigation latéral dans le modèle de toutes les pages,
 * avec l'élément correspondant à l'URL courante marqué comme actif.
 */
@ControllerAdvice
public class NavigationControllerAdvice {

    @ModelAttribute("menuNavigation")
    public List<NavigationItemDTO> menuNavigation(HttpServletRequest request) {
        List<NavigationItemDTO> menu = construireMenu();
        marquerActif(menu, request.getRequestURI());
        return menu;
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
                        new NavigationItemDTO("Achats", "/achats"),
                        new NavigationItemDTO("Dépenses", "/depenses"),
                        new NavigationItemDTO("Catégories de dépenses", "/categories-depenses"),
                        new NavigationItemDTO("Fournisseurs", "/fournisseurs"),
                        new NavigationItemDTO("Intrants", "/intrants"),
                        new NavigationItemDTO("Historique", "/achats-depenses/historique"))),

                new NavigationItemDTO("Ventes", "bi-currency-exchange", List.of(
                        new NavigationItemDTO("Ventes", "/ventes"),
                        new NavigationItemDTO("Clients", "/clients"),
                        new NavigationItemDTO("Historique des ventes", "/ventes/historique"),
                        new NavigationItemDTO("Dashboard", "/ventes/dashboard"))),

                new NavigationItemDTO("Finance", "bi-graph-up-arrow", List.of(
                        new NavigationItemDTO("Prévisions financières", "/finance/previsions"))),

                new NavigationItemDTO("Alertes", "bi-bell", List.of(
                        new NavigationItemDTO("Historique des alertes", "/alertes/historique"))),

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

    /**
     * Marque comme actif le lien dont l'URL correspond le mieux (préfixe le plus long)
     * à l'URI courante, pour éviter que "/stocks" et "/stocks/mouvements" soient actifs en même temps.
     */
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
