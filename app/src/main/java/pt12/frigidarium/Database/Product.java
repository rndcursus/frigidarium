package pt12.frigidarium.Database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Het lezen van waardes altijd doen via OnProductChangeListener.
 */

public class Product extends DatabaseEntryOwner{

    public static final String BRAND = "brand";
    public static final String BARCODE = "barcode";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    private Set<OnProductChangeListener> productListeners;

    private static DatabaseReference createReference(String barcode){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products").child(barcode);
        return myRef;
    }

    private static Map<String, DatabaseEntry> getEntries(String barcode){
        DatabaseReference ref = createReference(barcode);
        Map<String, DatabaseEntry>  entries = new HashMap<>();
        entries.put(BARCODE, new DatabaseEntry<Product,String>(BARCODE, ref.child(BARCODE)));
        entries.put(NAME, new DatabaseEntry<Product,String>(NAME, ref.child(NAME)));
        entries.put(BRAND, new DatabaseEntry<Product,String>(BRAND, ref.child(BRAND)));
        entries.put(CONTENT, new DatabaseEntry<Product,String>(CONTENT, ref.child(CONTENT)));
        entries.put(URL, new DatabaseEntry<Product,String>(URL, ref.child(URL)));
        return entries;
    }

    public Product(String identifier) {
        super(createReference(identifier), getEntries(identifier));
        productListeners = new HashSet<>();
        DatabaseEntry.OnChangeListener listener = new DatabaseEntry.OnChangeListener() {
            @Override
            public void OnChange(DatabaseEntryOwner owner, String name, Object value) {
                for (OnProductChangeListener listener : productListeners) {
                    listener.onChange(name);
                }
            }
        };

        super.getEntry(BARCODE).addListener(listener);
        super.getEntry(NAME).addListener(listener);
        super.getEntry(URL).addListener(listener);
        super.getEntry(BRAND).addListener(listener);
        super.getEntry(CONTENT).addListener(listener);
    }

    public void setBarcode(String barcode){
        DatabaseEntry<Product, String> entry = super.getEntry(BARCODE);
        entry.setValue(barcode);
    }
    public void setName(String name){
        DatabaseEntry<Product, String> entry = super.getEntry(NAME);
        entry.setValue(name);
    }
    public void setBrand(String brand){
        DatabaseEntry<Product, String> entry = super.getEntry(BRAND);
        entry.setValue(brand);
    }
    public void setContent(String content){
        DatabaseEntry<Product, String> entry = super.getEntry(CONTENT);
        entry.setValue(content);
    }
    public void setUrl(String url){
        DatabaseEntry<Product, String> entry = super.getEntry(URL);
        entry.setValue(url);
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
        /**
         * only call this in onChange.
         */
        public String getBarcode(){
            DatabaseEntry<Product, String> entry = product.getEntry("barcode"); //Todo magic number
            return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getName(){
            DatabaseEntry<Product, String> entry = product.getEntry(NAME);
            return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getBrand(){
            DatabaseEntry<Product, String> entry = product.getEntry(BRAND);
             return entry.getValue();
        }
        /**
         * only call this in onChange.
         */
        public String getContent(){
            DatabaseEntry<Product, String> entry = product.getEntry(CONTENT);
            return entry.getValue();
        }

        /**
         * only call this in onChange.
         */
        public String getUrl(){
            DatabaseEntry<Product, String> entry = product.getEntry(URL);
            return entry.getValue();
        }

        /**
         * This method is called after a change in the database.
         * This class then contains all the updated data.
         * this method will only be called after the
         * @param name
         */
        public abstract void onChange(String name);
    }

}
