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

package com.makesense.labs.curvefit.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.model.Polyline;
import com.makesense.labs.curvefit.Curve;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.interfaces.OnCurveClickListener;
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback;
import com.makesense.labs.curvefit.interfaces.UiThreadCallback;
import com.makesense.labs.curvefit.models.MessageQueueData;
import com.makesense.labs.curvefit.utils.Constants;

import java.util.HashMap;

public final class CurveManager implements UiThreadCallback {

    private GoogleMap googleMap;
    private HashMap<Polyline, Curve> curves;
    private OnCurveDrawnCallback onCurveDrawnCallback;
    private WorkerHandlerThread mHandlerThread;
    private UiHandler mUiHandler;
    private OnCurveClickListener onCurveClickListener;
    private DelegatingCurveClickListener delegatingCurveClickListener;
    private GoogleMap.OnPolylineClickListener onPolylineClickListener;

    public CurveManager(GoogleMap map) {
        mHandlerThread = new WorkerHandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandlerThread.setUiThreadCallback(this);

        // Initialize the handler for Ui thread to handle messages from worker thread
        mUiHandler = new UiHandler(Looper.getMainLooper());
        this.googleMap = map;
        this.curves = new HashMap<>();
    }

    /*
     * Callback to be invoked when curve is drawn on map
     */
    public void setOnCurveDrawnCallback(OnCurveDrawnCallback onCurveDrawnCallback) {
        this.onCurveDrawnCallback = onCurveDrawnCallback;
    }

    /*
     * Draws a curve, given options set in CurveOptions object.
     * This method passes curveOptions object to the WorkerThread
     * which computes all the intermediate points and invokes a
     * callback once it is drawn.
     */
    public void drawCurveAsync(CurveOptions curveOptions) {
        addToRequestQueue(curveOptions);
    }

    /*
     * Draws a curve, given options set in CurveOptions object.
     * Important note: This method must be used only when the curve has
     * to be redrawn after the screen rotation or orientation change,
     * given curveOptions object is retained in onSaveInstanceState() method.
     */
    public void drawRetainedCurve(CurveOptions curveOptions) {
        Polyline polyline = googleMap.addPolyline(curveOptions.getReal());
        Curve curve = new DelegatingCurve(polyline, this);
        curves.put(polyline, curve);
        if (onCurveDrawnCallback != null) {
            onCurveDrawnCallback.onCurveDrawn(curve, curveOptions);
        }
    }

    /*
     * Receives messages from handler thread after computing all intermediate points,
     * draws curve on map and invokes callback if it is registered
     */
    private class UiHandler extends Handler {

        UiHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Runs on UI thread
            if (msg.what == Constants.TASK_COMPLETE) {
                CurveOptions curveOptions = (CurveOptions) msg.obj;
                // Draws curve on map
                Polyline real = googleMap.addPolyline(curveOptions.getReal());
                Curve curve = new DelegatingCurve(real, CurveManager.this);
                curves.put(real, curve);
                if (onCurveDrawnCallback != null) {
                    onCurveDrawnCallback.onCurveDrawn(curve, curveOptions);

                }
            }
        }
    }

    /*
     * Sets a callback to be invoked when a polyline is clicked on the map.
     * Important note: This method must be used if you are adding a listener
     *                 to the polyline, instead of directly setting a listener
     *                 to the googleMap object (Actually, curve is nothing but a polyline :P).
     *                 Otherwise, curve click listener may not be invoked.
     */
    public void setOnPolylineClickListener(GoogleMap.OnPolylineClickListener onPolylineClickListener) {
        this.onPolylineClickListener = onPolylineClickListener;
    }

    /*
     * Initializes a callback to be invoked when the curve is clicked
     */
    public void setOnCurveClickListener(OnCurveClickListener onCurveClickListener) {
        this.onCurveClickListener = onCurveClickListener;
        if (googleMap != null) {
            this.delegatingCurveClickListener = new DelegatingCurveClickListener(
                    onCurveClickListener);
            googleMap.setOnPolylineClickListener(delegatingCurveClickListener);
        }
    }

    private class DelegatingCurveClickListener implements GoogleMap.OnPolylineClickListener {

        private OnCurveClickListener onCurveClickListener;

        DelegatingCurveClickListener(OnCurveClickListener curveClickListener) {
            this.onCurveClickListener = curveClickListener;
        }

        @Override
        public void onPolylineClick(Polyline polyline) {
            if (curves.containsKey(polyline)) {
                if (onCurveClickListener != null) {
                    onCurveClickListener.onCurveClick(curves.get(polyline));
                }
            } else {
                if (onPolylineClickListener != null) {
                    onPolylineClickListener.onPolylineClick(polyline);
                }
            }
        }

        void cleanup() {
            this.onCurveClickListener = null;
        }
    }

    private void addToRequestQueue(CurveOptions options) {
        if (options.getLatLngList().isEmpty() || options.getLatLngList().size() <= 1) {
            throw new IllegalArgumentException("Requires at least two LatLng points");
        }
        MessageQueueData messageQueueData = new MessageQueueData(
                options, googleMap.getProjection());
        Message message = Message.obtain(null, Constants.TASK_START, messageQueueData);
        mHandlerThread.addMessage(message);
    }

    /*
     * Sends message to ui thread received from worker thread
     */
    @Override
    public void publishToUiThread(Message message) {
        if (mUiHandler != null) {
            mUiHandler.sendMessage(message);
        }
    }

    /*
     * Nullifies all references to avoid leaks
     */
    public void unregister() {
        if (mUiHandler != null) {
            mUiHandler.removeMessages(Constants.TASK_COMPLETE, null);
            mUiHandler = null;
        }
        if (curves != null) {
            curves.clear();
            curves = null;
        }
        if (delegatingCurveClickListener != null) {
            delegatingCurveClickListener.cleanup();
            delegatingCurveClickListener = null;
        }
        if (onCurveClickListener != null) {
            onCurveClickListener = null;
        }
        if (onPolylineClickListener != null) {
            onPolylineClickListener = null;
        }
        if (onCurveDrawnCallback != null) {
            onCurveDrawnCallback = null;
        }
        if (googleMap != null) {
            googleMap.setOnPolylineClickListener(null);
            googleMap = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread.setUiThreadCallback(null);
            mHandlerThread = null;
        }
    }

    final void onRemove(Polyline polyline) {
        curves.remove(polyline);
    }
}