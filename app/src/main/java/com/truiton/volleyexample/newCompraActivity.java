package com.truiton.volleyexample;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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
    static final int DATE_DIALOG_ID = 0;

    private TextView mName;
    private TextView mDescription;
    private TextView mMinQuota;
    private TextView mMaxQuota;
    private TextView mPriceQuota;
    private TextView mTextView;
    private TextView mEnd;
    private TextView mAddress;
    private Button mButton;
    private RequestQueue mQueue;
    private Button botao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compra);

        botao = (Button) findViewById(R.id.btn);
        botao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == botao)
                    showDialog(DATE_DIALOG_ID);
            }
        });

        mName = (TextView) findViewById(R.id.nameField);
        mDescription = (TextView) findViewById(R.id.descriptionField);
        mMinQuota = (TextView) findViewById(R.id.minQuotaField);
        mMaxQuota = (TextView) findViewById(R.id.maxQuotaField);
        mPriceQuota = (TextView) findViewById(R.id.priceQuotaField);
        mEnd = (TextView) findViewById(R.id.endField);
        mAddress = (TextView) findViewById(R.id.addressField);
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
        if( TextUtils.isEmpty(mAddress.getText())) {
            mAddress.setError("Campo obrigatório");
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
                jsonParams.put("address", mAddress.getText().toString());
                jsonParams.put("end", mEnd.getText().toString());
                jsonParams.put("status", "1");
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
                        finish(); // proibe de voltar para essa activity ao apertar o botao de retorno
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

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendario = Calendar.getInstance();

        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, ano, mes,
                        dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    String data = String.valueOf(dayOfMonth) + " /"
                            + String.valueOf(monthOfYear+1) + " /" + String.valueOf(year);
                    Toast.makeText(newCompraActivity.this,
                            "DATA = " + data, Toast.LENGTH_SHORT)
                            .show();
                }
            };
}
