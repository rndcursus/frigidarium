package pt12.frigidarium.Database.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mattijn on 11/05/17.
 */

public class DatabaseMapEntry<O extends DatabaseEntryOwner, V> extends DatabaseEntry<O> {
    private HashMap<String, V> map;
    private Class<? extends V> valueType;

    public DatabaseMapEntry(String name, DatabaseReference ref, Class<? extends V> valueType) {
        super(name,ref);
        this.valueType = valueType;
        this.map = new HashMap<>();
    }

    @Override
    protected void setOwner(final O owner) {
        super.setOwner(owner);
        if (owner == null) {
            return;
        }
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(name)) {
                    map.put(dataSnapshot.getKey(), dataSnapshot.getValue(valueType));
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener<O>() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O> listener : listeners) {
                                if (listener instanceof DatabaseMapEntry.OnChangeListener){
                                    DatabaseMapEntry.OnChangeListener<O,V> l = (DatabaseMapEntry.OnChangeListener<O,V>)  listener;
                                    l.onChildAdded(owner,name,map.get(dataSnapshot.getKey()),dataSnapshot.getKey());
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
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(name)) {
                    final V oldData = map.get(dataSnapshot.getKey());
                    map.put(dataSnapshot.getKey(), dataSnapshot.getValue(valueType));
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener<O>() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O> listener : listeners) {
                                if (listener instanceof DatabaseMapEntry.OnChangeListener){
                                    DatabaseMapEntry.OnChangeListener<O,V> l = (DatabaseMapEntry.OnChangeListener<O,V>)  listener;
                                    l.onChildChanged(owner,name,map.get(dataSnapshot.getKey()),dataSnapshot.getKey(), oldData);
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
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(name)) {
                    map.remove(dataSnapshot.getKey());
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener<O>() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O> listener : listeners) {
                                if (listener instanceof DatabaseMapEntry.OnChangeListener){
                                    DatabaseMapEntry.OnChangeListener<O,V> l = (DatabaseMapEntry.OnChangeListener<O,V>)  listener;
                                    l.onChildRemoved(owner,name,map.get(dataSnapshot.getKey()),dataSnapshot.getKey());
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
            public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
                //hier doen we niks mee dit heeft te maken met priority stuffs.
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

    public HashMap<String, V> getMap() {
        return map;
    }
    public void setItem(String key, V val){
        ref.child(key).setValue(val);
    }
    public void add(V value){
        Task<Void> x = ref.push().setValue(value);
    }
    public V getItem(String key){
        if (map.containsKey(key)){
            return map.get(key);
        }else{
            return null;
        }
    }
    public void remove(String key){
        ref.child(key).removeValue();
    }
    public void change(String key, V val){
        ref.child(key).setValue(val);
    }
    public String getKey(V value){
        for (Map.Entry<String,V> entry  : map.entrySet()){
            if (entry.getValue().equals(value)){
                return entry.getKey();
            }
        }
        return null;
    }

    public V getValue(String key) {
        return map.get(key);
    }

    public int size() {
        return map.size();
    }

    public interface OnChangeListener<O extends DatabaseEntryOwner,V> extends DatabaseEntry.OnChangeListener<O> {
        public void onChildAdded(O owner, String mapName, V element, String key);
        public void onChildChanged(O owner, String mapName, V element, String key, V oldElement);
        public void onChildRemoved(O owner, String mapName, V element, String key);
    }
}