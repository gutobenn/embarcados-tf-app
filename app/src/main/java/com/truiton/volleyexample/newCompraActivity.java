package com.truiton.volleyexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    public static final String REQUEST_TAG = "newCompraActivity";
    private TextView mName;
    private TextView mDescription;
    private TextView mTextView;
    private Button mButton;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_compra);

        mName = (TextView) findViewById(R.id.nameField);
        mDescription = (TextView) findViewById(R.id.descriptionField);
        mButton = (Button) findViewById(R.id.submitButton);
        mTextView = (TextView) findViewById(R.id.result);
    }

    public void submitForm(View view) {
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();

        String url = "https://ccapi.florescer.xyz/api/v1/compras/";
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("name", mName.getText().toString());
            jsonParams.put("description", mDescription.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomJSONObjectRequest sr = new CustomJSONObjectRequest(Request.Method.POST,url, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //mPostCommentResponse.requestCompleted();
                // TODO TRATAR RESPOSTA. redirecionar pra pagina da compra (e se voltar nao voltar pro form)
                mTextView.setText("Response is: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mPostCommentResponse.requestEndedWithError(error);
            }
        });

        mQueue.add(sr);
    }
}
