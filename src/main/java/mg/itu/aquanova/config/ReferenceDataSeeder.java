package mg.itu.aquanova.config;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mg.itu.aquanova.production.models.StatutLotEnum;
import mg.itu.aquanova.production.models.StatutLotModels;
import mg.itu.aquanova.production.models.TypeEvenementLot;
import mg.itu.aquanova.production.models.TypeRecolteEnum;
import mg.itu.aquanova.production.models.TypeRecoltes;
import mg.itu.aquanova.production.repositories.StatutLotRepository;
import mg.itu.aquanova.production.repositories.TypeEvenementLotRepository;
import mg.itu.aquanova.production.repositories.TypeRecoltesRepository;
import mg.itu.aquanova.referentiel.models.LibelleStatutBassin;
import mg.itu.aquanova.referentiel.models.StadeCroissanceModels;
import mg.itu.aquanova.referentiel.models.StatutBassin;
import mg.itu.aquanova.referentiel.models.TypeAlimentModels;
import mg.itu.aquanova.referentiel.models.TypeBassin;
import mg.itu.aquanova.referentiel.repositories.StadeCroissanceRepository;
import mg.itu.aquanova.referentiel.repositories.StatutBassinRepository;
import mg.itu.aquanova.referentiel.repositories.TypeAlimentRepository;
import mg.itu.aquanova.referentiel.repositories.TypeBassinRepository;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenance;
import mg.itu.aquanova.sanitaire_equipement.models.CategorieMaintenanceEnum;
import mg.itu.aquanova.sanitaire_equipement.repositories.CategorieMaintenanceRepository;
import mg.itu.aquanova.security.models.RoleModels;
import mg.itu.aquanova.security.models.User;
import mg.itu.aquanova.security.models.UserRoleModels;
import mg.itu.aquanova.security.repositories.RoleRepository;
import mg.itu.aquanova.security.repositories.UserRepository;
import mg.itu.aquanova.security.repositories.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import mg.itu.aquanova.vente.models.StatutVente;
import mg.itu.aquanova.vente.models.StatutVenteEnum;
import mg.itu.aquanova.vente.models.TypeClient;
import mg.itu.aquanova.vente.models.TypeClientEnum;
import mg.itu.aquanova.vente.repositories.StatutVenteRepository;
import mg.itu.aquanova.vente.repositories.TypeClientRepository;

@Component
public class ReferenceDataSeeder implements CommandLineRunner {

    private static final String[] ROLES = { "ADMIN", "TECHNICIEN" };

    private static final String[] TYPES_BASSIN = {
            "GROSSISSEMENT",
            "ALEVINAGE",
            "TRAITEMENT",
            "STOCKAGE",
            "RESERVE_EAU"
    };

    private static final String[] TYPES_ALIMENT = {
            "DEMARRAGE",
            "CROISSANCE",
            "FINITION",
            "GENITEUR",
            "NATUREL"
    };

    private record StadeSeed(String nom, String poidsMin, String poidsMax) {
    }

    private static final StadeSeed[] STADES_CROISSANCE = {
            new StadeSeed("ALEVIN", "0", "5"),
            new StadeSeed("JUVENILE", "5", "50"),
            new StadeSeed("GROSSISSEMENT", "50", "300"),
            new StadeSeed("FINITION", "300", "600"),
            new StadeSeed("ADULTE", "600", null)
    };

    private static final Map<StatutLotEnum, String> STATUT_LOT_DESCRIPTIONS = Map.of(
            StatutLotEnum.EN_CROISSANCE, "Lot en croissance",
            StatutLotEnum.CLOTURE, "Lot cloture",
            StatutLotEnum.ANNULE, "Lot annule");

    private static final Map<TypeRecolteEnum, String> TYPE_RECOLTE_DESCRIPTIONS = Map.of(
            TypeRecolteEnum.PARTIELLE, "Recolte partielle du lot",
            TypeRecolteEnum.TOTALE, "Recolte totale du lot");

    private static final Map<StatutVenteEnum, String> STATUT_VENTE_LIBELLES = Map.of(
            StatutVenteEnum.CREEE, "Creee",
            StatutVenteEnum.VALIDEE, "Validee",
            StatutVenteEnum.PAYEE, "Payee",
            StatutVenteEnum.ANNULEE, "Annulee");

