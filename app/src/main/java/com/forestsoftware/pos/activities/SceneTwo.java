package com.forestsoftware.pos.activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zj.com.cn.bluetooth.sdk.BluetoothService;
import zj.com.cn.bluetooth.sdk.DeviceListActivity;
import zj.com.command.sdk.Command;
import zj.com.command.sdk.PrintPicture;
import zj.com.command.sdk.PrinterCommand;
import zj.com.customize.sdk.Other;


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
    private ImageView menu_button, sync, logout, print;
    private TextView grand_total, grand_discount, overall_total;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private double grandTotal;
    private double discount;
    private boolean isPercentageDiscount;
    private List<Product> parsedProducts;
    private PAS pas;
    private Button submit1, submit2;
    String theVendorId = "";
    private boolean isAlreadyDiscounted = false;
    private boolean optionISChecked = false;
    private static NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRENCH);


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
        print = (ImageView) findViewById(R.id.print);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productAdapter.recyclerView.removeAllViews();
                currentProductList.clear();
                overall_total.setText("");
                grand_discount.setText("");
                grand_total.setText("");
                pas.getProducts().clear();
                isAlreadyDiscounted = false;
                optionISChecked = false;


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

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTable = true;

//                printTableTest();
                printTry();
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
                            final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup1);

                            for (int i = 0; i < radioGroup.getChildCount(); i++) {

                                if (isAlreadyDiscounted == false) {
                                    //Todo && optionISChecked == true
                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            String s = perecntField.getText().toString();
                                            double dees = s.isEmpty() ? 0 : Double.valueOf(s);
                                            switch (checkedId) {
                                                case R.id.radio_percentage:
                                                    isPercentageDiscount = true;
                                                    //  checkDiscounted(radioGroup);
                                                    theTrue[0] = true;
                                                    Log.wtf("Value of true: ", "" + theTrue[0] + "and the value of total is" + "" + grand_total.getText());

                                                    break;
                                                case R.id.radio_fixed_amount:
                                                    isPercentageDiscount = false;
                                                    //  checkDiscounted(radioGroup);
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

                                            if (s.isEmpty() && optionISChecked == false) {
                                                Toast.makeText(SceneTwo.this, "Discount is nothing", Toast.LENGTH_SHORT).show();

                                            } else {
                                                if (theTrue[0] == true) {
                                                    // isAlreadyDiscounted = true;
                                                    optionISChecked = true;
                                                    double yutu = (dees / 100) * total;
                                                    double t = total - yutu;
                                                    Log.wtf("Yutu is: ", "" + yutu);
                                                    Log.wtf("Value of true: ", "" + t);
                                                    grand_discount.setText("" + dees + "%");
                                                    overall_total.setText("" + t + "€");
                                                    isAlreadyDiscounted = true;

                                                } else {
                                                    optionISChecked = true;
                                                    double t = total - dees;
                                                    Log.wtf("Value of false: ", "" + t);
                                                    grand_discount.setText("" + dees + "€");
                                                    grand_total.setText("" + getCurrencyFormat(t));
                                                    overall_total.setText("" + getCurrencyFormat(t) + "€");
                                                    // new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));


                                                    isAlreadyDiscounted = true;

//                                                    dialog.dismiss();
                                                }
                                                dialog.dismiss();

                                            }


                                        }
                                    });

                                    dialog.show();
                                    // isAlreadyDiscounted = true;
                                    // radioGroup.getChildAt(i).setEnabled(false);


                                } else {
                                    Toast.makeText(SceneTwo.this, "Discount already taken", Toast.LENGTH_SHORT).show();
//                                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                    radioGroup.getChildAt(i).setEnabled(true);
//                                }
                                }

                            }
                        }
