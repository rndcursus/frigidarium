package pt12.frigidarium;


import android.content.res.Resources;
import android.media.Image;

import android.support.v4.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;


import java.util.LinkedList;
import java.util.Map;

import pt12.frigidarium.database2.models.Product;
import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.StockEntry;

public class ShoppingViewHolder extends AbstractSwipeableItemViewHolder
        implements ExpandableItemViewHolder {

    private TextView textView;
    FrameLayout containerView;

    private int expandStateFlags;

    public ShoppingViewHolder(View view){
        super(view);
        containerView = (FrameLayout) view.findViewById(R.id.container);
    }

    @Override
    public View getSwipeableContainerView() {
        return containerView;
    }

    public FrameLayout getContainer(){
        return containerView;
    }

    @Override
    public void setExpandStateFlags(int flags) {
        expandStateFlags = flags;
    }

    @Override
    public int getExpandStateFlags() {
        return expandStateFlags;
    }



    public static class ShoppingTitleViewHolder extends ShoppingViewHolder {
        private TextView textView;
        private ValueEventListener mValueEventListener;
        private View view;
        private Product product;
        private int position;

        private ImageButton plus;
        private ImageButton minus;
        private final ShoppingAdapter adapter;

        public ShoppingTitleViewHolder(View view, final ShoppingAdapter mAdapter) {
            super(view);
            this.view = view;
            this.adapter = mAdapter;

            textView = (TextView) view.findViewById(R.id.product_name);
            plus = (ImageButton) view.findViewById(R.id.plus);
            minus = (ImageButton) view.findViewById(R.id.minus);

            plus.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    // Some code
                    if(product != null){
                        StockEntry entry = new StockEntry(product.getUid(), null);
                        Stock.addStockEntryToOutStock(LoginActivity.getCurrentStock(), entry);
                    }

                }
            });
            minus.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    // Some code
                    if(product != null){
                        LinkedList<String> keys = new LinkedList<String>(adapter.getData().get(position).second.keySet());
                        if(keys.size() > 0){
                            String key = keys.get(0);
                            Stock.removeFromOutStock(LoginActivity.getCurrentStock(), adapter.getData().get(position).first.first, key);
                        }
                    }

                }
            });
        }

        /**
         * Set the position in the recyclerlist of this viewHolder
         * @param posistion position in list
         */
        public void setPosistion(int posistion){
            this.position = posistion;
        }

        // Get product details from the database
        public void setproduct(final Pair<Pair<String, Long>, Map<String, StockEntry>> products){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Product.TABLENAME + "/" +products.first.first);
            if (mValueEventListener != null){
                ref.removeEventListener(mValueEventListener);
            }

            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Product p =  dataSnapshot.getValue(Product.class);
                    product = p;
                    getTextView().setText(p.name);
                    ((TextView) view.findViewById(R.id.product_brand)).setText(p.brand);

                    TextView product_disc = (TextView) view.findViewById(R.id.product_description);
                    product_disc.setText(R.string.inShoppingCart);
                    product_disc.setText(product_disc.getText() + Integer.toString(products.second.size()));

                    TextView product_barcode = (TextView) view.findViewById(R.id.product_barcode);
                    product_barcode.setText(R.string.barcode);
                    product_barcode.setText(product_barcode.getText() + p.barcode);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addValueEventListener(mValueEventListener);
        }

        public TextView getTextView(){
            return textView;
        }
    }

    public static class ShoppingDetailsViewHolder extends ShoppingViewHolder {
        // This class is currently not used, but is available for future development.
        public ShoppingDetailsViewHolder(View v) {
            super(v);
        }
    }
}