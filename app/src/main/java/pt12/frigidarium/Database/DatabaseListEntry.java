package pt12.frigidarium.Database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mattijn on 11/05/17.
 */

public class DatabaseListEntry<O extends DatabaseEntryOwner, V> extends DatabaseEntry<O,V> {

    protected DatabaseListEntry(String name, DatabaseReference ref, Class<? extends V> valueType) {
        super(name, ref, valueType);
    }

    @Override
    protected void setOwner(final O owner) {
        super.setOwner(owner);
        if (owner == null) {
            return;
        }
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String elemName = s;
                if (dataSnapshot.getKey().equals(name)) {
                    value = dataSnapshot.getValue(valueType);
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O, V> listener : listeners) {
                                if (listener instanceof DatabaseListEntry.OnChangeListener){
                                    ((DatabaseListEntry.OnChangeListener<O,V>)  listener).OnListChange(owner,value,name, elemName,ListChanges.Added);
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final String elemName = s;
                if (dataSnapshot.getKey().equals(name)) {
                    value = dataSnapshot.getValue(valueType);
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O, V> listener : listeners) {
                                if (listener instanceof DatabaseListEntry.OnChangeListener){
                                    ((DatabaseListEntry.OnChangeListener<O,V>)  listener).OnListChange(owner,value,name, elemName,ListChanges.Changed);
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
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String elemName = "";
                if (dataSnapshot.getKey().equals(name)) {
                    value = dataSnapshot.getValue(valueType);
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O, V> listener : listeners) {
                                if (listener instanceof DatabaseListEntry.OnChangeListener){
                                    ((DatabaseListEntry.OnChangeListener<O,V>)  listener).OnListChange(owner,value,name, elemName,ListChanges.Removed);
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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                final String elemName = s;
                if (dataSnapshot.getKey().equals(name)) {
                    value = dataSnapshot.getValue(valueType);
                    DatabaseEntryOwner.OnFinishedListener fListener = new DatabaseEntryOwner.OnFinishedListener() {
                        @Override
                        public void onFinished(DatabaseEntryOwner unused) {
                            for (DatabaseEntry.OnChangeListener<O, V> listener : listeners) {
                                if (listener instanceof DatabaseListEntry.OnChangeListener){
                                    ((DatabaseListEntry.OnChangeListener<O,V>)  listener).OnListChange(owner,value,name, elemName,ListChanges.Moved);
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

            }
        });
    }

    public enum ListChanges {
        Added,
        Changed,
        Moved,
        Removed
    }

    public interface OnChangeListener<O extends DatabaseEntryOwner,V> extends DatabaseEntry.OnChangeListener<O,V> {
        public void OnListChange(O owner, V value, String listName, String elemName, ListChanges changType);
    }
}