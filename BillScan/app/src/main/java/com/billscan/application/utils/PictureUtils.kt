package com.billscan.application.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

class PictureUtils {

    companion object {

        fun getScaledBitmap(path: String, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return getScaledBitmap(path, size.x, size.y)
        }

        fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)

            val srcWidth: Float = options.outWidth.toFloat()
            val srcHeight: Float = options.outHeight.toFloat()

            var sampleSize = 1
            if (srcHeight > destHeight || srcWidth > destWidth) {
                val heightScale: Float = srcHeight / destHeight
                val widthScale: Float = srcWidth / destWidth
                sampleSize = if (heightScale > widthScale) {
                    heightScale.roundToInt()
                } else
                    widthScale.roundToInt()

            }

            options = BitmapFactory.Options()
            options.inSampleSize = sampleSize

            return BitmapFactory.decodeFile(path, options)
        }


    }


}