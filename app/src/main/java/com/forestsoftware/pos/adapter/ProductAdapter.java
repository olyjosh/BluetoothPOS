package com.forestsoftware.pos.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private List<Product> productList;
    private ItemClickListener itemClickListener;
    public ViewGroup recyclerView;



    public ProductAdapter(Context context, List<Product> productList, ViewGroup tv, ItemClickListener itemClickListener) {
        this.context = context;
        this.productList = productList;
        recyclerView = tv;
        this.itemClickListener = itemClickListener;
    }

    public void resetData(List<Product> products){
        recyclerView.removeAllViews();
        this.productList = products;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);
            final Product product = productList.get(position);
            gridView = inflater.inflate(R.layout.content_item_custom, null);
            gridView.setTag(product);
            TextView t1 = (TextView) gridView.findViewById(R.id.c1);
            TextView t2 = (TextView) gridView.findViewById(R.id.c2);


            t1.setText(product.getName());
            t2.setText(String.valueOf(product.getPrice()));

            Intent intent = new Intent("custom-message");

            intent.putExtra("name", product.getName().toString());
            intent.putExtra("name", product.getName().toString());
            intent.putExtra("name", product.getName().toString());
            intent.putExtra("name", product.getName().toString());
            intent.putExtra("name", product.getName().toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            final View finalGridView = gridView;
            gridView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemClickListener.onClick(product, 0, false);
                }
            });


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }
    public void doAddView(View v, Product product)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.content_item_custom_2, null);
        recyclerView.addView(rowView, recyclerView.getChildCount() - 1);

        final Product pr = (Product) v.getTag();

        final int[] quantity = {1};


        TextView name = (TextView) rowView.findViewById(R.id.article);
       // final TextView amount = (TextView) rowView.findViewById(R.id.amount);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        final TextView quan = (TextView) rowView.findViewById(R.id.quantity);
        final TextView total = (TextView) rowView.findViewById(R.id.total);

        final int thePrice = pr.getPrice();
        final int[] amnt = {thePrice * quantity[0]};

        name.setText(pr.getName());
        //amount.setText(String.valueOf(amnt[0]));
        price.setText(String.valueOf(pr.getPrice())  + "€");
        quan.setText(String.valueOf(quantity[0]));
//        total.setText(String.valueOf(amount.getText()) + "€");
        product.setAdded(true);


        Intent intent = new Intent("custom-message");
        intent.putExtra("quantity", total.getText().toString());
        //  intent.putExtra("item",ItemName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        quan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_dialog_quantity);

                final EditText quantField = (EditText) dialog.findViewById(R.id.quantity_field);
                quantField.setText(""+pr.getQuantity());
                Button buttonClose = (Button) dialog.findViewById(R.id.btn_close);
                Button buttonSave = (Button) dialog.findViewById(R.id.btn_save);

                buttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quan.setText(String.valueOf(quantField.getText()));
                        quantity[0] = Integer.valueOf(quantField.getText().toString());
                        amnt[0] = thePrice * quantity[0];
                        //amount.setText(String.valueOf(amnt[0]));
                        total.setText(String.valueOf(amnt[0]) + "€");
                        pr.setQuantity(quantity[0]);


                        dialog.dismiss();

                    }
                });

                dialog.show();
            }

        });
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

    public void saveQuantity()
    {

    }
}
