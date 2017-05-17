package pt12.frigidarium.Database.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt12.frigidarium.Database.firebase.DatabaseEntry;
import pt12.frigidarium.Database.firebase.DatabaseEntryOwner;
import pt12.frigidarium.Database.firebase.DatabaseGroupedEntry;
import pt12.frigidarium.Database.firebase.DatabaseMapEntry;
import pt12.frigidarium.Database.firebase.DatabaseSingleEntry;

/**
 * Created by mattijn on 15/05/17.
 */

public class Stock extends DatabaseEntryOwner<Stock> {
    public static final String USERS = "users";
    public static final String INSTOCK =  "in_stock";
    public static final String OUTSTOCK = "out_stock";
    public static final String NAME = "name";
    public static final String UID = "uid";
    public static final String EVENTS = "events";
    private static Map<String,Stock> stocks= new HashMap<>();

    private Set<OnStockChangeListener> listeners = new HashSet<>();
    private static final String ADD_OUT_Of_STOCK = "added_to_out_of_stock";
    private static final String REMOVE_OUT_Of_STOCK = "removed_out_of_stock";

    public static Stock getInstanceByUID(String uid){
        if (!stocks.containsKey(uid)){
            stocks.put(uid,new Stock(uid));
        }
        return stocks.get(uid);
    }

    private static DatabaseReference createReference(String uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("stocks").child(uid);
        return myRef;
    }
    private static Map<String, DatabaseEntry> getEntries(String uid){
        DatabaseReference ref = createReference(uid);
        Map<String, DatabaseEntry>  entries = new HashMap<>();
        entries.put(UID, new DatabaseSingleEntry<Stock,String>(UID, ref.child(UID), String.class));
        entries.put(NAME, new DatabaseSingleEntry<Stock,String>(NAME, ref.child(NAME), String.class));
        entries.put(USERS, new DatabaseMapEntry<Stock,String>(USERS, ref.child(USERS), String.class));
        entries.put(INSTOCK, new DatabaseGroupedEntry<Stock,StockEntry>(INSTOCK, ref.child(INSTOCK), StockEntry.class));
        entries.put(OUTSTOCK, new DatabaseGroupedEntry<Stock,StockEntry>(OUTSTOCK, ref.child(OUTSTOCK), StockEntry.class));
        entries.put(EVENTS, new DatabaseMapEntry<Stock,StockEvent>(EVENTS, ref.child(EVENTS), StockEvent.class));
        return entries;
    }

    private Stock(String uid){
        super(uid, createReference(uid),getEntries(uid));

    }
    public void removeFromOutOfStock(StockEntry entry){
        DatabaseGroupedEntry<Stock, StockEntry> outofstock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(OUTSTOCK);
        outofstock.removeEntry(entry,entry.getProduct_uid());
    }
    public void addToOutOfStock(StockEntry entry){
        DatabaseGroupedEntry<Stock, StockEntry> outofstock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(OUTSTOCK);
        outofstock.addEntry(entry, entry.getProduct_uid());
    }

    public void moveInStock(StockEntry entry){
        DatabaseGroupedEntry<Stock, StockEntry> outofstock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(OUTSTOCK);
        DatabaseGroupedEntry<Stock, StockEntry> instock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(INSTOCK);
        outofstock.removeEntry(entry,entry.getProduct_uid());
        instock.addEntry(entry, entry.getProduct_uid());
    }

    public void moveOutOfStock(StockEntry entry){
        DatabaseGroupedEntry<Stock, StockEntry> outofstock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(OUTSTOCK);
        DatabaseGroupedEntry<Stock, StockEntry> instock  = (DatabaseGroupedEntry<Stock, StockEntry>) getEntry(INSTOCK);
        instock.removeEntry(entry,entry.getProduct_uid());
        outofstock.addEntry(entry, entry.getProduct_uid());
    }

    public abstract class OnStockChangeListener{
        private Stock stock;

        private void  setStock(Stock stock){
            this.stock = stock;
        }
        public abstract void onNameChange(Stock owner, String value);
    }

    public static class StockEvent{
        Map<String, String> timestamp;
        String user_uid;
        String product_uid;
        String event;
        Double amount;

        public StockEvent() {}

        public Map<String, String> getTimestamp(){
            return timestamp;
        }
        public String getUser_uid(){
            return user_uid;
        }
        public String getProduct_uid(){
            return product_uid;
        }
        public String getEvent(){
            return event;
        }
        public Double getAmount(){
            return amount;
        }
    }

    public static class StockEntry {
        String product_uid;
        Map<String, String> timeAdded;
        Long best_before;
        String addedByUser;

        /**
         * never call this constructor. use the other one;
         *
         */
        @Deprecated
        public StockEntry() {
        }

        public StockEntry(String product_uid, Long best_before) {
            this.product_uid = product_uid;
            this.best_before = best_before;
            addedByUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            timeAdded= ServerValue.TIMESTAMP;
        }

        public String getProduct_uid() {
            return product_uid;
        }

        public String toString() {
            return "{" + product_uid + ":" + best_before + "}";
        }

        /**
         * looks at the product_uid and the bestbefore date.
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof StockEntry) {
                if (best_before == null && ((StockEntry) o).best_before == null) {
                    return product_uid.equals(((StockEntry) o).getProduct_uid());
                }else if (best_before == null && !(((StockEntry) o).best_before == null)) {
                    return false;
                }else{
                    return product_uid.equals(((StockEntry) o).getProduct_uid())  && best_before.equals(((StockEntry) o).best_before);
                }
            }
            return true;
        }
    }
    public class OnStockChangedListener{
        //public void on
    }
}
