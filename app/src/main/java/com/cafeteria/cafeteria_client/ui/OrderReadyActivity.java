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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ready);

        ibNextOrder = (ImageButton)findViewById(R.id.ibNextOrder);
        ibPrevOrder = (ImageButton)findViewById(R.id.ibPrevOrder);

        orderNumber = getIntent().getIntExtra("order_number",0);

        // check if there are ready orders in the shared preferences storage
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String readyOrdersString = sharedPreferences.getString("readyOrders", "");
        List<Integer> readyOrders;
        if (readyOrdersString.isEmpty()){
            readyOrders = new ArrayList<>();
        } else{
            Type listType = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            readyOrders = new Gson().fromJson(readyOrdersString,listType);
        }

        if( orderNumber > 0 ) {
            readyOrders.add(orderNumber);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("readyOrders", new Gson().toJson(readyOrders));
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

//        if( orderNumber > 0 ) {
//            DataHolder.getInstance().setReadyOrderNumber(orderNumber);
//        } else {
//            orderNumber = DataHolder.getInstance().getReadyOrderNumber();
//        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        getSupportActionBar().setTitle(this.getTitle());
        tvOrderNumber = (TextView)findViewById(R.id.tvOrderNumber);
        tvOrderNumber.setText(orderNumber+"");

        ivQrCode = (ImageView)findViewById(R.id.ivQrCode);
        generateQRCode();

        findViewById(R.id.btnDelivered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get ready order list from shared preferences
                String readyOrdersString = sharedPreferences.getString("readyOrders", "");
                List<Integer> readyOrders;
                Type listType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                readyOrders = new Gson().fromJson(readyOrdersString,listType);
                readyOrders.remove(Integer.valueOf(orderNumber));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("readyOrders", new Gson().toJson(readyOrders));
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
                String readyOrdersString = sharedPreferences.getString("readyOrders", "");
                List<Integer> readyOrders;
                Type listType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                readyOrders = new Gson().fromJson(readyOrdersString,listType);

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
                String readyOrdersString = sharedPreferences.getString("readyOrders", "");
                List<Integer> readyOrders;
                Type listType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                readyOrders = new Gson().fromJson(readyOrdersString,listType);

                orderNumber = readyOrders.get(--index);
                tvOrderNumber.setText(orderNumber+"");
                generateQRCode();
                showNextPrevButtons();
            }
        });

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
        String readyOrdersString = sharedPreferences.getString("readyOrders", "");
        List<Integer> readyOrders;
        Type listType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        readyOrders = new Gson().fromJson(readyOrdersString,listType);

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