    private static final Map<TypeClientEnum, String> TYPE_CLIENT_LIBELLES = Map.of(
            TypeClientEnum.GROSSISTE, "Grossiste",
            TypeClientEnum.REVENDEUR, "Revendeur",
            TypeClientEnum.PARTICULIER, "Particulier",
            TypeClientEnum.RESTAURANT, "Restaurant");

    private static final Map<CategorieMaintenanceEnum, String> CATEGORIE_MAINTENANCE_DESCRIPTIONS = Map.of(
            CategorieMaintenanceEnum.MAINTENANCE_PREVENTIVE, "Maintenance preventive planifiee",
            CategorieMaintenanceEnum.MAINTENANCE_CORRECTIVE, "Maintenance corrective",
            CategorieMaintenanceEnum.PANNE, "Intervention sur panne",
            CategorieMaintenanceEnum.NETTOYAGE, "Operation de nettoyage",
            CategorieMaintenanceEnum.REPARATION, "Operation de reparation");

    private static final String ADMIN_EMAIL = "admin@aquanova.mg";
    private static final String ADMIN_MOT_DE_PASSE = "admin123";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TypeBassinRepository typeBassinRepository;
    private final TypeAlimentRepository typeAlimentRepository;
    private final StatutBassinRepository statutBassinRepository;
    private final StatutLotRepository statutLotRepository;
    private final TypeRecoltesRepository typeRecoltesRepository;
    private final TypeEvenementLotRepository typeEvenementLotRepository;
    private final StatutVenteRepository statutVenteRepository;
    private final TypeClientRepository typeClientRepository;
    private final CategorieMaintenanceRepository categorieMaintenanceRepository;
    private final StadeCroissanceRepository stadeCroissanceRepository;

