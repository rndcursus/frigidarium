package pt12.frigidarium.Database.models;

import com.google.android.gms.common.api.PendingResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt12.frigidarium.Database.firebase.DatabaseEntry;
import pt12.frigidarium.Database.firebase.DatabaseEntryOwner;
import pt12.frigidarium.Database.firebase.DatabaseSingleEntry;

/**
 * Het lezen van waardes altijd doen via OnProductChangeListener.
 */

public class Product extends DatabaseEntryOwner<Product> {

    public static final String BRAND = "brand";
    public static final String UID = "uid";
    public static final String BARCODE = "barcode";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    private Set<OnProductChangeListener> productListeners;
    private static Map<String,Product> products = new HashMap<>();
    public static Product getInstanceByUID(String uid){
        if (!products.containsKey(uid)){
            products.put(uid,new Product(uid));
        }
        return products.get(uid);
    }
    private static DatabaseReference createReference(String uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products").child(uid);
        return myRef;
    }

    private static Map<String, DatabaseEntry> getEntries(String uid){
        DatabaseReference ref = createReference(uid);
        Map<String, DatabaseEntry>  entries = new HashMap<>();
        entries.put(BARCODE, new DatabaseSingleEntry<Product,String>(BARCODE, ref.child(BARCODE), String.class));
        entries.put(NAME, new DatabaseSingleEntry<Product,String>(NAME, ref.child(NAME), String.class));
        entries.put(BRAND, new DatabaseSingleEntry<Product,String>(BRAND, ref.child(BRAND), String.class));
        entries.put(CONTENT, new DatabaseSingleEntry<Product,String>(CONTENT, ref.child(CONTENT), String.class));
        entries.put(URL, new DatabaseSingleEntry<Product,String>(URL, ref.child(URL), String.class));
        entries.put(UID, new DatabaseSingleEntry<Product,String>(UID, ref.child(UID), String.class));
        return entries;
    }

    private Product(String identifier) {
        super(identifier,createReference(identifier), getEntries(identifier));
        productListeners = new HashSet<>();
        final Product p = this;
        DatabaseEntry.OnChangeListener listener = new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (OnProductChangeListener listener : productListeners) {
                    listener.onChange(p, name);
                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {

            }
        };

        super.getEntry(BARCODE).addListener(listener);
        super.getEntry(NAME).addListener(listener);
        super.getEntry(URL).addListener(listener);
        super.getEntry(BRAND).addListener(listener);
        super.getEntry(CONTENT).addListener(listener);
        super.getEntry(UID).addListener(listener);

    }

    public void addListener(OnProductChangeListener onProductChangeListener) {
        onProductChangeListener.setProduct(this);
        this.productListeners.add(onProductChangeListener);
    }

    public abstract static class OnProductChangeListener{
        private Product product;
        public OnProductChangeListener(){

        }
        private void setProduct(Product p){
            product = p;
        }
        public void setBarcode(String barcode){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(BARCODE);
            entry.setValue(barcode);
        }
        public void setName(String name){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(NAME);
            entry.setValue(name);
        }
        public void setBrand(String brand){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(BRAND);
            entry.setValue(brand);
        }
        public void setContent(String content){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(CONTENT);
            entry.setValue(content);
        }
        public void setUrl(String url){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(URL);
            entry.setValue(url);
        }

        /**
         * only call this in onChange.
         */
        public String getBarcode(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(BARCODE);
            return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getName(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(NAME);
            return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getBrand(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(BRAND);
             return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getContent(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(CONTENT);
            return entry.getValue();
        }

        /**
         * only call this in onChange.
         */
        public String getUrl(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(URL);
            return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getUID(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) product.getEntry(UID);
            return entry.getValue();
        }
        /**
         * This method is called after a change in the database.
         * This class then contains all the updated data.
         * this method will only be called after the
         * @param name
         */
        public abstract void onChange(Product p, String name);
    }

}