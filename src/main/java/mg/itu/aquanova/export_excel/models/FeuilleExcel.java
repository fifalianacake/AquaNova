package mg.itu.aquanova.export_excel.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeuilleExcel {

    private final String nom;
    private String titre;
    private String sousTitre;
    private List<String> colonnes = new ArrayList<>();
    private final List<List<Object>> lignes = new ArrayList<>();
    private final Set<Integer> colonnesTotalisees = new HashSet<>();

    private FeuilleExcel(String nom) {
        this.nom = nom;
    }

    public static FeuilleExcel of(String nom) {
        return new FeuilleExcel(nom);
    }

    public FeuilleExcel titre(String titre) {
        this.titre = titre;
        return this;
    }

    public FeuilleExcel sousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
        return this;
    }

    public FeuilleExcel colonnes(String... colonnes) {
        this.colonnes = Arrays.asList(colonnes);
        return this;
    }

    public FeuilleExcel ligne(Object... valeurs) {
        this.lignes.add(Arrays.asList(valeurs));
        return this;
    }

    public FeuilleExcel totaliser(Integer... indexColonnes) {
        this.colonnesTotalisees.addAll(Arrays.asList(indexColonnes));
        return this;
    }

    public String getNom() {
        return nom;
    }

    public String getTitre() {
        return titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public List<String> getColonnes() {
        return colonnes;
    }

    public List<List<Object>> getLignes() {
        return lignes;
    }

    public Set<Integer> getColonnesTotalisees() {
        return colonnesTotalisees;
    }
}