    public ReferenceDataSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            TypeBassinRepository typeBassinRepository,
            TypeAlimentRepository typeAlimentRepository,
            StatutBassinRepository statutBassinRepository,
            StatutLotRepository statutLotRepository,
            TypeRecoltesRepository typeRecoltesRepository,
            TypeEvenementLotRepository typeEvenementLotRepository,
            StatutVenteRepository statutVenteRepository,
            TypeClientRepository typeClientRepository,
            CategorieMaintenanceRepository categorieMaintenanceRepository,
            StadeCroissanceRepository stadeCroissanceRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.typeBassinRepository = typeBassinRepository;
        this.typeAlimentRepository = typeAlimentRepository;
        this.statutBassinRepository = statutBassinRepository;
        this.statutLotRepository = statutLotRepository;
        this.typeRecoltesRepository = typeRecoltesRepository;
        this.typeEvenementLotRepository = typeEvenementLotRepository;
        this.statutVenteRepository = statutVenteRepository;
        this.typeClientRepository = typeClientRepository;
        this.categorieMaintenanceRepository = categorieMaintenanceRepository;
        this.stadeCroissanceRepository = stadeCroissanceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedAdminParDefaut();
        seedTypesBassin();
        seedTypesAliment();
        seedStatutsBassin();
        seedStatutsLot();
        seedTypesRecolte();
        seedTypesEvenementLot();
        seedStatutsVente();
        seedTypesClient();
        seedCategoriesMaintenance();
        seedStadesCroissance();
    }

    private void seedRoles() {
        for (String role : ROLES) {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new RoleModels(role));
            }
        }
    }

    /**
     * Compte administrateur par défaut (pré-rempli sur la page de connexion).
     * Créé uniquement s'il n'existe pas encore ; le mot de passe n'est jamais
     * réécrit sur un compte existant.
     */
    private void seedAdminParDefaut() {
        if (userRepository.findByEmail(ADMIN_EMAIL) != null) {
            return;
        }

        User admin = new User(
                "Admin",
                "AquaNova",
                ADMIN_EMAIL,
                passwordEncoder.encode(ADMIN_MOT_DE_PASSE),
                new java.sql.Date(System.currentTimeMillis()));
        admin = userRepository.save(admin);

        RoleModels roleAdmin = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rôle ADMIN introuvable au seed."));
        userRoleRepository.save(new UserRoleModels(admin, roleAdmin));
    }

    private void seedTypesBassin() {
        for (String libelle : TYPES_BASSIN) {
            if (!typeBassinRepository.existsByLibelleIgnoreCase(libelle)) {
                TypeBassin type = new TypeBassin();
                type.setLibelle(libelle);
                typeBassinRepository.save(type);
            }
        }
    }

    private void seedTypesAliment() {
        for (String nom : TYPES_ALIMENT) {
            if (!typeAlimentRepository.existsByNomIgnoreCase(nom)) {
                TypeAlimentModels type = new TypeAlimentModels();
                type.setNom(nom);
                typeAlimentRepository.save(type);
            }
        }
    }

    private void seedStatutsBassin() {
        for (LibelleStatutBassin libelle : LibelleStatutBassin.values()) {
            if (!statutBassinRepository.existsByLibelle(libelle)) {
                StatutBassin statut = new StatutBassin();
                statut.setLibelle(libelle);
                statutBassinRepository.save(statut);
            }
        }
    }

    private void seedStatutsLot() {
        for (StatutLotEnum libelle : StatutLotEnum.values()) {
            if (!statutLotRepository.existsByLibelle(libelle)) {
                StatutLotModels statut = new StatutLotModels();
                statut.setLibelle(libelle);
                statut.setDescription(STATUT_LOT_DESCRIPTIONS.getOrDefault(libelle, defaultLabel(libelle)));
                statutLotRepository.save(statut);
            }
        }
    }

    private void seedTypesRecolte() {
        for (TypeRecolteEnum libelle : TypeRecolteEnum.values()) {
            if (!typeRecoltesRepository.existsByLibelle(libelle)) {
                TypeRecoltes type = new TypeRecoltes();
                type.setLibelle(libelle);
                type.setDescription(TYPE_RECOLTE_DESCRIPTIONS.getOrDefault(libelle, defaultLabel(libelle)));
                typeRecoltesRepository.save(type);
            }
        }
    }

    private void seedTypesEvenementLot() {
        for (TypeEvenementLot.LibelleEvenement libelle : TypeEvenementLot.LibelleEvenement.values()) {
            if (!typeEvenementLotRepository.existsByLibelle(libelle)
                    && !typeEvenementLotRepository.existsByCode(libelle.name())) {
                TypeEvenementLot type = new TypeEvenementLot();
                type.setCode(libelle.name());
                type.setLibelle(libelle);
                typeEvenementLotRepository.save(type);
            }
        }
    }

    private void seedStatutsVente() {
        for (StatutVenteEnum code : StatutVenteEnum.values()) {
            if (!statutVenteRepository.existsByCode(code)) {
                StatutVente statut = new StatutVente();
                statut.setCode(code);
                statut.setLibelle(STATUT_VENTE_LIBELLES.getOrDefault(code, defaultLabel(code)));
                statutVenteRepository.save(statut);
            }
        }
    }

    private void seedTypesClient() {
        for (TypeClientEnum code : TypeClientEnum.values()) {
            if (!typeClientRepository.existsByCode(code)) {
                TypeClient type = new TypeClient();
                type.setCode(code);
                type.setLibelle(TYPE_CLIENT_LIBELLES.getOrDefault(code, defaultLabel(code)));
                typeClientRepository.save(type);
            }
        }
    }

    private void seedCategoriesMaintenance() {
        for (CategorieMaintenanceEnum libelle : CategorieMaintenanceEnum.values()) {
            if (!categorieMaintenanceRepository.existsByLibelle(libelle)) {
                CategorieMaintenance categorie = new CategorieMaintenance();
                categorie.setLibelle(libelle);
                categorie.setDescription(CATEGORIE_MAINTENANCE_DESCRIPTIONS.getOrDefault(libelle, defaultLabel(libelle)));
                categorieMaintenanceRepository.save(categorie);
            }
        }
    }

    private void seedStadesCroissance() {
        for (StadeSeed stade : STADES_CROISSANCE) {
            if (!stadeCroissanceRepository.existsByNomIgnoreCase(stade.nom())) {
                StadeCroissanceModels modele = new StadeCroissanceModels();
                modele.setNom(stade.nom());
                modele.setPoidsMin(stade.poidsMin() != null ? new BigDecimal(stade.poidsMin()) : null);
                modele.setPoidsMax(stade.poidsMax() != null ? new BigDecimal(stade.poidsMax()) : null);
                stadeCroissanceRepository.save(modele);
            }
        }
    }

    private static String defaultLabel(Enum<?> value) {
        String text = value.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        StringBuilder label = new StringBuilder(text.length());
        boolean capitalize = true;

        for (char character : text.toCharArray()) {
            if (capitalize && Character.isLetter(character)) {
                label.append(Character.toUpperCase(character));
                capitalize = false;
            } else {
                label.append(character);
            }

            if (character == ' ') {
                capitalize = true;
            }
        }

        return label.toString();
    }
}
