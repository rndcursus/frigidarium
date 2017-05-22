package pt12.frigidarium;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import org.w3c.dom.Text;

public class ProductViewHolder extends ParentViewHolder {

    private TextView textView;
    private ImageButton showDetails;

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
        showDetails = (ImageButton) view.findViewById(R.id.show_details);
    }

    public TextView getTextView(){
        return textView;
    }

}
