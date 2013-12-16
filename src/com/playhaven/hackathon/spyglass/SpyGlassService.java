package com.playhaven.hackathon.spyglass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Binder;
import android.widget.RemoteViews;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.*;

/**
 * Created by francescosimoneschi on 12/14/13.
 */
public class SpyGlassService extends Service{

    // "Life cycle" constants
    // Currently not being used...

    // [1] Starts from this..
    private static final int STATE_NORMAL = 1;

    // [2] When panic action has been triggered by the user.
    private static final int STATE_PANIC_TRIGGERED = 2;

    // [3] Note that cancel, or successful send, etc. change the state back to normal
    // These are intermediate states...
    private static final int STATE_CANCEL_REQUESTED = 4;
    private static final int STATE_CANCEL_PROCESSED = 8;
    private static final int STATE_PANIC_PROCESSED = 16;
    // ....

    // Global "state" of the service.
    private int currentState;

    private static final String TAG = "SpyGlassService";
    private static final String LIVE_CARD_ID = "spyglass";


    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;
    private SpyGlassDrawer mCallback;

    final Map<String, String> devices = new Hashtable<String, String>();

    // No need for IPC...
    public class LocalBinder extends Binder {
        public SpyGlassService getService() {
            return SpyGlassService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        currentState = STATE_NORMAL;

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.xxx");   // TBD:..

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onServiceStart();
        return START_STICKY;
    }

    // Service state handlers.
    // ....

    private boolean onServiceStart()
    {
        Log.d(TAG,"onServiceStart() called.");

        // Publish live card...
        publishCard(this);

        StartDiscoverDevices();
        //GetDevice() or do something

        currentState = STATE_NORMAL;
        return true;
    }

    private boolean onServicePause()
    {
        Log.d(TAG,"onServicePause() called.");
        return true;
    }
    private boolean onServiceResume()
    {
        Log.d(TAG,"onServiceResume() called.");
        return true;
    }

    private boolean onServiceStop()
    {
        Log.d(TAG,"onServiceStop() called.");

        // TBD:
        // Unpublish livecard here
        // .....
        unpublishCard(this);
        // ...

        StopDiscoverDevices();

        return true;
    }

    // For live cards...

    private void publishCard(Context context)
    {
        Log.d(TAG,"publishCard() called.");
        // if (liveCard == null || !liveCard.isPublished()) {
        if (mLiveCard == null) {
            TimelineManager tm = TimelineManager.from(context);
            mLiveCard = tm.getLiveCard(LIVE_CARD_ID);
            // liveCard.setNonSilent(false);       // Initially keep it silent ???
            mLiveCard.setNonSilent(true);      // for testing, it's more convenient. Bring the card to front.
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.card_showdevices);
            mLiveCard.setViews(remoteViews);
            Intent intent = new Intent(context, MainMenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
            mLiveCard.publish();
        } else {
            // Card is already published.
            return;
        }
    }

    // This will be called by the "HeartBeat".
    private void updateCard(Context context)
    {
        Log.d(TAG,"updateCard() called.");
        // if (liveCard == null || !liveCard.isPublished()) {
        if (mLiveCard == null) {
            // Use the default content.
            publishCard(context);
        } else {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.card_showdevices);

            String content = "";

            content = joinValues(devices, " | ");

            remoteViews.setTextViewText(R.id.devices_name_view, content);
            mLiveCard.setViews(remoteViews);

            // Do we need to re-publish ???
            // Unfortunately, the view does not refresh without this....
            Intent intent = new Intent(context, MainMenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
            // Is this if() necessary???? or Is it allowed/ok not to call publish() when updating????
            if(! mLiveCard.isPublished()) {
                mLiveCard.publish();
            } else {

            }
        }
    }

    String joinValues(Map<String, String> map, String separator)
    {
        StringBuilder out=new StringBuilder();
        for(String value : map.values())
        {
            out.append(separator).append(value);

        }
        return out.toString();
    }

    private void unpublishCard(Context context) {
        if (mLiveCard != null) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
    }

    private void StartDiscoverDevices()
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }

        adapter.startDiscovery();

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    devices.put(device.getAddress(), device.getName());

                    updateCard(SpyGlassService.this);
                    Log.d("Found:", device.getName());
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    public void StopDiscoverDevices()
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.cancelDiscovery();
    }

    @Override
    public void onDestroy() {
        unpublishCard(this.getApplicationContext());
        super.onDestroy();
    }

}
