package pt12.frigidarium;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.value;

public class BarcodeScanActivity extends Activity{

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Tracker tracker;
    private Boolean scanningPaused = false;
    private String barcode;
    Activity a = this;

    public static String BARCODE = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        //addNewProduct("hoi");

        if(!permissionsGranted()) requestPermissionsForCamera(); // CHECK IF PERMISSIONS GRANTED. IF NOT, REQUEST PERMISSIONS.
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        //barcodeInfo = (TextView) getView().findViewById(R.id.code_info);
        createCameraSource();




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


        //START CAMERA
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

        barcodeDetector.setProcessor( new Detector.Processor<Barcode>(){

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(!scanningPaused)
                {
                    if(barcodes.size() >0){
                        if(barcodes.valueAt(0).valueFormat != Barcode.QR_CODE)
                            addNewProduct(barcodes.valueAt(0).displayValue);
                        else
                            addToNewList(barcodes.valueAt(0).displayValue);
                    }
                }
            }
        });

    }


    /**
     * FUNCTION THAT IS CALLED WHEN A NEW BARCODE IS SCANNED. BARCODE IS ADDED TO DATABASE.
     * @param bc
     */
    private void addNewProduct(String bc){
        scanningPaused = true;
        barcode = bc;
        if(/*TODO: !productIsRegistered(barcode)*/ true){
            Intent intent;
            intent = new Intent(getApplicationContext(), RegisterNewProductActivity.class);
            intent.putExtra("barcode", barcode); //Get tht latest Barcode
            startActivity(intent);

        }
        final AlertDialog.Builder add_dialog = new AlertDialog.Builder(BarcodeScanActivity.this);
        final EditText input = new EditText(this);
        input.setHint(R.string.date_hint);
        add_dialog.setView(input);
        add_dialog.setMessage(getResources().getString(R.string.dialog_add_to_stock, /*TODO: getProductName(barcode)*/ "productnaam"));
        add_dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
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
                        /*TODO: addProduct(barcode, exdate);*/
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
            }
        });
        add_dialog.show();


        scanningPaused = false;
    }


    private int componentTimeToTimestamp(int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (int) (c.getTimeInMillis() / 1000L);
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the AddProductDialogFragment.AddProductDialogListener interface
    /*
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        exdate = addproductdialog.getDate();
        //TODO: addProductToStock(barcode, exdate);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }
    */

    /**
     * FUNCTION THAT IS CALLED WHEN A QE CODE IS SCANNED. USER ADDED TO NEW LIST
     * @param qrcode
     */
    private void addToNewList(String qrcode){
        scanningPaused = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_switch_list);

        builder.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //switchUserToList(qrcode);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        scanningPaused = false;




        // startActivity(intent);
        //
    }

    /**
     * FUNCTION TO CHECK IF CAMERA PERMISSION IS GRANTED
     * @return
     */
    private boolean permissionsGranted(){
        String permission = "android.permission.CAMERA";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * FUNCTION THAT REQUESTS THE PERMISSION FOR THE CAMERA
     */
    private void requestPermissionsForCamera(){
        final int PERMISSION_CODE = 123; // USED FOR CAMERA PERMISSIONS
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE); // REQUEST CAMERA PERMISSIONS
    }

}