package pt12.frigidarium.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mattijn on 12/05/17.
 */

public class User extends DatabaseEntryOwner {
    public static final String UID  = "uid";
    public static final String NAME  = "name";
    public static final String USERNAME  = "username";
    public static final String HOUSES  = "houses";

    private static DatabaseReference createReference(String uid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        return myRef;
    }

    private static Map<String, DatabaseEntry> getEntries(String uid){
        DatabaseReference ref = createReference(uid);
        Map<String, DatabaseEntry>  entries = new HashMap<>();
        entries.put(UID, new DatabaseEntry<Product,String>(UID, ref.child(UID), String.class));
        entries.put(NAME, new DatabaseEntry<Product,String>(NAME, ref.child(NAME), String.class));
        entries.put(USERNAME, new DatabaseEntry<Product,String>(USERNAME, ref.child(USERNAME), String.class));
        entries.put(HOUSES, new DatabaseEntry<Product,List>(HOUSES, ref.child(HOUSES), List.class));
        return entries;
    }

    protected User(String uid) {
        super(createReference(uid), getEntries(uid));
    }
    public abstract class OnProductChangeListener{
        private User user;

        private void setUser(User user){
            this.user = user;
        }
        public abstract void onChange(String name);
    }
}
