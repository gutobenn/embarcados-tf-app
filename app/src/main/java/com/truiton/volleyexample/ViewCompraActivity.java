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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
    public static final String ID_USER = "com.truiton.volleyexample.ID_USER";
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String productName;
    private String priceQuotas;
    private TextView mTextView;
    private TextView mPriceQuota;
    private TextView mDescription;
    private TextView mAddress;
    private TextView mUser;
    private TextView mEnd;
    private TextView mStatus;
    private Button mButton;
    private RequestQueue mQueue;
    private String compraId;
    private ImageView mPicture;
    private View mProgressView;
    private View mRelativeLayout;
    private String lat;
    private String lng;
    private String distance;

    private String availableQuota;
    private TextView mAvailable;
    private String userMail;


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
        mProgressView = findViewById(R.id.load_progress);
        mStatus = findViewById(R.id.status);
        mRelativeLayout = findViewById(R.id.relativelayout);
        mButton = (Button) findViewById(R.id.buyButton);

        mAvailable = (TextView) findViewById(R.id.available);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        compraId = intent.getStringExtra(MainActivity.ID_TO_VIEW_MSG);
        distance = intent.getStringExtra(MainActivity.ID_DISTANCE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgress(true);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResponse(Object response) {
        try {
            showProgress(false);

            setTitle(((JSONObject) response).getJSONObject("data").getString("name"));
            mDescription.setText(((JSONObject) response).getJSONObject("data").getString
                    ("description"));
            mPriceQuota.setText(String.format("R$%,.2f", Float.parseFloat(((JSONObject) response).getJSONObject("data").getString
                    ("price_per_quota"))));
            mUser.setText(((JSONObject) response).getJSONObject("data").getString
                    ("user_email"));

            switch (((JSONObject) response).getJSONObject("data").getString
                    ("status")) {
                case "1":
                    mStatus.setText("Pedido em Aberto");
                    mStatus.setTextColor(Color.rgb(0, 128, 0));
                    break;
                case "2":
                    mStatus.setText("Aguardando Pedido");
                    mStatus.setTextColor(Color.DKGRAY);
                    break;
                case "3":
                    mStatus.setText("Aguardando Entrega");
                    mStatus.setTextColor(Color.YELLOW);
                    break;
                case "4":
                    mStatus.setText("Disponível para Retirada");
                    mStatus.setTextColor(Color.RED);
                    break;
            }

            userMail = ((JSONObject) response).getJSONObject("data").getString("user_email");

            productName = ((JSONObject) response).getJSONObject("data").getString("name");
            setTitle(productName);

            mDescription.setText(((JSONObject) response).getJSONObject("data").getString
                    ("description"));

            priceQuotas = ((JSONObject) response).getJSONObject("data").getString("price_per_quota");
            mPriceQuota.setText(String.format("R$%,.2f", Float.parseFloat(priceQuotas)));


            float boughtQuota = Float.parseFloat(((JSONObject) response).getJSONObject("data").getString("bought_quotas"));
            float minQuota = Float.parseFloat(((JSONObject) response).getJSONObject("data").getString("min_number_of_quotas"));
            int restQuota = Math.round(minQuota - boughtQuota);
            if (restQuota <= 0) {
                availableQuota = "O pedido já atingiu o mínimo de cotas!";
            }
            else {
                availableQuota = "Falta " + Integer.toString(restQuota) + " cotas para fechar o pedido!";
            }

            mAvailable.setText(availableQuota);

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
            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM");
            mEnd.setText("Até " + dateFormat2.format(endDate));

            String address = ((JSONObject) response).getJSONObject("data").getString
                    ("address");
            mAddress.setText(address + " (" + String.valueOf(distance)+ "km)");
            lat = (((JSONObject) response).getJSONObject("data").getString
                        ("latitude"));
            lng = (((JSONObject) response).getJSONObject("data").getString
                        ("longitude"));
            if (address == "null" || lat == "null" || lng == "null" ) {
                mAddress.setTextColor(Color.BLACK);
                mAddress.setOnClickListener(null);
            }

        } catch (Exception e) {
            showProgress(false);
            Toast.makeText(ViewCompraActivity.this, "Não foi possível carregar a lista de compras", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void openMap(View v){
        Intent intent = new Intent(ViewCompraActivity.this, MapsActivity.class);
        intent.putExtra(ID_LAT, lat);
        intent.putExtra(ID_LNG, lng);
        startActivity(intent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mRelativeLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void buyCotas(View view) {
        Intent intent = new Intent(ViewCompraActivity.this, buyCotasActivity.class);
        intent.putExtra(ID_TO_BUY_MSG, compraId);
        intent.putExtra(ID_QUOTAS, priceQuotas);
        intent.putExtra(ID_NAME, productName);
        intent.putExtra(ID_USER, userMail);

        startActivity(intent);
    }
}
