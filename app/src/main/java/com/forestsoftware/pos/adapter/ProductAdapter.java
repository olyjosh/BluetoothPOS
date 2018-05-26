package com.forestsoftware.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    private final List<Product> productList;
    private ItemClickListener itemClickListener;
    //private final List<ViewModel>theViewList;
    private ViewGroup recyclerView;


    public ProductAdapter(Context context, List<Product> productList, ViewGroup tv) {
        this.context = context;
        this.productList = productList;
        recyclerView = tv;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            final Product product = productList.get(position);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.content_item_custom, null);
            gridView.setTag(product);
            // set value into textview
            TextView t1 = (TextView) gridView.findViewById(R.id.c1);
            TextView t2 = (TextView) gridView.findViewById(R.id.c2);


            t1.setText(product.getName());
            t2.setText(String.valueOf(product.getPrice()));


            final View finalGridView = gridView;
            gridView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.isAdded()) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.content_item_custom_2, null);
                        recyclerView.addView(rowView, recyclerView.getChildCount() - 1);

                        Product pr = (Product) finalGridView.getTag();


                        TextView t1 = (TextView) rowView.findViewById(R.id.article);
                        TextView t2 = (TextView) rowView.findViewById(R.id.amount);
                        TextView t3 = (TextView) rowView.findViewById(R.id.price);
                        TextView t4 = (TextView) rowView.findViewById(R.id.quantity);
                        TextView t5 = (TextView) rowView.findViewById(R.id.total);

                        t1.setText(pr.getName());
                        t2.setText(pr.getName());
                        t3.setText(pr.getName());
                        t4.setText(pr.getName());
                        t2.setText(pr.getName());
                    } else {
                        Product p;
                        for (int i = 0; i< productList.size(); i++) {
                            p= productList.get(i);
                            if(p.getId()==product.getId()){
                                recyclerView.removeViewAt(i);
                                break;
                            }
                        }

                    }


                }
            });


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
