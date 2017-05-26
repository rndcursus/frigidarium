package pt12.frigidarium;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import org.w3c.dom.Text;

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

        public ProductTitleViewHolder(View view) {
            super(view);
            indicator = (ImageButton) view.findViewById(R.id.indicator);

            ((TextView) view.findViewById(R.id.product_brand)).setText("Brand");
            ((TextView) view.findViewById(R.id.product_description)).setText("Nog 1 op voorraad");

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

        public TextView getTextView(){
            return textView;
        }
    }

    public static class ProductDetailsViewHolder extends ProductViewHolder {
        public ProductDetailsViewHolder(View v) {
            super(v);
        }
    }
}


