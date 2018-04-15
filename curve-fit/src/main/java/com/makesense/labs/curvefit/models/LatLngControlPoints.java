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

package com.makesense.labs.curvefit.models;

import com.google.android.gms.maps.model.LatLng;

public class LatLngControlPoints {
    private LatLng firstLatLngControlPoint;
    private LatLng secondLatLngControlPoint;

    public LatLngControlPoints(LatLng firstLatLngControlPoint, LatLng secondLatLngControlPoint) {
        this.firstLatLngControlPoint = firstLatLngControlPoint;
        this.secondLatLngControlPoint = secondLatLngControlPoint;
    }

    public LatLng getFirstLatLngControlPoint() {
        return firstLatLngControlPoint;
    }

    public LatLng getSecondLatLngControlPoint() {
        return secondLatLngControlPoint;
    }
}
