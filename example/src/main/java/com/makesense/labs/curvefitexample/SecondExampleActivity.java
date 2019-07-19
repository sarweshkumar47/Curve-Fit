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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PatternItem;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.impl.CurveManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondExampleActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CurveManager curveManager;
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_example);
        setTitle("Travel History");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        curveManager = new CurveManager(map);

        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        latLngArrayList.add(new LatLng(12.30548451, 76.65521267));
        latLngArrayList.add(new LatLng(19.81516491, 85.83133625));
        latLngArrayList.add(new LatLng(26.9124336, 75.7872709));
        latLngArrayList.add(new LatLng(28.596111, 83.820278));

        CurveOptions curveOptions = new CurveOptions();
        curveOptions.addAll(latLngArrayList);
        curveOptions.color(Color.DKGRAY);
        curveOptions.setComputePointsBasedOnScreenPixels(false);
        curveOptions.setAlpha(0.5f);
        curveOptions.width(10);
        List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(20));
        curveOptions.pattern(pattern);
        curveOptions.geodesic(false);

        for (LatLng position : latLngArrayList) {
            new MarkerOptions().position(position);
            map.addMarker(new MarkerOptions().position(position));
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.146633, 79.088860), 5));

        curveManager.drawCurveAsync(curveOptions);
    }

    @Override
    protected void onDestroy() {
        if (map != null) {
            map.stopAnimation();
            map.clear();
            map = null;
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(null);
            mapFragment = null;
        }
        if (curveManager != null) {
            curveManager.unregister();
            curveManager = null;
        }
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
