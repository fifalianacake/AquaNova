package mg.itu.aquanova.production.specifications;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.data.jpa.domain.Specification;

import mg.itu.aquanova.production.models.LotModels;

public final class LotSpecifications {

    private LotSpecifications() {}

    public static Specification<LotModels> withId(Long id) {
        return (root, query, cb) -> id == null ? cb.conjunction() : cb.equal(root.get("id"), id);
    }

    public static Specification<LotModels> withCode(String code) {
        return (root, query, cb) -> (code == null || code.isBlank()) ? cb.conjunction() : cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<LotModels> withEspece(Integer especeId) {
        return (root, query, cb) -> especeId == null ? cb.conjunction() : cb.equal(root.get("espece").get("id"), especeId);
    }

    public static Specification<LotModels> withBassin(Long bassinId) {
        return (root, query, cb) -> bassinId == null ? cb.conjunction() : cb.equal(root.get("bassin").get("id"), bassinId);
    }

    public static Specification<LotModels> withStade(Integer stadeId) {
        return (root, query, cb) -> stadeId == null ? cb.conjunction() : cb.equal(root.get("stadeCroissance").get("id"), stadeId);
    }

    public static Specification<LotModels> withStatut(Long statutId) {
        return (root, query, cb) -> statutId == null ? cb.conjunction() : cb.equal(root.get("statutLot").get("id"), statutId);
    }

    public static Specification<LotModels> withDateFrom(String dateFrom) {
        if (dateFrom == null || dateFrom.isBlank()) return (r, q, cb) -> cb.conjunction();
        try {
            LocalDate d = LocalDate.parse(dateFrom);
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dateMiseEnCharge"), d);
        } catch (DateTimeParseException ex) {
            return (r, q, cb) -> cb.conjunction();
        }
    }

    public static Specification<LotModels> withDateTo(String dateTo) {
        if (dateTo == null || dateTo.isBlank()) return (r, q, cb) -> cb.conjunction();
        try {
            LocalDate d = LocalDate.parse(dateTo);
            return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dateMiseEnCharge"), d);
        } catch (DateTimeParseException ex) {
            return (r, q, cb) -> cb.conjunction();
        }
    }

    public static Specification<LotModels> withEffectifMin(Integer min) {
        return (root, query, cb) -> min == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("effectifActuel"), min);
    }

    public static Specification<LotModels> withEffectifMax(Integer max) {
        return (root, query, cb) -> max == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("effectifActuel"), max);
    }
}
