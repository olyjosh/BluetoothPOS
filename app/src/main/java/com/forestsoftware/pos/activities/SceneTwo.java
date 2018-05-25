package com.forestsoftware.pos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.forestsoftware.pos.R;
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

/**
 * Created by HP-PC on 5/24/2018.
 */

public class SceneTwo extends AppCompatActivity implements ProductCategoryAdapt.ItemClickListener
{
    private RecyclerView categoryRecyclerView;
    private ProductCategoryAdapt categoryRecycler;
    private List<String>horizontalList;
    private List<com.forestsoftware.pos.model.ProductCategory>pCategory;
    private List<String>pCategory2;
    private GridView gridView;
    ProductAdapter productAdapter;
    private List<Product>productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_two);
        categoryRecyclerView= (RecyclerView) findViewById(R.id.category_recycler_view);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(verticalLayoutmanager);

        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setNumColumns(2);




        horizontalList=new ArrayList<>();
        horizontalList.add("horizontal 1");
        horizontalList.add("horizontal 2");
        horizontalList.add("horizontal 3");
        horizontalList.add("horizontal 4");
        horizontalList.add("horizontal 5");
        horizontalList.add("horizontal 6");
        horizontalList.add("horizontal 7");
        horizontalList.add("horizontal 8");
        horizontalList.add("horizontal 9");
        horizontalList.add("horizontal 10");

      //  categoryRecycler = new ProductCategoryAdapt( horizontalList,SceneTwo.this);
       // categoryRecyclerView.setAdapter(categoryRecycler);



        String value = getIntent().getExtras().getString("VENDOR_ID");
        doFetchProducts(value);
      //  doFetchProductsCategory(value);
    }


    public void doFetchProducts(String vendorId) {

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<ProductBase> userCall = service.getVendor(vendorId);
        userCall.enqueue(new Callback<ProductBase>() {
            @Override
            public void onResponse(Call<ProductBase> call, Response<ProductBase> response) {

                if (response.isSuccessful())
                {
                   ProductBase productBase = response.body();

                   List<com.forestsoftware.pos.model.ProductCategory>p = new ArrayList<>();
                    List<Product>propro = new ArrayList<>();
                   p = productBase.getProductCategories();

                   List<String>op =new ArrayList<>();
                   op.add(productBase.getProductCategories().get(0).getName());
                 //  propro.add(p.get(0).getName());

                   //ProductCategoryAdapt productCategory = p.get(0).getName();

                   List<Product>pp = new ArrayList<>();
                   pp = p.get(0).getProducts();

                   String pro = pp.get(0).getName();



                     pCategory = p;
//                     pCategory2.add(p.get(2).getName());
                    //pCategory2.add(p.get(0).getProducts().get(0).getName());

                    categoryRecycler = new ProductCategoryAdapt( productBase.getProductCategories(),SceneTwo.this);
                    categoryRecyclerView.setAdapter(categoryRecycler);

                    categoryRecycler.setItemClickListener(SceneTwo.this);

                    productList = new ArrayList<>();
                    productAdapter = new ProductAdapter(SceneTwo.this,propro);



                    String m = p.get(1).getName();

                   // String n = productBase.get;
                  //  ProductCategoryAdapt p = response.body();

                 //   String n = p.getName();
                    //ProductCategoryAdapt p = pCategory.get


                    Toast.makeText(SceneTwo.this, "Name is: "+m, Toast.LENGTH_SHORT).show();

                    Log.wtf("Get Default Message: ", "" + response.code() + " And the >> is: "+response.body());

                }
                else {
                    if (response.code() == 401)
                    {

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
                Toast.makeText(SceneTwo.this, "there is another error", Toast.LENGTH_SHORT).show();


            }
        });
    }
List<Product>por ;
    @Override
    public void onClick(List<Product> products)
    {
        ProductAdapter productAdapter = new ProductAdapter(SceneTwo.this,products);
        gridView.setAdapter(productAdapter);

        por = products;
        Log.wtf("????????????","///"+por);
    }

    @Override
    public void onClick(Product product) {

    }
}
