package pt12.frigidarium;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductsAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private String[] data;

    public ProductsAdapter(String[] data) {
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

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

}
