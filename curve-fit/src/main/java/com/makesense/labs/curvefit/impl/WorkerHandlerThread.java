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

import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.android.libraries.maps.Projection;
import com.google.android.libraries.maps.model.LatLng;
import com.makesense.labs.curvefit.CurveOptions;
import com.makesense.labs.curvefit.interfaces.UiThreadCallback;
import com.makesense.labs.curvefit.models.LatLngControlPoints;
import com.makesense.labs.curvefit.models.MessageQueueData;
import com.makesense.labs.curvefit.models.PixelControlPoints;
import com.makesense.labs.curvefit.utils.BezierCurveUtils;
import com.makesense.labs.curvefit.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * WorkerHandlerThread responsible for computing all intermediate points
 * between two LatLng coordinates
 */

public final class WorkerHandlerThread extends HandlerThread {

    private static Handler workerHandler;
    private WeakReference<UiThreadCallback> uiThreadCallback;

    WorkerHandlerThread(String name) {
        super(name);
    }

    /*
     * Ui thread sends messages to the worker thread's message queue
     */
    public void addMessage(Message message) {
        getLooper();
        if (workerHandler != null) {
            workerHandler.sendMessage(message);
        }
    }

    public void setUiThreadCallback(UiThreadCallback callback) {
        this.uiThreadCallback = new WeakReference<>(callback);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        // Get a reference to worker thread's handler after looper is prepared
        workerHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case Constants.TASK_START: {
                        MessageQueueData messageQueueData = (MessageQueueData) msg.obj;
                        CurveOptions options = computePoints(messageQueueData.getCurveOptions(),
                                messageQueueData.getProjection());
                        Message message = Message.obtain(null, Constants.TASK_COMPLETE, options);
                        /* Sends message back to Ui thread*/
                        uiThreadCallback.get().publishToUiThread(message);
                        break;
                    }
                }
            }
        };
    }

    /*
     * Function computes all intermediate points between two latLng coordinates
     * using Bezier cubic equation and returns CurveOptions object
     */
    private CurveOptions computePoints(CurveOptions curveOptions, Projection mapProjection) {

        List<LatLng> latLongArrayList = curveOptions.getLatLngList();
        Long start = System.currentTimeMillis();
        for (int index = 0; index < latLongArrayList.size() - 1; index++) {
            LatLng source = latLongArrayList.get(index);
            LatLng destination = latLongArrayList.get(index + 1);

            if (curveOptions.isComputePointsBasedOnScreenPixels()) {

                // Convert start and end LatLng coordinates to screen pixel points.
                // Compute all intermediate screen pixel points using bezier cubic equation.
                // Finally, screen pixel points will be converted back to geographic locations.

                Point startPoint = mapProjection.toScreenLocation(source);
                Point endPoint = mapProjection.toScreenLocation(destination);

                PixelControlPoints PixelControlPoints = BezierCurveUtils.
                        computeCurveControlPoints(curveOptions.getAlpha(), startPoint, endPoint);
                for (double step = 0.0; step < 1.005; step += 0.005) {
                    Point curveXYPoint = BezierCurveUtils.computeCurvePoints(startPoint,
                            PixelControlPoints.getFirstPixelControlPoint(),
                            PixelControlPoints.getSecondPixelControlPoint(), endPoint, step);
                    LatLng curveLatLng = mapProjection.fromScreenLocation(curveXYPoint);
                    curveOptions.getReal().add(curveLatLng);
                }
            } else {

                LatLngControlPoints latLngControlPoints = BezierCurveUtils.
                        computeCurveControlPoints(curveOptions.getAlpha(), source, destination);
                for (double step = 0.0; step < 1.005; step += 0.005) {
                    LatLng curveLatLng = BezierCurveUtils.computeCurvePoints(source,
                            latLngControlPoints.getFirstLatLngControlPoint(),
                            latLngControlPoints.getSecondLatLngControlPoint(), destination, step);
                    curveOptions.getReal().add(curveLatLng);
                }
            }
        }
        Long end = System.currentTimeMillis();
        Log.d("WorkerHandlerThread", "Curve-Fit workerThread finished, took: " + (end - start) + " ms");
        return curveOptions;
    }

    @Override
    public boolean quit() {
        cleanupHandler();
        return super.quit();
    }

    /*
     * Removes any pending messages and sets handler to null
     */
    private void cleanupHandler() {
        if (workerHandler != null) {
            workerHandler.removeMessages(Constants.TASK_START, null);
            workerHandler = null;
        }
    }
}

