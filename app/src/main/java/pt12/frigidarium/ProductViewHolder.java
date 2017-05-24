package pt12.frigidarium;

import android.support.v4.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import org.w3c.dom.Text;

import java.util.Map;

import pt12.frigidarium.Database.models.Stock;
import pt12.frigidarium.database2.models.Product;
import pt12.frigidarium.database2.models.StockEntry;

public class ProductViewHolder extends AbstractSwipeableItemViewHolder {

    private TextView textView;
    FrameLayout containerView;
    private ValueEventListener mValueEventListener;
    private View view;


    public ProductViewHolder(View view){
        super(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(view.getContext(), textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        textView = (TextView) view.findViewById(R.id.product_name);
        containerView = (FrameLayout) view.findViewById(R.id.container);
        this.view = view;
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

    public void setproduct(final Pair<String, Map<String, StockEntry>> products){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products/"+products.first);
        if (mValueEventListener != null){
            ref.removeEventListener(mValueEventListener);
        }

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product p =  dataSnapshot.getValue(Product.class);
                getTextView().setText(p.name);
                ((TextView) view.findViewById(R.id.product_brand)).setText(p.brand);
                ((TextView) view.findViewById(R.id.product_description)).setText("Nog "+   products.second.size() + " op voorraad");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(mValueEventListener);
    }
}
