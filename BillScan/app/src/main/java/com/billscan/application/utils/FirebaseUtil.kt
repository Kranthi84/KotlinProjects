package com.billscan.application.utils

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

class FirebaseUtil(val view: View) {
    fun runTextRecogmition(selectedImage: Bitmap) {
        view.showProgress()
        val image = FirebaseVisionImage.fromBitmap(selectedImage)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        view.hideProgress()
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            view.showNoTextMessage()
            return
        }
        blocks.forEach { block ->
            block.lines.forEach { line ->
                line.elements.forEach { element ->
                    if (looksLikeHandle(element.text)) {
                        view.showHandle(element.text, element.boundingBox)
                    }
                }
            }
        }
    }

    private fun looksLikeHandle(text: String) =
        text.matches(Regex("@(\\w+)"))

    interface View {
        fun showNoTextMessage()
        fun showHandle(text: String, boundingBox: Rect?)
        fun showBox(boundingBox: Rect?)
        fun showProgress()
        fun hideProgress()
    }
}