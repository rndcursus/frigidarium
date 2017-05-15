package pt12.frigidarium.Database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mattijn on 15/05/17.
 */

public class DatabaseSingleEntry<O extends DatabaseEntryOwner,V> extends DatabaseEntry<O>{
    protected V value;
    protected final Class<? extends V> valueType;

    public DatabaseSingleEntry(String  name, DatabaseReference ref,  Class<? extends V> valueType) {
        super(name,ref);
        this.valueType = valueType;
    }

    protected void setValue(V value){
        ref.setValue(value);
    }
    protected void setOwner (final O owner) {
       super.setOwner(owner);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getKey().equals(name)) {
                        value = dataSnapshot.getValue(valueType);
                        DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                            @Override
                            public void onFinished(DatabaseEntryOwner unused) {
                                for (DatabaseEntry.OnChangeListener<O> listener : listeners) {
                                    if (listener instanceof DatabaseSingleEntry) {
                                        DatabaseSingleEntry.OnChangeListener<O,V> l = (DatabaseSingleEntry.OnChangeListener) listener;
                                        l.onChange(owner, name, value);
                                    }
                                }
                            }
                        };
                        if (owner.isFinished(name)) {
                            fListener.onFinished(owner);
                        } else {
                            owner.addOnFinishedListener(fListener);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    for (DatabaseEntry.OnChangeListener<O> listener : listeners) {
                        listener.onError(owner, name, databaseError.getCode(),
                                databaseError.getMessage(),
                                databaseError.getDetails());
                    }
                }
            });

    }
    protected V getValue() {
        return value;
    }

    protected interface OnChangeListener<O extends DatabaseEntryOwner,V> extends DatabaseEntry.OnChangeListener<O>{
        public void onChange(O owner, String name, V value);
    }
}
