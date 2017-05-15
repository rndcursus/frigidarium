package pt12.frigidarium.Database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * this is similar to a row in SQL.
 */

public class DatabaseEntryOwner<O extends DatabaseEntryOwner> {
    private Map<String, DatabaseEntry> entries;
    private Map<String, Boolean> entriesDone;
    private Set<OnFinishedListener<O>> finishedListeners;
    private boolean isFinished = true;

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

    protected DatabaseEntryOwner(DatabaseReference ref, Map<String, DatabaseEntry> entries){
        this.entries = entries;
        entriesDone = new HashMap<>();
        finishedListeners  = new HashSet();
        for (Map.Entry<String,DatabaseEntry> entry: entries.entrySet()){
            entriesDone.put(entry.getKey(),false);
            entry.getValue().setOwner(this);
        }
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

    protected interface OnFinishedListener<O>{
        public void onFinished(O owner);
    }

    protected DatabaseEntry getEntry(String name){
        return entries.get(name);
    }
}
