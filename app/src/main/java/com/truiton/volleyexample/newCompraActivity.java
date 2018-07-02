package com.truiton.volleyexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class newCompraActivity extends AppCompatActivity {
    public static final String ID_TO_VIEW_MSG = "com.truiton.volleyexample.ID_TO_VIEW_MSG";
    public static final int GET_FROM_GALLERY = 3;
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
    private Bitmap bitmap;
    private View mProgressView;
    private View mFormView;

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
        mProgressView = findViewById(R.id.submit_progress);
        mFormView = findViewById(R.id.form);

        bitmap = null;
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

            showProgress(true);

            try {
                if (bitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                    String encodedImage = "data:image/jpeg;base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    //jsonParams.put('imagename', etxtUpload.getText().toString().trim());
                    jsonParams.put("picture", encodedImage);
                };
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
                    showProgress(false);
                    try {
                        Intent intent = new Intent(newCompraActivity.this, ViewCompraActivity.class);
                        intent.putExtra(ID_TO_VIEW_MSG, ((JSONObject) response).getJSONObject("data").getString("id"));
                        startActivity(intent);
                        finish(); // proibe de voltar para essa activity ao apertar o botao de retorno
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    //mTextView.setText("Erro ao cadastrar");
                    Toast.makeText(newCompraActivity.this, "Erro ao cadastrar!", Toast.LENGTH_SHORT).show();
                }
            });

            sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mQueue.add(sr);
        }
    }

    public void selectImage(View v){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
