package com.forestsoftware.pos.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.forestsoftware.pos.R;
import com.forestsoftware.pos.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HP-PC on 5/24/2018.
 */

public class PAS extends RecyclerView.Adapter<PAS.MyViewHolder> {
    private ItemClickListener itemClickListener;

    public Map<Integer, Product> products;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, quan, total;


        public MyViewHolder(View view) {
            super(view);
            //buttonProduct = (Button) view.findViewById(R.id.category_button);
            name = (TextView) view.findViewById(R.id.article);
         //   amount = (TextView) view.findViewById(R.id.amount);
            price = (TextView) view.findViewById(R.id.price);
            quan = (TextView) view.findViewById(R.id.quantity);
            total = (TextView) view.findViewById(R.id.total);



        }
    }


    public PAS(Context ctx, ItemClickListener itemClickListener) {
        this.products = new LinkedHashMap<>();
        this.context = ctx;
        this.itemClickListener =itemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.content_item_custom_2, parent, false);


        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder,  int position) {

       // position = products.size() -(1+position);
        final Integer key = (Integer)products.keySet().toArray()[position];

        final Product product = products.get(key);
        final double price  = Double.valueOf(product.getPrice());
        int quantity = Integer.valueOf(product.getQuantity());
        final double[] amount = {price * quantity};

        holder.name.setText(""+ product.getName());
       // holder.amount.setText( amount[0]+" €");
        holder.price.setText(""+ product.getPrice() +" €");
        holder.quan.setText(""+ product.getQuantity());
        holder.total.setText(amount[0]+" €");


        holder.quan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_dialog_quantity);

                final EditText quantField = (EditText) dialog.findViewById(R.id.quantity_field);
                quantField.setText(""+ product.getQuantity());
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
                        int quantity = Integer.valueOf(quantField.getText().toString());
                        product.setQuantity(quantity);
                        products.put(key,product);
                        notifyDataSetChanged();
                        calculateTotal();


                        dialog.dismiss();

                    }
                });

                dialog.show();
            }

        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Map getProducts() {
        return products;
    }


    public void addOrRemove(Product product){

        if(products.containsKey(product.getId())){
             products.remove(product.getId());
        }else{
            products.put(product.getId(),product);
        }
        notifyDataSetChanged();
        calculateTotal();
    }

 double theTotal = 0;
    public void calculateTotal(){

        double tot=0;
        Product product;
        for (Map.Entry<Integer, Product> e: products.entrySet()) {
            product =e.getValue();
            tot += product.getPrice() * product.getQuantity();
            theTotal = tot;
        }
        itemClickListener.onTotalChange(tot);
    }
    public double calculatePrice(){

        double price=0;
        Product product;
        for (Map.Entry<Integer, Product> e: products.entrySet()) {
            product =e.getValue();
            price += product.getPrice();
        }
        itemClickListener.onPriceChange(price);
        return price;

    }

    public List<Product> construct(){
        List<Product>addedProducts = new ArrayList<>(products.values());

        return addedProducts;
    }

    public void calculateTotalDiscount(double discount){

       theTotal -= discount;
    }

}