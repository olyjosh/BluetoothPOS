package com.forestsoftware.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.forestsoftware.pos.R;
import com.forestsoftware.pos.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-PC on 5/24/2018.
 */

//public class ProductCategoryAdapt {
//}
public class ProductCategoryAdapt extends RecyclerView.Adapter<ProductCategoryAdapt.MyViewHolder>
{
    private ItemClickListener itemClickListener;

    private List<Product>product;
    private List<com.forestsoftware.pos.model.ProductCategory> category;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (Button) view.findViewById(R.id.category_button);

        }
    }


    public ProductCategoryAdapt(List<com.forestsoftware.pos.model.ProductCategory> horizontalList, Context ctx) {
        this.category = horizontalList;
        this.context = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recyclerview_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtView.setText(category.get(position).getName());

        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                itemClickListener.onClick(category.get(position).getProducts());
                Toast.makeText(context,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onClick(List<Product> products);
        void onClick(Product product);
    }

}