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

public class HandleOrientationChangeExampleActivity extends AppCompatActivity implements
        OnMapReadyCallback, OnCurveDrawnCallback {

    public static final String CURVE_OPTIONS_LIST = "curveOptionsList";
    public static final String TARGET = "target";
    public static final String ZOOM = "zoom";
    private CurveManager curveManager;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private LatLng sourceLatLng1 = new LatLng(12.86069528, 74.83520513);
    private LatLng destinationLatLng1 = new LatLng(13.07469926, 80.28442388);
    private LatLng sourceLatLng2 = new LatLng(15.46966522, 73.81347662);
    private LatLng destinationLatLng2 = new LatLng(15.73383948, 80.89965826);

    private Set<CurveOptions> curveOptionsHashSet = new HashSet<>();
    private ArrayList<CurveOptions> curveOptionsArrayList = new ArrayList<>();
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_google_maps);
        setTitle("Handle Orientation");

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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(sourceLatLng2);
        builder.include(destinationLatLng1);
        LatLngBounds bounds = builder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        if (savedInstanceState == null) {
            drawCurveAsync();
        } else {
            drawCurveAfterScreenRotation();
        }
    }

    private void drawCurveAsync() {
        //////////////////////////////////////////////////////
        CurveOptions curveOptions1 = new CurveOptions();
        curveOptions1.add(sourceLatLng1);
        curveOptions1.clickable(true);
        curveOptions1.add(destinationLatLng1);
        curveOptions1.color(Color.RED);
        curveOptions1.width(12);
        List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(30));
        curveOptions1.pattern(pattern);
        curveOptions1.geodesic(false);

        map.addMarker(new MarkerOptions()
                .position(sourceLatLng1)
                .anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions()
                .position(destinationLatLng1)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .anchor(0.5f, 1f));

        /////////////////////////////////////////////////////

        CurveOptions curveOptions2 = new CurveOptions();
        curveOptions2.add(sourceLatLng2);
        curveOptions2.clickable(true);
        curveOptions2.add(destinationLatLng2);
        curveOptions2.color(Color.DKGRAY);
        curveOptions2.width(12);
        curveOptions2.geodesic(false);

        map.addMarker(new MarkerOptions().position(sourceLatLng2).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(destinationLatLng2).anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        /////////////////////////////////////////////////////

        curveManager.drawCurveAsync(curveOptions1);
        curveManager.drawCurveAsync(curveOptions2);
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
                    }
                }
            }
        }
        map.addMarker(new MarkerOptions().position(sourceLatLng1).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(destinationLatLng1).anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        map.addMarker(new MarkerOptions().position(sourceLatLng2).anchor(0.5f, 1f));
        map.addMarker(new MarkerOptions().position(destinationLatLng2).anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    @Override
    public void onCurveDrawn(Curve curve, CurveOptions options) {
        // Add curveOptions object in a list to store/restore during orientation change
        if (curveOptionsHashSet.add(options)) {
            curveOptionsArrayList.add(options);
        }
    }

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
