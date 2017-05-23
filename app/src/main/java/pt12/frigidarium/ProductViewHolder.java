package pt12.frigidarium;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import org.w3c.dom.Text;

public class ProductViewHolder extends AbstractSwipeableItemViewHolder {

    private TextView textView;
    FrameLayout containerView;

    public ProductViewHolder(View view){
        super(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(view.getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        ((TextView) view.findViewById(R.id.product_brand)).setText("Brand");
        ((TextView) view.findViewById(R.id.product_description)).setText("Nog 1 op voorraad");
        textView = (TextView) view.findViewById(R.id.product_name);
        containerView = (FrameLayout) view.findViewById(R.id.container);
    }

    @Override
    public View getSwipeableContainerView() {
        return containerView;
    }

    public TextView getTextView(){
        return textView;
    }

    public FrameLayout getContainer(){
        return containerView;
    }

}
