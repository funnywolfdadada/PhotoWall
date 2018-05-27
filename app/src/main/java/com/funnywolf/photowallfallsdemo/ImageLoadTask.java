package com.funnywolf.photowallfallsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileDescriptor;

import okhttp3.ResponseBody;

/**
 * Created by funnywolf on 18-5-25.
 */
public class ImageLoadTask  extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "ImageLoadTask";

    private String mUrl;
    private ImageView mImageView;
    private ZoomImageView mZoomImageView;

    public ImageLoadTask(ImageView imageView) {
        mImageView = imageView;
    }
    public ImageLoadTask(ZoomImageView zoomImageView) {
        mZoomImageView = zoomImageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        mUrl = strings[0];
        Bitmap bitmap = ImageLoader.getInstance().getBitmapFromDiskCache(mUrl);
        if(bitmap == null) {
            ResponseBody responseBody = OkHttpUtils.getResponseBody(mUrl);
            if (responseBody != null) {
                ImageLoader.getInstance().addBitmapToDiskCache(mUrl, responseBody.byteStream());
                responseBody.close();
                bitmap = ImageLoader.getInstance().getBitmapFromDiskCache(mUrl);
            }
            if(bitmap != null) {
                ImageLoader.getInstance().addBitmapToMemoryCache(mUrl, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            if(mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            }else if(mZoomImageView != null) {
                mZoomImageView.setImageBitmap(bitmap);
            }
        }
    }
}
