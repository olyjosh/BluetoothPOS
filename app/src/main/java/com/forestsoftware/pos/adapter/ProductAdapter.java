package com.forestsoftware.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forestsoftware.pos.R;
import com.forestsoftware.pos.model.Product;

import java.util.List;

/**
 * Created by HP-PC on 5/24/2018.
 */

//public class ProductAdapter {
//}

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private final List<Product>productList;

    public ProductAdapter(Context context, List<Product>productList) {
        this.context = context;
        this.productList = productList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.content_item_custom, null);

            // set value into textview
            TextView t1 = (TextView) gridView.findViewById(R.id.c1);
            TextView t2 = (TextView) gridView.findViewById(R.id.c2);

            t1.setText(productList.get(position).getName());
            t2.setText(productList.get(position).getPrice());


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
