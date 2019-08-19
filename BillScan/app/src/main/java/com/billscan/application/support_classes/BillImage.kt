package com.billscan.application.support_classes

import android.content.Context
import java.io.File

import kotlin.random.Random

data class BillImage(
    var context: Context?,
    val file_name: String = "IMG_" + Random.nextLong(until = 999999999) + ".jpg",
    val file_location: File? = context?.let {
        it.filesDir
    }
) {
    fun photoFile(): File? {
        return file_location?.let { File(file_location, file_name) }
    }
}
