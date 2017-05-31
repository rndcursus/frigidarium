package pt12.frigidarium;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterNewProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterNewProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterNewProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BARCODE = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String barcode;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText productName;
    private EditText productBrand;
    private EditText productContent;
    private EditText productUrl;
    private RadioButton liter;
    private RadioButton gram;
    private Button submit;
    private Spinner contentUnitDropdown;

    public RegisterNewProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param barcode Barcode van product.
     * @return A new instance of fragment RegisterNewProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterNewProductFragment newInstance(String barcode) {
        RegisterNewProductFragment fragment = new RegisterNewProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BARCODE, barcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode = getArguments().getString(ARG_BARCODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_new_product, container, false);
        productName = (EditText) rootView.findViewById(R.id.product_name);
        productBrand = (EditText) rootView.findViewById(R.id.product_brand);
        productContent = (EditText) rootView.findViewById(R.id.product_content);
        //liter = (RadioButton) rootView.findViewById(R.id.liter);
        //gram = (RadioButton) rootView.findViewById(R.id.gram);
        productUrl = (EditText) rootView.findViewById(R.id.product_url);
        submit = (Button) rootView.findViewById(R.id.submit);
        contentUnitDropdown = (Spinner) rootView.findViewById(R.id.content_units_drop);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.content_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contentUnitDropdown.setAdapter(adapter);
        contentUnitDropdown.setSelection(0);

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pn, pb, pc, purl;
                        pn = productName.getText().toString().trim();
                        pb = productBrand.getText().toString().trim();
                        pc = productContent.getText().toString().trim() + " " + contentUnitDropdown.getSelectedItem().toString();
                        /*
                        if(liter.isChecked())
                        {
                            pc = pc + " L";
                        }
                        if(gram.isChecked())
                        {
                            pc = pc + " g";
                        }
                        */
                        purl = productUrl.getText().toString();
                        if(pn.equals("") || pb.equals("") || productContent.getText().toString().trim().equals("") || purl.equals(""))
                        {
                            Toast.makeText(getActivity(), "Not all required fields are filled", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            RegisterProduct(pn, pb, pc, purl);
                        }

                    }
                }
        );


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void RegisterProduct(String productName, String productBrand, String productContent, String productUrl)
    {
        //TODO: database dingen: voeg nieuw product toe aan productendatabase, maar ook aan fridge van gebruiker
        Log.v("datalog", "barcode:"+barcode+"pn:"+productName+", pb:"+productBrand+", pc:"+productContent+", purl:"+productUrl);
    }
}
