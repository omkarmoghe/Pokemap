package com.omkarmoghe.pokemap.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aronhomberg on 26.07.16.
 */
public class RemoteImageLoader {

    private static  Map<String, BitmapDescriptor> bitmapCache = new HashMap<>();

    public static void load(final String url, int pxWidth, int pxHeight, Context context, final Callback onFetch) {

        // ultra-fast cache reply
        if (bitmapCache.containsKey(url)) {

            onFetch.onFetch(bitmapCache.get(url));

        } else {

            Glide.with(context).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {

                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

                        bitmapCache.put(url, bitmapDescriptor);

                        onFetch.onFetch(bitmapDescriptor);
                    }
                });
        }
    }

    public interface Callback {
        void onFetch(BitmapDescriptor bitmapDescriptor);
    }
}
