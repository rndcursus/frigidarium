package pt12.frigidarium;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

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
    }

    public TextView getTextView(){
        return textView;
    }

}
