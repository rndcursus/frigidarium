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

/**
 * This adapter is used by the shopping cart tab to display items that are currently in the user's
 * shopping cart list.
 */

public class ShoppingAdapter
        extends AbstractExpandableItemAdapter<ShoppingViewHolder.ShoppingTitleViewHolder, ShoppingViewHolder.ShoppingDetailsViewHolder>
        implements ExpandableSwipeableItemAdapter<ShoppingViewHolder.ShoppingTitleViewHolder, ShoppingViewHolder.ShoppingDetailsViewHolder> {

    private List<Pair<Pair<String, Long>,Map<String, StockEntry>>> data;
    private final RecyclerViewExpandableItemManager expandableItemManager;

    public ShoppingAdapter(RecyclerViewExpandableItemManager expandableItemManager, List<Pair<Pair<String, Long>,Map<String, StockEntry>> > data) {
        setHasStableIds(true);
        this.data = data;
        this.expandableItemManager = expandableItemManager;
    }

    @Override
    public int onGetGroupItemSwipeReactionType(ShoppingViewHolder.ShoppingTitleViewHolder holder, int groupPosition, int x, int y) {
        // Product items can be swiped in both directions horizontally
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(ShoppingViewHolder.ShoppingDetailsViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // In the shopping cart tab, individual instances of products aren't showed. So swiping
        // is not needed here.
        return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY;
    }

    public void onSetSwipeBackground(ShoppingViewHolder holder, int position, int type) {
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
    public void onSetGroupItemSwipeBackground(ShoppingViewHolder.ShoppingTitleViewHolder holder, int groupPosition, int type) {
        onSetSwipeBackground(holder, groupPosition, type);
    }

    @Override
    public void onSetChildItemSwipeBackground(ShoppingViewHolder.ShoppingDetailsViewHolder holder, int groupPosition, int childPosition, int type) {
        onSetSwipeBackground(holder, groupPosition, type);
    }

    // Method to handle swipe movements on products
    public SwipeResultAction onSwipeItem(ShoppingViewHolder holder, int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT) {
            // Action for a lef-swipe
            return new ShoppingAdapter.SwipeLeftResultAction(this, position);
        } else if(result == SwipeableItemConstants.RESULT_SWIPED_RIGHT){
            // Action for a right-swipe
            return new ShoppingAdapter.SwipeRightResultAction(this, position);
        } else {
            // Action for no swipe
            return new SwipeResultActionDoNothing();
        }
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(ShoppingViewHolder.ShoppingTitleViewHolder holder, int groupPosition, int result) {
        return onSwipeItem(holder, groupPosition, result);
    }

    @Override
    public SwipeResultAction onSwipeChildItem(ShoppingViewHolder.ShoppingDetailsViewHolder holder, int groupPosition, int childPosition, int result) {
        // Child items cannot be swiped in this adapter
        return new SwipeResultActionDoNothing();
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        // Child items are not used in this adapter
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        //return data.get(groupPosition).getId();
        //return data.get(groupPosition).hashCode();
        return data.get(groupPosition).first.second;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // Child items are not used in this adapter
        return 0;
    }

    // Static properties and method to generate ids
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
    public ShoppingViewHolder.ShoppingTitleViewHolder onCreateGroupViewHolder(ViewGroup viewGroup, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        // Corresponding original adapter method is: onCreateViewHolder

        // Inflate the view for this view holder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_shopping_layout, viewGroup, false);
        // Call the view holder's constructor, and pass the view to it;
        // return that new view holder
        return new ShoppingViewHolder.ShoppingTitleViewHolder(thisItemsView);
    }

    @Override
    public ShoppingViewHolder.ShoppingDetailsViewHolder onCreateChildViewHolder(ViewGroup viewGroup, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        // Almost same method as onCreateGroupViewHolder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_product_details, viewGroup, false);
        return new ShoppingViewHolder.ShoppingDetailsViewHolder(thisItemsView);
    }

    @Override
    public void onBindGroupViewHolder(ShoppingViewHolder.ShoppingTitleViewHolder viewHolder, int position, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
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
            viewHolder.getContainer().setBackgroundResource(bgResId);
        }
    }

    @Override
    public void onBindChildViewHolder(ShoppingViewHolder.ShoppingDetailsViewHolder holder, int groupPosition, int childPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        return;
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(ShoppingViewHolder.ShoppingTitleViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }

    // Class to perform left-swipe action: Remove from stock list and add to schopping list
    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private ShoppingAdapter adapter;
        private final int position;

        SwipeLeftResultAction(ShoppingAdapter adapter, int position) {
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Stock.TABLENAME + "/" + LoginActivity.getCurrentStock() + "/" + Stock.OUTSTOCK + "/" + adapter.data.get(position).first.first);
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
    }

    // Class to perform right-swipe action: Remove from stock list
    private static class SwipeRightResultAction extends SwipeResultActionRemoveItem {
        private ShoppingAdapter adapter;
        private final int position;

        SwipeRightResultAction(ShoppingAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Stock.TABLENAME + "/" + LoginActivity.getCurrentStock() + "/" + Stock.OUTSTOCK + "/" + adapter.data.get(position).first.first);
            //adapter.notifyDataSetChanged();
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
    }
}
