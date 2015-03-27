package com.detroitlabs.mapmodule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.detroitlabs.feedback.util.FileHelper;
import com.detroitlabs.feedback.core.AttachmentManger;
import com.detroitlabs.feedback.core.Plugin;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;


/**
 * Created by andrewgiang on 3/19/15.
 */
public class MapPlugin implements Plugin {

    @Override
    public void execute(final Context context, final AttachmentManger attachmentManger) {
        final View view = ((Activity)context).getWindow().getDecorView().getRootView();
        final MapFinder mapFinder = new MapFinder();
        LayoutTraverser.build(mapFinder).traverse((ViewGroup) view);
        final MapView mapView = mapFinder.getMapView();

        if(mapView != null){
            mapView.getMap().snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    attachmentManger.add(FileHelper.bitmapToFile(context, "map.jpg", bitmap));
                }
            });
        }
    }

    public static class MapFinder implements LayoutTraverser.Processor{
        private MapView mapView;

        public MapView getMapView() {
            return mapView;
        }

        @Override
        public boolean process(View view) {
            if(view instanceof MapView){
                this.mapView = (MapView) view;
                return false;
            }
            return true;
        }
    }

}
