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

package com.makesense.labs.curvefit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.libraries.maps.model.Cap;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.PatternItem;
import com.google.android.libraries.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurveOptions implements Parcelable {

    private PolylineOptions real = new PolylineOptions();
    private List<LatLng> latLngList = new ArrayList<>();
    private float alpha = 0.5f;
    private boolean computePointsBasedOnScreenPixels;
    private Object data;
    private boolean zoomToPosition;

    private CurveOptions(Parcel in) {
        real = in.readParcelable(PolylineOptions.class.getClassLoader());
        latLngList = in.createTypedArrayList(LatLng.CREATOR);
        alpha = in.readFloat();
        computePointsBasedOnScreenPixels = in.readByte() != 0;
        zoomToPosition = in.readByte() != 0;
    }

    public CurveOptions() {
    }

    public static final Creator<CurveOptions> CREATOR = new Creator<CurveOptions>() {
        @Override
        public CurveOptions createFromParcel(Parcel in) {
            return new CurveOptions(in);
        }

        @Override
        public CurveOptions[] newArray(int size) {
            return new CurveOptions[size];
        }
    };

    public CurveOptions add(LatLng point) {
        this.latLngList.add(point);
        return this;
    }

    public CurveOptions add(LatLng... points) {
        Collections.addAll(this.latLngList, points);
        return this;
    }

    public CurveOptions addAll(Iterable<LatLng> points) {
        for (LatLng point : points) {
            this.latLngList.add(point);
        }
        return this;
    }

    public CurveOptions clickable(boolean clickable) {
        real.clickable(clickable);
        return this;
    }

    public CurveOptions data(Object data) {
        this.data = data;
        return this;
    }

    public CurveOptions color(int color) {
        real.color(color);
        return this;
    }

    public CurveOptions endCap(Cap cap) {
        real.endCap(cap);
        return this;
    }

    public CurveOptions geodesic(boolean geodesic) {
        real.geodesic(geodesic);
        return this;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public int getColor() {
        return real.getColor();
    }

    public Object getData() {
        return data;
    }

    public Cap getEndCap() {
        return real.getEndCap();
    }

    public int getJointType() {
        return real.getJointType();
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public List<PatternItem> getPattern() {
        return real.getPattern();
    }

    public List<LatLng> getPoints() {
        return real.getPoints();
    }

    public PolylineOptions getReal() {
        return real;
    }

    public Cap getStartCap() {
        return real.getStartCap();
    }

    public float getWidth() {
        return real.getWidth();
    }

    public float getZIndex() {
        return real.getZIndex();
    }

    public boolean isComputePointsBasedOnScreenPixels() {
        return computePointsBasedOnScreenPixels;
    }

    public boolean isClickable() {
        return real.isClickable();
    }

    public boolean isGeodesic() {
        return real.isGeodesic();
    }

    public boolean isVisible() {
        return real.isVisible();
    }

    public CurveOptions jointType(int type) {
        real.jointType(type);
        return this;
    }

    public CurveOptions pattern(List<PatternItem> pattern) {
        real.pattern(pattern);
        return this;
    }

    public CurveOptions setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public CurveOptions setComputePointsBasedOnScreenPixels(boolean computePointsBasedOnPixels) {
        this.computePointsBasedOnScreenPixels = computePointsBasedOnPixels;
        return this;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public CurveOptions setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
        return this;
    }

    public CurveOptions startCap(Cap cap) {
        real.startCap(cap);
        return this;
    }

    public CurveOptions visible(boolean visible) {
        real.visible(visible);
        return this;
    }

    public CurveOptions width(float width) {
        real.width(width);
        return this;
    }

    public CurveOptions zIndex(float zIndex) {
        real.zIndex(zIndex);
        return this;
    }

    public boolean isZoomToPosition() {
        return zoomToPosition;
    }

    public CurveOptions setZoomToPosition(boolean zoomToPosition) {
        this.zoomToPosition = zoomToPosition;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(real, flags);
        dest.writeTypedList(latLngList);
        dest.writeFloat(alpha);
        dest.writeByte((byte) (computePointsBasedOnScreenPixels ? 1 : 0));
        dest.writeByte((byte) (zoomToPosition ? 1 : 0));
    }
}