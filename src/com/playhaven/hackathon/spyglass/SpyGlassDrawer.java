package com.playhaven.hackathon.spyglass;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;

public class SpyGlassDrawer implements SurfaceHolder.Callback {
    private static final String TAG = "SpyGlassDrawer";


    ShowDevicesView showDevicesView;
    private SurfaceHolder mHolder;

    public SpyGlassDrawer(Context context)
    {
        showDevicesView = new ShowDevicesView(context);
        showDevicesView.setListener(new ShowDevicesView.ChangeListener() {

            @Override
            public void onChange() {
                draw(showDevicesView);
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface created");
        mHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        showDevicesView.measure(measuredWidth, measuredHeight);
        showDevicesView.layout(
                0, 0, showDevicesView.getMeasuredWidth(), showDevicesView.getMeasuredHeight());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface destroyed");
        mHolder = null;
    }

    /**
     * Draws the view in the SurfaceHolder's canvas.
     */
    private void draw(View view) {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            view.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
