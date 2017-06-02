package pt12.frigidarium.database2.models;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by mattijn on 24/05/17.
 * This Class represents a stock Entry in a The Database.
 * A Stock entry represents a product in the stockList and the shoppingcard.
 * but holds some information unique to a single product.
 * like the best-before/use-before date.
 */

public class StockEntry {


    public String product_uid;//public because firebase needs it to be public
    public Long timeAdded;//public because firebase needs it to be public
    public Long best_before;//public because firebase needs it to be public
    public String addedByUser;//public because firebase needs it to be public

    /**
     * Don't use this constructor to create your own instance of StockEntry.
     * This class is used by firebase.
     */
    public StockEntry() {}


    /**
     * use this constructor to create a StockEntry Instance that can be added to the database.
     * @param uid the ProductUid of a product in the products database
     * @param best_before the best before date of the item. in UTS.
     */
    public StockEntry(String uid, Long best_before){
        this.product_uid  = uid;
        this.best_before = best_before;
        this.timeAdded = System.currentTimeMillis()/1000L;
        this.addedByUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * get the productUid of a StockEntry
     * @return The productId of the product this stockentry belongs to.
     */
    public String getProductUid() {
        return product_uid;
    }

    /**
     * Get the time this StockEntry  was created.
     * @return the UTS of the time this Entry was created.
     */
    public Long getTimeAdded(){
        return timeAdded;
    }

    /**
     * get the best before time  of  the StockEntry
     * @return the best before time of Product  as a UTS.
     */
    public Long bestBest_before(){
        return best_before;
    }

    /**
     * get the uid of the user that created this stockEntry.
     * @return the Uid of the user that added this stockEntry.
     */
    public String getAddedByUser(){
        return addedByUser;
    }
}