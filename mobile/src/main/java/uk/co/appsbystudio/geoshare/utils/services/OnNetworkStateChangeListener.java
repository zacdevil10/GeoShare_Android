package uk.co.appsbystudio.geoshare.utils.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.HashSet;
import java.util.Set;

import uk.co.appsbystudio.geoshare.utils.Connectivity;

public class OnNetworkStateChangeListener extends BroadcastReceiver {

    protected Set<NetworkStateReceiverListener> listeners;
    protected Boolean connected;
    protected Boolean isConnectedWifi;
    protected Boolean isConnectedMobile;

    public OnNetworkStateChangeListener() {
        listeners = new HashSet<NetworkStateReceiverListener>();
        connected = null;
        isConnectedWifi = null;
        isConnectedMobile = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        if (Connectivity.isConnected(context)) {
            connected = true;
            if (Connectivity.isConnectedWifi(context)) isConnectedWifi = true;
            if (Connectivity.isConnectedMobile(context)) isConnectedMobile = true;
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            connected = false;
            isConnectedWifi = false;
            isConnectedMobile = false;
        }

        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : listeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (connected == null || listener == null || isConnectedMobile == null || isConnectedWifi == null) {
            return;
        }

        if (connected) {
            listener.networkAvailable();
        } else {
            listener.networkUnavailable();
        }

        if (isConnectedMobile) {
            listener.networkMobile();
        }

        if (isConnectedWifi) {
            listener.networkWifi();
        }
    }

    public void addListener(NetworkStateReceiverListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkStateReceiverListener listener) {
        listeners.remove(listener);
    }

    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
        void networkWifi();
        void networkMobile();
    }
}
