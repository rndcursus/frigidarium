package pt12.frigidarium.database2.models;

import java.util.Map;

/**
 * Created by mattijn on 24/05/17.
 */

public class StockEntry {
    public String getProductUid() {
        return product_uid;
    }
    public String product_uid;
    //public Map<String, String> timeAdded;
    public Long best_before;
    public String addedByUser;
}
