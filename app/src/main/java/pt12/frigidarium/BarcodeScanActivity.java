package pt12.frigidarium;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.DatePicker;
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
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


import android.app.AlertDialog;
import pt12.frigidarium.database2.models.CheckExist;
import pt12.frigidarium.database2.models.Product;
import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.StockEntry;
import pt12.frigidarium.database2.models.User;

import static android.R.id.input;

public class BarcodeScanActivity extends Activity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Tracker tracker;
    private Boolean scanningPaused = false;
    private String barcode;
    Activity a = this;

    long exdate;
    private Handler dialogHandler;

    public static final String BARCODE = "barcode";
    private static final int CREATE_NEW_USER_DIALOG = 10;
    private static final int CREATE_NEW_DATE_DIALOG = 8;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        //addNewProduct("hoi");
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        createCameraSource();
        dialogHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CREATE_NEW_USER_DIALOG:
                        if (msg.obj instanceof String){
                            String stockId = (String) msg.obj;
                            addToNewList(stockId);
                        }
                    case CREATE_NEW_DATE_DIALOG:
                        if (msg.obj instanceof AlertDialog.Builder){
                            AlertDialog.Builder dialog = (AlertDialog.Builder) msg.obj;
                            dialog.create().show();
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Simple fix to stop the barcode detector when activity is not running.
        scanningPaused = true;
        //barcodeDetector.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //scanningPaused = false;
    }

    /**
     * FUNCTION THAT CREATES THE CAMERA SOURCE, AND KEEPS HOLD OF NEW BARCODES THAT ARE SCANNED.
     */
    private void createCameraSource() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8 | Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
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
                        //// TODO: 1-6-2017 alert the user that the camera is not started
                        finish();
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
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (!scanningPaused) {
                    if (barcodes.size() > 0) {
                        scanningPaused = true;
                        if (barcodes.valueAt(0).valueFormat != Barcode.QR_CODE) {
                            if (barcodes.valueAt(0).displayValue.startsWith(SettingsFragment.USERPREFIX)) {
                                String s = barcodes.valueAt(0).displayValue.split(SettingsFragment.USERPREFIX)[1];
                                dialogHandler.sendMessage(Message.obtain(dialogHandler,CREATE_NEW_USER_DIALOG,s));
                            }else {
                                addNewProduct(barcodes.valueAt(0).displayValue);
                            }
                        }else {
                            if (barcodes.valueAt(0).displayValue.startsWith(SettingsFragment.USERPREFIX)){
                                String s = barcodes.valueAt(0).displayValue.split(SettingsFragment.USERPREFIX)[1];
                                dialogHandler.sendMessage(Message.obtain(dialogHandler,CREATE_NEW_USER_DIALOG,s));
                            }else {
                                addNewProduct(barcodes.valueAt(0).displayValue);
                            }
                        }
                    }
                }
            }
        });

    }


    /**
     * FUNCTION THAT IS CALLED WHEN A NEW BARCODE IS SCANNED. BARCODE IS ADDED TO DATABASE.
     * @param barcode
     */
    private void addNewProduct(final String barcode) {
        //scanningPaused = true;
        //barcodeDetector.release();

        Product.checkExist(barcode, new CheckExist<Product>() {
            @Override
            public void onExist(Product product) {
                dialogHandler.sendMessage(Message.obtain(dialogHandler,CREATE_NEW_DATE_DIALOG,createDialog(barcode, true)));
            }

            @Override
            public void onDoesNotExist(String uid) {
                dialogHandler.sendMessage(Message.obtain(dialogHandler,CREATE_NEW_DATE_DIALOG,createDialog(barcode, false)));
            }

            @Override
            public void onError(DatabaseError error) {
                //// TODO: 30-5-2017 handle error
            }
        });
        //scanningPaused = false;
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
    private AlertDialog.Builder createDialog(final String barcode, final boolean exists)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Product.TABLENAME + "/" + Product.createProductUID(barcode));
        final AlertDialog.Builder add_dialog = new AlertDialog.Builder(BarcodeScanActivity.this);
        final DatePicker input = new DatePicker(this);
        add_dialog.setMessage(exists ? (getResources().getString(R.string.dialog_add_to_stock, "loading name")) : (getResources().getString(R.string.dialog_add_to_stock_does_not_exist)));
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

        //input.setHint(R.string.date_hint);
        add_dialog.setView(input);

        add_dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {

            /**
             *
             * @param dialog
             * @param whichButton
             */
            public void onClick(DialogInterface dialog, int whichButton) {
                exdate = calcExdate(input.getDayOfMonth(), input.getMonth(), input.getYear());
                if(!exists)
                {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), RegisterNewProductActivity.class);
                    intent.putExtra(RegisterNewProductActivity.BARCODE, barcode);
                    intent.putExtra(RegisterNewProductActivity.EXDATE, exdate);
                    startActivity(intent);
                }
                else
                {
                    String stockId = LoginActivity.getCurrentStock();
                    Stock.addStockEntryToInStock(stockId, new StockEntry(Product.createProductUID(barcode),exdate));
                }

                scanningPaused = false;

            }
        });
        add_dialog.setNeutralButton(R.string.add_without_date,  new DialogInterface.OnClickListener() {

            /**
             *
             * @param dialog
             * @param whichButton
             */
            public void onClick(DialogInterface dialog, int whichButton) {
                exdate = 0L;

                if(!exists)
                {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), RegisterNewProductActivity.class);
                    intent.putExtra(RegisterNewProductActivity.BARCODE, barcode);
                    intent.putExtra(RegisterNewProductActivity.EXDATE, exdate);
                    startActivity(intent);
                }
                else
                {
                    String stockId = LoginActivity.getCurrentStock();
                    Stock.addStockEntryToInStock(stockId, new StockEntry(Product.createProductUID(barcode),exdate));
                }

                scanningPaused = false;
            }
        });
        add_dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                scanningPaused = false;
            }
        });
        return add_dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDatePickerDialog(final Product product){
        //test datepicker
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                GregorianCalendar date = new GregorianCalendar();
                date.set(year,month,dayOfMonth);
                long bestBefore = date.getTimeInMillis()/1000L;
                StockEntry entry = new StockEntry(Product.createProductUID(product.getUid()), bestBefore);
                String stockId = LoginActivity.getCurrentStock();
                if (stockId.equals("")){
                    //todo no current stock
                    return;
                }
                Stock.addStockEntryToInStock(stockId, entry);
                scanningPaused = false;
            }
        },cal.get(GregorianCalendar.YEAR),cal.get(GregorianCalendar.MONTH),cal.get(GregorianCalendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     *
     * @param qrcode
     */
    private void addToNewList(final String qrcode){

        scanningPaused = true;
        barcodeDetector.release();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_switch_list);

        builder.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                addUserToList(qrcode);
                scanningPaused = false;
              }
        });
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                scanningPaused = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private long calcExdate(int day, int month, int year)
    {
        Calendar c = new GregorianCalendar();
        c.set(year,month,day);
        return c.getTimeInMillis() / 1000L;
    }
}