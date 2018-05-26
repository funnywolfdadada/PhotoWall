package com.funnywolf.photowallfallsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

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
        Bitmap bitmap = null;
        ResponseBody responseBody = OkHttpUtils.getResponseBody(mUrl);
        if(responseBody != null) {
            bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            ImageLoader.getInstance().addBitmapToMemoryCache(mUrl, bitmap);
            if(mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            }else if(mZoomImageView != null) {
                mZoomImageView.setImageBitmap(bitmap);
            }
        }
    }
}
