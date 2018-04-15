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

package com.makesense.labs.curvefit.utils;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class MercatorUtils {

    /**
     * The earth's radius, in meters.
     */
    private static final double RADIUS = 6371009;

    /**
     * Returns the LatLng resulting from moving a distance from a given LatLng
     * in the specified heading
     *
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees.
     */
    public static LatLng latLngOffset(LatLng from, double distance, double heading) {
        // https://www.movable-type.co.uk/scripts/latlong.html
        // φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ )
        // λ2 = λ1 + atan2( sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 ) */
        distance /= RADIUS;
        heading = toRadians(heading);
        double fromLatitude = toRadians(from.latitude);
        double fromLongitude = toRadians(from.longitude);
        double newLng = asin(sin(fromLatitude) * cos(distance) +
                cos(fromLatitude) * sin(distance) * cos(heading));
        double newLat = fromLongitude + atan2(sin(heading) * sin(distance) * cos(fromLatitude),
                cos(distance) - sin(fromLatitude) * sin(newLng));
        return new LatLng(toDegrees(newLng), toDegrees(newLat));
    }

    /**
     * Returns the distance between two LatLng points.
     *
     * @param from Starting point.
     * @param to   Ending point.
     */
    public static double distanceBetween(LatLng from, LatLng to) {
        // https://www.movable-type.co.uk/scripts/latlong.html
        // a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
        // c = 2 ⋅ atan2( √a, √(1−a) )
        // d = R ⋅ c
        double fromLatitude = toRadians(from.latitude);
        double fromLongitude = toRadians(from.longitude);
        double toLatitude = toRadians(to.latitude);
        double toLongitude = toRadians(to.longitude);

        double dLat = toLatitude - fromLatitude;
        double dLon = toLongitude - fromLongitude;
        double a = pow(sin(dLat * 0.5), 2) + cos(fromLatitude) * cos(toLatitude) *
                pow(sin(dLon * 0.5), 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return RADIUS * c;
    }
}
