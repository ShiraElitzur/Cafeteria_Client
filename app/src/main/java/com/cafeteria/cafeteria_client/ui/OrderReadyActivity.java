package com.cafeteria.cafeteria_client.ui;

import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static android.R.attr.width;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class OrderReadyActivity extends AppCompatActivity {

    private TextView tvOrderNumber;
    private ImageView ivQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ready);

        int orderNumber = getIntent().getIntExtra("order_number",0);
        if( orderNumber > 0 ) {
            DataHolder.getInstance().setReadyOrderNumber(orderNumber);
        } else {
            orderNumber = DataHolder.getInstance().getReadyOrderNumber();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        getSupportActionBar().setTitle(this.getTitle());
        tvOrderNumber = (TextView)findViewById(R.id.tvOrderNumber);
        tvOrderNumber.setText(orderNumber+"");

        ivQrCode = (ImageView)findViewById(R.id.ivQrCode);

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

        findViewById(R.id.btnDelivered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataHolder.getInstance().setReadyOrderNumber(0);
                OrderReadyActivity.this.finish();
            }
        });

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
