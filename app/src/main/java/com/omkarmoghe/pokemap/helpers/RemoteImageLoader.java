package com.omkarmoghe.pokemap.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

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

    public static void loadMapIcon(Context context, final String url, int pxWidth, int pxHeight,
                                   Drawable placeholderDrawable, final Callback onFetch) {
        Glide.with(context).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .placeholder(placeholderDrawable)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        onFetch.onFetch(bitmap);
                    }
                });
    }

    public static void loadMapIcon(Context context, final String url, int pxWidth, int pxHeight,
                                   final Callback onFetch) {
        Glide.with(context).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        onFetch.onFetch(bitmap);
                    }
                });
    }

    public static void loadInto(ImageView view, String url, Drawable placeholderDrawable) {
        Glide.with(view.getContext()).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .placeholder(placeholderDrawable)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(view);
    }

    public static void loadInto(ImageView view, String url) {
        Glide.with(view.getContext()).load(url)
                .asBitmap()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(view);
    }

    public interface Callback {
        void onFetch(Bitmap bitmap);
    }
}
