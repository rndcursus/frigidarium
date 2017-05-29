package pt12.frigidarium;

import android.support.annotation.IntRange;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.List;
import java.util.Map;

import pt12.frigidarium.database2.models.StockEntry;

public class ProductsAdapter
        extends AbstractExpandableItemAdapter<ProductViewHolder.ProductTitleViewHolder, ProductViewHolder.ProductDetailsViewHolder>
        implements ExpandableSwipeableItemAdapter<ProductViewHolder.ProductTitleViewHolder, ProductViewHolder.ProductDetailsViewHolder> {

    private List<Pair<String,Map<String, StockEntry>>> data;
    private final RecyclerViewExpandableItemManager expandableItemManager;

    public ProductsAdapter(RecyclerViewExpandableItemManager expandableItemManager, List<Pair<String,Map<String, StockEntry>> > data) {
        setHasStableIds(true);
        this.data = data;
        this.expandableItemManager = expandableItemManager;
    }

    /*@Override
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
        viewHolder.setproduct(data.get(position));

        // set background resource (target view ID: container)
        final int swipeState = viewHolder.getSwipeStateFlags();

        if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId = 0;

            if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.product_swiping_active_state;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                //bgResId = R.drawable.product_swiping_state;
            } else {
                bgResId = R.drawable.product_swiping_normal_state;
            }

            viewHolder.getContainer().setBackgroundResource(bgResId);
        }

        // set swiping properties
        //viewHolder.setSwipeItemHorizontalSlideAmount(item.isPinned() ? SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_LEFT : 0);

    }*/

    /*@Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position){
        return 0;// // TODO: 24/05/17 deze functie moet een goed id teruggeven
    }
        return data.get(position).getId();
    }*/

    /*public int onGetSwipeReactionType(ProductViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }*/

    @Override
    public int onGetGroupItemSwipeReactionType(ProductViewHolder.ProductTitleViewHolder holder, int groupPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY;
    }

    public void onSetSwipeBackground(ProductViewHolder holder, int position, int type) {
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
    }

    @Override
    public void onSetGroupItemSwipeBackground(ProductViewHolder.ProductTitleViewHolder holder, int groupPosition, int type) {
        onSetSwipeBackground((ProductViewHolder) holder, groupPosition, type);
    }

    @Override
    public void onSetChildItemSwipeBackground(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, int type) {
        return;
    }

    public SwipeResultAction onSwipeItem(ProductViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeLeftResultAction(this, position);
            /*
            return new SwipeResultActionMoveToSwipedDirection() {

                // Optionally, you can override these three methods
                // - void onPerformAction()
                // - void onSlideAnimationEnd()
                public void onSlideAnimationEnd(){
                    Log.e("Animation", "Ended");
                }
                // - void onCleanUp()
            };*/
        } else if(result == SwipeableItemConstants.RESULT_SWIPED_RIGHT){
            return new SwipeRightResultAction(this, position);
        } else {
            return new SwipeResultActionDoNothing();
        }
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(ProductViewHolder.ProductTitleViewHolder holder, int groupPosition, int result) {
        return onSwipeItem(holder, groupPosition, result);
    }

    @Override
    public SwipeResultAction onSwipeChildItem(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, int result) {
        return null;
    }




    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return data.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return data.get(groupPosition).getId();
    }

    @Override
    public ProductViewHolder.ProductTitleViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        // Corresponding original adapter method is: onCreateViewHolder

        // Inflate the view for this view holder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_product_layout, viewGroup, false);
        // Call the view holder's constructor, and pass the view to it;
        // return that new view holder
        return new ProductViewHolder.ProductTitleViewHolder(thisItemsView);
    }

    @Override
    public ProductViewHolder.ProductDetailsViewHolder onCreateChildViewHolder(ViewGroup viewGroup, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        // Almost same method as onCreateGroupViewHolder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_product_details, viewGroup, false);
        return new ProductViewHolder.ProductDetailsViewHolder(thisItemsView);
    }

    @Override
    public void onBindGroupViewHolder(ProductViewHolder.ProductTitleViewHolder viewHolder, int position, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        // Corresponding original adapter method is: onBindViewHolder
        viewHolder.getTextView().setText(data.get(position).getName() + " product");

        // set background resource (target view ID: container)
        final int swipeState = viewHolder.getSwipeStateFlags();
        final int expandState = viewHolder.getExpandStateFlags();
        boolean isExpanded;

        if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId = 0;

            if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.product_swiping_active_state;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                //bgResId = R.drawable.product_swiping_state;
            } else {
                bgResId = R.drawable.product_swiping_normal_state;
            }

            if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            viewHolder.setExpandState(isExpanded/*, animateIndicator*/);
            viewHolder.getContainer().setBackgroundResource(bgResId);
        }
    }

    @Override
    public void onBindChildViewHolder(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        //holder.getTextView().setText(data.get(groupPosition).getName());

        final int swipeState = holder.getSwipeStateFlags();

        if (((swipeState & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.product_swiping_active_state;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.product_swiping_state;
            } else {
                bgResId = R.drawable.product_swiping_normal_state;
            }

            holder.getContainer().setBackgroundResource(bgResId);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(ProductViewHolder.ProductTitleViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return true;
    }

    // Class to perform left-swipe action: Remove from stock list and add to schopping list
    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private ProductsAdapter adapter;
        private final int position;

        SwipeLeftResultAction(ProductsAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            // Remove from list
            adapter.data.remove(position);
            adapter.notifyItemRemoved(position);

            // Add to shopping list
            // TODO: Add some code to add a product to the user's shopping list

        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            adapter = null;
        }

        /*@Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && adapter.mEventListener != null) {
                adapter.mEventListener.onItemPinned(position);
            }*/
        }

    // Class to perform right-swipe action: Remove from stock list
    private static class SwipeRightResultAction extends SwipeResultActionRemoveItem {
        private ProductsAdapter adapter;
        private final int position;

        SwipeRightResultAction(ProductsAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            // Remove from list
            adapter.data.remove(position);
            adapter.notifyItemRemoved(position);

        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            adapter = null;
        }

        /*@Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (adapter.mEventListener != null) {
                adapter.mEventListener.onItemRemoved(mPosition);
            }
        }*/
    }
}