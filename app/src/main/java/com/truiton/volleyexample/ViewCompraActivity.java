/*
 * Copyright (c) 2016. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.truiton.volleyexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class ViewCompraActivity extends AppCompatActivity implements Response.Listener,
        Response.ErrorListener {
    public static final String REQUEST_TAG = "ViewCompraActivity";
    public static final String ID_LAT = "com.truiton.volleyexample.ID_LAT";
    public static final String ID_LNG = "com.truiton.volleyexample.ID_LNG";
    public static final String ID_QUOTAS = "com.truiton.volleyexample.ID_QUOTAS";
    public static final String ID_TO_BUY_MSG = "com.truiton.volleyexample.ID_TO_BUY_MSG";
    public static final String ID_NAME = "com.truiton.volleyexample.ID_NAME";
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String productName;
    private String priceQuotas;
    private TextView mTextView;
    private TextView mPriceQuota;
    private TextView mDescription;
    private TextView mAddress;
    private TextView mUser;
    private TextView mEnd;
    private Button mButton;
    private RequestQueue mQueue;
    private String compraId;
    private ImageView mPicture;
    private String lat;
    private String lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_compra);

        mTextView = (TextView) findViewById(R.id.textView);
        mDescription = (TextView) findViewById(R.id.description);
        mPriceQuota = (TextView) findViewById(R.id.priceQuota);
        mAddress = (TextView) findViewById(R.id.address);
        mUser = (TextView) findViewById(R.id.user);
        mEnd = (TextView) findViewById(R.id.end);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mPicture = (ImageView) findViewById(R.id.picture);
        mButton = (Button) findViewById(R.id.buyButton);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        compraId = intent.getStringExtra(MainActivity.ID_TO_VIEW_MSG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Instantiate the RequestQueue.
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        String url = "https://ccapi.florescer.xyz/api/v1/compras/" + compraId;
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method
                .GET, url,
                new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);

        mQueue.add(jsonRequest);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mQueue.add(jsonRequest);
                mSwipeRefreshLayout.setRefreshing(false); // TODO na real nao ta no lugar certo acho, mas visualmente até parece que funciona
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText(error.getMessage());
    }

    @Override
    public void onResponse(Object response) {
        try {
            productName = ((JSONObject) response).getJSONObject("data").getString("name");
            setTitle(productName);

            mDescription.setText(((JSONObject) response).getJSONObject("data").getString
                    ("description"));

            priceQuotas = ((JSONObject) response).getJSONObject("data").getString("price_per_quota");
            mPriceQuota.setText(String.format("R$%,.2f", Float.parseFloat(priceQuotas)));


            JSONObject picEl = (JSONObject) ((JSONObject) response).getJSONObject("data").get("picture");
            String imgPath = picEl.getString("url");
            if (imgPath == "null" || imgPath == null) {
                mPicture.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            } else {
                String url = "https://ccapi.florescer.xyz" + imgPath;
                GlideApp.with(ViewCompraActivity.this).load(url).into(mPicture);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date endDate = dateFormat.parse(((JSONObject) response).getJSONObject("data").getString(
                    ("end")));
            mEnd.setText(endDate.toString());

            String address = ((JSONObject) response).getJSONObject("data").getString
                    ("address");
            mAddress.setText(address);
            lat = (((JSONObject) response).getJSONObject("data").getString
                        ("latitude"));
            lng = (((JSONObject) response).getJSONObject("data").getString
                        ("longitude"));
            if (address == "null" || lat == "null" || lng == "null" ) {
                mAddress.setTextColor(Color.BLACK);
                mAddress.setOnClickListener(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMap(View v){
        Intent intent = new Intent(ViewCompraActivity.this, MapsActivity.class);
        intent.putExtra(ID_LAT, lat);
        intent.putExtra(ID_LNG, lng);
        startActivity(intent);
    }

    public void buyCotas(View view) {
        Intent intent = new Intent(ViewCompraActivity.this, buyCotasActivity.class);
        intent.putExtra(ID_TO_BUY_MSG, compraId);
        intent.putExtra(ID_QUOTAS, priceQuotas);
        intent.putExtra(ID_NAME, productName);

        startActivity(intent);
    }
}
