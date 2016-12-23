package com.cafeteria.cafeteria_client.ui;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.width;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class OrderReadyActivity extends AppCompatActivity {

    private TextView tvOrderNumber;
    private ImageView ivQrCode;
    private ImageButton ibNextOrder;
    private ImageButton ibPrevOrder;
    private int index = 0;
    private int orderNumber;
    private SharedPreferences sharedPreferences;
    private Button btnDelivered;
    private Customer c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ready);
        orderNumber = getIntent().getIntExtra("order_number",0);
        ibNextOrder = (ImageButton)findViewById(R.id.ibNextOrder);
        ibPrevOrder = (ImageButton)findViewById(R.id.ibPrevOrder);
        tvOrderNumber = (TextView)findViewById(R.id.tvOrderNumber);
        ivQrCode = (ImageView)findViewById(R.id.ivQrCode);
        btnDelivered = (Button) findViewById(R.id.btnDelivered);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(this.getTitle());
    }

    @Override
    protected void onResume() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String customerJSON = sharedPreferences.getString("customer", "");
        c = new Gson().fromJson(customerJSON, Customer.class);
        // check if there are ready orders in the shared preferences storage
        String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
        HashMap<Integer,List<Integer>> readyOrdersForUsers;
        if (readyOrdersForUserString.isEmpty()){
            readyOrdersForUsers = new HashMap<>();
            readyOrdersForUsers.put(c.getId(),new ArrayList<Integer>());
        } else{
            Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
            }.getType();
            readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
        }

        List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());
        if( readyOrders == null ) {
            readyOrders = new ArrayList<Integer>();
        }

        if( orderNumber > 0 ) {
            readyOrders.add(orderNumber);
            readyOrdersForUsers.put(c.getId(),readyOrders);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("readyOrdersForUsers", new Gson().toJson(readyOrdersForUsers));
            editor.apply();
            //DataHolder.getInstance().addReadyOrder(orderNumber);
        } else {
            if(readyOrders.size() > 0 ) {
                orderNumber = readyOrders.get(index);
            }
//            if( DataHolder.getInstance().getReadyOrders().size() > 0 ) {
//                orderNumber = DataHolder.getInstance().getReadyOrders().get(index);
//            }
        }

        tvOrderNumber.setText(orderNumber+"");
        generateQRCode();

        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get ready order list from shared preferences
                String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
                HashMap<Integer,List<Integer>> readyOrdersForUsers;
                Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
                }.getType();
                readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
                List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());

                readyOrders.remove(Integer.valueOf(orderNumber));
                readyOrdersForUsers.put(c.getId(),readyOrders);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("readyOrdersForUsers", new Gson().toJson(readyOrdersForUsers));
                editor.apply();

                //DataHolder.getInstance().setReadyOrderNumber(0);
                //DataHolder.getInstance().removeReadyOrder(orderNumber);
                OrderReadyActivity.this.finish();
            }
        });

        showNextPrevButtons();

        ibNextOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if( index == DataHolder.getInstance().getReadyOrders().size()-1 ) {
//                    index = -1;
//                }

                // get ready order list from shared preferences
                String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
                HashMap<Integer,List<Integer>> readyOrdersForUsers;
                Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
                }.getType();
                readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
                List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());

                orderNumber = readyOrders.get(++index);
                tvOrderNumber.setText(orderNumber+"");
                generateQRCode();
                showNextPrevButtons();
            }
        });

        ibPrevOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if( index == DataHolder.getInstance().getReadyOrders().size()-1 ) {
//                    index = -1;
//                }

                // get ready order list from shared preferences
                String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
                HashMap<Integer,List<Integer>> readyOrdersForUsers;
                Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
                }.getType();
                readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
                List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());

                orderNumber = readyOrders.get(--index);
                tvOrderNumber.setText(orderNumber+"");
                generateQRCode();
                showNextPrevButtons();
            }
        });
        super.onResume();
    }

    public void generateQRCode() {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(orderNumber+"",BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //Bitmap bitmap = encodeAsBitmap(orderNumber+"");
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void showNextPrevButtons() {

        // get ready order list from shared preferences
        String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
        HashMap<Integer,List<Integer>> readyOrdersForUsers;
        Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
        }.getType();
        readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
        List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());

        index = readyOrders.indexOf(Integer.valueOf(orderNumber));
        if( index == 0 ) {
            ibPrevOrder.setVisibility(View.GONE);
        } else {
            ibPrevOrder.setVisibility(View.VISIBLE);
        }

        if( index == readyOrders.size()-1 ) {
            ibNextOrder.setVisibility(View.GONE);
        } else {
            ibNextOrder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
