package mg.itu.aquanova.production.services;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class LotFilter {
    private Long id;
    private String code;
    private Integer especeId;
    private Long bassinId;
    private Integer stadeId;
    private Long statutId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private Integer effectifMin;
    private Integer effectifMax;

    public LotFilter() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getEspeceId() { return especeId; }
    public void setEspeceId(Integer especeId) { this.especeId = especeId; }
    public Long getBassinId() { return bassinId; }
    public void setBassinId(Long bassinId) { this.bassinId = bassinId; }
    public Integer getStadeId() { return stadeId; }
    public void setStadeId(Integer stadeId) { this.stadeId = stadeId; }
    public Long getStatutId() { return statutId; }
    public void setStatutId(Long statutId) { this.statutId = statutId; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public Integer getEffectifMin() { return effectifMin; }
    public void setEffectifMin(Integer effectifMin) { this.effectifMin = effectifMin; }
    public Integer getEffectifMax() { return effectifMax; }
    public void setEffectifMax(Integer effectifMax) { this.effectifMax = effectifMax; }
}
