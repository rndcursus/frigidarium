package pt12.frigidarium;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

/**
 * Created by ten Klooster on 22-5-2017.
 */

public class ProductDetailsViewHolder extends ChildViewHolder {

    private TextView addedBy;

    public ProductDetailsViewHolder(View view){
        super(view);

        addedBy = (TextView) view.findViewById(R.id.added_by);
    }

    public TextView getTextView(){
        return addedBy;
    }
}
