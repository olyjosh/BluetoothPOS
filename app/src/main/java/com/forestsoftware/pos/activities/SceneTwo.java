package com.forestsoftware.pos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forestsoftware.pos.R;
import com.forestsoftware.pos.adapter.ItemClickListener;
import com.forestsoftware.pos.adapter.ProductCategoryAdapt;
import com.forestsoftware.pos.model.Product;
import com.forestsoftware.pos.model.ProductBase;
import com.forestsoftware.pos.model.ProductCategory;
import com.forestsoftware.pos.rest.ApiClient;
import com.forestsoftware.pos.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.forestsoftware.pos.adapter.ProductAdapter;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by HP-PC on 5/24/2018.
 */

public class SceneTwo extends AppCompatActivity implements ItemClickListener {
    private RecyclerView categoryRecyclerView;
    private ProductCategoryAdapt categoryRecycler;
    private List<String> horizontalList;
    private List<com.forestsoftware.pos.model.ProductCategory> pCategory;
    private List<String> pCategory2;
    private GridView gridView;
    ProductAdapter productAdapter;
    private List<Product> productList;
    private LinearLayout parentLinearLayout;
    private ImageView menu_button, sync,logout;

    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_two);
        final String value = getIntent().getExtras().getString("VENDOR_ID");

        categoryRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutmanager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.VERTICAL, false);

        categoryRecyclerView.setLayoutManager(verticalLayoutmanager);

        gridView = (GridView) findViewById(R.id.gridView1);
        avLoadingIndicatorView = new AVLoadingIndicatorView(SceneTwo.this);
        avLoadingIndicatorView = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadingAnimation);
        avLoadingIndicatorView.setIndicatorColor(R.color.button_color);

        menu_button = (ImageView) findViewById(R.id.menu);
        sync = (ImageView) findViewById(R.id.sync);
        logout= (ImageView)findViewById(R.id.logout);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync.setClickable(false);
                doFetchProducts(value);
                sync.setClickable(true);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SceneTwo.this, MainActivity.class));
                finish();
            }
        });


        gridView.setNumColumns(2);

        //  relativeLayout = (RecyclerView) findViewById(R.id.the_relative);
        //   relativeLayout.setLayoutManager(horizontalLayoutmanager);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

        productAdapter = new ProductAdapter(SceneTwo.this, null, parentLinearLayout);


        doFetchProducts(value);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SceneTwo.this, menu_button);

                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                SceneTwo.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });


                popup.show();

            }
        });
    }


    public void doFetchProducts(String vendorId) {

        avLoadingIndicatorView.setVisibility(View.VISIBLE);

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<ProductBase> userCall = service.getVendor(vendorId);
        userCall.enqueue(new Callback<ProductBase>() {
            @Override
            public void onResponse(Call<ProductBase> call, Response<ProductBase> response) {

                if (response.isSuccessful()) {
                    avLoadingIndicatorView.setVisibility(View.GONE);

                    ProductBase productBase = response.body();
                    List<com.forestsoftware.pos.model.ProductCategory> p = new ArrayList<>();
                    List<Product> propro = new ArrayList<>();
                    p = productBase.getProductCategories();

                    List<String> op = new ArrayList<>();

                    List<Product> pp = new ArrayList<>();
                    pp = p.get(0).getProducts();

                    String pro = pp.get(0).getName();


                    pCategory = p;


                    categoryRecycler = new ProductCategoryAdapt(productBase.getProductCategories(), SceneTwo.this);
                    categoryRecyclerView.setAdapter(categoryRecycler);

                    categoryRecycler.setItemClickListener(SceneTwo.this);

                    productList = new ArrayList<>();
                    productAdapter = new ProductAdapter(SceneTwo.this, propro, parentLinearLayout);


//                    String m = p.get(1).getName();


                    Toast.makeText(SceneTwo.this, "Success", Toast.LENGTH_SHORT).show();

                    Log.wtf("Get Default Message: ", "" + response.code() + " And the >> is: " + response.body());

                } else {
                    if (response.code() == 401)
                    {
                        avLoadingIndicatorView.setVisibility(View.GONE);

                        Toast.makeText(SceneTwo.this, "401", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(SceneTwo.this, "there is another error", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ProductBase> call, Throwable t) {
                // hidepDialog();
                Log.wtf("onFailure", t.toString());
                avLoadingIndicatorView.setVisibility(View.VISIBLE);

                Toast.makeText(SceneTwo.this, "there is another error", Toast.LENGTH_SHORT).show();


            }
        });
    }

    List<Product> por;

    @Override
    public void onClick(List<Product> products) {
        ProductAdapter productAdapter = new ProductAdapter(SceneTwo.this, products, parentLinearLayout);
        gridView.setAdapter(productAdapter);

        por = products;
        Log.wtf("????????????", "///" + por);
    }

    @Override
    public void onClick(Product product) {

    }

    @Override
    public void onClickView(ViewGroup vg, Product gridproduct) {

    }


}
