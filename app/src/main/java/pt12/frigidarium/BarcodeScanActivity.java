package pt12.frigidarium;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;

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
    public static String BARCODE = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);


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
                if(barcodes.size() >0){
                    if(barcodes.valueAt(0).valueFormat != Barcode.QR_CODE)
                        addNewProduct(barcodes.valueAt(0).displayValue);
                    else
                        addToNewList(barcodes.valueAt(0).displayValue);
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
                Stock.addStockEntryToInStock(Product.createProductUID(barcode), entry);
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
    private void addToNewList(String userID){
        String stockId = getPreferences(MODE_PRIVATE).getString("current_stock",null); //// TODO: 30-5-2017 uitzoeken welke mode moet en magic number weghalen
        //// TODO: 30-5-2017 ask the user for permission to add the user to add the user to a list.
        if (stockId != null) {
            Stock.addUserToStock(stockId, userID);
            User.addUserToStock(userID, stockId);
        }else{
            // todo current user is not set.
        }
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
