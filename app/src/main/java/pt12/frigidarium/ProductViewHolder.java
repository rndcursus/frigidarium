package pt12.frigidarium;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import org.w3c.dom.Text;

public class ProductViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;


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

        showDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isExpanded()){
                    collapseView();
                }else{
                    expandView();
                }
            }
        });
    }

    public TextView getTextView(){
        return textView;
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion(){
        return false;
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * INITIAL_POSITION,
                        ROTATED_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            showDetails.startAnimation(rotateAnimation);
        }
    }

}
