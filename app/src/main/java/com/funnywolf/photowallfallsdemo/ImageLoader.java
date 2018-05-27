package com.funnywolf.photowallfallsdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by funnywolf on 18-5-22.
 */

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private static ImageLoader mImageLoader;

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ImageLoader() {
        int cacheSize = (int) Runtime.getRuntime().maxMemory() / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        File cacheDir = Utils.getDiskCacheDir("bitmap");
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, Utils.getAppVersion(), 1,
                    20 * 1024 *1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ImageLoader getInstance() {
        if(mImageLoader == null)
            mImageLoader = new ImageLoader();
        return mImageLoader;
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if(key == null || bitmap == null) {
            return;
        }
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap = null;
        key = Utils.hashKeyForDisk(key);
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if(snapshot != null) {
                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                snapshot.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void addBitmapToDiskCache(String url, InputStream is) {
        DiskLruCache.Editor editor = null;
        String key = Utils.hashKeyForDisk(url);
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if(snapshot == null) {
                editor = mDiskLruCache.edit(key);
            }
            if(editor != null) {
                OutputStream os = editor.newOutputStream(0);
                byte[] bytes = new byte[8 * 1024];
                int len;
                while((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                editor.commit();
                editor = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(editor != null) {
                    editor.abort();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public void fluchCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
