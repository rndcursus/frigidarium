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

public class ProductViewHolder extends AbstractSwipeableItemViewHolder
    implements ExpandableItemViewHolder {

    private TextView textView;
    FrameLayout containerView;

    private int expandStateFlags;

    public ProductViewHolder(View view){
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







    public static class ProductTitleViewHolder extends ProductViewHolder {
        private ImageButton indicator;
        private boolean isExpanded;

        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;
        private static final float PIVOT_VALUE = 0.5f;
        private static final long DEFAULT_ROTATE_DURATION_MS = 200;

        private TextView textView;

        private ValueEventListener mValueEventListener;

        private View view;

        public ProductTitleViewHolder(View view) {
            super(view);
            this.view = view;
            indicator = (ImageButton) view.findViewById(R.id.indicator);

            textView = (TextView) view.findViewById(R.id.product_name);

        }

        @Override
        public void setExpandStateFlags(int flags) {
            super.setExpandStateFlags(flags);
        }

        public void setExpandState(boolean isExpanded){
            if(this.isExpanded != isExpanded){
                onExpansionToggled(isExpanded);
            }
            this.isExpanded = isExpanded;
        }

        public void onExpansionToggled(boolean expanded) {
            //super.onExpansionToggled(expanded);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                RotateAnimation rotateAnimation;
                if (expanded) { // rotate clockwise
                    rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                            INITIAL_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                } else { // rotate counterclockwise
                    rotateAnimation = new RotateAnimation(-1 * INITIAL_POSITION,
                            ROTATED_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                }
                rotateAnimation.setDuration(200);
                rotateAnimation.setFillAfter(true);
                indicator.startAnimation(rotateAnimation);
            }*/
        }

        public void setproduct(final Pair<Pair<String, Long>, Map<String, StockEntry>> products){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products/"+products.first.first);
            if (mValueEventListener != null){
                ref.removeEventListener(mValueEventListener);
            }

            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Product p =  dataSnapshot.getValue(Product.class);
                    getTextView().setText(p.name);
                    ((TextView) view.findViewById(R.id.product_brand)).setText(p.brand);
                    ((TextView) view.findViewById(R.id.product_description)).setText(Resources.getSystem().getString(R.string.opVooraad, products.second.size()));
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

    public static class ProductDetailsViewHolder extends ProductViewHolder {
        private View view;
        private Map.Entry<String, StockEntry> details;
        public ProductDetailsViewHolder(View v) {
            super(v);
            this.view = v;
        }

        public void setDetails(final Map.Entry<String, StockEntry> details){
            this.details = details;
        }

        public void setBestBeforeText(){
            ((TextView) view.findViewById(R.id.product_best_before)).setText(R.string.expirationDate + details.getValue().best_before.toString());
        }

        public String getDatabaseKey(){
            return details.getKey();
        }
    }
}
