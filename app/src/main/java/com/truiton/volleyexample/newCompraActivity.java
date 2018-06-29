package com.truiton.volleyexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class newCompraActivity extends AppCompatActivity {
    public static final String ID_TO_VIEW_MSG = "com.truiton.volleyexample.ID_TO_VIEW_MSG";
    public static final String REQUEST_TAG = "newCompraActivity";
    private TextView mName;
    private TextView mDescription;
    private TextView mMinQuota;
    private TextView mMaxQuota;
    private TextView mPriceQuota;
    private TextView mTextView;
    private TextView mEnd;
    private Button mButton;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compra);

        mName = (TextView) findViewById(R.id.nameField);
        mDescription = (TextView) findViewById(R.id.descriptionField);
        mMinQuota = (TextView) findViewById(R.id.minQuotaField);
        mMaxQuota = (TextView) findViewById(R.id.maxQuotaField);
        mPriceQuota = (TextView) findViewById(R.id.priceQuotaField);
        mEnd = (TextView) findViewById(R.id.endField);
        mButton = (Button) findViewById(R.id.submitButton);
        mTextView = (TextView) findViewById(R.id.result);
    }
    protected boolean fieldsAreValid(){
        boolean result = true;
        if( TextUtils.isEmpty(mName.getText())){
            mName.setError( "Campo obrigatório" );
            result = false;
        }
        if( TextUtils.isEmpty(mMinQuota.getText())) {
            mMinQuota.setError("Campo obrigatório");
        }
        if( TextUtils.isEmpty(mPriceQuota.getText())) {
            mPriceQuota.setError("Campo obrigatório");
            result = false;
        }
        if( TextUtils.isEmpty(mEnd.getText())) {
            mEnd.setError("Campo obrigatório");
            result = false;
        }
        return result;
    }

    public void submitForm(View view) {
        if( fieldsAreValid()) {
            mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                    .getRequestQueue();

            String url = "https://ccapi.florescer.xyz/api/v1/compras/";
            JSONObject jsonParams = new JSONObject();

            try {
                jsonParams.put("name", mName.getText().toString());
                jsonParams.put("description", mDescription.getText().toString());
                jsonParams.put("min_number_of_quotas", mMinQuota.getText().toString());
                jsonParams.put("max_number_of_quotas", mMaxQuota.getText().toString());
                jsonParams.put("price_per_quota", mPriceQuota.getText().toString());
                jsonParams.put("end", mEnd.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            CustomJSONObjectRequest sr = new CustomJSONObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Intent intent = new Intent(newCompraActivity.this, ViewCompraActivity.class);
                        intent.putExtra(ID_TO_VIEW_MSG, ((JSONObject) response).getString("id"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTextView.setText("Erro ao cadastrar");
                }
            });

            mQueue.add(sr);
        }
    }
}
