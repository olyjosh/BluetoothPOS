package com.forestsoftware.pos.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.forestsoftware.pos.R;
import com.forestsoftware.pos.adapter.ItemClickListener;
import com.forestsoftware.pos.adapter.PAS;
import com.forestsoftware.pos.adapter.ProductAdapter;
import com.forestsoftware.pos.adapter.ProductCategoryAdapt;
import com.forestsoftware.pos.model.GeneralResponse;
import com.forestsoftware.pos.model.Product;
import com.forestsoftware.pos.model.ProductBase;
import com.forestsoftware.pos.model.ProductCategory;
import com.forestsoftware.pos.model.SubmitProductBase;
import com.forestsoftware.pos.rest.ApiClient;
import com.forestsoftware.pos.rest.ApiInterface;
import com.forestsoftware.pos.util.SessionManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HP-PC on 5/24/2018.
 */

public class SceneTwo extends AppCompatActivity implements ItemClickListener {
    private RecyclerView categoryRecyclerView;
    private ProductCategoryAdapt categoryRecycler;
    private List<String> horizontalList;
    private List<ProductCategory> pCategory;
    private List<String> pCategory2;
    private GridView gridView;
    ProductAdapter productAdapter;
    private List<Product> currentProductList;
    private RecyclerView parentLinearLayout;
    private ImageView menu_button, sync, logout;
    private TextView grand_total, grand_discount, overall_total;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private double grandTotal;
    private double discount;
    private boolean isPercentageDiscount;
    private List<Product> parsedProducts;
    private PAS pas;
    private Button submit1, submit2;
    String theVendorId = "";

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String name = intent.getStringExtra("name");
            String amount = intent.getStringExtra("name");
            String price = intent.getStringExtra("name");
            String total = intent.getStringExtra("name");
            String quantity = intent.getStringExtra("name");
            Log.wtf("things i got>>>>>>>>>", name);


            Toast.makeText(SceneTwo.this, name + " ", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_two);
        final String vendorId = getIntent().getExtras().getString("VENDOR_ID");
        theVendorId = vendorId;

