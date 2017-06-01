package pt12.frigidarium;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


import android.app.AlertDialog;
import pt12.frigidarium.database2.models.CheckExist;
import pt12.frigidarium.database2.models.Product;
import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.StockEntry;
import pt12.frigidarium.database2.models.User;

public class BarcodeScanActivity extends Activity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Tracker tracker;
    private Boolean scanningPaused = false;
    private String barcode;
    Activity a = this;

    public static final String BARCODE = "barcode";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        //addNewProduct("hoi");
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        createCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Simple fix to stop the barcode detector when activity is not running.
        barcodeDetector.release();
    }

    /**
     * FUNCTION THAT CREATES THE CAMERA SOURCE, AND KEEPS HOLD OF NEW BARCODES THAT ARE SCANNED.
     */
    private void createCameraSource(){

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8 | Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600,1024)
                .setRequestedFps(15.0f)
                .build();


        /**
         * CAMERAVIEW USES THE CAMERASOURCE TO START THE CAMERA.
         */
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // CHECK AGAIN IF PERMISSION GRANTED. IF PERMISSION NOT GRANTED, THEN THE CAMERA IS NOT STARTED.
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        /**
         * BARCODE TETECTOR. KEEPS TRACK OF NEW BARCODES THAT ARE SCANNED, AND CALLS THE CORRESPONDING FUNCTION
         */
        barcodeDetector.setProcessor( new Detector.Processor<Barcode>(){

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(!scanningPaused){
                    if(barcodes.size() > 0){
                        scanningPaused = true;
                        if(barcodes.valueAt(0).valueFormat != Barcode.QR_CODE) {
                            if (barcodes.valueAt(0).displayValue.startsWith(SettingsFragment.USERPREFIX)){
                                String s = barcodes.valueAt(0).displayValue.split(SettingsFragment.USERPREFIX)[1];
                                addToNewList(s);
                            }else {
                                addNewProduct(barcodes.valueAt(0).displayValue);
                            }
                        }else {
                            if (barcodes.valueAt(0).displayValue.startsWith(SettingsFragment.USERPREFIX)){
                                String s = barcodes.valueAt(0).displayValue.split(SettingsFragment.USERPREFIX)[0];
                                addToNewList(s);
                            }else {
                                addNewProduct(barcodes.valueAt(0).displayValue);
                            }
                        }
                        scanningPaused = false;
                    }
                }
            }
        });

    }


    /**
     * FUNCTION THAT IS CALLED WHEN A NEW BARCODE IS SCANNED. BARCODE IS ADDED TO DATABASE.
     * @param barcode
     */
    private void addNewProduct(final String barcode){
        Product.checkExist(barcode, new CheckExist<Product>() {
            @Override
            public void onExist(Product product) {
                long best_before = 0L;
                StockEntry entry = new StockEntry(Product.createProductUID(barcode), best_before);
                String stockId = LoginActivity.getCurrentStock();
                if (stockId.equals("")){
                    //todo no current stock
                    return;
                }
                Stock.addStockEntryToInStock(stockId, entry);
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDoesNotExist(String uid) {
                Intent intent;
                intent = new Intent(getApplicationContext(), RegisterNewProductActivity.class);
                intent.putExtra(RegisterNewProductActivity.BARCODE, barcode); //Get the latest Barcode
                startActivity(intent);
                Toast.makeText(getApplicationContext(), R.string.addedSuccesful, Toast.LENGTH_SHORT).show();
                //CreateDialog();
                //// TODO: 31-5-2017 dialog kan pas worden aangeroepen nadat het formulier is ingevuld.
            }

            @Override
            public void onError(DatabaseError error) {
                //// TODO: 30-5-2017 handle error
            }
        });

    }

    /**
     * FUNCTION THAT IS CALLED WHEN A QR CODE IS SCANNED. USER ADDED TO NEW LIST
     * @param userID the userID to be added to te current list.
     */
    private void addUserToList(String userID){
        String stockId = LoginActivity.getCurrentStock();
        //// TODO: 30-5-2017 ask the user for permission to add the user to add the user to a list.
        if (!stockId.equals("")) {
            Stock.addUserToStock(stockId, userID);
            User.addUserToStock(userID, stockId);
        }else{
            // todo current user is not set.
        }
        finish();
    }

    /**
     * INVULLEN NOG
     * @param barcode
     */
    private void reateDialog(final String barcode)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Product.TABLENAME + "/" + Product.createProductUID(barcode));
        final AlertDialog.Builder add_dialog = new AlertDialog.Builder(BarcodeScanActivity.this);
        final EditText input = new EditText(this);
        add_dialog.setMessage(getResources().getString(R.string.dialog_add_to_stock, "loading name"));
        ref.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product p = dataSnapshot.getValue(Product.class);
                if (p != null) {
                    add_dialog.setMessage(getResources().getString(R.string.dialog_add_to_stock, p.getName()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        input.setHint(R.string.date_hint);
        add_dialog.setView(input);

        add_dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {

            /**
             *
             * @param dialog
             * @param whichButton
             */
            public void onClick(DialogInterface dialog, int whichButton) {
                String exdatestring = input.getText().toString().trim();
                if(!exdatestring.equals(""))
                {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                        Date date = simpleDateFormat.parse(exdatestring);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        long exdate = (cal.getTimeInMillis() / 1000L);
                        StockEntry entry = new StockEntry(Product.createProductUID(barcode), exdate);
                        String stockId = getPreferences(0).getString("current_stock", "");
                        if (stockId.equals("")){
                            //todo no current stock
                            return;
                        }
                        Stock.addStockEntryToInStock(stockId, entry);
                        Log.v("datalog", "barcode:"+barcode+", date:"+exdate);

                    } catch (ParseException e) {
                        Toast.makeText(a, R.string.date_toast, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        add_dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                scanningPaused = false;
            }
        });
        add_dialog.show();
    }

    /**
     *
     * @param qrcode
     */
    private void addUserToList(final String qrcode){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_switch_list);

        builder.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addUserToList(qrcode);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        scanningPaused = false;
    }


}