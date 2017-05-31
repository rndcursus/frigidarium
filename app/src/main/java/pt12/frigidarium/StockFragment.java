package pt12.frigidarium;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.LoginFilter;
import android.util.Log;
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
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;


import java.util.LinkedList;
import java.util.Map;

import pt12.frigidarium.database2.models.StockEntry;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockFragment extends Fragment
        implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IS_IN_STOCK = "isInStock";

    private boolean isInStock;

    //private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProductsAdapter adapter;

    // Variables for expand and swipe funtionality
    private RecyclerViewExpandableItemManager recyclerViewExpandableItemManager;
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerView.Adapter wrappedAdapter;

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
        // Set layout manager for linear layout
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Manager for expand functionality
        final Parcelable savedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        recyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(savedState);
        recyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        recyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        // Add divider between items
        Drawable divider = ContextCompat.getDrawable(this.getContext() ,R.drawable.divider);
        RecyclerView.ItemDecoration dividerDecoration = new ProductDividerDecoration(divider);
        recyclerView.addItemDecoration(dividerDecoration);

        // Init data set
        final LinkedList<Pair<String,Map<String,StockEntry>>> data = new LinkedList<>();
        adapter = new ProductsAdapter(recyclerViewExpandableItemManager, data);

        // Add swipe functionality -------------------------------------------------
        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

        wrappedAdapter = recyclerViewExpandableItemManager.createWrappedAdapter(adapter);       // wrap for expanding
        wrappedAdapter = swipeManager.createWrappedAdapter(wrappedAdapter);                     // wrap for swiping

        recyclerView.setAdapter(wrappedAdapter);

        // Animator config
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);

        recyclerView.setHasFixedSize(false);

        //mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView); NOT NEEDED
        swipeManager.attachRecyclerView(recyclerView);
        recyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        // --------------------------------------------------------------------------


        String stock_uid = LoginActivity.getCurrentStock();
        DatabaseReference inStockref;
        if (!stock_uid.equals("")) {
            if(isInStock) {
                inStockref = FirebaseDatabase.getInstance().getReference("stocks/" + stock_uid + "/in_stock");
             } else {
                inStockref = FirebaseDatabase.getInstance().getReference("stocks/" + stock_uid + "/out_stock");
            }
            inStockref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {
                    };
                    Pair<String, Map<String, StockEntry>> pair = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(genericTypeIndicator));
                    data.add(pair);
                    int index = data.indexOf(pair);
                    adapter.notifyItemInserted(index);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    int index = -1;
                    for (Pair<String, Map<String, StockEntry>> entry : data) {
                        if (entry.first.equals(dataSnapshot.getKey())) {
                            index = data.indexOf(entry);
                            break;
                        }
                    }
                    if (index < 0) {
                        return;
                    }
                    GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {
                    };
                    Pair<String, Map<String, StockEntry>> pair = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(genericTypeIndicator));
                    data.set(index, pair);
                    adapter.notifyItemChanged(index);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    int index = -1;
                    for (Pair<String, Map<String, StockEntry>> entry : data) {
                        if (entry.first.equals(dataSnapshot.getKey())) {
                            index = data.indexOf(entry);
                            break;
                        }
                    }
                    if (index < 0) {
                        return;
                    }
                    GenericTypeIndicator<Map<String, StockEntry>> genericTypeIndicator = new GenericTypeIndicator<Map<String, StockEntry>>() {
                    };
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
        } else {
            //todo er is geen stock in de settings code zal hier nooit  mogen komen.
        }
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

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {
        if (fromUser) {
            int childItemHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.list_item_height);
            int topMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
            int bottomMargin = topMargin; // bottom-spacing: 16dp

            recyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin, bottomMargin);
        }
    }
}