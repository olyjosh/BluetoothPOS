package com.forestsoftware.pos.adapter;

import android.view.ViewGroup;
import android.widget.GridView;

import com.forestsoftware.pos.model.Product;

import java.util.List;

/**
 * Created by HP-PC on 5/25/2018.
 */


public interface ItemClickListener
{
    void onClick(List<Product> products);
    void onClick(Product product);
    void onClickView(ViewGroup vg, Product gridproduct);
}