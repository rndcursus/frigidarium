package pt12.frigidarium.database2.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mattijn on 24/05/17.
 */

public class Stock {
    public static final String TABLENAME = "stocks";
    public static final String USERS = "users";
    public static final String INSTOCK =  "in_stock";
    public static final String OUTSTOCK = "out_stock";
    public static final String NAME = "name";
    public static final String UID = "name";
    public String uid;
    public String name;
    public Map<String, String> users;
    public Map<String, Map<String, StockEntry>> in_stock;
    public Map<String, Map<String, StockEntry>> out_stock;

    public String getUid(){
        return uid;
    }

    public String getName(){
        return name;
    }

    public Map<String,String> getUsers(){
        if (users == null){
            return new HashMap<>();
        }
        return users;
    }
    public Map<String, Map<String, StockEntry>> getIn_stock(){
        if (in_stock == null) {
            return new HashMap<>();
        }
        return in_stock;
    }
    public Map<String, Map<String, StockEntry>>  getOut_stock(){
        if (out_stock == null){
            return  new HashMap<>();
        }
        return out_stock;
    }

    /**
     * Do not use this class to create your own instance of Stock.
     */
    public Stock(){}
    public Stock(String uid, String name){
        this.uid = uid;
        this.name = name;
        this.users = null;
        this.in_stock = null;
        this.out_stock = null;
    }

    /**
     * creates a new stock in the database
     * @param stock the stock to be added to the database.
     * @param userID The user that owns this database.
     */
    public static void createStock(Stock stock, String userID){
        getRef(stock.getUid()).setValue(stock);
        getRef(stock.getUid()).child(USERS).push().setValue(userID);
    }

    public static DatabaseReference getRef(String uid){
        return FirebaseDatabase.getInstance().getReference(TABLENAME+"/"+uid);
    }

    public static void checkExist(final String uid, final CheckExist<Stock> checkExist){
        getRef(uid).addValueEventListener(new ValueEventListener() {
            boolean called = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!called) {
                    if (dataSnapshot.getValue(Stock.class) == null) {
                        checkExist.onDoesNotExist(uid);
                    } else {
                        checkExist.onExist(dataSnapshot.getValue(Stock.class));
                    }
                    called = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                checkExist.onError(databaseError);
            }
        });
    }

    public static void removeUserFromStock(final String stockUid, final String userUid){
        getRef(stockUid).child(USERS).addValueEventListener(new ValueEventListener() {
            boolean called = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!called){
                    called =  true;
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                    Map<String,String> users = dataSnapshot.getValue(genericTypeIndicator);
                    for (Map.Entry<String,String> user: users.entrySet()){
                        if (user.getValue().equals(userUid)){
                            getRef(stockUid).child(USERS).child(user.getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //// TODO: 24/05/17 handle error
            }
        });
    }

    public static void addUserToStock(String stockUid, String userUid){
        getRef(stockUid).child(USERS).push().setValue(userUid);
    }

    public static void addStockEntryToInStock(String stockUid, StockEntry entry){
        getRef(stockUid).child(INSTOCK).child(entry.getProductUid()).push().setValue(entry);
    }

    public static void removeFromInStock(String stockUid,String product_uid, String stockEntryKey){
        getRef(stockUid).child(INSTOCK).child(product_uid).child(stockEntryKey).removeValue();
    }

    public static void addStockEntryToOutStock(String stockUid, StockEntry entry){
        entry.best_before =  null;
        getRef(stockUid).child(OUTSTOCK).child(entry.getProductUid()).push().setValue(entry);
    }

    public static void removeFromOutStock(String stockUid,String product_uid, String stockEntryKey){
        getRef(stockUid).child(OUTSTOCK).child(product_uid).child(stockEntryKey).removeValue();
    }
}
