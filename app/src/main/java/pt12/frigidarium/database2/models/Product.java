package pt12.frigidarium.database2.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;

import pt12.frigidarium.Database.firebase.DatabaseEntry;

/**
 * Created by mattijn on 24/05/17.
 */

public class Product {
    public static final String TABLENAME = "products";
    public static final String BRAND = "brand";
    public static final String BARCODE = "barcode";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String CONTENT = "content";

    public String name;
    public String brand;
    public String barcode;
    public String url;
    public String content;
    public String uid;


    public static DatabaseReference getRef(String uid){
        return FirebaseDatabase.getInstance().getReference(TABLENAME+"/"+uid);
    }

    public Product(@NonNull String uid, String name, String brand, String barcode, String url, String content){
        this.uid = uid;
        this.name =  name;
        this.barcode =  barcode;
        this.url = url;
        this.content =  content;
        this.brand = brand;
    }

    public static void createProduct(Product p){
        getRef(p.uid).setValue(p);
    }
    
    public String getName(){
        return name;
    }

    public String getBrand(){
        return brand;
    }

    public String getBarcode(){
        return barcode;
    }

    public String  getUrl(){
        return url;
    }

    public String getContent(){
        return content;
    }

    public String getUid(){
        return uid;
    }

    public static void writeName(String uid, String name){
        getRef(uid).child(NAME).setValue(name);
    }
    public static void writeBrand(String uid, String brand){
        getRef(uid).child(BRAND).setValue(brand);
    }
    public static void writeContent(String uid, String content){
        getRef(uid).child(CONTENT).setValue(content);
    }
    public static void writeBarcode(String uid, String barcode){
        getRef(uid).child(BARCODE).setValue(barcode);
    }
    public static void writeUrl(String uid, String url){
        getRef(uid).child(URL).setValue(url);
    }

    public static void checkExist(final String uid, final CheckExist checkExist){
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

    public interface CheckExist{
        public  void onExist(Product product);
        public void onDoesNotExist(String uid);
        public void onError(DatabaseError error);
    }
}
