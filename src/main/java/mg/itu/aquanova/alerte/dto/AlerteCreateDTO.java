package mg.itu.aquanova.alerte.dto;

import mg.itu.aquanova.alerte.models.ModuleSource;
import mg.itu.aquanova.alerte.models.NiveauCriticite;
import mg.itu.aquanova.alerte.models.TypeAlerte;
import mg.itu.aquanova.production.models.LotModels;
import mg.itu.aquanova.referentiel.models.Aliment;
import mg.itu.aquanova.referentiel.models.Bassin;

public class AlerteCreateDTO {
    private ModuleSource moduleSource;
    private TypeAlerte typeAlerte;
    private NiveauCriticite niveauCriticite;
    private String message;

    private LotModels lot;
    private Bassin bassin;
    private Aliment aliment;

    public AlerteCreateDTO() {
    }

    public AlerteCreateDTO(ModuleSource moduleSource, TypeAlerte typeAlerte, NiveauCriticite niveauCriticite,
            String message, LotModels lot, Bassin bassin) {
        this(moduleSource, typeAlerte, niveauCriticite, message, lot, bassin, null);
    }

    public AlerteCreateDTO(ModuleSource moduleSource, TypeAlerte typeAlerte, NiveauCriticite niveauCriticite,
            String message, LotModels lot, Bassin bassin, Aliment aliment) {
        this.moduleSource = moduleSource;
        this.typeAlerte = typeAlerte;
        this.niveauCriticite = niveauCriticite;
        this.message = message;
        this.lot = lot;
        this.bassin = bassin;
        this.aliment = aliment;
    }

    public static AlerteCreateDTO pourLot(ModuleSource module, TypeAlerte type, NiveauCriticite niveau,
            String message, LotModels lot) {
        return new AlerteCreateDTO(module, type, niveau, message, lot, null, null);
    }

    public static AlerteCreateDTO pourBassin(ModuleSource module, TypeAlerte type, NiveauCriticite niveau,
            String message, Bassin bassin) {
        return new AlerteCreateDTO(module, type, niveau, message, null, bassin, null);
    }

    public static AlerteCreateDTO pourAliment(ModuleSource module, TypeAlerte type, NiveauCriticite niveau,
            String message, Aliment aliment) {
        return new AlerteCreateDTO(module, type, niveau, message, null, null, aliment);
    }

    public void setModuleSource(ModuleSource moduleSource) {
        this.moduleSource = moduleSource;
    }
    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }
    public void setNiveauCriticite(NiveauCriticite niveauCriticite) {
        this.niveauCriticite = niveauCriticite;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setLot(LotModels lot) {
        this.lot = lot;
    }
    public void setBassin(Bassin bassin) {
        this.bassin = bassin;
    }
    public void setAliment(Aliment aliment) {
        this.aliment = aliment;
    }

    public ModuleSource getModuleSource() {
        return moduleSource;
    }
    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }
    public NiveauCriticite getNiveauCriticite() {
        return niveauCriticite;
    }
    public String getMessage() {
        return message;
    }
    public LotModels getLot() {
        return lot;
    }
    public Bassin getBassin() {
        return bassin;
    }
    public Aliment getAliment() {
        return aliment;
    }
}
