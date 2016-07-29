package com.omkarmoghe.pokemap.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Cache wrapper around the Glide image loader.
 * 29.07.16: for now, the second layer cache is removed, caching is handled by Glide.
 * <p>
 * Created by aronhomberg on 26.07.16.
 */
public class RemoteImageLoader {

    public static void load(final String url, int pxWidth, int pxHeight,
                            Drawable placeholderDrawable, Context context, final Callback onFetch) {
        Glide.with(context).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .placeholder(placeholderDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        onFetch.onFetch(bitmap);
                    }
                });
    }

    public interface Callback {
        void onFetch(Bitmap bitmap);
    }
}
