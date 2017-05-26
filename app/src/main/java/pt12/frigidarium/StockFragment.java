package pt12.frigidarium;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;


import java.util.LinkedList;
import java.util.Map;

import pt12.frigidarium.database2.models.StockEntry;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link StockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IS_IN_STOCK = "isInStock";

    private boolean isInStock;

    //private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProductsAdapter adapter;


    private StockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isInStock Parameter 1.
     * @return A new instance of fragment StockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockFragment newInstance(Boolean isInStock) {
        StockFragment fragment = new StockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IS_IN_STOCK, String.valueOf(isInStock));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isInStock = Boolean.parseBoolean(getArguments().getString(ARG_IS_IN_STOCK)  );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stock, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Add divider between items
        Drawable divider = ContextCompat.getDrawable(this.getContext() ,R.drawable.divider);
        RecyclerView.ItemDecoration dividerDecoration = new ProductDividerDecoration(divider);
        recyclerView.addItemDecoration(dividerDecoration);

        // Init data set
        final LinkedList<Pair<String,Map<String,StockEntry>>> data = new LinkedList<>();
        adapter = new ProductsAdapter(data);

        // Add swipe functionality -------------------------------------------------
        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();
        RecyclerView.Adapter wrappedAdapter = swipeManager.createWrappedAdapter(adapter);

        recyclerView.setAdapter(wrappedAdapter);

        // Animator config
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);

        swipeManager.attachRecyclerView(recyclerView);

        // --------------------------------------------------------------------------
        String stock_uid = "stock_test"; //// TODO: 24/05/17 via code de uid opvragen
        DatabaseReference inStockref;
        if(isInStock) {
            inStockref = FirebaseDatabase.getInstance().getReference("stocks/" + stock_uid + "/in_stock");
        }
        else {
            inStockref = FirebaseDatabase.getInstance().getReference("stocks/" + stock_uid + "/out_stock");
        }
        inStockref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {};
                Pair<String, Map<String, StockEntry>> pair = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(genericTypeIndicator));
                data.add(pair);
                int index = data.indexOf(pair);
                adapter.notifyItemInserted(index);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index =  -1;
                for (Pair<String,Map<String,StockEntry>> entry: data){
                    if (entry.first.equals(dataSnapshot.getKey())){
                        index = data.indexOf(entry);
                        break;
                    }
                }
                if (index <  0){
                    return;
                }
                GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {};
                Pair<String, Map<String, StockEntry>> pair = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(genericTypeIndicator));
                data.set(index, pair);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index =  -1;
                for (Pair<String,Map<String,StockEntry>> entry: data){
                    if (entry.first.equals(dataSnapshot.getKey())){
                        index = data.indexOf(entry);
                        break;
                    }
                }
                if (index <  0){
                    return;
                }
                GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {};
                data.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //// TODO: 24/05/17 handle errors
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {/*
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);/*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TOD O: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
    
}
