package pt12.frigidarium;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;

import java.util.LinkedList;
import java.util.List;

public class ProductsAdapter
        extends RecyclerView.Adapter<ProductViewHolder>
        implements SwipeableItemAdapter<ProductViewHolder> {

    private List<String> data;

    public ProductsAdapter(List<String> data) {
        setHasStableIds(true);
        this.data = data;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Inflate the view for this view holder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_product_layout,
                viewGroup, false);
        // Call the view holder's constructor, and pass the view to it;
        // return that new view holder
        return new ProductViewHolder(thisItemsView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(data.get(position) + " product");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int onGetSwipeReactionType(ProductViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(ProductViewHolder holder, int position, int type) {
        Log.i("hoi", "hoi");
        int bgRes = 0;
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.product_swipe_neutral;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.product_swipe_left;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.product_swipe_right;
                break;
        }


        holder.itemView.setBackgroundResource(bgRes);
/*
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }*/
    }

    @Override
    public SwipeResultAction onSwipeItem(ProductViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeResultActionMoveToSwipedDirection() {
                // Optionally, you can override these three methods
                // - void onPerformAction()
                // - void onSlideAnimationEnd()
                // - void onCleanUp()
            };
        } else {
            return new SwipeResultActionDoNothing();
        }
    }
}
