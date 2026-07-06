package mg.itu.aquanova.production.dto;

import java.time.LocalDate;

public class RecolteForm {
    private Long lotId;
    private Long typeRecolteId;
    private LocalDate dateRecolte;
    private Integer effectifRecolte;

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Long getTypeRecolteId() {
        return typeRecolteId;
    }

    public void setTypeRecolteId(Long typeRecolteId) {
        this.typeRecolteId = typeRecolteId;
    }

    public LocalDate getDateRecolte() {
        return dateRecolte;
    }

    public void setDateRecolte(LocalDate dateRecolte) {
        this.dateRecolte = dateRecolte;
    }

    public Integer getEffectifRecolte() {
        return effectifRecolte;
    }

    public void setEffectifRecolte(Integer effectifRecolte) {
        this.effectifRecolte = effectifRecolte;
    }

}
