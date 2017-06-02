package pt12.frigidarium;

import android.support.annotation.IntRange;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.StockEntry;

public class ProductsAdapter
        extends AbstractExpandableItemAdapter<ProductViewHolder.ProductTitleViewHolder, ProductViewHolder.ProductDetailsViewHolder>
        implements ExpandableSwipeableItemAdapter<ProductViewHolder.ProductTitleViewHolder, ProductViewHolder.ProductDetailsViewHolder> {

    private List<Pair<Pair<String, Long>,Map<String, StockEntry>>> data;
    private final RecyclerViewExpandableItemManager expandableItemManager;

    public ProductsAdapter(RecyclerViewExpandableItemManager expandableItemManager, List<Pair<Pair<String, Long>,Map<String, StockEntry>> > data) {
        setHasStableIds(true);
        this.data = data;
        this.expandableItemManager = expandableItemManager;
    }

    @Override
    public int onGetGroupItemSwipeReactionType(ProductViewHolder.ProductTitleViewHolder holder, int groupPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT;
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
        onSetSwipeBackground(holder, groupPosition, type);
    }

    @Override
    public void onSetChildItemSwipeBackground(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, int type) {
        onSetSwipeBackground(holder, groupPosition, type);
    }

    public SwipeResultAction onSwipeItem(ProductViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            return new SwipeLeftResultAction(this, position);

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
        if (result == SwipeableItemConstants.RESULT_SWIPED_RIGHT) {
            return new SwipeChildResultAction(this, groupPosition, childPosition, holder);
        } else {
            return new SwipeResultActionDoNothing();
        }
    }




    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return data.get(groupPosition).second.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        //return data.get(groupPosition).getId();
        //return data.get(groupPosition).hashCode();
        return data.get(groupPosition).first.second;
        // TODO: getGroupId()
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //return data.get(groupPosition).getId();
        LinkedList<Map.Entry<String, StockEntry>> list = new LinkedList<Map.Entry<String, StockEntry>>(data.get(groupPosition).second.entrySet());
        return getId(list.get(childPosition).getKey());
        // TODO: getChildId()
    }

    private static HashMap<String, Long> map = new HashMap<>();
    private static long childId = 1;
    private static long getId(String s){
        if(map.size() < 1){
            map.put("tmp", (long) 0);
        }
        if (!map.keySet().contains(s)) {
            map.put(s, childId);
            childId++;
        }
        return map.get(s);
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
        viewHolder.setproduct(data.get(position));

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

            isExpanded = (expandState & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0;

            viewHolder.setExpandState(isExpanded/*, animateIndicator*/);
            viewHolder.getContainer().setBackgroundResource(bgResId);
        }
    }

    @Override
    public void onBindChildViewHolder(ProductViewHolder.ProductDetailsViewHolder holder, int groupPosition, int childPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        //holder.getTextView().setText(data.get(groupPosition).getName());
        LinkedList<Map.Entry<String, StockEntry>> entries = new LinkedList<Map.Entry<String, StockEntry>>(data.get(groupPosition).second.entrySet());
        holder.setDetails(entries.get(childPosition));
        holder.setBestBeforeText();

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

            // Add to shopping list
            LinkedList<Map.Entry<String, StockEntry>> entries = new LinkedList<>(adapter.data.get(position).second.entrySet());
            Stock.addStockEntryToOutStock(LoginActivity.getCurrentStock(), entries.getFirst().getValue());

            // Remove from stock list
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("stocks/" + LoginActivity.getCurrentStock() + "/in_stock/" + adapter.data.get(position).first.first);
            adapter.data.remove(position);
            adapter.expandableItemManager.notifyGroupItemRemoved(position);
            ref.removeValue();

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

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("stocks/" + LoginActivity.getCurrentStock() + "/in_stock/" + adapter.data.get(position).first.first);
            //adapter.notifyDataSetChanged();
            adapter.data.remove(position);
            adapter.expandableItemManager.notifyGroupItemRemoved(position);
            ref.removeValue();

            // Remove from list
            //adapter.data.remove(position);
            //adapter.notifyItemRemoved(position);

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

    // Class to perform right-swipe action: Remove from stock list
    private static class SwipeChildResultAction extends SwipeResultActionRemoveItem {
        private ProductsAdapter adapter;
        private final int groupPosition;
        private final int childPosition;
        private final ProductViewHolder.ProductDetailsViewHolder viewHolder;

        SwipeChildResultAction(ProductsAdapter adapter, int groupPosition, int childPosition, ProductViewHolder.ProductDetailsViewHolder viewHolder) {
            this.adapter = adapter;
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
            this.viewHolder = viewHolder;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            String databaseKey = viewHolder.getDatabaseKey();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("stocks/" + LoginActivity.getCurrentStock() + "/in_stock/" + adapter.data.get(groupPosition).first.first + "/" + databaseKey);
            //adapter.notifyDataSetChanged();
            adapter.data.get(groupPosition).second.remove(databaseKey);
            adapter.expandableItemManager.notifyChildItemRemoved(groupPosition, childPosition);
            ref.removeValue();
            // Remove from list
            //adapter.data.remove(position);
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            adapter = null;
        }
    }
}