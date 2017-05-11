package pt12.frigidarium;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.widget.TextView;

import pt12.frigidarium.Database.Product;

public class FirebaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        Product p = new Product("test_product");
        p.addListener(new Product.OnProductChangeListener() {
            @Override
            public void onChange(String name) {
                if (name.equals(Product.BRAND)){
                    TextView tv = (TextView) findViewById(R.id.textView2);
                    tv.setText(getBrand());
                }
            }
        });
    }
}
