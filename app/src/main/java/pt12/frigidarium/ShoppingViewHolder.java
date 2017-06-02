package pt12.frigidarium;


import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;


import java.util.Map;

import pt12.frigidarium.database2.models.Product;
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

        public ShoppingTitleViewHolder(View view) {
            super(view);
            this.view = view;

            textView = (TextView) view.findViewById(R.id.product_name);
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
                    getTextView().setText(p.name);
                    ((TextView) view.findViewById(R.id.product_brand)).setText(p.brand);

                    TextView product_disc = (TextView) view.findViewById(R.id.product_description);
                    product_disc.setText(R.string.opVooraad);
                    product_disc.setText(product_disc.getText() + Integer.toString(products.second.size()));
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