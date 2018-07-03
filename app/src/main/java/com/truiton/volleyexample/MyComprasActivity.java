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

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.layout.simple_list_item_1;


public class MyComprasActivity extends AppCompatActivity implements Response.Listener,
        Response.ErrorListener {
    public static final String ID_TO_VIEW_MSG = "com.truiton.volleyexample.ID_TO_VIEW_MSG";
    public static final String ID_DISTANCE = "com.truiton.volleyexample.ID_DISTANCE";
    public static final String REQUEST_TAG = "MainActivity";
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private RequestQueue mQueue;
    List<Compra> compras = new ArrayList<>();

    private Activity mActivity = this;

    private FloatingActionButton mFloatingActionButton;
    final Context c = this;

    private Float radius;

    private Location currentLocation;

    private GpsTracker gpsTracker;

    private Double longitude;
    private Double latitude;

    private Context mContext = MyComprasActivity.this;
    private static final String TAG = "MyComprasActivity";
    private static final int ACTIVITY_NUM = 2;
    private EditText userInputDialogEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: starting.");

        currentLocation = new Location("user location");

        mTextView = (TextView) findViewById(R.id.textView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        setupBottomNavigationView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        getLocation();

        loadCompras();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCompras();
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
        //mTextView.setText(error.getMessage());
    }

    @Override
    public void onResponse(Object response) {
        //mTextView.setText("Response is: " + response);
        compras.clear();
        try {
            for (int i = 0; i < ((JSONArray) response).length(); i++) {
                try {
                    JSONObject jo = ((JSONArray) response).getJSONObject(i);
                    Location compraDistance = new Location("retirada");
                    compraDistance.setLatitude(Float.parseFloat(((JSONObject) jo).getString("latitude")));
                    compraDistance.setLongitude(Float.parseFloat(((JSONObject) jo).getString("longitude")));

                    float distance = currentLocation.distanceTo(compraDistance) / 1000; // in km
                    Log.e("distanciaa", Float.toString(distance));

                    compras.add(
                            new Compra(
                                Integer.parseInt(((JSONObject) jo).getString("id")),
                                ((JSONObject) jo).getString("name"),
                                distance
                            ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(compras);
            ListView listaDeCompras = (ListView) findViewById(R.id.listCompras);

            ArrayAdapter<Compra> adapter = new ArrayAdapter<Compra>(this,
                    simple_list_item_1, compras);

            listaDeCompras.setAdapter(adapter);

/*            listaDeCompras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    //String item = ((TextView)view).getText().toString();
                    String item = Integer.toString(compras.get(position).getId());
                    String itemDistance = String.format("%.1f", compras.get(position).getDistance());

                    Intent intent = new Intent(MyComprasActivity.this, ViewCompraActivity.class);
                    intent.putExtra(ID_TO_VIEW_MSG, item);
                    intent.putExtra(ID_DISTANCE, itemDistance);
                    startActivity(intent);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(MyComprasActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void loadCompras(){
        try {
            // Instantiate the RequestQueue.
            mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                    .getRequestQueue();

            String url = "https://ccapi.florescer.xyz/api/v1/compras?my=true";

            final CustomJSONArrayRequest jsonRequest = new CustomJSONArrayRequest(Request.Method
                    .GET, url,
                    new JSONArray(), this, this);
            jsonRequest.setTag(REQUEST_TAG);

            mQueue.add(jsonRequest);

        } catch (Exception e) {
            Toast.makeText(MyComprasActivity.this, "Erro ao carregar compras", Toast.LENGTH_LONG).show();
        }
    }

}
