# Curve-Fit
Android library for drawing curves on Google Maps. This library uses Bezier cubic equation in order to compute all
intermediate points of a curve.

# Demo
<p align="center" >
<img src="images/sample_demo.gif" alt="demo" width="220" align="left" />
<img src="images/sample_india.jpg" alt="sample_india" width="220"/>
<img src="images/sample_denmark.jpg" alt="sample_denmark" width="220" align="right" /> </p>

# Setup
### Gradle
```
dependencies {
    compile 'com.github.sarweshkumar47:curve-fit:1.0.0'
}
```
  
### Maven
```
<dependency>
 <groupId>com.github.sarweshkumar47</groupId>
 <artifactId>curve-fit</artifactId>
 <version>1.0.0</version>
 <type>pom</type>
</dependency>
```

# Usage
In your activity's onCreate() method, use ```getMapAsync()``` to register for the map callback. 
Implement the ```OnMapReadyCallback``` interface and override the ```onMapReady()``` method

```java
@Override
public void onMapReady(GoogleMap googleMap) {

    // Create CurveManager object and pass googleMaps reference to it
    curveManager = new CurveManager(googleMap);

    // Register a callback to be invoked after curve is drawn on map
    curveManager.setOnCurveDrawnCallback(this);
    
    // Set a listener for curve click events.
    curveManager.setOnCurveClickListener(this);
        
    // Create a CurveOptions object and add atleast latlong points to it
    // You can set different options in CurveOptions object similar to PolyLineOptions
    CurveOptions curveOptions = new CurveOptions();
    curveOptions.add(new LatLng(12.9715987, 77.5945627));
    curveOptions.add(new LatLng(12.2958104, 76.6393805));
    curveOptions.color(Color.DKGRAY);
    curveOptions.setAlpha(0.5f);
    curveOptions.width(12);
    List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(30));
    curveOptions.pattern(pattern);
    curveOptions.geodesic(false);

    map.addMarker(new MarkerOptions().position(sourceLatLng).anchor(0.5f, 1f));
    map.addMarker(new MarkerOptions().position(destinationLatLng).icon(BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).anchor(0.5f, 1f));

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.64779202, 77.16562563), 14));
    
    // Draws curve asynchronously
    curveManager.drawCurveAsync(curveOptions);
}
```

Remove listeners in order to prevent memory leaks.
``` java
@Override
protected void onDestroy() {
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
    super.onDestroy();
}

```
Check example projects for more info.

# Advanced usage

### CurveOptions

* __Alpha__
```java 
CurveOptions setAlpha(float alpha)
```

Defines shape and direction of the curve. Alpha range can vary from -1 to 1


* __ComputePointsBasedOnScreenPixels__
```java 
CurveOptions setComputePointsBasedOnScreenPixels(boolean computePointsBasedOnPixels)
```

If set to ```true```, geographic location points will be converted to screen pixel points and algorithm uses screen pixel points
to compute all curve points.

# Contributions
Contributions are welcome and much appreciated.

# Credit
### Icons and images
* <div>Icons made by <a href="https://www.flaticon.com/authors/dinosoftlabs" title="DinosoftLabs">DinosoftLabs</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

* https://upload.wikimedia.org/wikipedia/commons/d/d5/India_gate.jpg



# License
    Copyright 2018 Sarweshkumar C R

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
