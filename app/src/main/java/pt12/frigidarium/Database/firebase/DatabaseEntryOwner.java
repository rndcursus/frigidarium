package pt12.frigidarium.Database.firebase;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt12.frigidarium.Database.models.Stock;

/**
 * this is similar to a row in SQL.
 */

public class DatabaseEntryOwner<O extends DatabaseEntryOwner<O>> {
    private Map<String, DatabaseEntry> entries;
    private Map<String, Boolean> entriesDone;
    private Set<OnFinishedListener<O>> finishedListeners;
    private boolean isFinished = true;
    private Set<DataAccessor<O>> dataAccessors;

    /**
     * @param name the name of the  value that was downloaded
     * @return true if this was the last element to be added. false if at least 1 value still has not been downloaded.
     */
    protected boolean isFinished(String name){
        if (entriesDone.containsKey(name)){
            entriesDone.put(name, true);
            return isFinished();
        }
        return isFinished();
    }

    protected DatabaseEntryOwner(String name, DatabaseReference ref, Map<String, DatabaseEntry> entries){
        //super(name, ref);
        this.entries = entries;
        entriesDone = new HashMap<>();
        finishedListeners  = new HashSet();
        for (Map.Entry<String,DatabaseEntry> entry: entries.entrySet()){
            entriesDone.put(entry.getKey(),false);
            entry.getValue().setOwner(this);
        }
        dataAccessors = new HashSet<>();
    }

    /**
     * check if all values have been downloaded from firebase
     * this prevents null reference errors
     * @return true if all values have been downloaded
     */
    public boolean isFinished(){
        if (isFinished){
            return true;
        }
        for (Map.Entry<String, Boolean> entry : entriesDone.entrySet()){
            if(!entry.getValue()){
                return false;
            }
        }
        isFinished = true;
        for (OnFinishedListener listener : finishedListeners){
            listener.onFinished(this);
        }
        return true;
    }

    protected void addOnFinishedListener(OnFinishedListener<O> listener){
        if (!isFinished()){
            finishedListeners.add(listener);
        }else{
            listener.onFinished((O) this);
        }
    }

    public void addDataAccessor(DataAccessor<O> listener){
        dataAccessors.add(listener);
    }
    public void removeDataAccesor(DataAccessor<O> accessor){
        dataAccessors.remove(accessor);
    }
    public void getOnce(final DataAccessor<O> listener){
        OnFinishedListener<O> onFinishedListener = new OnFinishedListener<O>() {
            @Override
            public void onFinished(O owner) {
                listener.onGetOnce();
            }
        };
        addOnFinishedListener(onFinishedListener);
    }

    protected interface OnFinishedListener<O>{
        public void onFinished(O owner);
    }

    protected DatabaseEntry getEntry(String name){
        return entries.get(name);
    }

    public static abstract class DataAccessor<O extends DatabaseEntryOwner<O>>{
        private O owner;

        public void setOwner(O owner){
            if (owner != null){
                throw new RuntimeException("Owner of a DataAccessor can only be set once.");
            }
            this.owner = owner;
        }

        protected O getOwner(){
            return owner;
        }
        public abstract void onGetOnce();
    }
}
