package pt12.frigidarium;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.LinkedList;
import java.util.List;

import static android.R.attr.data;

public class ProductsAdapter extends ExpandableRecyclerAdapter<ProductViewHolder, ProductDetailsViewHolder> {

    private LayoutInflater inflater;

    public ProductsAdapter(Context context, List<ListProduct> productsList) {
        super(productsList);
        inflater = LayoutInflater.from(context);
    }

    /*@Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Inflate the view for this view holder
        View thisItemsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_product_layout,
                viewGroup, false);
        // Call the view holder's constructor, and pass the view to it;
        // return that new view holder
        return new ProductViewHolder(thisItemsView);
    }*/

    @Override
    public ProductViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
            View view = inflater.inflate(R.layout.list_product_layout, parentViewGroup, false);
            return new ProductViewHolder(view);
        }

        @Override
        public ProductDetailsViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
            View view = inflater.inflate(R.layout.list_product_details, childViewGroup, false);
            return new ProductDetailsViewHolder(view);
        }

        @Override
        public void onBindParentViewHolder(ProductViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
            parentViewHolder.getTextView().setText(((ListProduct) parentListItem).getName());
        }

        @Override
        public void onBindChildViewHolder(ProductDetailsViewHolder childViewHolder, int position, Object childListItem) {
            childViewHolder.getTextView().setText(((ProductDetails) childListItem).getAddedBy());
    }

    /*@Override
    public void onBindViewHolder(ProductViewHolder viewHolder, int position) {
        viewHolder.getTextView().setText(data.get(position) + " product");
    }*/

    /*@Override
    public int getItemCount() {
        return data.size();
    }*/

}
