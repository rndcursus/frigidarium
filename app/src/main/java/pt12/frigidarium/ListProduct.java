package pt12.frigidarium;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ten Klooster on 22-5-2017.
 */

// LET OP: DEZE KLASSE IS TIJDELIJK //
public class ListProduct implements ParentListItem {

    private String name;
    private String brand;

    private List<ProductDetails> detailsList = new LinkedList<ProductDetails>();

    public ListProduct(String name, String brand, String addedBy){
        this.name = name;
        this.brand = brand;
        detailsList.add(new ProductDetails(addedBy));
    }

    @Override
    public List<ProductDetails> getChildItemList() {
        return detailsList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}
