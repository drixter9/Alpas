package com.example.alpas.utilsAlpas

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.alpas.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserImage(image: Any, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.user_placeholder)
                .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun loadProductImage(image: Any, imageView: ImageView){
        try{
            Glide
                    .with(context)
                    .load(image)
                    .centerCrop()
                .placeholder(R.drawable.ic_baseline_no_image)
                    .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}