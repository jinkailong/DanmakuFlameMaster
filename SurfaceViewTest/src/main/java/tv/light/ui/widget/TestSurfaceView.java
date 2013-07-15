/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.light.ui.widget;

import tv.light.controller.DrawHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private HandlerThread mDrawThread;

    private DrawHandler handler;

    private long startTime;

    private float cx, cy;

    private long avgDuration;

    private long maxDuration;

    public TestSurfaceView(Context context) {
        super(context);
        init();
    }

    public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        // startDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if (event.getAction() == MotionEvent.ACTION_UP) {
        // quitDrawThread();
        // }

        updateCxCy(event.getX(), event.getY());

        return true;
    }

    private void updateCxCy(float x, float y) {
        cx = x;
        cy = y;
    }

    private void startDraw() {
        // mDrawThread = new HandlerThread("draw thread");
        // mDrawThread.start();
        // handler = new DrawHandler(mDrawThread.getLooper());
        handler = new DrawHandler();
        handler.sendEmptyMessage(DrawHandler.START);
    }

    private void quitDrawThread() {
        if (handler != null) {
            handler.quit();// .sendEmptyMessage(DrawHandler.STOP);
            handler = null;
        }
        if (mDrawThread != null) {
            mDrawThread.quit();
            mDrawThread = null;
        }
    }

    private void drawTime() {
        drawSomeThing(System.currentTimeMillis() + "ms");
    }

    void drawSomeThing(String text) {
        if (startTime <= 0) {
            startTime = System.currentTimeMillis();
        }
        long temp;
        Log.e("", "cycle:" + (temp = System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {

            DrawHelper.clearCanvas(canvas);
            DrawHelper.drawDuration(
                    canvas,
                    String.valueOf(maxDuration = Math.max(temp, maxDuration)) + ":"
                            + String.valueOf(avgDuration = (temp + avgDuration) / 2) + ":"
                            + String.valueOf(temp));
            DrawHelper.drawText(canvas, text);
            DrawHelper.drawCircle(cx, cy, canvas);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            Log.e("draw Time", "draw time:" + (System.currentTimeMillis() - startTime));
        }

    }

    public class DrawHandler extends Handler {

        private static final int START = 1;

        private static final int UPDATE = 2;

        private boolean quitFlag;

        public DrawHandler() {
            super();
        }

        public DrawHandler(Looper looper) {
            super(looper);
        }

        public void quit() {
            quitFlag = true;
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case START:
                    quitFlag = false;
                    sendEmptyMessage(UPDATE);
                    break;
                case UPDATE:
                    if (!quitFlag) {
                        drawTime();
                        sendEmptyMessageDelayed(UPDATE, 0);
                    }
                    break;
            }
        }

    }

}