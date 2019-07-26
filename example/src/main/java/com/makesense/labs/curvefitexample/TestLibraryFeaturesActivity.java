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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.LatLngBounds;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PatternItem;
import com.makesense.labs.curvefit.Curve;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.impl.CurveManager;
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestLibraryFeaturesActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnCurveDrawnCallback {

    public static final String CURVE_OPTIONS_LIST = "curveOptionsList";
    public static final String TARGET = "target";
    public static final String ZOOM = "zoom";
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private RelativeLayout inputLayout;
    private EditText latitudeInputOneEditText, longitudeInputOneEditText;
    private EditText latitudeInputTwoEditText, longitudeInputTwoEditText;
    private EditText alphaEditText;
    private CheckBox checkBox;
    private CurveManager curveManager;

    private Set<CurveOptions> curveOptionsHashSet = new HashSet<>();
    private ArrayList<CurveOptions> curveOptionsArrayList = new ArrayList<>();
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_test_library_features);
        setTitle("Test Curve-Fit Library");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        inputLayout = findViewById(R.id.inputLayout);
        Button drawButton = findViewById(R.id.drawCurveLineButton);
        latitudeInputOneEditText = findViewById(R.id.latitudeEditTextOne);
        longitudeInputOneEditText = findViewById(R.id.longitudeEditTextOne);
        latitudeInputTwoEditText = findViewById(R.id.latitudeEditTextTwo);
        longitudeInputTwoEditText = findViewById(R.id.longitudeEditTextTwo);
        alphaEditText = findViewById(R.id.alphaEditText);
        Button clearButton = findViewById(R.id.clearButton);
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
                curveOptionsArrayList.clear();
                curveOptionsHashSet.clear();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        curveManager = new CurveManager(map);
        curveManager.setOnCurveDrawnCallback(this);

        if (savedInstanceState != null) {
            drawCurveAfterScreenRotation();
        }
    }

    private void drawCurveLine() {
        String sourceLatitude = latitudeInputOneEditText.getText().toString().trim();
        String sourceLongitude = longitudeInputOneEditText.getText().toString().trim();
        String destLatitude = latitudeInputTwoEditText.getText().toString().trim();
        String destLongitude = longitudeInputTwoEditText.getText().toString().trim();
        String alpha = alphaEditText.getText().toString().trim();
        if (sourceLatitude.isEmpty() || sourceLongitude.isEmpty()
                || destLatitude.isEmpty() || destLongitude.isEmpty() || alpha.isEmpty()) {
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

        map.addMarker(new MarkerOptions().position(initLatLng).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(finalLatLng).anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

        curveManager.drawCurveAsync(curveOptions);
    }

    // Draws curve after screen rotation
    public void drawCurveAfterScreenRotation() {
        float zoom = savedInstanceState.getFloat(ZOOM);
        LatLng target = savedInstanceState.getParcelable(TARGET);

        // Move the map camera towards the target before drawing the curve
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, zoom));

        if (savedInstanceState.containsKey(CURVE_OPTIONS_LIST)) {
            ArrayList<CurveOptions> list = savedInstanceState
                    .getParcelableArrayList(CURVE_OPTIONS_LIST);
            if (list != null && list.size() > 0) {
                for (CurveOptions curveOptions : list) {
                    if (curveOptionsHashSet.add(curveOptions)) {
                        curveOptionsArrayList.add(curveOptions);
                        /*
                         * Here, no need to compute all intermediate points
                         * Important note: This method must be used only when the curve has
                         * to be redrawn after the screen rotation or orientation change,
                         * given curveOptions object is retained in onSaveInstanceState() method.
                         */
                        curveManager.drawRetainedCurve(curveOptions);

                        map.addMarker(new MarkerOptions().position(curveOptions.getPoints().get(0)).anchor(0.5f, 1f));
                        map.addMarker(new MarkerOptions().position(curveOptions.getPoints()
                                .get(curveOptions.getPoints().size() - 1)).anchor(0.5f, 1f)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }
        }
    }

    @Override
    public void onCurveDrawn(Curve curve, CurveOptions options) {
        Toast.makeText(getBaseContext(), "Curve is drawn..!!!", Toast.LENGTH_SHORT).show();
        // Add curveOptions object in a list to store/restore during orientation change
        if (curveOptionsHashSet.add(options)) {
            curveOptionsArrayList.add(options);
        }
    }

    /*
     * Save list of CurveOptions object, map target location
     * and zoom information in bundle.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat(ZOOM, map.getCameraPosition().zoom);
        outState.putParcelable(TARGET, map.getCameraPosition().target);
        outState.putParcelableArrayList(CURVE_OPTIONS_LIST, curveOptionsArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (curveManager != null) {
            curveManager.unregister();
            curveManager.setOnCurveDrawnCallback(null);
            curveManager = null;
        }
        if (map != null) {
            map.stopAnimation();
            map.clear();
            map = null;
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(null);
            mapFragment = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}