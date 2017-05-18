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
    private final Set<OnUserChangeListener> userListeners;
    private static Map<String,User> users= new HashMap<>();

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
        userListeners = new HashSet<>();
        final User u = this;

        super.getEntry(STOCKS).addListener(new DatabaseMapEntry.OnChangeListener<User, String>() {
            @Override
            public void onChildAdded(User owner, String mapName, String element, String key) {
                for (OnUserChangeListener listener : userListeners){
                    listener.onAddedToStock(owner,mapName, key,element);
                }
            }

            @Override
            public void onChildChanged(User owner, String mapName, String element, String key, String oldElement) {
                for (OnUserChangeListener listener : userListeners){
                    listener.onRemovedFromStock(owner,mapName, key, oldElement);
                }
                for (OnUserChangeListener listener : userListeners){
                    listener.onAddedToStock(owner,mapName, key, element);
                }

            }

            @Override
            public void onChildRemoved(User owner, String mapName, String element, String key) {
                for (OnUserChangeListener listener : userListeners){
                    listener.onRemovedFromStock(owner,mapName, key,element);
                }
            }

            @Override
            public void onError(User owner, String name, int code, String message, String details) {
                for (OnUserChangeListener listener : userListeners){
                    listener.onError(owner,name,code,message,details);
                }
            }
        });
    }
    public String getUID(){
        DatabaseSingleEntry<User, String > entry = (DatabaseSingleEntry<User, String>) this.getEntry(UID);
        return entry.getValue();
    }

    public void addListener(OnUserChangeListener listener){
        listener.setOwner(this);
        userListeners.add(listener);
    }

    public void removeListener(OnUserChangeListener  listener){
        userListeners.remove(listener);
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
