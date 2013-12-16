package com.playhaven.hackathon.spyglass;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by francescosimoneschi on 12/14/13.
 */
public class ShowDevicesView extends FrameLayout {
    /**
     * Interface to listen for changes on the view layout.
     */
    public interface ChangeListener {
        /** Notified of a change in the view. */
        public void onChange();
    }

    private final TextView mShowDevicesTextView;

    private boolean mVisible;
    private String mStatus = "";
    private ChangeListener mChangeListener;

    public ShowDevicesView(Context context) {
        this(context, null, 0);
    }

    public ShowDevicesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowDevicesView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        LayoutInflater.from(context).inflate(R.layout.card_showdevices, this);

        mShowDevicesTextView = (TextView) findViewById(R.id.devices_name_view);

    }

    public void setStatus(String status)
    {
        mStatus = status;
    }

    /**
     * Set a {@link ChangeListener}.
     */
    public void setListener(ChangeListener listener) {
        mChangeListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

    }



    private void updateText(String text) {
        mShowDevicesTextView.setText(text);
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }

}
