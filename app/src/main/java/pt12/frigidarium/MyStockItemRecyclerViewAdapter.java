package pt12.frigidarium;

import android.app.Activity;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.User;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyStockItemRecyclerViewAdapter extends RecyclerView.Adapter<MyStockItemRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final Activity mActivity;
    private ViewHolder currentStockView;

    public MyStockItemRecyclerViewAdapter(Activity activity, List<String> items) {
        mValues = items;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stockitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setStock(mValues.get(position));


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        private final Button mLeaveButton;
        private final Button mSetCurrentListButton;
        public String mItem;
        private ValueEventListener stockNameListener;
        private DatabaseReference stockRef;
        private static final int selectedColor = Color.GREEN;
        private static final int unselectedColor = Color.WHITE;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mLeaveButton = (Button) view.findViewById(R.id.step_out_list);
            mSetCurrentListButton = (Button) view.findViewById(R.id.set_current_list);
            mView.setBackgroundColor(unselectedColor);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        public void setStock(final String stock) {
            final String current_sid = mActivity.getPreferences(Context.MODE_PRIVATE).getString(LoginActivity.STOCKPREFERNCEKEY, "");
            if (current_sid.equals(stock)){
                mView.setBackgroundColor(selectedColor);
            }
            mItem = stock;
            if (stockRef == null){
                stockRef = FirebaseDatabase.getInstance().getReference(Stock.TABLENAME + "/" + stock);
            }else{
                if (stockNameListener != null){
                    stockRef.child(Stock.NAME).removeEventListener(stockNameListener);
                }
            }
            if (stockNameListener == null) {
                this.stockNameListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mContentView.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //// TODO: 29/05/17 handle error
                    }
                };
            }
            stockRef.child(Stock.NAME).addValueEventListener(this.stockNameListener);

            mLeaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String current_sid = mActivity.getPreferences(Context.MODE_PRIVATE).getString(LoginActivity.STOCKPREFERNCEKEY, "");
                    if (current_sid.equals(stock)){
                        mView.setBackgroundColor(unselectedColor);
                        mActivity.getPreferences(Context.MODE_PRIVATE).edit().putString(LoginActivity.STOCKPREFERNCEKEY,"").apply();
                    }
                    User.removeUserFromStock(FirebaseAuth.getInstance().getCurrentUser().getUid(),stock);
                    Stock.removeUserFromStock(stock, FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
            });
            final ViewHolder vh = this;
            mSetCurrentListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentStockView != null){
                        currentStockView.mView.setBackgroundColor(unselectedColor);
                    }
                    currentStockView = vh;
                    currentStockView.mView.setBackgroundColor(selectedColor);
                    mActivity.getPreferences(Context.MODE_PRIVATE).edit().putString(LoginActivity.STOCKPREFERNCEKEY,stock).apply();
                }
            });

        }
    }
}
