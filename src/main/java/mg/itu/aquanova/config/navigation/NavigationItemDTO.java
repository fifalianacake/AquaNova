package mg.itu.aquanova.config.navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Élément du menu de navigation latéral (sidebar).
 * Un élément est soit une section (avec sous-items), soit un lien direct (avec url).
 */
public class NavigationItemDTO {

    private String libelle;
    private String url;
    private String icone;
    private boolean actif;
    private List<NavigationItemDTO> sousItems = new ArrayList<>();

    public NavigationItemDTO() {
    }

    /** Lien direct. */
    public NavigationItemDTO(String libelle, String url) {
        this.libelle = libelle;
        this.url = url;
    }

    /** Section avec icône et sous-items. */
    public NavigationItemDTO(String libelle, String icone, List<NavigationItemDTO> sousItems) {
        this.libelle = libelle;
        this.icone = icone;
        this.sousItems = sousItems;
    }

    /** Une section est "ouverte" si un de ses sous-items est actif. */
    public boolean isOuvert() {
        return sousItems.stream().anyMatch(NavigationItemDTO::isActif);
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public List<NavigationItemDTO> getSousItems() {
        return sousItems;
    }

    public void setSousItems(List<NavigationItemDTO> sousItems) {
        this.sousItems = sousItems;
    }
}
