package pt12.frigidarium.Database;

import android.widget.TextView;

/**
 * Created by mattijn on 11/05/17.
 */

public class ProductExample {
    public final void Main(String[] args){
        Product p = new Product("6898978");

        // to write a value to p

        p.setBrand("campina");

        // to read a  value from p
        final exampleTextView tv = new ProductExample.exampleTextView();
        Product.OnProductChangeListener listener = new Product.OnProductChangeListener() {
            @Override
            public void onChange(String name) {
                if (name.equals(Product.BRAND)) {
                    String brand = this.getBrand();
                    //do something with brand.
                    tv.setText(brand);
                }
            }
        };
        p.addListener(listener);
    }

    //this class represents a tv in the example
    class exampleTextView{
        public void setText(String text){

        }
    }
}
