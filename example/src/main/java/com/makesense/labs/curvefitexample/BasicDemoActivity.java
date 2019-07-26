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
import android.widget.Toast;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PatternItem;
import com.makesense.labs.curvefit.Curve;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.impl.CurveManager;
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback;

import java.util.Arrays;
import java.util.List;

public class BasicDemoActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnCurveDrawnCallback {

    private CurveManager curveManager;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private LatLng sourceLatLng = new LatLng(56.14013025, 10.22158774);
    private LatLng destinationLatLng = new LatLng(56.153919, 10.199716);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_demo);
        setTitle("Trip booking");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        curveManager = new CurveManager(map);
        curveManager.setOnCurveDrawnCallback(this);

        CurveOptions curveOptions = new CurveOptions();
        curveOptions.add(sourceLatLng);
        curveOptions.add(destinationLatLng);
        curveOptions.color(Color.DKGRAY);
        curveOptions.setComputePointsBasedOnScreenPixels(true);
        curveOptions.setAlpha(0.5f);
        curveOptions.width(12);
        List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(30));
        curveOptions.pattern(pattern);
        curveOptions.geodesic(false);

        map.addMarker(new MarkerOptions().position(sourceLatLng).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(destinationLatLng).anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // move the map camera towards a center latLng before drawing the curve
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.14683221, 10.2079815), 14));

        curveManager.drawCurveAsync(curveOptions);
    }

    @Override
    protected void onDestroy() {
        if (curveManager != null) {
            curveManager.unregister();
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

    @Override
    public void onCurveDrawn(Curve curve, CurveOptions options) {
        Toast.makeText(getBaseContext(), "Curve is drawn..!!!", Toast.LENGTH_SHORT).show();
    }
}