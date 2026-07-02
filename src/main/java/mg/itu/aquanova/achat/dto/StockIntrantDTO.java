package mg.itu.aquanova.achat.dto;

import java.math.BigDecimal;

import mg.itu.aquanova.achat.models.Intrant;

public class StockIntrantDTO {

    private Intrant intrant;
    private BigDecimal stockActuel;

    public StockIntrantDTO(Intrant intrant, BigDecimal stockActuel) {
        this.intrant = intrant;
        this.stockActuel = stockActuel;
    }

    public Intrant getIntrant() {
        return intrant;
    }

    public void setIntrant(Intrant intrant) {
        this.intrant = intrant;
    }

    public BigDecimal getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }
}
