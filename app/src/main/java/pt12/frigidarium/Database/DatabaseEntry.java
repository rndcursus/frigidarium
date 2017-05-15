package pt12.frigidarium.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;


/**
 * This class automaticly updates the value from the data base.
 * @param <O> the type of the owner of the value.
 */
public class DatabaseEntry<O extends DatabaseEntryOwner> {
    protected final String name;
    protected final DatabaseReference ref;

    protected O owner;
    protected final Set<OnChangeListener<O>> listeners;

    protected DatabaseEntry(final String name, DatabaseReference ref) {
        this.name = name;
        this.ref = ref;
        listeners = new HashSet<>();
    }

    protected String getName() {
        return name;
    }
    protected void setOwner(O owner){
        if (owner == null){
            return;
        }
        if (this.owner == null) {
            this.owner = owner;
        }  else {
            throw new RuntimeException("This entry already has a owner");
        }
    }
    protected void addListener(OnChangeListener<O> listener){
        listeners.add(listener);
    }

    protected void removeListener(OnChangeListener<O> listener){
        listeners.remove(listener);
    }

    protected interface OnChangeListener<O extends DatabaseEntryOwner> {
        public void onError(O owner, String name, int code, String message, String details);
    }

}
