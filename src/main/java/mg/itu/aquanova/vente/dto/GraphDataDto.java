package mg.itu.aquanova.vente.dto;

import java.util.List;

public class GraphDataDto {

    private List<String> labels;
    private List<Double> values;

    public GraphDataDto() {}

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Double> getValues() { return values; }
    public void setValues(List<Double> values) { this.values = values; }
}