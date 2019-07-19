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

import java.lang.ref.WeakReference;
import java.util.HashMap;

public final class CurveManager implements UiThreadCallback {

    private GoogleMap googleMap;
    private HashMap<Polyline, Curve> curves;
    private OnCurveDrawnCallback onCurveDrawnCallback;
    private WorkerHandlerThread mHandlerThread;
    private UiHandler mUiHandler;
    private OnCurveClickListener onCurveClickListener;
    private DelegatingCurveClickListener delegatingCurveClickListener;

    public CurveManager(GoogleMap map) {
        mHandlerThread = new WorkerHandlerThread("HandlerThread");
        mHandlerThread.setUiThreadCallback(this);
        mHandlerThread.start();

        // Initialize the handler for Ui thread to handle messages from worker thread
        mUiHandler = new UiHandler(Looper.getMainLooper(), this);
        this.googleMap = map;
        this.curves = new HashMap<>();
    }

    /**
     * Callback to be invoked when curve is drawn on map
     */
    public void setOnCurveDrawnCallback(OnCurveDrawnCallback onCurveDrawnCallback) {
        this.onCurveDrawnCallback = onCurveDrawnCallback;
    }

    public void drawCurveAsync(CurveOptions curveOptions) {
        addToRequestQueue(curveOptions);
    }

    /*
     * Receives messages from handler thread after computing all intermediate points,
     * draws curve on map and invokes callback if it is registered
     */
    private static class UiHandler extends Handler {
        private WeakReference<CurveManager> curveManagerWeakReference;
        private CurveManager curveManager;

        UiHandler(Looper looper, CurveManager manager) {
            super(looper);
            this.curveManagerWeakReference = new WeakReference<>(manager);
            this.curveManager = curveManagerWeakReference.get();
        }

        // This method will run on UI thread
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.TASK_COMPLETE:
                    if (this.curveManager != null) {
                        CurveOptions curveOptions = (CurveOptions) msg.obj;
                        // Draws curve on map
                        Polyline real = curveManagerWeakReference.get().googleMap.
                                addPolyline(curveOptions.getReal());
                        Curve curve = new DelegatingCurve(real, curveManager);
                        curveManager.curves.put(real, curve);
                        if (curveManager.onCurveDrawnCallback != null) {
                            curveManager.onCurveDrawnCallback.onCurveDrawn(curve);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        void cleanup() {
            if (curveManagerWeakReference != null) {
                this.curveManagerWeakReference.clear();
                this.curveManagerWeakReference = null;
            }
            if (curveManager != null) {
                this.curveManager = null;
            }
        }
    }

    /*
     * Initializes a callback to be invoked when the curve is clicked
     */
    public void setOnCurveClickListener(OnCurveClickListener onCurveClickListener) {
        this.onCurveClickListener = onCurveClickListener;
        if (googleMap != null) {
            this.delegatingCurveClickListener = new DelegatingCurveClickListener(
                    onCurveClickListener, curves);
            googleMap.setOnPolylineClickListener(delegatingCurveClickListener);
        }
    }

    private static class DelegatingCurveClickListener implements GoogleMap.OnPolylineClickListener {

        private WeakReference<OnCurveClickListener> onCurveClickListenerWeakReference;
        private WeakReference<HashMap<Polyline, Curve>> mapWeakReference;

        DelegatingCurveClickListener(OnCurveClickListener curveClickListener,
                                     HashMap<Polyline, Curve> curves) {
            this.onCurveClickListenerWeakReference = new WeakReference<>(curveClickListener);
            this.mapWeakReference = new WeakReference<>(curves);
        }

        @Override
        public void onPolylineClick(Polyline polyline) {
            onCurveClickListenerWeakReference.get().onCurveClick(mapWeakReference.get().get(polyline));
        }

        void cleanup() {
            this.onCurveClickListenerWeakReference = null;
            this.mapWeakReference = null;
        }
    }

    private void addToRequestQueue(CurveOptions options) {
        if (options.getLatLngList().isEmpty() || options.getLatLngList().size() <= 1) {
            throw new IllegalArgumentException("Requires atleast two latlong points");
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
        if (curves != null) {
            curves.clear();
            curves = null;
        }
        if (mUiHandler != null) {
            mUiHandler.removeMessages(Constants.TASK_COMPLETE, null);
            mUiHandler.cleanup();
            mUiHandler = null;
        }
        if (delegatingCurveClickListener != null) {
            delegatingCurveClickListener.cleanup();
            delegatingCurveClickListener = null;
        }
        if (onCurveClickListener != null) {
            onCurveClickListener = null;
        }
        if (onCurveDrawnCallback != null) {
            onCurveDrawnCallback = null;
        }
        if (googleMap != null) {
            googleMap.setOnPolylineClickListener(null);
            googleMap = null;
        }
        if (mHandlerThread != null) {
            boolean suc = mHandlerThread.quit();
            mHandlerThread.setUiThreadCallback(null);
            mHandlerThread = null;
        }
    }

    protected final void onRemove(Polyline polyline) {
        curves.remove(polyline);
    }

    public Curve getCurve(Polyline polyline) {
        if (curves.containsKey(polyline)) {
            return curves.get(polyline);
        }
        return null;
    }
}
