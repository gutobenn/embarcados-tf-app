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
import android.text.TextWatcher;
import android.text.Editable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class buyCotasActivity extends AppCompatActivity {
    public static final String REQUEST_TAG = "BuyCotasActivity";
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private TextView mPriceQuota;
    private TextView mUser;
    private TextView mTotal;
    private TextView mEnd;
    private TextView mNumQuotas;
    private String compraId;
    private String priceQuota;
    private String productName;
    private ImageView mPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_cotas);

        mTextView = (TextView) findViewById(R.id.textView);
        mPriceQuota = (TextView) findViewById(R.id.priceQuota);
        mUser = (TextView) findViewById(R.id.user);
        mUser = (TextView) findViewById(R.id.total);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mNumQuotas = (TextView) findViewById(R.id.numQuotasField);
        mTotal = (TextView) findViewById(R.id.total);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        compraId = intent.getStringExtra(ViewCompraActivity.ID_TO_BUY_MSG);
        productName = intent.getStringExtra(ViewCompraActivity.ID_NAME);
        priceQuota = intent.getStringExtra(ViewCompraActivity.ID_QUOTAS);

        setTitle(productName);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNumQuotas.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    mTotal.setText(String.format("Total R$%,.2f", Float.parseFloat(priceQuota) * Integer.parseInt(s.toString())));
                else mTotal.setText("Total R$0,00");
            }
        });

        mPriceQuota.setText(String.format("R$%,.2f", Float.parseFloat(priceQuota)));
    }


    public void buyCotas(View view) {
        mTotal.setText(String.format("Você comprou %s cotas", mNumQuotas.getText().toString()));
    }
}
