package pt12.frigidarium;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import pt12.frigidarium.Database.firebase.DatabaseEntryOwner;
import pt12.frigidarium.Database.models.Product;
import pt12.frigidarium.Database.models.User;

public class FirebaseTestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        Product p = Product.getInstanceByUID("test_product");
        p.addDataAccessor(new Product.OnProductChangeListener() {


            @Override
            public void onError(Product owner, String name, int code, String message, String details) {

            }

            @Override
            public void onGetInstance(Product owner) {
                TextView tv = (TextView) findViewById(R.id.textView2);
                tv.setText(getBrand());
            }

            @Override
            public void onChange(Product p, String name) {
                if (name.equals(Product.BRAND)){
                    TextView tv = (TextView) findViewById(R.id.textView2);
                    tv.setText(getBrand());
                }
            }
        });
        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }
}
