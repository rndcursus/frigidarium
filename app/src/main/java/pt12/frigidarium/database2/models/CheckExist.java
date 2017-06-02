package pt12.frigidarium.database2.models;

import com.google.firebase.database.DatabaseError;

/**
 * Created by mattijn on 24/05/17.
 * checks if an item exists in the firebasedatabase
 */

public interface CheckExist<T> {
    /**
     * this function is called if item exists in the firebase  database
     * @param item the item that exists
     */
    public  void onExist(T item);

    /**
     * this is called if the item does not exist in the database.
     * use this function to create a new entry in the database.
     * @param uid
     */
    public void onDoesNotExist(String uid);

    /**
     * an error
     * @param error
     */
    public void onError(DatabaseError error);
}
