package pt12.frigidarium.database2.models;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mattijn on 24/05/17.
 */

public class User {
    public static final String TABLENAME = "users";
    public static final String UID = "uid";
    public static final String STOCKS = "stocks";
    public static final String NAME = "name";
    public String uid;
    public String name;
    public Map<String,String> stocks;
    public String getUid(){
        return uid;
    }
    public String getName(){
        return name;
    }

    public Map<String, String> getStocks(){
        if (stocks == null){
            stocks = new HashMap<>();
        }
        return stocks;
    }
    public User(){};

    public User(String uid, String name){
        this.uid = uid;
        this.name = name;

    }

    /**
     * creates a new stock in the database
     * @param user the user to be added to the database.
     */
    public static void createUser(User user){
        getRef(user.getUid()).setValue(user);
    }

    public static DatabaseReference getRef(String uid){
        return FirebaseDatabase.getInstance().getReference(TABLENAME+"/"+uid);
    }

    public static void checkExist(final String uid, final CheckExist<User> checkExist){
        getRef(uid).addValueEventListener(new ValueEventListener() {
            boolean called = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!called) {
                    if (dataSnapshot.getValue(User.class) == null) {
                        checkExist.onDoesNotExist(uid);
                    } else {
                        checkExist.onExist(dataSnapshot.getValue(User.class));
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


    /**\
     * this is called so that the user also knows to whichs stocks it belongs.
     * Dont forget to call Stock.addUserToStock(String stockUid, String userUid);
     * @param userUid the user that should know it has been added to a stock.
     * @param stockUid the stock a user has been added to
     */
    public static void addUserToStock(String userUid, String stockUid){
        getRef(userUid).child(STOCKS).push().setValue(stockUid);
    }

    /**
     * Dont forget to call Stock.removeUserToStock(String stockUid, String userUidKey);
     * @param userUid
     * @param stockUid
     */
    public static void removeUserFromStock(final String userUid, final String stockUid){
        getRef(userUid).child(STOCKS).addChildEventListener(new ChildEventListener() {
            boolean called = false;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!called){
                    if (stockUid.equals(dataSnapshot.getValue(String.class))){
                        getRef(userUid).child(STOCKS).child(dataSnapshot.getKey()).removeValue();
                        called = true;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
