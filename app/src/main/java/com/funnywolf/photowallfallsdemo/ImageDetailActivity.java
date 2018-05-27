package com.funnywolf.photowallfallsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.disklrucache.DiskLruCache;

/**
 * Created by funnywolf on 18-5-25.
 */

public class ImageDetailActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {
    private static final String TAG = "ImageDetailActivity";

    private ViewPager mViewPager;
    private TextView mPageText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details);

        mViewPager = findViewById(R.id.view_pager);
        mPageText = findViewById(R.id.page_text);

        int position = getIntent().getIntExtra("image_position", 0);
        mPageText.setText((position + 1) + "/" + Images.imageUrls.length);
        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(this);
        //mViewPager.setPageTransformer(false, new DepthPageTransformer());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPageText.setText((position + 1) + "/" + Images.imageUrls.length);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ImageDetailActivity.this)
                    .inflate(R.layout.zoom_image_layout, null);
            ZoomImageView zoomImageView = view.findViewById(R.id.zoom_image_view);

            String url = Images.imageUrls[position];
            Bitmap bitmap = ImageLoader.getInstance().getBitmapFromMemoryCache(url);
            if (bitmap == null) {
                new ImageLoadTask(zoomImageView).execute(url);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
            }
            zoomImageView.setImageBitmap(bitmap);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return Images.imageUrls.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) {// [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor); view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
