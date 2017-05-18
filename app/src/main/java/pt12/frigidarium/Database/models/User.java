package pt12.frigidarium.Database.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt12.frigidarium.Database.firebase.DatabaseEntry;
import pt12.frigidarium.Database.firebase.DatabaseEntryOwner;
import pt12.frigidarium.Database.firebase.DatabaseMapEntry;
import pt12.frigidarium.Database.firebase.DatabaseSingleEntry;

/**
 * Created by mattijn on 15/05/17.
 */

public class User extends DatabaseEntryOwner<User> {
    private static final String UID = "uid";
    public static final String STOCKS = "stocks";
    public static final String NAME = "name";
    private static Map<String,User> users= new HashMap<>();
    /**
     * Use this function to create a User. This User will be passed in callback.
     * @param uid the uid of a User
     * @param callback the callback after The User has been created.
     */
    public static void getInstanceByUID(String uid, final DatabaseEntryOwner.onReadyCallback<User> callback){
        User s = getInstanceByUID(uid);
        s.addDataAccessor(new DataAccessor<User>() {
            @Override
            public void onError(User owner, String name, int code, String message, String details) {
                callback.onError(owner,name,code,message,details);
            }

            @Override
            public void onGetInstance(User owner) {
                if (getUid() == null || getUid().equals("")){
                    callback.OnDoesNotExist(owner);
                }else {
                    callback.onExist(owner);
                }
            }
        });
    }
    /**
     * Use this function to create a user.
     * @param uid the uid of a use
     * @return null if the user does not exsist in the database
     */
    public static User getInstanceByUID(String uid){
        if (!users.containsKey(uid)){
            users.put(uid,new User(uid));
        }
        for (DataAccessor<User> l: users.get(uid).getDataAccessors()){
            l.onGetInstance(users.get(uid));
        }
        return users.get(uid);
    }

    /**
     * This function creates a new entry in the firebase database.
     * However if the User already exists it will be overridden.
     * @param uid the firebaseuid of the user.
     * @param name the name of the user
     * @return the newly created entry
     */
    public static User createUser(String uid, String name){
        User u =  User.getInstanceByUID(uid);
        ((DatabaseSingleEntry<Product,String>)  u.getEntry(UID)).setValue(uid);
        ((DatabaseSingleEntry<Product,String>)  u.getEntry(NAME)).setValue(name);
        ((DatabaseMapEntry<Product,String>)  u.getEntry(STOCKS)).init();
        return u;
    }

    private static DatabaseReference createReference(String uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        return myRef;
    }

    private static Map<String, DatabaseEntry> getEntries(String uid){
        DatabaseReference ref = createReference(uid);
        Map<String, DatabaseEntry>  entries = new HashMap<>();
        entries.put(UID, new DatabaseSingleEntry<Product,String>(UID, ref.child(UID), String.class));
        entries.put(NAME, new DatabaseSingleEntry<Product,String>(NAME, ref.child(NAME), String.class));
        entries.put(STOCKS, new DatabaseMapEntry<Product,String>(STOCKS, ref.child(STOCKS), String.class));
        return entries;
    }

    private User(String identifier) {
        super(identifier, createReference(identifier), getEntries(identifier));
        final User u = this;

        super.getEntry(STOCKS).addListener(new DatabaseMapEntry.OnChangeListener<User, String>() {
            @Override
            public void onChildAdded(User owner, String mapName, String element, String key) {
                for (DataAccessor l : getDataAccessors()){
                    if (l instanceof OnUserChangeListener) {
                        OnUserChangeListener listener = (OnUserChangeListener) l;
                        listener.onAddedToStock(owner, mapName, key, element);
                    }

                }
            }

            @Override
            public void onChildChanged(User owner, String mapName, String element, String key, String oldElement) {
                for (DataAccessor l : getDataAccessors()) {
                    if (l instanceof OnUserChangeListener) {
                        OnUserChangeListener listener = (OnUserChangeListener) l;
                        listener.onRemovedFromStock(owner, mapName, key, oldElement);
                    }
                }
                for (DataAccessor l : getDataAccessors()) {
                    if (l instanceof OnUserChangeListener) {
                        OnUserChangeListener listener = (OnUserChangeListener) l;
                        listener.onAddedToStock(owner, mapName, key, element);
                    }
                }

            }

            @Override
            public void onChildRemoved(User owner, String mapName, String element, String key) {
                for (DataAccessor l : getDataAccessors()) {
                    if (l instanceof OnUserChangeListener) {
                        OnUserChangeListener listener = (OnUserChangeListener) l;
                    listener.onRemovedFromStock(owner, mapName, key, element);
                }
                }
            }

            @Override
            public void onError(User owner, String name, int code, String message, String details) {
                for (DataAccessor<User> listener : getDataAccessors()){
                    listener.onError(owner,name,code,message,details);
                }
            }
        });
    }
    public String getUID(){
        DatabaseSingleEntry<User, String > entry = (DatabaseSingleEntry<User, String>) this.getEntry(UID);
        return entry.getValue();
    }

    /**
     * Deze class moet gebruikt worden om data over een User te lezen en of te schrijven.
     * Dit moet gedaan worden vanuit een subclass van OnUserChangeListener.
     */
    public static abstract class OnUserChangeListener extends DataAccessor<User>{

        protected String getName(){
            DatabaseSingleEntry<User, String > entry = (DatabaseSingleEntry<User, String>) this.getOwner().getEntry(NAME);
            return entry.getValue();
        }

        protected String getUID(){
            DatabaseSingleEntry<User, String > entry = (DatabaseSingleEntry<User, String>) this.getOwner().getEntry(UID);
            return entry.getValue();
        }

        protected Map<String, String> getStocks(){
            DatabaseMapEntry<User,String> entry  = (DatabaseMapEntry<User, String>) this.getOwner().getEntry(STOCKS);
            return entry.getMap();
        }

        protected void removeStockByKey(String key){
            DatabaseMapEntry<User,String> entry  = (DatabaseMapEntry<User, String>) this.getOwner().getEntry(STOCKS);
            entry.remove(key);
        }

        protected void removeStockByVal(String val){
            DatabaseMapEntry<User,String> entry  = (DatabaseMapEntry<User, String>) this.getOwner().getEntry(STOCKS);
            String key = entry.getKey(val);
            if (key != null) {
                entry.remove(key);
            }
        }

        protected void addStock(String stockUid){
            DatabaseMapEntry<User,String> entry  = (DatabaseMapEntry<User, String>) this.getOwner().getEntry(STOCKS);
            entry.add(stockUid);
        }

        protected String getStockByKey(String  key){
            DatabaseMapEntry<User,String> entry  = (DatabaseMapEntry<User, String>) this.getOwner().getEntry(STOCKS);
            return entry.getValue(key);
        }

        public abstract void onAddedToStock(User owner, String mapName, String key, String element);

        public abstract void onRemovedFromStock(User owner, String mapName, String key, String element);
    }
}