        submit1 = (Button) findViewById(R.id.cash);
        submit2 = (Button) findViewById(R.id.cash_2);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));

        categoryRecyclerView = (RecyclerView) findViewById(R.id.category_recycler_view);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutmanager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.VERTICAL, false);

        categoryRecyclerView.setLayoutManager(verticalLayoutmanager);

        gridView = (GridView) findViewById(R.id.gridView1);
        avLoadingIndicatorView = new AVLoadingIndicatorView(SceneTwo.this);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loadingAnimation);
        avLoadingIndicatorView.setIndicatorColor(R.color.button_color);

        menu_button = (ImageView) findViewById(R.id.menu);
        grand_total = (TextView) findViewById(R.id.grand_total);
        grand_discount = (TextView) findViewById(R.id.grand_discount);
        overall_total = (TextView) findViewById(R.id.overal_total);

        sync = (ImageView) findViewById(R.id.sync);
        logout = (ImageView) findViewById(R.id.logout);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productAdapter.recyclerView.removeAllViews();
                currentProductList.clear();
                overall_total.setText("");
                grand_discount.setText("");
                grand_total.setText("");
                pas.getProducts().clear();


            }
        });
        gridView.setNumColumns(2);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SceneTwo.this, MainActivity.class));
                finish();
            }
        });


        LinearLayoutManager horizontalLayoutmanager2 = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.VERTICAL, false);

        parentLinearLayout = (RecyclerView) findViewById(R.id.parent_linear_layout);
        LinearLayoutManager manager = new LinearLayoutManager(SceneTwo.this, LinearLayoutManager.VERTICAL, false);

        parentLinearLayout.setLayoutManager(manager);
        pas = new PAS(SceneTwo.this, SceneTwo.this);


        parentLinearLayout.setAdapter(pas);

        productAdapter = new ProductAdapter(SceneTwo.this, null, parentLinearLayout, SceneTwo.this);
        doFetchProducts(vendorId);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SceneTwo.this, menu_button);

                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.discount) {
                            final Dialog dialog = new Dialog(SceneTwo.this);
                            final double[] calculatedDiscount = {0.0};


                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.custom_dialog_discount);

                            final boolean[] theTrue = {false};

                            final EditText perecntField = (EditText) dialog.findViewById(R.id.discount_field);
                            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    String s = perecntField.getText().toString();
                                    double dees = s.isEmpty() ? 0 : Double.valueOf(s);
                                    switch (checkedId) {
                                        case R.id.radio_percentage:
                                            isPercentageDiscount = true;
                                            theTrue[0] = true;
                                            Log.wtf("Value of true: ", "" + theTrue[0] + "and the value of total is" + "" + grand_total.getText());

                                            break;
                                        case R.id.radio_fixed_amount:
                                            isPercentageDiscount = false;
                                            theTrue[0] = false;
                                            Log.wtf("Value of true: ", "" + theTrue[0] + "and the value of total is" + "" + grand_total.getText());

                                            break;

                                    }
                                }
                            });


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

                                    String totu = removePound(grand_total.getText().toString());
                                    double total = totu.isEmpty() ? 0 : Double.valueOf(totu);
                                    String x1 = grand_total.getText().toString();
                                    String s = perecntField.getText().toString();
                                    double dees = s.isEmpty() ? 0 : Double.valueOf(s);
                                //    discount = x1.isEmpty() ? 0 : Double.valueOf(x1);

                                  //  double disc = discount;

                                    if (theTrue[0] == true) {
                                        double yutu = (dees / 100) * total;
                                        double t = total - yutu;
                                        Log.wtf("Yutu is: ", "" + yutu);
                                        Log.wtf("Value of true: ", "" + t);
                                        grand_discount.setText("" + dees + "%");
                                        overall_total.setText("" + t +"€");

                                    } else {
                                        double t = total - dees;
                                        Log.wtf("Value of false: ", "" + t);
                                        grand_discount.setText("" + dees);
                                        grand_total.setText("" + t);

                                    }

                                    dialog.dismiss();


                                }
                            });

                            dialog.show();
                        }

                        return true;
                    }
                });

                popup.show();

            }
        });

        currentProductList = new ArrayList<Product>();
        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = grand_total.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(SceneTwo.this, "Toatal can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    doCashExSubmit();

                }

            }
        });

        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = grand_total.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(SceneTwo.this, "Toatal can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    doCashSubmit();

                }


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
                    List<ProductCategory> p = new ArrayList<>();
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

                    productAdapter = new ProductAdapter(SceneTwo.this, propro, parentLinearLayout, SceneTwo.this);

                    Toast.makeText(SceneTwo.this, "Success", Toast.LENGTH_SHORT).show();

                    Log.wtf("Get Default Message: ", "" + response.code() + " And the >> is: " + response.body());

                } else {
                    if (response.code() == 401) {
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

    @Override
    public void onClick(List<Product> products) {
        parsedProducts = products;
        ProductAdapter productAdapter = new ProductAdapter(SceneTwo.this, parsedProducts, parentLinearLayout, SceneTwo.this);
        gridView.setAdapter(productAdapter);

    }

    @Override
    public void onClick(Product product, double disc, boolean isDiscountPercent) {
        pas.addOrRemove(product);
    }

    private void recalculateTotals(double grandTotal, double discount) {
        grand_total.setText("" + grandTotal + "€");
        grand_discount.setText("" + discount );
        double netTotal = grandTotal - discount;
        overall_total.setText("" + netTotal +"€");
    }

    @Override
    public void onTotalChange(double total) {
        recalculateTotals(total, discount);
    }

    @Override
    public void onPriceChange(double price) {

    }
//
//    @Override
//    public void onDiscountChange(double total) {
//        recalculateTotals(total, discount);
//    }


    public void doCashSubmit() {
        // Map products = pas.getProducts();

        String token = SessionManager.getTOKEN();
        Log.wtf("-----token----", "" + token);
        int venderId = Integer.valueOf(theVendorId);

        double total = Double.valueOf(grand_total.getText().toString());
        double discount = Double.valueOf(grand_discount.getText().toString());

        int price = (int) pas.calculatePrice();
        String payment = "Exact";
        int change = 0;
        List<Product> listproducts = pas.construct();

        Log.wtf("<<>>>>>>>", "-" + venderId + "-" + total + "-" + discount + "-" + price + "-" + payment + "-" + change + "-" + listproducts + "-");
        avLoadingIndicatorView.setVisibility(View.VISIBLE);

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<GeneralResponse> userCall = service.submit(new SubmitProductBase(venderId, total, discount, price, payment, change, listproducts));
        userCall.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {


                if (response.isSuccessful()) {
                    GeneralResponse generalResponse = response.body();
                    String success = generalResponse.getMessage();
                    Toast.makeText(SceneTwo.this, "" + success, Toast.LENGTH_SHORT).show();
                    Log.wtf("Body: ", "" + success);

                    avLoadingIndicatorView.setVisibility(View.GONE);

                } else {
                    Toast.makeText(SceneTwo.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    avLoadingIndicatorView.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                // hidepDialog();
                Log.wtf("onFailure", t.toString());
                avLoadingIndicatorView.setVisibility(View.GONE);

                Toast.makeText(SceneTwo.this, "there is another error", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void doCashExSubmit() {

        final Dialog dialog = new Dialog(SceneTwo.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_calculate_change);

        final EditText moneyDialog = (EditText) dialog.findViewById(R.id.money_dialog);
        final TextView amountDialog = (TextView) dialog.findViewById(R.id.amount_dialog);
        final TextView changeDialog = (TextView) dialog.findViewById(R.id.change_dialog);

        Button buttonClose = (Button) dialog.findViewById(R.id.btn_close_change);
        Button buttonSave = (Button) dialog.findViewById(R.id.btn_save_change);
        final double[] theNewChange = {0};

        changeDialog.setText("" + 0.0);

        moneyDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double money = Double.valueOf(moneyDialog.getText().toString());
                double amount = Double.valueOf(amountDialog.getText().toString());
                double totalChange = money - amount;
                changeDialog.setText("" + totalChange);
                theNewChange[0] = totalChange;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        amountDialog.setText("" + grand_total.getText());

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String token = SessionManager.getTOKEN();
                Log.wtf("-----token----", "" + token);
                int venderId = Integer.valueOf(theVendorId);
                double total = Double.valueOf(grand_total.getText().toString());
                double discount = Double.valueOf(grand_discount.getText().toString());

                int price = (int) pas.calculatePrice();
                String payment = "InExact";
                double change = theNewChange[0];

                List<Product> listproducts = pas.construct();

                Log.wtf("<<>>>>>>>", "-" + venderId + "-" + total + "-" + discount + "-" + price + "-" + payment + "-" + change + "-" + listproducts + "-");
                avLoadingIndicatorView.setVisibility(View.VISIBLE);

                ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
                Call<GeneralResponse> userCall = service.submit(new SubmitProductBase(venderId, total, discount, price, payment, change, listproducts));
                userCall.enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {


                        if (response.isSuccessful()) {

                            GeneralResponse generalResponse = response.body();
                            String success = generalResponse.getMessage();
                            Toast.makeText(SceneTwo.this, "" + success, Toast.LENGTH_SHORT).show();
                            Log.wtf("Body: ", "" + success);


                            avLoadingIndicatorView.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(SceneTwo.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                            avLoadingIndicatorView.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
                        // hidepDialog();
                        Log.wtf("onFailure", t.toString());
                        avLoadingIndicatorView.setVisibility(View.GONE);

                        Toast.makeText(SceneTwo.this, "there is another error", Toast.LENGTH_SHORT).show();


                    }
                });


                dialog.dismiss();

            }
        });

        dialog.show();


    }

    public String removePound(String pound)
    {
        if(pound != null && pound.length()> 0 &&pound.charAt(pound.length()-1) =='€')
        {
            pound = pound.substring(0, pound.length()-1);
        }
        return pound;
    }
}
