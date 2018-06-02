package com.forestsoftware.pos.adapter;

import com.forestsoftware.pos.model.Product;

import java.util.List;

/**
 * Created by HP-PC on 5/25/2018...
 */


public interface ItemClickListener
{
    void onClick(List<Product> products);
    void onClick(Product product, double disc, boolean isDiscountPercen);
    void onTotalChange(double total);
    void onPriceChange(double price);
}