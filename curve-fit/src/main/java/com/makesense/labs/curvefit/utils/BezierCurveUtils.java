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

import com.google.android.gms.maps.model.LatLng;
import com.makesense.labs.curvefit.models.LatLngControlPoints;
import com.makesense.labs.curvefit.models.PixelControlPoints;

import static java.lang.Math.toRadians;

public class BezierCurveUtils {

    /**
     * Returns a curve point which lies the given fraction of the way between the
     * origin Point and the destination Point
     *
     * @param from    The Point from which to start.
     * @param iPoint1 Intermediate control point 1.
     * @param iPoint2 Intermediate control point 2.
     * @param to      The Point toward which to travel
     * @param step    Resolution.
     */
    public static Point computeCurvePoints(Point from, Point iPoint1, Point iPoint2, Point to, double step) {
        // https://en.wikipedia.org/wiki/B%C3%A9zier_curve
        double arcX = (1 - step) * (1 - step) * (1 - step) * from.x
                + 3 * (1 - step) * (1 - step) * step * iPoint1.x
                + 3 * (1 - step) * step * step * iPoint2.x
                + step * step * step * to.x;
        double arcY = (1 - step) * (1 - step) * (1 - step) * from.y
                + 3 * (1 - step) * (1 - step) * step * iPoint1.y
                + 3 * (1 - step) * step * step * iPoint2.y
                + step * step * step * to.y;
        return new Point((int) arcX, (int) arcY);
    }

    /**
     * Returns the LatLng which lies the given fraction of the way between the
     * origin LatLng and the destination LatLng
     *
     * @param from     The LatLng from which to start.
     * @param iLatLng1 Intermediate control point 1.
     * @param iLatLng2 Intermediate control point 2.
     * @param to       The LatLng toward which to travel
     * @param step     Resolution.
     */
    public static LatLng computeCurvePoints(LatLng from, LatLng iLatLng1, LatLng iLatLng2, LatLng to, double step) {
        // https://en.wikipedia.org/wiki/B%C3%A9zier_curve
        double arcLatitude = (1 - step) * (1 - step) * (1 - step) * from.latitude
                + 3 * (1 - step) * (1 - step) * step * iLatLng1.latitude
                + 3 * (1 - step) * step * step * iLatLng2.latitude
                + step * step * step * to.latitude;
        double arcLongitude = (1 - step) * (1 - step) * (1 - step) * from.longitude
                + 3 * (1 - step) * (1 - step) * step * iLatLng1.longitude
                + 3 * (1 - step) * step * step * iLatLng2.longitude
                + step * step * step * to.longitude;
        return new LatLng(arcLatitude, arcLongitude);
    }

    /**
     * Returns the LatLngControlPoints which determines the shape of the curve
     *
     * @param alpha Curve coefficient
     * @param from  The LatLng from which to start.
     * @param to    The LatLng toward which to travel
     */
    public static LatLngControlPoints computeCurveControlPoints(double alpha, LatLng from, LatLng to) {

        double curveTangentAngle = 90 * alpha;
        double distanceBetween = MercatorUtils.distanceBetween(from, to);

        double lineHeadingFromStart = MathUtils.angleBetween(from, to);
        double lineHeadingFromEnd = MathUtils.angleBetween(to, from);

        double controlPointHeading1 = 0, controlPointHeading2 = 0;
        if (lineHeadingFromStart == 0 && lineHeadingFromEnd == 180) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle;
        } else if (lineHeadingFromStart == 180 && lineHeadingFromEnd == 0) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle;
        } else if (lineHeadingFromStart > 0 && lineHeadingFromEnd > 180) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle;
            if (controlPointHeading2 >= 360) {
                controlPointHeading2 = controlPointHeading2 - 360;
            }
        } else if (lineHeadingFromStart > 180 && lineHeadingFromEnd > 0) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle;
            if (controlPointHeading1 >= 360) {
                controlPointHeading1 = controlPointHeading1 - 360;
            }
        }
        LatLng pA = MercatorUtils.latLngOffset(from, distanceBetween / 3, controlPointHeading1);
        LatLng pB = MercatorUtils.latLngOffset(to, distanceBetween / 3, controlPointHeading2);

        return new LatLngControlPoints(pA, pB);
    }

    /**
     * Returns the PixelControlPoints which determines the shape of the curve
     *
     * @param alpha Curve coefficient
     * @param from  The Point (screen pixel) from which to start.
     * @param to    The Point (screen pixel) toward which to travel
     */
    public static PixelControlPoints computeCurveControlPoints(double alpha, Point from, Point to) {

        double curveTangentAngle = 90 * alpha;
        double distanceBetween = MathUtils.distanceBetween(from, to);
        double lineHeadingFromStart = MathUtils.angleBetween(from, to);
        double lineHeadingFromEnd = MathUtils.angleBetween(to, from);

        double controlPointHeading1 = 0, controlPointHeading2 = 0;
        if (lineHeadingFromStart < 90 && lineHeadingFromEnd < 270) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle;
        } else if (lineHeadingFromStart < 270 && lineHeadingFromEnd < 90) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle;
        } else if (lineHeadingFromStart >= 90 && lineHeadingFromEnd >= 270) {
            controlPointHeading1 = lineHeadingFromStart + curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle;
        } else if (lineHeadingFromStart >= 270 && lineHeadingFromEnd >= 90) {
            controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle;
            controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle;
        }
        Point pA = MathUtils.pixelOffset(from, distanceBetween / 3, toRadians(controlPointHeading1));
        Point pB = MathUtils.pixelOffset(to, distanceBetween / 3, toRadians(controlPointHeading2));
        return new PixelControlPoints(pA, pB);
    }
}
