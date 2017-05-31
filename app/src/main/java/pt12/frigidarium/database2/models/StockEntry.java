package pt12.frigidarium.database2.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by mattijn on 24/05/17.
 */

public class StockEntry {
    public String getProductUid() {
        return product_uid;
    }
    public String product_uid;
    public Map<String, String> timeAdded;
    public Long best_before;
    public String addedByUser;
    public StockEntry() {}
    public StockEntry(String uid, Long best_before){
        this.product_uid  = uid;
        this.best_before = best_before;
        this.timeAdded = ServerValue.TIMESTAMP;
        this.addedByUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
