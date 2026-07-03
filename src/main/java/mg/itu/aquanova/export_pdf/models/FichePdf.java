package mg.itu.aquanova.export_pdf.models;

import java.util.List;

public class FichePdf {
    private final Object mainObject;      
    private final String subListTitle;    
    private final List<?> subList; 

    public FichePdf(Object mainObject, String subListTitle, List<?> subList) {
        this.mainObject = mainObject;
        this.subListTitle = subListTitle;
        this.subList = subList;
    }

    public Object getMainObject() { 
        return mainObject; 
    }
    public String getSubListTitle() { 
        return subListTitle; 
    }
    public List<?> getSubList() { 
        return subList; 
    }
}