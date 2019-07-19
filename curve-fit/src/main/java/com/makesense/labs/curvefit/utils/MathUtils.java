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

import android.graphics.Point;

import com.google.android.libraries.maps.model.LatLng;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public class MathUtils {

    /**
     * Returns the angle between two LatLongs, in degrees.
     */
    public static double angleBetween(LatLng from, LatLng to) {
        double angle = toDegrees(atan2((to.longitude - from.longitude),
                (to.latitude - from.latitude)));
        if (angle < 0) {
            angle = 360 + angle;
        }
        return angle;
    }

    /**
     * Returns the angle between two points, in degrees.
     */
    public static double angleBetween(Point from, Point to) {
        double angle = toDegrees(atan2((double) (to.y - from.y), (double) (to.x - from.x)));
        if (angle < 0) {
            angle = 360 + angle;
        }
        return angle;
    }

    /**
     * Returns the distance between two points.
     */
    public static double distanceBetween(Point from, Point to) {
        return sqrt(pow(abs(to.x - from.x), 2) + pow(abs(to.y - from.y), 2));
    }

    /**
     * Returns the Point resulting from moving a distance from an origin
     * in the specified heading
     *
     * @param from     The Point from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees.
     */
    public static Point pixelOffset(Point from, double distance, double heading) {
        double x = from.x + (distance * cos(heading));
        double y = from.y + (distance * sin(heading));
        return new Point((int) x, (int) y);
    }
}
