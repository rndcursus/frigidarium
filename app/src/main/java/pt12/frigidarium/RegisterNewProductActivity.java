package pt12.frigidarium;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterNewProductActivity extends AppCompatActivity {

    public static final String BARCODE = "barcode";
    private EditText productName;
    private EditText productBrand;
    private EditText productContent;
    private EditText productUrl;
    private RadioButton liter;
    private RadioButton gram;
    private Button submit;
    private Spinner contentUnitDropdown;
    private String barcode = "ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getStringExtra(BARCODE) != null)
        {
            barcode = getIntent().getStringExtra(BARCODE);
        }
        setContentView(R.layout.fragment_register_new_product);
        productName = (EditText) findViewById(R.id.product_name);
        productBrand = (EditText) findViewById(R.id.product_brand);
        productContent = (EditText) findViewById(R.id.product_content);
        productUrl = (EditText) findViewById(R.id.product_url);
        submit = (Button) findViewById(R.id.submit);
        contentUnitDropdown = (Spinner) findViewById(R.id.content_units_drop);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
                            Toast.makeText(getApplicationContext(), "Not all required fields are filled", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            RegisterProduct(pn, pb, pc, purl);
                            finish();
                        }


                    }
                }
        );


    }

    public void RegisterProduct(String productName, String productBrand, String productContent, String productUrl)
    {
        //TODO: database dingen: voeg nieuw product toe aan productendatabase, maar ook aan fridge van gebruiker
        Log.v("datalog", "barcode:"+barcode+", pn:"+productName+", pb:"+productBrand+", pc:"+productContent+", purl:"+productUrl);
    }

}