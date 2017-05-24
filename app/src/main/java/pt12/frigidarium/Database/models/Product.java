package pt12.frigidarium.Database.models;

import com.google.android.gms.common.api.PendingResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    public static final String BARCODE = "barcode";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String CONTENT = "content";
    private static Map<String,Product> products = new HashMap<>();
    /**
     * Use this function to create a Product. This Stock will be passed in callback.
     * @param uid the uid of a Product
     * @param callback the callback after The Product has been created.
     */
    public static void getInstanceByUID(String uid, final DatabaseEntryOwner.onReadyCallback<Product> callback){
        Product s = getInstanceByUID(uid);
        final boolean[] called = {false};
        s.addDataAccessor(new DataAccessor<Product>() {
            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                callback.onError(owner,name,code,message,details);
            }

            @Override
            public void onGetInstance(Product owner) {
                called[0] = true;
                if (getUid() == null || getUid().equals("")){
                    callback.OnDoesNotExist(owner);
                }else {
                    callback.onExist(owner);
                }
            }
        });
        if (!called[0] && s.isFinished()){
            for (final DataAccessor<Product> l: products.get(uid).getDataAccessors()){
                l.onGetInstance(products.get(uid));
            }
        }
    }
    /**
     * Use this function to create a Product.
     * @param uid the uid of a product
     * @return null if the product does not exsist in the database
     */
    public static Product getInstanceByUID(final String uid){
        if (!products.containsKey(uid)){
            products.put(uid,new Product(uid));
        }
        for (final DataAccessor<Product> l: products.get(uid).getDataAccessors()){
            DatabaseEntryOwner.OnFinishedListener<Product>  lf = new OnFinishedListener<Product>() {
                @Override
                public void onFinished(Product owner) {
                    l.onGetInstance(products.get(uid));
                }
            };
            products.get(uid).addOnFinishedListener(lf);

        }
        return products.get(uid);
    }
    /**
     * This function creates a new entry in the firebase database.
     * However if the User already exists it will be overridden.
     * @param uid the firebaseuid of the user.
     * @param name the name of the user
     * @param barcode the barcode of the product.
     * @param brand the brand of the product.
     * @param url the url of the product
     * @param added_by the user that added this product to the database. //currently not in use
     * @return the newly created entry
     */
    public static Product createProduct(String uid, String name, String brand,  String barcode, String url, String content, String added_by){
        Product p =  Product.getInstanceByUID(uid);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(BRAND)).setValue(brand);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(BARCODE)).setValue(barcode);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(URL)).setValue(url);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(NAME)).setValue(name);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(CONTENT)).setValue(content);
        ((DatabaseSingleEntry<Product,String>)  p.getEntry(UID)).setValue(uid);
        return p;
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
        return entries;
    }

    private Product(String identifier) {
        super(identifier,createReference(identifier), getEntries(identifier));
        final Product p = this;
        DatabaseEntry.OnChangeListener listener = new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        };

        super.getEntry(BARCODE).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });
        super.getEntry(NAME).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }
                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });
        super.getEntry(URL).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });
        super.getEntry(BRAND).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });
        super.getEntry(CONTENT).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });
        super.getEntry(UID).addListener(new DatabaseSingleEntry.OnChangeListener<Product,String>() {
            @Override
            public void onChange(Product owner, String name, String value) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    if (listener instanceof Product.OnProductChangeListener){
                        OnProductChangeListener l = (OnProductChangeListener) listener;
                        l.onChange(p, name);
                    }

                }
            }

            @Override
            public void onError(Product owner, String name, int code, String message, String details) {
                for (DataAccessor<Product> listener : getDataAccessors()) {
                    listener.onError(owner,name,code,message,details);

                }
            }
        });

    }

    public void setBarcode(String barcode){
        DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getEntry(BARCODE);
        entry.setValue(barcode);
    }
    public void setName(String name){
        DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getEntry(NAME);
        entry.setValue(name);
    }
    public void setBrand(String brand){
        DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getEntry(BRAND);
        entry.setValue(brand);
    }
    public void setContent(String content){
        DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getEntry(CONTENT);
        entry.setValue(content);
    }
    public void setUrl(String url) {
        DatabaseSingleEntry<Product, String> entry = (DatabaseSingleEntry<Product, String>) this.getEntry(URL);
        entry.setValue(url);
    }
    public abstract static class OnProductChangeListener extends DatabaseEntryOwner.DataAccessor<Product>{
        public OnProductChangeListener(){

        }
        protected void setBarcode(String barcode){
            getOwner().setBarcode(barcode);
        }
        protected void setName(String name){
            getOwner().setName(name);
        }
        protected void setBrand(String brand){
            getOwner().setBrand(brand);
        }
        protected void setContent(String content){
            getOwner().setContent(content);
        }
        protected void setUrl(String url) {
            getOwner().setUrl(url);
        }

        /**
         * get's the most up to date barcode of this product
         * @return the uid of this product
         */
        protected String getBarcode(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getOwner().getEntry(BARCODE);
            return entry.getValue();
        }
        /**
         * get's the most up to date name of this product
         * @return the uid of this product
         */
        protected String getName(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getOwner().getEntry(NAME);
            return entry.getValue();
        }
        /**
         * get's the most up to date brand of this product
         * @return the uid of this product
         */
        protected String getBrand(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getOwner().getEntry(BRAND);
             return entry.getValue();
        }
        /**
         * get's the most up to date content of this product
         * @return the uid of this product
         */
        protected String getContent(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getOwner().getEntry(CONTENT);
            return entry.getValue();
        }
        /**
         * get's the most up to date url of this product
         * @return the uid of this product
         */
        protected String getUrl(){
            DatabaseSingleEntry<Product,String> entry = (DatabaseSingleEntry<Product, String>) getOwner().getEntry(URL);
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
