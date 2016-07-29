package com.omkarmoghe.pokemap.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache wrapper around the Glide image loader.
 * <p>
 * Created by aronhomberg on 26.07.16.
 */
public class RemoteImageLoader {

    private static Map<String, Bitmap> bitmapCache = new HashMap<>();

    public static void load(final String url, int pxWidth, int pxHeight,
                            Drawable placeholderDrawable, Context context, final Callback onFetch) {
        // ultra-fast cache reply
        if (bitmapCache.containsKey(url)) {
            onFetch.onFetch(bitmapCache.get(url));
        } else {
            Glide.with(context).load(url)
                    .asBitmap()
                    .skipMemoryCache(false)
                    .placeholder(placeholderDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            bitmapCache.put(url, bitmap);
                            onFetch.onFetch(bitmap);
                        }
                    });
        }
    }

    public interface Callback {
        void onFetch(Bitmap bitmap);
    }
}
