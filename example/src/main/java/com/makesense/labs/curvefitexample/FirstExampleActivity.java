/*
 * Copyright 2018 Sarweshkumar C R
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makesense.labs.curvefitexample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.makesense.labs.curvefit.Curve;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.impl.CurveManager;
import com.makesense.labs.curvefit.interfaces.OnCurveClickListener;
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback;

import java.util.Arrays;
import java.util.List;

public class FirstExampleActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnCurveDrawnCallback,
        OnCurveClickListener {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private EditText latitudeInputOneEditText, longitudeInputOneEditText;
    private EditText latitudeInputTwoEditText, longitudeInputTwoEditText;
    private EditText alphaEditText;
    private Button drawButton, clearButton;
    private CheckBox checkBox;
    private CurveManager curveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_example);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        drawButton = findViewById(R.id.drawCurveLineButton);
        latitudeInputOneEditText = findViewById(R.id.latitudeEditTextOne);
        longitudeInputOneEditText = findViewById(R.id.longitudeEditTextOne);
        latitudeInputTwoEditText = findViewById(R.id.latitudeEditTextTwo);
        longitudeInputTwoEditText = findViewById(R.id.longitudeEditTextTwo);
        alphaEditText = findViewById(R.id.alphaEditText);
        clearButton = findViewById(R.id.clearButton);
        checkBox = findViewById(R.id.checkbox);

        mapFragment.getMapAsync(this);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                drawCurveLine();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitudeInputOneEditText.setText("");
                longitudeInputOneEditText.setText("");
                latitudeInputTwoEditText.setText("");
                longitudeInputTwoEditText.setText("");
                alphaEditText.setText("");
                checkBox.setChecked(false);
                map.clear();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        curveManager = new CurveManager(map);
        curveManager.setOnCurveDrawnCallback(this);
        curveManager.setOnCurveClickListener(this);
    }

    private void drawCurveLine() {
        String sourceLatitude = latitudeInputOneEditText.getText().toString().trim();
        String sourceLongitude = longitudeInputOneEditText.getText().toString().trim();
        String destLatitude = latitudeInputTwoEditText.getText().toString().trim();
        String destLongitude = longitudeInputTwoEditText.getText().toString().trim();
        String alpha = alphaEditText.getText().toString().trim();
        if (sourceLatitude.equals("") || sourceLongitude.equals("")
                || destLatitude.equals("") || destLongitude.equals("") || alpha.equals("")) {
            return;
        }

        LatLng initLatLng = new LatLng(Double.valueOf(sourceLatitude), Double.valueOf(sourceLongitude));
        LatLng finalLatLng = new LatLng(Double.valueOf(destLatitude), Double.valueOf(destLongitude));
        CurveOptions curveOptions = new CurveOptions();
        curveOptions.add(initLatLng);
        curveOptions.add(finalLatLng);
        curveOptions.clickable(true);
        curveOptions.setComputePointsBasedOnScreenPixels(checkBox.isChecked());
        curveOptions.setZoomToPosition(true);
        curveOptions.setAlpha(Float.valueOf(alpha));
        curveOptions.width(12);
        List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(25));
        curveOptions.pattern(pattern);
        curveOptions.geodesic(false);
        if (checkBox.isChecked()) {
            curveOptions.color(getResources().getColor(R.color.red_500));
        } else {
            curveOptions.color(getResources().getColor(R.color.indigo_a700));
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(initLatLng);
        builder.include(finalLatLng);
        LatLngBounds bounds = builder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

        if (curveManager != null) {
            curveManager.drawCurveAsync(curveOptions);
        }

        map.addMarker(new MarkerOptions().position(initLatLng).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(finalLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .anchor(0.5f, 1f));
    }

    @Override
    public void onCurveDrawn(Curve curve) {
        Toast.makeText(getBaseContext(), "Curve drawn..!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurveClick(Curve curve) {
        Toast.makeText(getBaseContext(), "Color updated..!!!", Toast.LENGTH_SHORT).show();
        curve.setColor(Color.parseColor("#3E2723"));
    }

    @Override
    protected void onDestroy() {
        if (map != null) {
            map.clear();
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(null);
            mapFragment = null;
        }
        if (curveManager != null) {
            curveManager.unregister();
            curveManager.setOnCurveDrawnCallback(null);
            curveManager.setOnCurveClickListener(null);
            curveManager = null;
        }
        drawButton.setOnClickListener(null);
        clearButton.setOnClickListener(null);
        super.onDestroy();
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
