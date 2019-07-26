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
import com.google.android.libraries.maps.model.Polyline;
import com.google.android.libraries.maps.model.PolylineOptions;
import com.makesense.labs.curvefit.Curve;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.impl.CurveManager;
import com.makesense.labs.curvefit.interfaces.OnCurveClickListener;
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback;

import java.util.Arrays;
import java.util.List;

public class DrawCurveAndPolylineTogetherExampleActivity extends AppCompatActivity implements
        OnMapReadyCallback, OnCurveClickListener, GoogleMap.OnPolylineClickListener,
        OnCurveDrawnCallback {

    private CurveManager curveManager;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private LatLng sourceLatLng = new LatLng(56.14013025, 10.22158774);
    private LatLng destinationLatLng = new LatLng(56.153919, 10.199716);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        setTitle("Curve - Polyline Draw");

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
        curveOptions.clickable(true);
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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.14683221, 10.2079815), 14));
        // Draws a curve asynchronously
        curveManager.drawCurveAsync(curveOptions);

        // Adds a polyline
        map.addPolyline(new PolylineOptions().add(sourceLatLng).add(destinationLatLng).clickable(true));

        curveManager.setOnCurveClickListener(this);
        /*
         * Don't use map.setOnPolylineClickListener() method directly.
         * Instead, try using curveManager.setOnPolylineClickListener(this) method.
         * Basically, Curve is nothing but a Polyline here :P
         */
        curveManager.setOnPolylineClickListener(this);
    }

    @Override
    public void onCurveClick(Curve curve) {
        Toast.makeText(getBaseContext(), "Curve clicked..!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        Toast.makeText(getBaseContext(), "Polyline clicked..!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurveDrawn(Curve curve, CurveOptions options) {
        Toast.makeText(getBaseContext(), "Curve is drawn..!!!", Toast.LENGTH_SHORT).show();
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
