package com.funnywolf.photowallfallsdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;

/**
 * Created by funnywolf on 18-5-25.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private static final String TAG = "ImageAdapter";

    private Context mContext;
    private List<String> mUrlList;

    public ImageAdapter(Context context, List<String> urls) {
        mContext = context;
        mUrlList = urls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageDetailActivity.class);
                intent.putExtra("image_position", viewHolder.getAdapterPosition());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = mUrlList.get(position);
        Bitmap bitmap = ImageLoader.getInstance().getBitmapFromMemoryCache(url);
        if(bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
        }else {
            holder.imageView.setImageResource(R.drawable.loading);
            new ImageLoadTask(holder.imageView).execute(url);
        }
    }

    @Override
    public int getItemCount() {
        return mUrlList != null ? mUrlList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
        }
    }
}
