package pt12.frigidarium;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.Arrays;
import java.util.LinkedList;


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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProductsAdapter adapter;

    // Variables for expand and swipe funtionality
    private RecyclerViewExpandableItemManager recyclerViewExpandableItemManager;
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private RecyclerView.Adapter wrappedAdapter;

    public StockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockFragment newInstance(String param1, String param2) {
        StockFragment fragment = new StockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        LinkedList<tmpProduct> data = new LinkedList<tmpProduct>(Arrays.asList(new tmpProduct("Cola", 0), new tmpProduct("Sinas", 1), new tmpProduct("Bier", 2), new tmpProduct("Wijn", 3)));
        //adapter = new ProductsAdapter(data);
        adapter = new ProductsAdapter(recyclerViewExpandableItemManager, data);

        // Add swipe functionality -------------------------------------------------
        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();

        wrappedAdapter = recyclerViewExpandableItemManager.createWrappedAdapter(adapter);       // wrap for expanding
        //wrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);  // wrap for dragging (NOT NEEDED)
        wrappedAdapter = swipeManager.createWrappedAdapter(wrappedAdapter);                     // wrap for swiping
        // OLD: RecyclerView.Adapter wrappedAdapter = swipeManager.createWrappedAdapter(adapter);

        recyclerView.setAdapter(wrappedAdapter);

        // Animator config
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        animator.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(animator);

        // OLD: swipeManager.attachRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(false);

        //mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView); NOT NEEDED
        swipeManager.attachRecyclerView(recyclerView);
        recyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        // --------------------------------------------------------------------------

        tmpProduct data5 = new tmpProduct("Kaas", 4);
        data.add(data5);
        adapter.notifyItemInserted(4);
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

    public static class tmpProduct{

        private String name;
        private long id;

        public tmpProduct(String name, long id){
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }
    }
}
