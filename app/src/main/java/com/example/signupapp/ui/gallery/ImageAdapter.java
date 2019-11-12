package com.example.signupapp.ui.gallery;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.signupapp.R;
import com.squareup.picasso.Picasso;

import com.example.signupapp.ui.gallery.Upload;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Objects;


public class ImageAdapter extends Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;

    public ImageAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        Upload uploadCurrent = mUploads.get(position);

        holder.tviewName.setText(mUploads.get(position).getName().toUpperCase());
        if (mUploads.get(position).getImageUrl() != ""){
            Log.i("url: ", mUploads.get(position).getImageUrl());
            Picasso.with(mContext).load(mUploads.get(position).getImageUrl()).into((holder.uploadedImageViewed));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,mUploads.get(position).getName()+"", Toast.LENGTH_SHORT).show();
            }
        });

        /*holder.tviewName.setText(uploadCurrent.getName());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.vision_icon)
                .fit()
                .centerCrop()
                .into(holder.uploadedImageViewed);*/
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView tviewName;
        public ImageView uploadedImageViewed;

        public ImageViewHolder(View itemView){
            super(itemView);

            tviewName = itemView.findViewById(R.id.tviewName);
            uploadedImageViewed = itemView.findViewById(R.id.uploadedImageViewed);
        }
    }
}
