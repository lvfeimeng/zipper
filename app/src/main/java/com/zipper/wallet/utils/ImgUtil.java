package com.zipper.wallet.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zipper.wallet.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ImgUtil {

    public static void load(String imgUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadCircleImage(String imgUrl, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadRoundCornerImage(Context context, String imgUrl, final ImageView imageView, int radius) {
        Glide.with(context)
                .load(imgUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void load(int imgId, ImageView imageView) {
        Glide.with(imageView.getContext()).load(imgId).into(imageView);
    }

    public static void loadCircleImage(int imgId, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(imgId)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadRoundCornerImage(int imgId, final ImageView imageView, int radius) {
        Glide.with(imageView.getContext())
                .load(imgId)
                .bitmapTransform(new RoundedCornersTransformation(imageView.getContext(), radius, 0))
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public static void loadGif(int imgId, ImageView imageView) {
        Glide.with(imageView.getContext()).load(imgId).asGif().into(imageView);
    }

//    public static void loadHead(String imgUrl, final ImageView imageView) {
//        Glide.with(imageView.getContext())
//                .load(imgUrl)
//                .error(R.mipmap.ic_launcher)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        imageView.setImageResource(R.mipmap.ic_launcher);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        imageView.setImageDrawable(resource);
//                        return false;
//                    }
//                })
//                .into(imageView);
//    }
}
