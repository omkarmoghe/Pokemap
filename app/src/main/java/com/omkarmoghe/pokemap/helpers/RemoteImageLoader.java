package com.omkarmoghe.pokemap.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Cache wrapper around the Glide image loader.
 * <p>
 * Created by aronhomberg on 26.07.16.
 */
public class RemoteImageLoader {
    private static final String TAG = "RemoteImageLoader";
    private static final int MAX_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<String, Bitmap>((int) (MAX_SIZE /8)){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    public static void load(final String url, final int pxWidth, final int pxHeight,
                            Drawable placeholderDrawable, Context context, final Callback onFetch) {
        final String key = url+pxWidth +"x" + pxHeight;
        Bitmap bitmap = BITMAP_CACHE.get(key);
        if(bitmap != null){
            onFetch.onFetch(bitmap);
        } else {

            Glide.with(context).load(url)
                    .asBitmap()
                    .skipMemoryCache(false)
                    .placeholder(placeholderDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>(pxWidth, pxHeight) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            addBitmapToMemoryCache(key, bitmap);
                            onFetch.onFetch(bitmap);
                        }
                    });
        }
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (BITMAP_CACHE.get(key) == null) {
            BITMAP_CACHE.put(key, bitmap);
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    private static Bitmap scaleBitmap(Bitmap bitmap, int reqWidth, int reqHeight){
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public interface Callback {
        void onFetch(Bitmap bitmap);
    }
}
