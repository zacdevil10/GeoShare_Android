package uk.co.appsbystudio.geoshare.utils.ui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.TreeMap;

public class MapStyleManager implements GoogleMap.OnCameraMoveListener {

    private final Context context;
    private final GoogleMap googleMap;
    private final GoogleMap.OnCameraMoveListener onCameraMoveListener;
    private final TreeMap<Float, Integer> styleMap = new TreeMap<>();

    private int currentMapStyleRes = 0;

    private MapStyleManager(Context context, GoogleMap googleMap, GoogleMap.OnCameraMoveListener onCameraMoveListener) {
        this.context = context;
        this.googleMap = googleMap;
        this.onCameraMoveListener = onCameraMoveListener;

        this.googleMap.setOnCameraMoveListener(this);
    }

    public static MapStyleManager attachToMap(Context context, GoogleMap map, GoogleMap.OnCameraMoveListener onCameraMoveListener) {
        return new MapStyleManager(context, map, onCameraMoveListener);
    }

    public static MapStyleManager attachToMap(Context context, GoogleMap map) {
        return new MapStyleManager(context, map, null);
    }

    @Override public void onCameraMove() {
        if (null != onCameraMoveListener) {
            onCameraMoveListener.onCameraMove();
        }
        updateMapStyle();
    }

    public void addStyle(int mapStyle) {
        this.styleMap.put((float) 14, mapStyle);
        updateMapStyle();
    }

    private void updateMapStyle() {
        CameraPosition cameraPosition = this.googleMap.getCameraPosition();
        float currentZoomLevel = cameraPosition.zoom;

        for (float key : this.styleMap.descendingKeySet()) {
            if (currentZoomLevel >= key) {
                Integer styleId = this.styleMap.get(key);
                setMapStyle(styleId);
                return;
            }
        }
    }

    private void setMapStyle(int styleRes) {
        if (this.currentMapStyleRes != styleRes) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this.context, styleRes);
            this.googleMap.setMapStyle(style);
            this.currentMapStyleRes = styleRes;
        }
    }
}
