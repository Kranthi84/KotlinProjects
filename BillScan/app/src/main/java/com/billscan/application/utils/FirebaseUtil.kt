package com.billscan.application.utils

import android.graphics.Bitmap
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
        val listOftexts = ArrayList<String>()
        blocks.forEach { block ->
            block.lines.forEach { line ->
                line.elements.forEach { element ->
                    listOftexts.add(element.text)

                }
            }
        }

        view.showHandle(listOftexts)

    }


    interface View {
        fun showNoTextMessage()
        fun showHandle(textList: List<String>)
        fun showProgress()
        fun hideProgress()
    }
}