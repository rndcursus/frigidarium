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
 * @param <V> the type of the value.
 */
public class DatabaseEntry<O extends DatabaseEntryOwner,V> {
    private final String name;
    private final DatabaseReference ref;
    private V value;
    private O owner;
    private final Set<OnChangeListener<O,V>> listeners;

    protected DatabaseEntry(final String name, DatabaseReference ref) {
        this.name = name;
        this.ref = ref;
        listeners = new HashSet<>();
    }
    protected void setOwner (final O owner){
        this.owner = owner;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(name)){
                    value = (V) dataSnapshot.getValue();
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (OnChangeListener<O,V> listener :listeners) {
                                listener.OnChange(owner,name,value);
                            }
                        }
                    };
                    if(owner.isFinished(name)) {
                        fListener.onFinished(owner);
                    }else {
                        owner.addOnFinishedListener(fListener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setValue(V value){
        ref.setValue(value);
    }
    public void addListener(OnChangeListener<O,V> listener){
        listeners.add(listener);
    }

    public void removeListener(OnChangeListener<O,V> listener){
        listeners.remove(listener);
    }

    public interface OnChangeListener<O extends DatabaseEntryOwner,V> {
        public void OnChange(O owner, String name, V value);
    }

}
