package com.billscan.application.view_models

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.billscan.application.database.BillDao

class CameraViewModelFactory(
    private val context: Context,
    private val application: Application,
    private val billDao: BillDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(context, application, billDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}