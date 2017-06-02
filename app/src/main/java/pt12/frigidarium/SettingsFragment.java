package pt12.frigidarium;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String USERPREFIX = "user:";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean firstSelection;

  //  private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        Button share = (Button) view.findViewById(R.id.button_share);
        share.setText(getString(R.string.share_button));
        share.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createQRCode();
            }
        });

        /*
        Creates the language selection spinner
         */
        firstSelection=true; //needed for it to not fire off immediately
        Spinner language_select = (Spinner) view.findViewById(R.id.spinner_lang);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getContext(),R.array.languages,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_select.setAdapter(adapter);
        language_select.setOnItemSelectedListener(this);
        TextView prompt = (TextView) view.findViewById(R.id.text_lang);
        prompt.setText(R.string.select_lang);
        /*
        Selects the current language as default selection.
        Spinner will not fire off when it gets reselected.
         */
        int def = 0;
        switch(Locale.getDefault().getDisplayLanguage()){
            case "English":break;
            case "Nederlands": def=1;break;
            case "Deutsch": def=2;break;
            default: break;
        }
        language_select.setSelection(def,true);
        return view;
    }

    /**
     * Uses the ImageView to set up a Bitmap.
     * From the user's ID a QR code will be generated
     * and pixels will be colored black or white.
     */
    private void createQRCode() {
        ImageView mImageView = (ImageView) getView().findViewById(R.id.image_qr);
        super.onStart();
        QRCodeWriter writer = new QRCodeWriter();
        mImageView.getWidth();
        int width = 256;
        int height = 256;
        String stockId= USERPREFIX + FirebaseAuth.getInstance().getCurrentUser().getUid();
        try{
            BitMatrix bitMatrix = writer.encode(stockId, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }
            mImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
      /* if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }

    /**
     * Should set the language to the selected one
     * @param parent Spinner
     * @param view View
     * @param pos selected position
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        if(firstSelection)
        firstSelection=false;
        else {
            switch (pos) {
                case 0:
                    setLocale("en");
                    break;
                case 1:
                    setLocale("nl");
                    break;
                case 2:
                    setLocale("de");
                    break;
                default:
                    break;
            }
        }
       // setLocale(lang.toString());
    }

    /**
     * Should set the current locale to a different language
     * @param lang language code
     */
    public void setLocale(String lang) {
        Resources res = getContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
        getActivity().finish();

        }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
}
