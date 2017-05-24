package pt12.frigidarium.Database.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Owner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt12.frigidarium.Database.models.Stock;

/**
 * Created by mattijn on 15/05/17.
 */

public class DatabaseGroupedEntry<O extends DatabaseEntryOwner, V> extends DatabaseEntry<O>{
    private Map<String,DatabaseMapEntry<O,V>> products   = new HashMap<>();
    private Class<? extends V> valueType;

    public DatabaseGroupedEntry(final String name, final DatabaseReference ref, final Class<? extends V> valueType) {
        super(name, ref);
        this. valueType = valueType;


    }

    protected void setOwner(final O owner){
        super.setOwner(owner);
        if (owner  == null){
            return;
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                owner.isFinished(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final DatabaseGroupedEntry<O,V>  t = this;
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                products.put(dataSnapshot.getKey(),
                        new DatabaseMapEntry<O, V>(dataSnapshot.getKey(),
                                ref.child(dataSnapshot.getKey()),
                                valueType));
                products.get(dataSnapshot.getKey()).setOwner(owner);

                for (DatabaseEntry.OnChangeListener<O> l : listeners){
                    if (l instanceof DatabaseGroupedEntry.OnChangeListener){
                        OnChangeListener<O, V> listener = (OnChangeListener<O, V>) l;
                        listener.onGroupAdded(owner,t, t.getGroup(dataSnapshot.getKey()));
                    }
                }
                products.get(dataSnapshot.getKey()).addListener(new DatabaseMapEntry.OnChangeListener<O,V>() {

                    @Override
                    public void onChildAdded(O owner, String mapName, V element, String key) {
                        for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                            if (l instanceof DatabaseGroupedEntry.OnChangeListener) {
                                OnChangeListener<O, V> listener = (OnChangeListener<O, V>) l;
                                listener.onEntryAdded(owner, t, t.getGroup(dataSnapshot.getKey()), element, dataSnapshot.getKey(),key);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(O owner, String mapName, V element, String key, V oldElement) {
                        for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                            if (l instanceof DatabaseGroupedEntry.OnChangeListener) {
                                OnChangeListener<O, V> listener = (OnChangeListener<O, V>) l;
                                listener.onEntryChanged(owner, t, t.getGroup(dataSnapshot.getKey()),oldElement, element, dataSnapshot.getKey(),key);
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(O owner, String mapName, V element, String key) {
                        for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                            if (l instanceof DatabaseGroupedEntry.OnChangeListener) {
                                OnChangeListener<O, V> listener = (OnChangeListener<O, V>) l;
                                listener.onEntryRemoved(owner, t, t.getGroup(dataSnapshot.getKey()), element, dataSnapshot.getKey(),key);
                            }
                        }
                    }

                    @Override
                    public void onError(O owner, String name, int code, String message, String details) {
                        for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                            l.onError(owner,name,code,message,details);
                        }
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DatabaseMapEntry<O, V> group = t.getGroup(dataSnapshot.getKey());
                products.remove(dataSnapshot.getKey());
                for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                    if (l instanceof DatabaseGroupedEntry.OnChangeListener) {
                        OnChangeListener<O, V> listener = (OnChangeListener<O, V>) l;
                        listener.onGroupRemoved(owner, t, group);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                for (DatabaseEntry.OnChangeListener<O> l : listeners) {
                    l.onError(owner,name,databaseError.getCode(),databaseError.getMessage(),databaseError.getDetails());
                }
            }
        });

    }
    private DatabaseMapEntry<O, V> getGroup(String key) {
        return products.get(key);
    }

    public int groups(){
        return products.size();
    }

    public int sizeOf(String product_id){
        if (!products.containsKey(product_id)){
            return 0;
        }
        return products.get(product_id).size();
    }

    public void addEntry(V entry, String groupUid){
        if (!products.containsKey(groupUid)){
            products.put(groupUid,new DatabaseMapEntry<O, V>(groupUid,ref.child(groupUid),valueType));
            products.get(groupUid).setOwner(owner);
        }
        products.get(groupUid).add(entry);
    }

    public void removeEntry(V entry, String groupUid){
        if (!products.containsKey(groupUid)){
            return;
        }
        String key = products.get(groupUid).getKey(entry);
        products.get(groupUid).remove(key);
        if (products.get(groupUid).size() == 0){
            products.remove(groupUid);
        }
    }

    public Set<String> getGroups() {
        return products.keySet();
    }

    public interface OnChangeListener<O extends DatabaseEntryOwner,V> extends DatabaseEntry.OnChangeListener<O>{
        public void onGroupAdded(O owner, DatabaseGroupedEntry<O,V> value, DatabaseMapEntry<O, V> group);
        public void onGroupRemoved(O owner, DatabaseGroupedEntry<O,V> value, DatabaseMapEntry<O, V> group);
        public void onEntryAdded(O owner, DatabaseGroupedEntry<O,V> grouped, DatabaseMapEntry<O,V> group, V Entry, String groupUid, String key);
        public void onEntryChanged(O owner, DatabaseGroupedEntry<O, V> t, DatabaseMapEntry<O, V> group, V oldElement, V element, String groupUid, String key);
        public void onEntryRemoved(O owner, DatabaseGroupedEntry<O, V> t, DatabaseMapEntry<O, V> group, V element, String groupUid, String key);
    }


}
