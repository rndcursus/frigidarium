package pt12.frigidarium.database2.models;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by mattijn on 24/05/17.
 * A representation of a Product in the firebase database.
 */

public class Product {
    public static final String TABLENAME = "products";
    public static final String BRAND = "brand";
    public static final String BARCODE = "barcode";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String CONTENT = "content";

    public String added_by;//public because firebase needs it to be public
    public String name;//public because firebase needs it to be public
    public String brand;//public because firebase needs it to be public
    public String barcode;//public because firebase needs it to be public
    public String url;//public because firebase needs it to be public
    public String content;//public because firebase needs it to be public
    public String uid;//public because firebase needs it to be public


    public static DatabaseReference getRef(String uid){
        return FirebaseDatabase.getInstance().getReference(TABLENAME+"/"+uid);
    }

    /**
     * Do not use this constructor to create your own instance of a Product.
     */
    public Product (){}

    /**
     * create a new product in the database
     * @param uid the uid of the new stock. this should not exsist already in the database
     * @param name the name of the product
     * @param brand the brand of the product
     * @param barcode the barcode of the product
     * @param url the url of the product
     * @param content the content of the product
     */
    public Product(@NonNull String uid, String name, String brand, String barcode, String url, String content){
        this.uid = uid;
        this.name =  name;
        this.barcode =  barcode;
        this.url = url;
        this.content =  content;
        this.brand = brand;
        this.added_by = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * create a new product in the firebase database.
     * Warning this overrides any product with the same uid
     * @param p the product to be added to the firebase database
     */
    public static void createProduct(Product p){
        getRef(p.uid).setValue(p);
    }

    /**
     * returns the name of the the product
     * @return the name of the product
     */
    public String getName(){
        return name;
    }

    /**
     * get the  brand of the product
     * @return the brand of the product
     */
    public String getBrand(){
        return brand;
    }

    /**
     * Get the barcode of the product.
     * @return The barcode of the product
     */
    public String getBarcode(){
        return barcode;
    }

    /**
     * Get the Url of the product
     * @return The url of the product "" if there is none.
     */
    public String  getUrl(){
        return url;
    }

    /**
     * Get the content of the Product
     * @return the Contents of the product.
     */
    public String getContent(){
        return content;
    }

    /**
     * Get the Uid of the product.
     * @return the uid of the product
     */
    public String getUid(){
        return uid;
    }

    /**
     * check if a product exists.
     * @param uid the uid product
     * @param checkExist called if it is known that the product exists.
     */
    public static void checkExist(final String uid, final CheckExist<Product> checkExist){
        getRef(uid).addValueEventListener(new ValueEventListener() {
            boolean called = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!called) {
                    if (dataSnapshot.getValue(Product.class) == null) {
                        checkExist.onDoesNotExist(uid);
                    } else {
                        checkExist.onExist(dataSnapshot.getValue(Product.class));
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

    /**
     * get Id unique for all products that are the same. based on te barcode.
     * @param barcode the barcode to base the Id on.
     * @return a unique Id
     * currently it uses the barcode as a id. But this wont work for example for vegables other weigthed products
     */
    public static String createProductUID(String barcode) {
        return barcode;
    }
}