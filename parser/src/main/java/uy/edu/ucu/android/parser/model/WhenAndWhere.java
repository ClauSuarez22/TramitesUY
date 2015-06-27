package uy.edu.ucu.android.parser.model;

import java.util.ArrayList;
import java.util.List;

/**
 * IMPORTANT! YOU SHOULD NOT CHANGE THIS CLASS
 */
public class WhenAndWhere {

    private List<Location> locations = new ArrayList<>();
    private String otherData;

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }

}