//                        return isAlreadyDiscounted;
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
                    Toast.makeText(SceneTwo.this, "Total can not be empty", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SceneTwo.this, "Total can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    doCashSubmit();

                }


            }
        });

        nf.setGroupingUsed(true);
        doBlutoothInitialization();


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
        grand_total.setText(grandTotal + "€");
        grand_discount.setText("" + discount);
        double netTotal = grandTotal - discount;
        overall_total.setText("" + netTotal + "€");
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

        double total = Double.valueOf(removePound(grand_total.getText().toString()));
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
                double amount = Double.valueOf(removePound(amountDialog.getText().toString()));
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
                double total = Double.valueOf(removePound(grand_total.getText().toString()));
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

    public String removePound(String pound) {
        if (pound != null && pound.length() > 0 && pound.charAt(pound.length() - 1) == '€') {
            pound = pound.substring(0, pound.length() - 1);
        }
        return pound;
    }

    public void checkDiscounted(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public static String getCurrencyFormat(double value) {
        return nf.format(value) + "€";
    }


    /***********************************************************************************************
     * Printing code
     **********************************************************************************************/
    private boolean isTable;

    private void doBlutoothInitialization() {
//        setTitle(R.string.app_title);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void printTry() {
        Intent serverIntent = new Intent(SceneTwo.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }


    private void printTableTest() {
        SendDataByte(Command.ESC_Init);
        SendDataByte(Command.LF);
        try {
            printTable2();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void printTable2() throws UnsupportedEncodingException {
        Map<Integer, Product> products = pas.getProducts();
        if (products.isEmpty()) {
            Toast.makeText(this, "No content to print", Toast.LENGTH_LONG).show();
            return;
        }

        List<byte[]> lines = new ArrayList<byte[]>();
        final byte[] sep = String.format("━━━━━━━━━━━━━━━━\n").getBytes();
        final byte[] title = String.format("┃    ┃%-4s┃    ┃%-3s┃    ┃%-3s┃    ┃%-4s┃\n", "Article", "Price", "Quantity", "Total").getBytes("GBK");

        int len = 0;
        lines.add(Command.ESC_Init);
        len = Command.ESC_Init.length > len ? Command.ESC_Init.length : len;
        lines.add(Command.ESC_Three);
        len = Command.ESC_Three.length > len ? Command.ESC_Three.length : len;
        lines.add(sep);
        len = sep.length > len ? sep.length : len;
        lines.add(title);
        len = title.length > len ? title.length : len;
        lines.add(sep);
        //len = bytes.length > len ? bytes.length : len;



        for (Product product : products.values()) {
            byte[] bytes = String.format("┃    ┃%-4s┃    ┃%-3s┃    ┃%-3s┃    ┃%-4s┃\n",
                    product.getName(), product.getPrice() + "€", product.getQuantity(),
                    (product.getQuantity() * product.getPrice()) + "€").getBytes("GBK");
            len = bytes.length > len ? bytes.length : len;

            lines.add(bytes);
        }

        Log.wtf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", ""+len);

        lines.add(Command.ESC_Align);
        len = Command.ESC_Align.length > len ? Command.ESC_Align.length : len;
        byte[] newLine = "\n".getBytes("GBK");
        len = newLine.length > len ? newLine.length : len;
        lines.add(newLine);
        Log.wtf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2", ""+len);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String date = str + "\n\n\n\n\n\n";
        if (is58mm) {

            Command.ESC_Align[2] = 0x02;
            byte[][] allbuf = new byte[lines.size()][len];

            for (int i = 0; i < lines.size(); i++) {
                byte[] by = lines.get(i);

                System.out.println("---------------------------"+i);

                for (int j = 0; j < by.length; j++) {
                    allbuf[i][j] = by[j];
                }
            }

            byte[] buf = Other.byteArraysToBytes(allbuf);
            SendDataByte(buf);
            SendDataString(date);
            SendDataByte(Command.GS_V_m_n);
        }
//        else {
//
//            Command.ESC_Align[2] = 0x02;
//            byte[][] allbuf;
//            try {
//                allbuf = new byte[][]{
//
//                        Command.ESC_Init, Command.ESC_Three,
//                        String.format("┏━━┳━━━━━━━┳━━┳━━━━━━━━┓\n").getBytes("GBK"),
//                        String.format("┃XXXX┃%-14s┃XXXX┃%-16s┃\n", "XXXX", "XXXX").getBytes("GBK"),
//                        String.format("┣━━╋━━━━━━━╋━━╋━━━━━━━━┫\n").getBytes("GBK"),
//                        String.format("┃XXXX┃%6d/%-7d┃XXXX┃%-16d┃\n", 1, 222, 55555555).getBytes("GBK"),
//                        String.format("┣━━┻┳━━━━━━┻━━┻━━━━━━━━┫\n").getBytes("GBK"),
//                        String.format("┃XXXXXX┃%-34s┃\n", "【XX】XXXX/XXXXXX").getBytes("GBK"),
//                        String.format("┣━━━╋━━━━━━┳━━┳━━━━━━━━┫\n").getBytes("GBK"),
//                        String.format("┃XXXXXX┃%-12s┃XXXX┃%-16s┃\n", "XXXX", "XXXX").getBytes("GBK"),
//                        String.format("┗━━━┻━━━━━━┻━━┻━━━━━━━━┛\n").getBytes("GBK"),
//                        Command.ESC_Align, "\n".getBytes("GBK")
//                };
//                byte[] buf = Other.byteArraysToBytes(allbuf);
//                SendDataByte(buf);
//                SendDataString(date);
//                SendDataByte(Command.GS_V_m_n);
//            } catch (UnsupportedEncodingException e) {
//                // TODO 自动生成的 catch 块
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * POS PRINTING CODE AS IMPORTED
     */

    /******************************************************************************************************/
    // Debugging
    private static final String TAG = "SceneTwo";
    private static final boolean DEBUG = true;
    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHOSE_BMP = 3;
    private static final int REQUEST_CAMER = 4;

    //QRcode
    private static final int QR_WIDTH = 350;
    private static final int QR_HEIGHT = 350;
    /*******************************************************************************************************/
    private static final String CHINESE = "GBK";
    private static final String THAI = "CP874";
    private static final String KOREAN = "EUC-KR";
    private static final String BIG5 = "BIG5";

    /*********************************************************************************/
    private TextView mTitle;
    EditText editText;
    ImageView imageViewPicture;
    private static boolean is58mm = true;
    private RadioButton width_58mm, width_80;
    private RadioButton thai, big5, Simplified, Korean;
    private CheckBox hexBox;
    private Button sendButton = null;
    private Button testButton = null;
    private Button printbmpButton = null;
    private Button btnScanButton = null;
    private Button btnClose = null;
    private Button btn_BMP = null;
    private Button btn_ChoseCommand = null;
    private Button btn_prtsma = null;
    private Button btn_prttableButton = null;
    private Button btn_prtcodeButton = null;
    private Button btn_scqrcode = null;
    private Button btn_camer = null;

    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

    /***************************   指                 令****************************************************************/
//    final String[] items = {"复位打印机", "打印并走纸", "标准ASCII字体", "压缩ASCII字体", "正常大小",
//            "二倍高倍宽", "三倍高倍宽", "四倍高倍宽", "取消加粗模式", "选择加粗模式", "取消倒置打印", "选择倒置打印", "取消黑白反显", "选择黑白反显",
//            "取消顺时针旋转90°", "选择顺时针旋转90°", "走纸到切刀位置并切纸", "蜂鸣指令", "标准钱箱指令",
//            "实时弹钱箱指令", "进入字符模式", "进入中文模式", "打印自检页", "禁止按键", "取消禁止按键",
//            "设置汉字字符下划线", "取消汉字字符下划线", "进入十六进制模式"};
//    final String[] itemsen = {"Print Init", "Print and Paper", "Standard ASCII font", "Compressed ASCII font", "Normal size",
//            "Double high power wide", "Twice as high power wide", "Three times the high-powered wide", "Off emphasized mode", "Choose bold mode", "Cancel inverted Print", "Invert selection Print", "Cancel black and white reverse display", "Choose black and white reverse display",
//            "Cancel rotated clockwise 90 °", "Select the clockwise rotation of 90 °", "Feed paper Cut", "Beep", "Standard CashBox",
//            "Open CashBox", "Char Mode", "Chinese Mode", "Print SelfTest", "DisEnable Button", "Enable Button",
//            "Set Underline", "Cancel Underline", "Hex Mode"};
    final byte[][] byteCommands = {
            {0x1b, 0x40, 0x0a},// 复位打印机
            {0x0a}, //打印并走纸
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x11},// 宽高加倍
            {0x1d, 0x21, 0x22},// 宽高加倍
            {0x1d, 0x21, 0x33},// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
            {0x0a, 0x1d, 0x56, 0x42, 0x01, 0x0a},//切刀指令
            {0x1b, 0x42, 0x03, 0x03},//蜂鸣指令
            {0x1b, 0x70, 0x00, 0x50, 0x50},//钱箱指令
            {0x10, 0x14, 0x00, 0x05, 0x05},//实时弹钱箱指令
            {0x1c, 0x2e},// 进入字符模式
            {0x1c, 0x26}, //进入中文模式
            {0x1f, 0x11, 0x04}, //打印自检页
            {0x1b, 0x63, 0x35, 0x01}, //禁止按键
            {0x1b, 0x63, 0x35, 0x00}, //取消禁止按键
            {0x1b, 0x2d, 0x02, 0x1c, 0x2d, 0x02}, //设置下划线
            {0x1b, 0x2d, 0x00, 0x1c, 0x2d, 0x00}, //取消下划线
            {0x1f, 0x11, 0x03}, //打印机进入16进制模式
    };
    /***************************条                          码***************************************************************/
    final String[] codebar = {"UPC_A", "UPC_E", "JAN13(EAN13)", "JAN8(EAN8)",
            "CODE39", "ITF", "CODABAR", "CODE93", "CODE128", "QR Code"};
    final byte[][] byteCodebar = {
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x40},// 复位打印机
    };

    /******************************************************************************************************/


    @Override
    public void onStart() {
        super.onStart();

        // If Bluetooth is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mService == null)
                KeyListenerInit();//监听
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mService != null) {

            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mService.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (DEBUG)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
        if (DEBUG)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    /*****************************************************************************************************/
    private void KeyListenerInit() {
        mService = new BluetoothService(this, mHandler);
    }

    /*****************************************************************************************************/
    /*
     * SendDataString
	 */
    private void SendDataString(String data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }

    /****************************************************************************************************/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setTitle(R.string.title_connected_to);
                            setTitle(getTitle() + " " + mConnectedDeviceName);

                            if (isTable)
                                printTableTest();
                            else
                                Print_Test();//
                            break;
                        case BluetoothService.STATE_CONNECTING:
//					mTitle.setText(R.string.title_connecting);
                            setTitle(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
//					mTitle.setText(R.string.title_not_connected);
                            setTitle(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    editText.setEnabled(false);
                    imageViewPicture.setEnabled(false);
                    width_58mm.setEnabled(false);
                    width_80.setEnabled(false);
                    hexBox.setEnabled(false);
                    sendButton.setEnabled(false);
                    testButton.setEnabled(false);
                    printbmpButton.setEnabled(false);
                    btnClose.setEnabled(false);
                    btn_BMP.setEnabled(false);
                    btn_ChoseCommand.setEnabled(false);
                    btn_prtcodeButton.setEnabled(false);
                    btn_prtsma.setEnabled(false);
                    btn_prttableButton.setEnabled(false);
                    btn_camer.setEnabled(false);
                    btn_scqrcode.setEnabled(false);
                    Simplified.setEnabled(false);
                    Korean.setEnabled(false);
                    big5.setEnabled(false);
                    thai.setEnabled(false);
                    break;
                case MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            case REQUEST_CHOSE_BMP: {
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaColumns.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(picturePath, opts);
                    opts.inJustDecodeBounds = false;
                    if (opts.outWidth > 1200) {
                        opts.inSampleSize = opts.outWidth / 1200;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opts);
                    if (null != bitmap) {
                        imageViewPicture.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.msg_statev1), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CAMER: {
                if (resultCode == Activity.RESULT_OK) {
                    handleSmallCameraPhoto(data);
                } else {
                    Toast.makeText(this, getText(R.string.camer), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

/****************************************************************************************************/
    /**
     * 连接成功后打印测试页
     */
    private void Print_Test() {
        String lang = getString(R.string.strLang);
        if ((lang.compareTo("en")) == 0) {
            String msg = "Congratulations!\n\n";
            String data = "You have sucessfully created communications between your device and our bluetooth printer.\n"
                    + "  the company is a high-tech enterprise which specializes" +
                    " in R&D,manufacturing,marketing of thermal printers and barcode scanners.\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, CHINESE, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, CHINESE, 0, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        } else if ((lang.compareTo("cn")) == 0) {
            String msg = "恭喜您!\n\n";
            String data = "您已经成功的连接上了我们的便携式蓝牙打印机！\n我们公司是一家专业从事研发，生产，销售商用票据打印机和条码扫描设备于一体的高科技企业.\n\n\n\n\n\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, CHINESE, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, CHINESE, 0, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        } else if ((lang.compareTo("hk")) == 0) {
            String msg = "恭喜您!\n";
            String data = "您已經成功的連接上了我們的便攜式藍牙打印機！ \n我們公司是一家專業從事研發，生產，銷售商用票據打印機和條碼掃描設備於一體的高科技企業.\n\n\n\n\n\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, BIG5, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, BIG5, 0, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        } else if ((lang.compareTo("kor")) == 0) {
            String msg = "축하 해요!\n";
            String data = "성공적으로 우리의 휴대용 블루투스 프린터에 연결 한! \n우리는 하이테크 기업 중 하나에서 개발, 생산 및 상업 영수증 프린터와 바코드 스캐닝 장비 판매 전문 회사입니다.\n\n\n\n\n\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, KOREAN, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, KOREAN, 0, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        } else if ((lang.compareTo("thai")) == 0) {
            String msg = "ขอแสดงความยินดี!\n";
            String data = "คุณได้เชื่อมต่อกับบลูทู ธ เครื่องพิมพ์แบบพกพาของเรา! \n เราเป็น บริษัท ที่มีความเชี่ยวชาญในการพัฒนา, การผลิตและการขายของเครื่องพิมพ์ใบเสร็จรับเงินและการสแกนบาร์โค้ดอุปกรณ์เชิงพาณิชย์ในหนึ่งในองค์กรที่มีเทคโนโลยีสูง.\n\n\n\n\n\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, THAI, 255, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, THAI, 255, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        }
    }


    /*
     * 打印图片
     */
    private void Print_BMP() {

        //	byte[] buffer = PrinterCommand.POS_Set_PrtInit();
        Bitmap mBitmap = ((BitmapDrawable) imageViewPicture.getDrawable())
                .getBitmap();
        int nMode = 0;
        int nPaperWidth = 384;
        if (width_58mm.isChecked())
            nPaperWidth = 384;
        else if (width_80.isChecked())
            nPaperWidth = 576;
        if (mBitmap != null) {
            /**
             * Parameters:
             * mBitmap  要打印的图片
             * nWidth   打印宽度（58和80）
             * nMode    打印模式
             * Returns: byte[]
             */
            byte[] data = PrintPicture.POS_PrintBMP(mBitmap, nPaperWidth, nMode);
            //	SendDataByte(buffer);
            SendDataByte(Command.ESC_Init);
            SendDataByte(Command.LF);
            SendDataByte(data);
            SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        }
    }


    /**
     * public static Bitmap createAppIconText(Bitmap icon, String txt, boolean is58mm, int hight)
     * Bitmap  icon     源图
     * String txt       要转换的字符串
     * boolean is58mm   打印宽度(58和80)
     * int hight        转换后的图片高度
     */
    private void GraphicalPrint() {

        String txt_msg = editText.getText().toString();
        if (txt_msg.length() == 0) {
            Toast.makeText(SceneTwo.this, getText(R.string.empty1), Toast.LENGTH_SHORT).show();
            return;
        } else {
            Bitmap bm1 = getImageFromAssetsFile("demo.jpg");
            if (width_58mm.isChecked()) {

                Bitmap bmp = Other.createAppIconText(bm1, txt_msg, 25, is58mm, 200);
                int nMode = 0;
                int nPaperWidth = 384;

                if (bmp != null) {
                    byte[] data = PrintPicture.POS_PrintBMP(bmp, nPaperWidth, nMode);
                    SendDataByte(Command.ESC_Init);
                    SendDataByte(Command.LF);
                    SendDataByte(data);
                    SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
                    SendDataByte(PrinterCommand.POS_Set_Cut(1));
                    SendDataByte(PrinterCommand.POS_Set_PrtInit());
                }
            } else if (width_80.isChecked()) {
                Bitmap bmp = Other.createAppIconText(bm1, txt_msg, 25, false, 200);
                int nMode = 0;

                int nPaperWidth = 576;
                if (bmp != null) {
                    byte[] data = PrintPicture.POS_PrintBMP(bmp, nPaperWidth, nMode);
                    SendDataByte(Command.ESC_Init);
                    SendDataByte(Command.LF);
                    SendDataByte(data);
                    SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
                    SendDataByte(PrinterCommand.POS_Set_Cut(1));
                    SendDataByte(PrinterCommand.POS_Set_PrtInit());
                }
            }
        }
    }

    /**
     * 打印指令测试
     */
//    private void CommandTest() {
//
//        String lang = getString(R.string.strLang);
//        if ((lang.compareTo("cn")) == 0) {
//            new AlertDialog.Builder(SceneTwo.this).setTitle(getText(R.string.chosecommand))
//                    .setItems(items, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            SendDataByte(byteCommands[which]);
//                            try {
//                                if (which == 16 || which == 17 || which == 18 || which == 19 || which == 22
//                                        || which == 23 || which == 24 || which == 0 || which == 1 || which == 27) {
//                                    return;
//                                } else {
//                                    SendDataByte("热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n热敏票据打印机ABCDEFGabcdefg123456,.;'/[{}]!\n".getBytes("GBK"));
//                                }
//
//                            } catch (UnsupportedEncodingException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }).create().show();
//        } else if ((lang.compareTo("en")) == 0) {
//            new AlertDialog.Builder(SceneTwo.this).setTitle(getText(R.string.chosecommand))
//                    .setItems(itemsen, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            SendDataByte(byteCommands[which]);
//                            try {
//                                if (which == 16 || which == 17 || which == 18 || which == 19 || which == 22
//                                        || which == 23 || which == 24 || which == 0 || which == 1 || which == 27) {
//                                    return;
//                                } else {
//                                    SendDataByte("Thermal Receipt Printer ABCDEFGabcdefg123456,.;'/[{}]!\nThermal Receipt PrinterABCDEFGabcdefg123456,.;'/[{}]!\nThermal Receipt PrinterABCDEFGabcdefg123456,.;'/[{}]!\nThermal Receipt PrinterABCDEFGabcdefg123456,.;'/[{}]!\nThermal Receipt PrinterABCDEFGabcdefg123456,.;'/[{}]!\nThermal Receipt PrinterABCDEFGabcdefg123456,.;'/[{}]!\n".getBytes("GBK"));
//                                }
//
//                            } catch (UnsupportedEncodingException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }).create().show();
//        }
//    }

    /************************************************************************************************/
    /*
     * 生成QR图
	 */
    private void createImage() {
        try {
            // 需要引入zxing包
            QRCodeWriter writer = new QRCodeWriter();

            String text = editText.getText().toString();

            Log.i(TAG, "生成的文本：" + text);
            if (text == null || "".equals(text) || text.length() < 1) {
                Toast.makeText(this, getText(R.string.empty), Toast.LENGTH_SHORT).show();
                return;
            }

            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT);

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            byte[] data = PrintPicture.POS_PrintBMP(bitmap, 384, 0);
            SendDataByte(data);
            SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //************************************************************************************************//
  	/*
  	 * 调用系统相机
  	 */
    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        Bitmap mImageBitmap = (Bitmap) extras.get("data");
        imageViewPicture.setImageBitmap(mImageBitmap);
    }
/****************************************************************************************************/
    /**
     * 加载assets文件资源
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

}
